package com.mohaberabi.bluechat.core.util.extensions


inline fun <reified T : Throwable, R> Result<R>.but(): Result<R> =
    onFailure { if (it is T) throw it }
