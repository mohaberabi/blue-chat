package com.mohaberabi.bluechat.core.util.extensions

import com.mohaberabi.bluechat.core.domain.model.errros.AppError
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.core.domain.model.errros.SocketConnectionError
import java.io.IOException

/**
 * Converts a [Throwable] into an [AppError] based on its type.
 */
val Throwable.toAppError: AppError
    get() = when (this) {
        is SecurityException -> BluetoothError.PermissionNotGranted
        is IllegalArgumentException -> BluetoothError.AlreadyUnregisteredResources
        is IOException -> SocketConnectionError.DataTransferError
        else -> BluetoothError.Unknown
    }

