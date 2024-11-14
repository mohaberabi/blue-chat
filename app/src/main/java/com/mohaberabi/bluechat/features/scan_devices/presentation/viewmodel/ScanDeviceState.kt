package com.mohaberabi.bluechat.features.scan_devices.presentation.viewmodel

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice


data class ScanDeviceState(
    val scannedDevices: Set<AppBluetoothDevice> = setOf(),
    val isConnecting: Boolean = false,
    val isScanning: Boolean = false,
)
