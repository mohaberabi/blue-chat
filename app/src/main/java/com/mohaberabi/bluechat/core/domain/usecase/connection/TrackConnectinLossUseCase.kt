package com.mohaberabi.bluechat.core.domain.usecase.connection

import com.mohaberabi.bluechat.core.domain.source.ConnectionStateTracker
import javax.inject.Inject

class CheckConnectionLossUseCase @Inject constructor(
    private val connectionStateTracker: ConnectionStateTracker
) {
    operator fun invoke() = connectionStateTracker.checkConnectionLost()
}