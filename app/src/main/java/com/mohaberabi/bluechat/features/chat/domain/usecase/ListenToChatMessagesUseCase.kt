package com.mohaberabi.bluechat.features.chat.domain.usecase

import com.mohaberabi.bluechat.features.chat.domain.repository.ChatRepository
import javax.inject.Inject

class ListenToChatMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {


    operator fun invoke() = chatRepository.listenToIncomingMessage()
}