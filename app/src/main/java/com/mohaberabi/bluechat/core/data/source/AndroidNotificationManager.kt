package com.mohaberabi.bluechat.core.data.source

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.mohaberabi.bluechat.core.domain.model.AppNotificationChannels
import com.mohaberabi.bluechat.core.domain.source.AppNotificationManager
import com.mohaberabi.bluechat.core.util.hasNotificationPermission
import javax.inject.Inject


/**
 *  AndroidNotificationManager implementation [AppNotificationManager]
 */
class AndroidNotificationManager @Inject constructor(
    private val context: Context,
    private val primaryNotificationBuilder: NotificationCompat.Builder
) : AppNotificationManager {

    /**
     * gets the notification  manager or throws [IllegalArgumentException] if was null
     */
    private val notificationManager by lazy {
        requireNotNull(context.getSystemService<NotificationManager>())
    }


    /**
     * shows a notification with the help of [notificationManager]
     * and the use of the [primaryNotificationBuilder] has a default logic shared across the whole app
     *
     */
    override fun notify(
        id: Int,
        title: String,
        body: String,
        channel: AppNotificationChannels,
    ) {

        val noti = primaryNotificationBuilder.setContentTitle(title)
            .setContentText(body)
            .setChannelId(channel.id)
            .build()
        if (context.hasNotificationPermission()) {
            notificationManager.notify(id, noti)
        }
    }
}