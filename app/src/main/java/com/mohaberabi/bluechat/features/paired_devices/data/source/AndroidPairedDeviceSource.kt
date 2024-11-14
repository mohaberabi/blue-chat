package com.mohaberabi.bluechat.features.paired_devices.data.source

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.core.content.getSystemService
import com.mohaberabi.bluechat.core.data.mapper.toAppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.errros.AppError
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.core.util.DispatcherProvider
import com.mohaberabi.bluechat.core.domain.model.errros.handleBluetoothOperation
import com.mohaberabi.bluechat.features.paired_devices.domain.source.PairedDeviceSource
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Android implementation for retrieving paired Bluetooth devices use the bluetooth adapter from system .
 * @property dispatchers Provides coroutine dispatchers  to be able to switch it in testing env
 * to make use of virtual clock not the dispatchers real clock as well as allowing any invokers to invoke
 * the function here in a main safe manner
 */
@SuppressLint("MissingPermission")
class AndroidPairedDeviceSource @Inject constructor(
    private val context: Context,
    private val dispatchers: DispatcherProvider,
) : PairedDeviceSource {

    /**
     * Getting  the bluetooth manager or throw [IllegalArgumentException] in case is null
     */
    private val bluetoothManager by lazy {
        requireNotNull(context.getSystemService<BluetoothManager>())
    }

    private val adapter by lazy {
        bluetoothManager.adapter
    }

    /**
     * Gets all paired Bluetooth devices. if the adapter returns them without any error else it returns
     * error with the reason [handleBluetoothOperation] is the global function handling all possible bluetooth
     * errors
     * @return  [AppResult]
     */
    override suspend fun getAllPairedDevices(
    ): AppResult<Set<AppBluetoothDevice>, AppError> {
        return withContext(dispatchers.io) {
            handleBluetoothOperation {
                adapter?.bondedDevices?.map {
                    it.toAppBluetoothDevice()
                }?.toSet() ?: setOf()
            }
        }
    }
}