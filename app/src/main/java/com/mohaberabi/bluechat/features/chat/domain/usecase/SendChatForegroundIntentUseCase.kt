package com.mohaberabi.bluechat.features.chat.domain.usecase

import com.mohaberabi.bluechat.core.data.services.ChatService
import com.mohaberabi.bluechat.core.domain.model.AppIntent
import com.mohaberabi.bluechat.core.domain.source.SimpleAppIntentInvoker
import javax.inject.Inject

class SendChatForegroundIntentUseCase @Inject constructor(
    private val invoker: SimpleAppIntentInvoker,
) {
    operator fun invoke(
        action: String,
    ) {
        val appIntent = AppIntent(ChatService::class, action)
        invoker.invoke(appIntent)
    }
}