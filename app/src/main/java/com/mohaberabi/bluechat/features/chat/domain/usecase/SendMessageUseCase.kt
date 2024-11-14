package com.mohaberabi.bluechat.features.chat.domain.usecase

import com.mohaberabi.bluechat.features.chat.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(text: String) = chatRepository.sendMessage(text)
}