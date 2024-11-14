package com.mohaberabi.bluechat.features.chat.domain.usecase

import com.mohaberabi.bluechat.core.domain.model.MessageModel
import com.mohaberabi.bluechat.features.chat.domain.repository.ChatRepository
import com.mohaberabi.bluechat.generator.messageModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

class ListenToChatMessagesUseCaseTest {

    private val chatRepository: ChatRepository = mockk()
    private lateinit var useCase: ListenToChatMessagesUseCase

    @Before
    fun setup() {
        useCase = ListenToChatMessagesUseCase(chatRepository)
    }

    @Test
    fun `invoke should call listenToIncomingMessage on repository`() {
        val flow = flowOf(messageModel())
        every { chatRepository.listenToIncomingMessage() } returns flow
        val result = useCase()

        assertEquals(flow, result)
        verify { chatRepository.listenToIncomingMessage() }
    }
}