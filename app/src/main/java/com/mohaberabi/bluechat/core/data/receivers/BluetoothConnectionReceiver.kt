package com.mohaberabi.bluechat.core.data.receivers

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mohaberabi.bluechat.core.data.mapper.toAppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice

/**
 * Listens for Bluetooth-related broadcasts and handles them.
 *
 * This receiver is mainly used to catch events like finding a new device, Bluetooth turning off,
 * or permission issues. It passes these events to the callback for further processing.
 *
 * @param onConnectionLost A callback to send back the result of the onReceive
 */
class BluetoothConnectionReceiver(
    private val onConnectionLost: () -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        val disconnected = action == BluetoothDevice.ACTION_ACL_DISCONNECTED
        if (disconnected) {
            onConnectionLost()
        }
    }

}

