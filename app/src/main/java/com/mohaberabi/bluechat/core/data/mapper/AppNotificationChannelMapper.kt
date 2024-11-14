package com.mohaberabi.bluechat.core.data.mapper

import android.app.NotificationChannel
import android.os.Build
import androidx.annotation.RequiresApi
import com.mohaberabi.bluechat.core.domain.model.AppNotificationChannels


@RequiresApi(Build.VERSION_CODES.O)
fun AppNotificationChannels.toAndroidChannel(
) = NotificationChannel(id, label, importance)