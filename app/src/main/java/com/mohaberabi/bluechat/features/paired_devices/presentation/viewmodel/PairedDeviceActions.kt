package com.mohaberabi.bluechat.features.paired_devices.presentation.viewmodel

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice

sealed interface PairedDeviceActions {
    data object GetPairedDevices : PairedDeviceActions
    data class ConnectToDevice(val device: AppBluetoothDevice) : PairedDeviceActions
    data object CancelConnection : PairedDeviceActions
}