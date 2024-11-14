package com.mohaberabi.bluechat.core.domain.usecase.connection

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.repository.SocketConnectionRepository
import javax.inject.Inject

class ConnectToDeviceUseCase(
    private val repository: SocketConnectionRepository
) {
    suspend operator fun invoke(device: AppBluetoothDevice) = repository.connectToDevice(device)
}