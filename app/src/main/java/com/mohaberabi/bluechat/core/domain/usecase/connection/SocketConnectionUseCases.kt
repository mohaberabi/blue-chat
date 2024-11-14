package com.mohaberabi.bluechat.core.domain.usecase.connection

data class SocketConnectionUseCases(
    val openSocket: OpenConnectionSocketUseCase,
    val connectToDevice: ConnectToDeviceUseCase,
    val closeConnection: CloseConnectionUseCase,
)
