package com.mohaberabi.bluechat

import android.app.Application
import android.app.NotificationManager
import androidx.core.content.getSystemService
import com.mohaberabi.bluechat.core.data.mapper.toAndroidChannel
import com.mohaberabi.bluechat.core.domain.model.AppNotificationChannels
import com.mohaberabi.bluechat.core.util.requiresNotificationsPermissions
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BlueChatApp : Application() {


    private val notificationManager by lazy {
        requireNotNull(getSystemService<NotificationManager>())
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (requiresNotificationsPermissions()) {
            val channels = AppNotificationChannels.entries.map { it.toAndroidChannel() }
            notificationManager.createNotificationChannels(channels)
        }
    }
}