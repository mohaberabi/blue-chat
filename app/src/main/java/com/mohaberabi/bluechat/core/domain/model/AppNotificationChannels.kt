package com.mohaberabi.bluechat.core.domain.model

import android.app.NotificationManager


enum class AppNotificationChannels(
    val label: String,
    val id: String,
    val importance: Int,
) {
    ScanDevices(
        "scanner",
        "scanner",
        NotificationManager.IMPORTANCE_LOW
    ),
    Chat(
        "chat",
        "chat",
        NotificationManager.IMPORTANCE_DEFAULT
    ),
}