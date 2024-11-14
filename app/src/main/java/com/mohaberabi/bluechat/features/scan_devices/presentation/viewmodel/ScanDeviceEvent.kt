package com.mohaberabi.bluechat.features.scan_devices.presentation.viewmodel

import com.mohaberabi.bluechat.core.domain.model.UiText

sealed interface ScanDeviceEvent {
    data class Connected(val deviceName: String) : ScanDeviceEvent
    data class Error(val error: UiText) : ScanDeviceEvent
}