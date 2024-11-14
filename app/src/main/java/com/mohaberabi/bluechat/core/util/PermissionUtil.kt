package com.mohaberabi.bluechat.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat


fun requiresBluetoothPermissions() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
fun requiresNotificationsPermissions() = Build.VERSION.SDK_INT >= 33

fun Context.hasPermission(permission: String) =
    ContextCompat.checkSelfPermission(
        this, permission
    ) == PackageManager.PERMISSION_GRANTED


fun Context.hasNotificationPermission(): Boolean =
    if (requiresNotificationsPermissions())
        hasPermission(Manifest.permission.POST_NOTIFICATIONS)
    else true

fun Context.hasBluetoothPermissions(): Boolean {
    return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
        hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        true
    }
}