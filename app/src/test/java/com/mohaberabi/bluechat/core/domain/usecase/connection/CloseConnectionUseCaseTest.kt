package com.mohaberabi.bluechat.core.domain.usecase.connection

import com.mohaberabi.bluechat.core.domain.repository.SocketConnectionRepository
import com.mohaberabi.bluechat.core.domain.source.ConnectionStateTracker
import io.mockk.*
import org.junit.Before
import org.junit.Test

class CloseConnectionUseCaseTest {

    private lateinit var repository: SocketConnectionRepository

    private lateinit var closeConnectionUseCase: CloseConnectionUseCase

    @Before
    fun setup() {
        repository = mockk()

        closeConnectionUseCase = CloseConnectionUseCase(repository)
    }

    @Test
    fun `invokes correctly `() {
        every { repository.closeConnection() } just Runs
        closeConnectionUseCase()

        verify { repository.closeConnection() }
    }


}