package com.mohaberabi.bluechat.core.domain.model

import com.mohaberabi.bluechat.core.domain.model.errros.AppError

sealed interface ConnectionSocketResult {
    data class Connected(val device: AppBluetoothDevice? = null) : ConnectionSocketResult
    data class Error(val error: AppError, val cause: Throwable? = null) :
        ConnectionSocketResult
}