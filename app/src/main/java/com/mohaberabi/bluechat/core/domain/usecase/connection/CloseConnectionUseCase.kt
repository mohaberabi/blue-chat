package com.mohaberabi.bluechat.core.domain.usecase.connection

import com.mohaberabi.bluechat.core.domain.repository.SocketConnectionRepository
import javax.inject.Inject

class CloseConnectionUseCase(
    private val repository: SocketConnectionRepository
) {
    operator fun invoke() {
        repository.closeConnection()
    }
}