package com.mohaberabi.bluechat.core.domain.usecase.notification

import com.mohaberabi.bluechat.core.domain.model.AppNotificationChannels
import com.mohaberabi.bluechat.core.domain.source.AppNotificationManager
import javax.inject.Inject

class ShowNotificationUseCase @Inject constructor(
    private val notificationManager: AppNotificationManager,
) {
    operator fun invoke(
        id: Int,
        title: String,
        body: String,
        channel: AppNotificationChannels
    ) = notificationManager.notify(id, title, body, channel)
}