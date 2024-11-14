package com.mohaberabi.bluechat.core.data.source

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter
import com.mohaberabi.bluechat.core.data.receivers.BluetoothConnectionReceiver
import com.mohaberabi.bluechat.core.domain.source.ConnectionLost
import com.mohaberabi.bluechat.core.domain.source.ConnectionStateTracker
import com.mohaberabi.bluechat.core.util.AppSupervisorScope
import com.mohaberabi.bluechat.core.util.DispatcherProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class BluetoothConnectionTracker @Inject constructor(
    private val context: Context,
    private val dispatchers: DispatcherProvider,
    private val appSupervisorScope: AppSupervisorScope
) : ConnectionStateTracker {
    private val channel = Channel<ConnectionLost>()

    private val connectionReceiver = BluetoothConnectionReceiver(
        onConnectionLost = {
            appSupervisorScope().launch {
                channel.send(true)
            }
        },
    )

    private fun registerConnectionReceiver() {
        try {
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
                addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            }
            context.registerReceiver(connectionReceiver, filter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun checkConnectionLost(): Flow<ConnectionLost> =
        channel.receiveAsFlow()
            .onStart {
                registerConnectionReceiver()
            }.onCompletion {
                stopTrackingConnectivityLoss()
            }.flowOn(dispatchers.io)

    private fun stopTrackingConnectivityLoss() {
        try {
            context.unregisterReceiver(connectionReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}