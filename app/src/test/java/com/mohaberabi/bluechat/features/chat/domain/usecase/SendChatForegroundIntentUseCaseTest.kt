package com.mohaberabi.bluechat.features.chat.domain.usecase

import com.mohaberabi.bluechat.core.data.services.ChatService
import com.mohaberabi.bluechat.core.domain.source.SimpleAppIntentInvoker
import com.mohaberabi.bluechat.generator.appIntent
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SendChatForegroundIntentUseCaseTest {

    private val intentInvoker: SimpleAppIntentInvoker = mockk()
    private lateinit var useCase: SendChatForegroundIntentUseCase

    @Before
    fun setup() {
        useCase = SendChatForegroundIntentUseCase(intentInvoker)
    }

    @Test
    fun `invoke should call invoker with correct AppIntent`() {
        val appIntent = appIntent(
            kclass = ChatService::class,
            action = ChatService.ACTION_START
        )
        val action = appIntent.intentAction

        every { intentInvoker.invoke(appIntent) } returns Unit
        useCase(action)
        verify { intentInvoker.invoke(appIntent) }
    }
}