package com.mohaberabi.bluechat.core.domain.usecase.notification

import com.mohaberabi.bluechat.core.domain.model.AppNotificationChannels
import com.mohaberabi.bluechat.core.domain.source.AppNotificationManager
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ShowNotificationUseCaseTest {

    private lateinit var notificationManager: AppNotificationManager
    private lateinit var showNotificationUseCase: ShowNotificationUseCase

    @Before
    fun setup() {
        notificationManager = mockk(relaxed = true)
        showNotificationUseCase = ShowNotificationUseCase(notificationManager)
    }

    @Test
    fun ` should call notificationManager correctly `() {
        val id = 1
        val title = "Hey Loser"
        val body = "Hey Loser"
        val channel = AppNotificationChannels.Chat
        showNotificationUseCase(id, title, body, channel)
        verify { notificationManager.notify(id, title, body, channel) }
    }
}