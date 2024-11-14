package com.mohaberabi.bluechat.core.domain.usecase.connection

import com.mohaberabi.bluechat.core.domain.model.ConnectionSocketResult
import com.mohaberabi.bluechat.core.domain.repository.SocketConnectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OpenConnectionSocketUseCase(
    private val repository: SocketConnectionRepository
) {
    operator fun invoke() = repository.openConnectionSocket()
}