package com.mohaberabi.bluechat.core.domain.usecase.connection

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.core.domain.repository.SocketConnectionRepository
import com.mohaberabi.bluechat.core.domain.source.ConnectionStateTracker
import com.mohaberabi.bluechat.generator.bluetoothDevice
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ConnectTodeviceUseCaseTest {

    private lateinit var repository: SocketConnectionRepository
    private lateinit var connectToDeviceUseCase: ConnectToDeviceUseCase

    @Before
    fun setup() {
        repository = mockk()
        connectToDeviceUseCase = ConnectToDeviceUseCase(repository)

    }

    @Test
    fun `returns correctly on done `() = runBlocking {
        val device = bluetoothDevice()
        coEvery { repository.connectToDevice(device) } coAnswers { AppResult.Done(Unit) }
        connectToDeviceUseCase(device)
        coVerify { repository.connectToDevice(device) }
    }

    @Test
    fun `returns correctly on error  `() = runBlocking {
        val device = bluetoothDevice()
        coEvery { repository.connectToDevice(device) } coAnswers { AppResult.Error(BluetoothError.Unknown) }
        connectToDeviceUseCase(device)
        coVerify { repository.connectToDevice(device) }
    }
}