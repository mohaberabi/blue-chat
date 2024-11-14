package com.mohaberabi.bluechat.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

interface AppSupervisorScope {
    operator fun invoke(): CoroutineScope
}


class DefaultAppSupervisorScope @Inject constructor(
    private val dispatchers: DispatcherProvider,
) : AppSupervisorScope {
    override fun invoke(
    ): CoroutineScope = CoroutineScope(
        SupervisorJob() + dispatchers.io,
    )
}