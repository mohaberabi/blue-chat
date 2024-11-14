package com.mohaberabi.bluechat.features.paired_devices.presentation.viewmodel

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.UiText


data class PairedDeviceState(
    val pairedDevices: Set<AppBluetoothDevice> = setOf(),
    val connecting: Boolean = false,
    val error: UiText = UiText.Empty,
    val loading: Boolean = false,
)
