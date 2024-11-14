package com.mohaberabi.bluechat.features.chat.domain.usecase


import com.mohaberabi.bluechat.core.data.services.ChatService
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.onDone
import com.mohaberabi.bluechat.core.domain.source.SimpleAppIntentInvoker
import com.mohaberabi.bluechat.features.chat.domain.repository.ChatRepository
import com.mohaberabi.bluechat.generator.appIntent
import com.mohaberabi.bluechat.generator.messageModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SendMessageUseCaseTest {

    private val chatRepository: ChatRepository = mockk()
    private lateinit var useCase: SendMessageUseCase

    @Before
    fun setup() {
        useCase = SendMessageUseCase(chatRepository)
    }

    @Test
    fun `should call sendMessage on repository`() = runTest {
        val message = messageModel()
        coEvery { chatRepository.sendMessage(message.text) } coAnswers { AppResult.Done(message) }
        val res = useCase(message.text)
        coVerify { chatRepository.sendMessage(message.text) }
        res.onDone {
            assertEquals(message, it)
        }
    }
}