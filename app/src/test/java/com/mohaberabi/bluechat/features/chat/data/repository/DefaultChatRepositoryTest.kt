package com.mohaberabi.bluechat.features.chat.data.repository

import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.errros.SocketConnectionError
import com.mohaberabi.bluechat.core.domain.source.SocketConnectionManager
import com.mohaberabi.bluechat.core.domain.source.UserLocalDataSource
import com.mohaberabi.bluechat.generator.messageModel
import com.mohaberabi.bluechat.generator.userModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultChatRepositoryTest {
    private lateinit var repository: DefaultChatRepository
    private val socketConnectionManager: SocketConnectionManager = mockk()
    private val userLocalDataSource: UserLocalDataSource = mockk()

    @Before
    fun setup() {
        repository = DefaultChatRepository(socketConnectionManager, userLocalDataSource)
    }

    @Test
    fun `listenToIncomingMessage should map incoming data to MessageModel`() = runTest {
        val message = messageModel(sentByMe = true)
        val byteArray = message.toByteArray
        val flow = flowOf(byteArray)
        every { socketConnectionManager.listenToIncomingDataOnConnection() } returns flow
        val result = repository.listenToIncomingMessage().first()
        assertEquals(message.copy(sentByMe = false), result)
        assertEquals(false, result.sentByMe)
        verify { socketConnectionManager.listenToIncomingDataOnConnection() }
    }

    @Test
    fun `sendMessage should return AppResult Done with the sent message`() = runTest {
        val user = userModel()
        val message = messageModel(sentByMe = true)
        val bytes = message.toByteArray
        coEvery { userLocalDataSource.getCurrentUser() } returns user

        coEvery {
            socketConnectionManager.sendDataToCurrentSocket(any())
        } coAnswers {
            AppResult.Done(
                bytes
            )
        }

        val result = repository.sendMessage(message.text)
        assert(result is AppResult.Done)
        coVerify { userLocalDataSource.getCurrentUser() }
        coVerify { socketConnectionManager.sendDataToCurrentSocket(any()) }
    }

    @Test
    fun `sendMessage should return AppResult Error on failure`() = runTest {
        val user = userModel()
        val message = messageModel()
        val bytes = message.toByteArray
        coEvery { userLocalDataSource.getCurrentUser() } returns user
        val error = SocketConnectionError.DataTransferError
        coEvery { socketConnectionManager.sendDataToCurrentSocket(bytes) } coAnswers {
            AppResult.Error(
                error
            )
        }
        val result = repository.sendMessage(message.text)
        assert(result is AppResult.Error)
        coVerify { userLocalDataSource.getCurrentUser() }
        coVerify { socketConnectionManager.sendDataToCurrentSocket(any()) }
    }
}