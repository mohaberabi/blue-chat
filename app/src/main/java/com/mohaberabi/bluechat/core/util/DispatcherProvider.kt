package com.mohaberabi.bluechat.core.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Defines a provider for different types of coroutine dispatchers.
 * to be used and injected specially for the testing env where we need to work with virtual clocks
 * and where the real dispatchers are not provided
 */
interface DispatcherProvider {
    /** Dispatcher optimized for IO Operations , network or db operations which uses thread pool  */
    val io: CoroutineDispatcher

    /** Dispatcher optimized for main-thread operations, or the ui thread as well*/
    val main: CoroutineDispatcher

    /** Dispatcher optimized for CPU-intensive tasks like sorting huge   lists  of parsing huge jsons data structures  or calculations. like [io] uses thread pool*/
    val default: CoroutineDispatcher

    /** Not Confined for a predefined thread */
    val unconfined: CoroutineDispatcher
}

/**
 * Default implementation of [DispatcherProvider], using standard coroutine dispatchers.
 * provided by kotlinx
 */
class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
    override val default: CoroutineDispatcher
        get() = Dispatchers.Default
    override val main: CoroutineDispatcher
        get() = Dispatchers.Main
    override val unconfined: CoroutineDispatcher
        get() = Dispatchers.Unconfined
}