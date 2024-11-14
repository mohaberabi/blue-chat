package com.mohaberabi.bluechat.features.chat.presentation.viewmodel

import com.mohaberabi.bluechat.core.domain.model.MessageModel

data class ChatState(
    val messages: List<MessageModel> = listOf(),
    val messageText: String = "",
    val senderName: String = ""
)
