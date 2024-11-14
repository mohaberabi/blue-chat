package com.mohaberabi.bluechat.core.domain.usecase.connection

import com.mohaberabi.bluechat.core.domain.repository.SocketConnectionRepository
import com.mohaberabi.bluechat.core.domain.source.ConnectionStateTracker
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CheckConnectionLostUseCaseTest {

    private lateinit var repository: SocketConnectionRepository
    private lateinit var connectionStateTracker: ConnectionStateTracker

    private lateinit var checkConnectionLossUseCase: CheckConnectionLossUseCase

    @Before
    fun setup() {
        repository = mockk()
        connectionStateTracker = mockk()
        checkConnectionLossUseCase = CheckConnectionLossUseCase(connectionStateTracker)
    }


    @Test
    fun `retuns flow from connectionStateTracker when true`() {
        val flow = flowOf(true)
        every { connectionStateTracker.checkConnectionLost() } returns flow
        val result = checkConnectionLossUseCase()
        assertEquals(flow, result)
        verify { connectionStateTracker.checkConnectionLost() }
    }

    @Test
    fun `retuns flow from connectionStateTracker when false `() {
        val flow = flowOf(false)
        every { connectionStateTracker.checkConnectionLost() } returns flow
        val result = checkConnectionLossUseCase()
        assertEquals(flow, result)
        verify { connectionStateTracker.checkConnectionLost() }
    }
}