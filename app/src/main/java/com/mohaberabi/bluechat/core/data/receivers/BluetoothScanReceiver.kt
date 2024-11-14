package com.mohaberabi.bluechat.core.data.receivers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mohaberabi.bluechat.core.data.mapper.toAppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.ScanDeviceResult
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.core.util.hasBluetoothPermissions

/**
 * Listens for Bluetooth-related broadcasts and handles them.
 *
 * This receiver is mainly used to catch events like finding a new device, Bluetooth turning off,
 * or permission issues. It passes these events to the callback for further processing.
 *
 * @param onReceiveResult A callback to send back when  onReceive invoked
 */
class BluetoothScanReceiver(
    private val onReceiveResult: (ScanDeviceResult) -> Unit
) : BroadcastReceiver() {

    /**
     * Reacts to Bluetooth-related broadcasts.
     *
     * Handles finding devices, Bluetooth state changes, and permission checks.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device = intent.getBluetoothDeviceFromIntent()?.toAppBluetoothDevice()
                device?.let {
                    onReceiveResult(ScanDeviceResult.DeviceFound(it))
                }
            }

            BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                if (state == BluetoothAdapter.STATE_OFF) {
                    onReceiveResult(ScanDeviceResult.ConnectionLost(BluetoothError.Disabled))
                }
            }

            else -> {
                context?.let {
                    if (!it.hasBluetoothPermissions()) {
                        onReceiveResult(ScanDeviceResult.ConnectionLost(BluetoothError.PermissionNotGranted))
                    }
                }
            }
        }
    }
}

/**
 * Extracts a Bluetooth device from the intent. and maps it to the domain model
 *
 * and handle backwards
 */
private fun Intent.getBluetoothDeviceFromIntent() =
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(
            BluetoothDevice.EXTRA_DEVICE,
            BluetoothDevice::class.java
        )
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
    }