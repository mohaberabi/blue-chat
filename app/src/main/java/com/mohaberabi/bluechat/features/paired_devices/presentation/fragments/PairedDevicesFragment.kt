package com.mohaberabi.bluechat.features.paired_devices.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mohaberabi.bluechat.core.presentation.adapter.DeviceListAdapter
import com.mohaberabi.bluechat.core.util.extensions.asString
import com.mohaberabi.bluechat.core.util.extensions.collectWithLifeCycle
import com.mohaberabi.bluechat.core.util.extensions.createLoadingDialog
import com.mohaberabi.bluechat.core.util.extensions.showIf
import com.mohaberabi.bluechat.core.util.extensions.showSnackBar
import com.mohaberabi.bluechat.databinding.FragmentPairedDevicesBinding
import com.mohaberabi.bluechat.features.paired_devices.presentation.viewmodel.PairedDeviceActions
import com.mohaberabi.bluechat.features.paired_devices.presentation.viewmodel.PairedDeviceEvents
import com.mohaberabi.bluechat.features.paired_devices.presentation.viewmodel.PairedDeviceState
import com.mohaberabi.bluechat.features.paired_devices.presentation.viewmodel.PairedDevicesViewModel
import com.mohaberabi.bluechat.features.scan_devices.presentation.fragments.ScanDeviceFragmentDirections
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PairedDevicesFragment : Fragment() {
    private val viewModel by viewModels<PairedDevicesViewModel>()
    private var _binding: FragmentPairedDevicesBinding? = null
    private val binding get() = requireNotNull(_binding)
    private lateinit var pairedDevicesAdapter: DeviceListAdapter
    private val connectingDialog by lazy {
        requireContext().createLoadingDialog(
            onCancel = { dialog ->
                viewModel.onActin(PairedDeviceActions.CancelConnection)
                dialog.dismiss()
            },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPairedDevicesBinding.inflate(
            inflater,
            container,
            false
        )
        setupViews()
        collectEvents()
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        collectState()
    }


    private fun setupViews() {
        pairedDevicesAdapter = DeviceListAdapter(
            onClick = {
                viewModel.onActin(PairedDeviceActions.ConnectToDevice(it))
            }
        )
        with(binding) {
            pairedRecView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = pairedDevicesAdapter
            }
            retryButton.setOnClickListener {
                viewModel.onActin(PairedDeviceActions.GetPairedDevices)
            }
        }

    }


    private fun collectEvents() {
        collectWithLifeCycle(
            viewModel.events,
        ) { event ->
            when (event) {
                is PairedDeviceEvents.Connected -> goToChatScreen(event.connectedName)
                is PairedDeviceEvents.Error -> requireView().showSnackBar(event.error)
            }
        }
    }

    private fun goToChatScreen(senderName: String) {
        findNavController().navigate(ScanDeviceFragmentDirections.goToChat(senderName))
    }

    private fun collectState() {
        collectWithLifeCycle(
            viewModel.state,
        ) { state ->
            bindPairedDevicesState(state)
            if (state.connecting) {
                connectingDialog.show()
            } else {
                connectingDialog.dismiss()
            }
            pairedDevicesAdapter.submitList(state.pairedDevices.toList())
        }
    }

    private fun bindPairedDevicesState(
        state: PairedDeviceState,
    ) {
        with(binding) {
            pairedRecView.showIf(!state.loading && state.error.isEmpty)
            state.error.apply {
                errorWidget.showIf(!isEmpty)
                errorText.showIf(!isEmpty) { text = asString(requireContext()) }
            }
            loader.showIf(state.loading)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}