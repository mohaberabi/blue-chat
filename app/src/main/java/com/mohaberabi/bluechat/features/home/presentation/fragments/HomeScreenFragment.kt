package com.mohaberabi.bluechat.features.home.presentation.fragments

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.mohaberabi.bluechat.core.util.requiresBluetoothPermissions
import com.mohaberabi.bluechat.core.util.requiresNotificationsPermissions
import com.mohaberabi.bluechat.databinding.FragmentHomeScreenBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeScreenFragment : Fragment() {
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val permissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(
            inflater,
            container,
            false
        )
        requestPermissions()
        setupButtons()
        return binding.root
    }


    private fun setupButtons() {
        with(binding) {
            pairedDevicesButton.setOnClickListener {
                goToPairedDevices()
            }
            scanDevicesButton.setOnClickListener {
                goToScanDevices()
            }
        }
    }

    private fun requestPermissions() {
        val permissions = buildPermissionsArray()
        permissionsLauncher.launch(permissions)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun goToPairedDevices() = findNavController().navigate(
        HomeScreenFragmentDirections.goToPaired(),
    )

    private fun goToScanDevices() = findNavController().navigate(
        HomeScreenFragmentDirections.goScanDevice(),
    )

    private fun buildPermissionsArray() = buildList {
        if (requiresNotificationsPermissions()) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (requiresBluetoothPermissions()) {
            add(Manifest.permission.BLUETOOTH_CONNECT)
            add(Manifest.permission.BLUETOOTH_SCAN)
        }
    }.toTypedArray()
}