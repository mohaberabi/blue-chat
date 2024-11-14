package com.mohaberabi.bluechat.core.domain.usecase.connection

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.ConnectionSocketResult
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

class OpenConnectionSocketUseCaseTest {
    private lateinit var repository: SocketConnectionRepository
    private lateinit var openConnectionSocketUseCase: OpenConnectionSocketUseCase

    @Before
    fun setup() {
        repository = mockk()
        openConnectionSocketUseCase = OpenConnectionSocketUseCase(repository)
    }


    @Test
    fun `should return flow from repository when done `() {
        val flow = flowOf(ConnectionSocketResult.Connected(bluetoothDevice()))
        every { repository.openConnectionSocket() } returns flow
        val result = openConnectionSocketUseCase()
        assertEquals(flow, result)
        verify { repository.openConnectionSocket() }
    }

    @Test
    fun `should return flow from repository when error `() {
        val flow = flowOf(ConnectionSocketResult.Error(BluetoothError.Unknown))
        every { repository.openConnectionSocket() } returns flow
        val result = openConnectionSocketUseCase()
        assertEquals(flow, result)
        verify { repository.openConnectionSocket() }
    }
}