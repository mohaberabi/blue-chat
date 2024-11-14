package com.mohaberabi.bluechat.core.domain.source

import kotlinx.coroutines.flow.Flow


typealias ConnectionLost = Boolean

interface ConnectionStateTracker {
    fun checkConnectionLost(): Flow<ConnectionLost>
}