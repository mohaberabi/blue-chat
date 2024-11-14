package com.mohaberabi.bluechat.features.paired_devices.presentation.viewmodel

import com.mohaberabi.bluechat.core.domain.model.UiText

sealed interface PairedDeviceEvents {
    data class Connected(val connectedName: String) : PairedDeviceEvents

    data class Error(val error: UiText) : PairedDeviceEvents
}