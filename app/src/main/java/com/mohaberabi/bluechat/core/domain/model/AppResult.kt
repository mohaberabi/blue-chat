package com.mohaberabi.bluechat.core.domain.model

import com.mohaberabi.bluechat.core.domain.model.errros.AppError


typealias EmptyResult = AppResult<Unit, AppError>

sealed interface AppResult<out T, out E : AppError> {
    data class Done<T>(val data: T) : AppResult<T, Nothing>
    data class Error<E : AppError>(
        val error: E,
        val cause: Throwable? = null
    ) : AppResult<Nothing, E>
}


inline fun <T, E : AppError> AppResult<T, E>.onDone(
    block: (T) -> Unit,
): AppResult<T, E> {
    if (this is AppResult.Done) {
        block(this.data)
    }
    return this
}

inline fun <T, E : AppError> AppResult<T, E>.onFailed(
    block: (E) -> Unit,
): AppResult<T, E> {
    if (this is AppResult.Error) {
        block(this.error)
    }
    return this
}
