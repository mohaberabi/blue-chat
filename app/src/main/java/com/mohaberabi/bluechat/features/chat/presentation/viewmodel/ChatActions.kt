package com.mohaberabi.bluechat.features.chat.presentation.viewmodel

sealed interface ChatActions {
    data class MessageTextChanged(val text: String) : ChatActions
    data object SendMessage : ChatActions
}