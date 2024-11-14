package com.mohaberabi.bluechat.features.chat.data.repository

import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.MessageModel
import com.mohaberabi.bluechat.core.domain.model.errros.AppError
import com.mohaberabi.bluechat.core.domain.model.errros.handleBluetoothOperation
import com.mohaberabi.bluechat.features.chat.domain.repository.ChatRepository
import com.mohaberabi.bluechat.core.domain.source.SocketConnectionManager
import com.mohaberabi.bluechat.core.domain.source.UserLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Default implementation of the [ChatRepository] interface.
 */
class DefaultChatRepository @Inject constructor(
    private val socketConnectionManager: SocketConnectionManager,
    private val userLocalDataSource: UserLocalDataSource,
) : ChatRepository {
    /**
     * Listens to incoming messages from the Bluetooth socket connection.
     * deserialize the incoming bytes into  [MessageModel]
     * maps  the coming  messages [sentByMe] into false as long as we are
     * receiving that means it is not our message
     * Converts raw byte data from the socket into [MessageModel] objects
     * @return A [Flow] of [MessageModel] representing incoming messages.
     */
    override fun listenToIncomingMessage(): Flow<MessageModel> {
        return socketConnectionManager.listenToIncomingDataOnConnection()
            .map { MessageModel.fromByteArray(it).copy(sentByMe = false) }
    }

    /**
     * Sends a message over the current  socket connection.
     * Creates a [MessageModel] with the given text, including the sender's name
     * retrieved from the [UserLocalDataSource]. The message is serialized to
     * a byte array and sent through the socket .
     * @return An [AppResult] containing either the sent [MessageModel] as a confirmation of done
     * or an [AppError] of failure.
     */
    override suspend fun sendMessage(
        text: String,
    ): AppResult<MessageModel, AppError> {
        return handleBluetoothOperation {
            val name = userLocalDataSource.getCurrentUser().name
            val message = MessageModel(
                text = text,
                senderName = name,
                sentByMe = true
            )
            socketConnectionManager.sendDataToCurrentSocket(message.toByteArray)
            message
        }
    }
}