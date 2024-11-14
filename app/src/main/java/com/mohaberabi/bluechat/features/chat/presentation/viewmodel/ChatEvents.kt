package com.mohaberabi.bluechat.features.chat.presentation.viewmodel

import com.mohaberabi.bluechat.core.domain.model.UiText

sealed interface ChatEvents {

    data object MessageReceiveError : ChatEvents
    data class SendMessageError(val error: UiText) : ChatEvents

    data object ConnectionLost : ChatEvents
}