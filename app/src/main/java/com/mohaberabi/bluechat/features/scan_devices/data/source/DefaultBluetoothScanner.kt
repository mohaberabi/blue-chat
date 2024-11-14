package com.mohaberabi.bluechat.features.scan_devices.data.source

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import androidx.core.content.getSystemService
import com.mohaberabi.bluechat.core.data.receivers.BluetoothScanReceiver
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.EmptyResult
import com.mohaberabi.bluechat.core.domain.model.ScanDeviceResult
import com.mohaberabi.bluechat.core.domain.model.errros.AppError
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.core.domain.model.errros.handleBluetoothOperation
import com.mohaberabi.bluechat.core.util.hasBluetoothPermissions
import com.mohaberabi.bluechat.features.scan_devices.domain.source.BluetoothScanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Default implementation of the `BluetoothScanner` interface for scanning Bluetooth devices.
 * Handles device nearby scanning and searching for them
 */
@SuppressLint("MissingPermission")
class DefaultBluetoothScanner(
    private val context: Context,
) : BluetoothScanner {
    private val manager by lazy {
        requireNotNull(context.getSystemService<BluetoothManager>())
    }

    private val adapter by lazy {
        manager.adapter
    }
    private val _foundDevices = MutableStateFlow<ScanDeviceResult>(ScanDeviceResult.Idle)
    override fun getFoundDevices(): Flow<ScanDeviceResult> = _foundDevices

    /**
     * Broadcast  receiver that mutates the flow when receives events related to found nearby device
     */
    private val scanReceiver = BluetoothScanReceiver(
        onReceiveResult = { result ->
            _foundDevices.update { result }
        },
    )

    /**
     * Requests to start scanning for Bluetooth devices.
     *
     * Validates Bluetooth state and permissions before starting discovery.
     *
     * @return [Done] if scanning starts successfully, or an error result otherwise.
     */
    override fun requestToScanForDevices(): AppResult<Unit, AppError> {
        if (adapter == null) {
            return AppResult.Error(BluetoothError.NotSupported)
        }
        if (!adapter.isEnabled) {
            return AppResult.Error(BluetoothError.Disabled)
        }
        if (!context.hasBluetoothPermissions()) {
            return AppResult.Error(BluetoothError.PermissionNotGranted)
        }
        return handleBluetoothOperation {
            context.registerReceiver(
                scanReceiver,
                IntentFilter().apply {
                    addAction(BluetoothDevice.ACTION_FOUND)
                    addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
                    addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                }
            )
            adapter.startDiscovery()
        }

    }

    /**
     * Stops the ongoing Bluetooth scanning process.
     *
     * Cancels the discovery and unregisters the scan receiver.
     *
     * @return [Done] if the operation completes successfully.
     */
    override fun stopScanning(): EmptyResult = handleBluetoothOperation {
        if (adapter?.isDiscovering == true) {
            adapter.cancelDiscovery()
        }
        context.unregisterReceiver(scanReceiver)
    }

}