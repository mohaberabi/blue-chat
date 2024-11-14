package com.mohaberabi.bluechat.core.domain.model

import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError

sealed interface ScanDeviceResult {
    data class ConnectionLost(val reason: BluetoothError) : ScanDeviceResult
    data class DeviceFound(val device: AppBluetoothDevice) : ScanDeviceResult
    data object Idle : ScanDeviceResult

}