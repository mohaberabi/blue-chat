package com.mohaberabi.bluechat.features.scan_devices.presentation.viewmodel

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice


sealed interface ScanDeviceAction {
    data object ToggleScanning : ScanDeviceAction
    data object OpenConnectionSocket : ScanDeviceAction
    data object CancelOperations : ScanDeviceAction
    data class ConnectToDevice(
        val device: AppBluetoothDevice,
    ) : ScanDeviceAction
}