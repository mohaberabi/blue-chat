package com.mohaberabi.bluechat.features.chat.domain.repository

import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.EmptyResult
import com.mohaberabi.bluechat.core.domain.model.MessageModel
import com.mohaberabi.bluechat.core.domain.model.errros.AppError
import com.mohaberabi.bluechat.core.domain.model.errros.SocketConnectionError
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun listenToIncomingMessage(): Flow<MessageModel>
    suspend fun sendMessage(text: String): AppResult<MessageModel, AppError>
}