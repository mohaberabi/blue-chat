package com.mohaberabi.bluechat.features.scan_devices.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mohaberabi.bluechat.R
import com.mohaberabi.bluechat.core.presentation.adapter.DeviceListAdapter
import com.mohaberabi.bluechat.core.util.extensions.collectWithLifeCycle
import com.mohaberabi.bluechat.core.util.extensions.createLoadingDialog
import com.mohaberabi.bluechat.core.util.extensions.showSnackBar
import com.mohaberabi.bluechat.databinding.FragmentScanDeviceBinding
import com.mohaberabi.bluechat.features.scan_devices.presentation.viewmodel.ScanDeviceAction
import com.mohaberabi.bluechat.features.scan_devices.presentation.viewmodel.ScanDeviceEvent
import com.mohaberabi.bluechat.features.scan_devices.presentation.viewmodel.ScanDeviceViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ScanDeviceFragment : Fragment() {
    private val viewModel by viewModels<ScanDeviceViewModel>()
    private var _binding: FragmentScanDeviceBinding? = null
    private val binding get() = requireNotNull(_binding)
    private lateinit var scannedDevicesAdapter: DeviceListAdapter
    private val connectingDialog by lazy {
        requireContext().createLoadingDialog(
            onCancel = { dialog ->
                viewModel.onAction(ScanDeviceAction.CancelOperations)
                dialog.dismiss()
            },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanDeviceBinding.inflate(
            inflater,
            container,
            false,
        )
        setupAdapter()
        setupRecyclerView()
        setupButtons()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collecteState()
        collectEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapter() {
        scannedDevicesAdapter = DeviceListAdapter(
            onClick = {
                viewModel.onAction(ScanDeviceAction.ConnectToDevice(it))
            }
        )
    }

    private fun setupRecyclerView() {
        binding.scanDeviceRecView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = scannedDevicesAdapter
        }
    }

    private fun collecteState() {
        collectWithLifeCycle(
            viewModel.state,
        ) { state ->
            binding.scanButton.text =
                if (state.isScanning) getString(R.string.stop_scan)
                else getString(R.string.start_scan)
            if (state.isConnecting) {
                connectingDialog.show()
            } else {
                connectingDialog.dismiss()
            }
            scannedDevicesAdapter.submitList(state.scannedDevices.toList())
        }
    }


    private fun collectEvents() {
        collectWithLifeCycle(
            viewModel.events,
        ) { event ->
            when (event) {
                is ScanDeviceEvent.Connected -> goToChatScreen(event.deviceName)
                is ScanDeviceEvent.Error -> requireView().showSnackBar(event.error)
            }
        }
    }


    private fun goToChatScreen(name: String) {
        findNavController().navigate(ScanDeviceFragmentDirections.goToChat(name))
    }

    private fun setupButtons() {
        with(binding) {
            scanButton.setOnClickListener {
                viewModel.onAction(ScanDeviceAction.ToggleScanning)
            }
            waitConnectin.setOnClickListener {
                viewModel.onAction(ScanDeviceAction.OpenConnectionSocket)
            }
        }

    }

}


