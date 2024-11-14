package com.mohaberabi.bluechat.core.domain.source

import com.mohaberabi.bluechat.core.domain.model.AppNotificationChannels


interface AppNotificationManager {
    fun notify(
        id: Int,
        title: String,
        body: String,
        channel: AppNotificationChannels,
    )
}