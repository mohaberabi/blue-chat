package com.mohaberabi.bluechat.core.data.repository

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.ConnectionSocketResult
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.core.domain.source.SocketConnectionManager
import com.mohaberabi.bluechat.generator.bluetoothDevice
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultSocketConnectionRepositoryTest {

    private lateinit var socketManager: SocketConnectionManager
    private lateinit var repository: DefaultSocketConnectionRepository

    @Before
    fun setup() {
        socketManager = mockk(relaxed = true)
        repository = DefaultSocketConnectionRepository(socketManager)
    }

    @Test
    fun `returns  flow from manager correctly `() {
        val device = bluetoothDevice()
        val flow = flowOf(ConnectionSocketResult.Connected(device))
        every { socketManager.openConnectionSocket() } returns flow
        val result = repository.openConnectionSocket()
        assert(result == flow)
        coVerify { socketManager.openConnectionSocket() }
    }

    @Test
    fun `connects to device correctly `() = runTest {
        val device = bluetoothDevice()
        coEvery { repository.connectToDevice(device) } coAnswers { AppResult.Done(Unit) }
        val res = repository.connectToDevice(device)
        assert(res is AppResult.Done)
        coVerify { socketManager.connectToDevice(device) }

    }

    @Test
    fun `connects to device correctly rerurns error when manager failes `() = runTest {
        val device = bluetoothDevice()
        coEvery { repository.connectToDevice(device) } coAnswers { AppResult.Error(BluetoothError.Unknown) }
        val res = repository.connectToDevice(device)
        assert(res is AppResult.Error)

        coVerify { socketManager.connectToDevice(device) }
    }

    @Test
    fun `calls call connection normally `() {
        repository.closeConnection()
        coVerify { socketManager.closeConnection() }
    }
}