package com.mohaberabi.bluechat.features.paired_devices.domain.usecase

import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.features.paired_devices.domain.repository.PairedDeviceRepository
import com.mohaberabi.bluechat.features.paired_devices.domain.usecase.GetPairedDevicesUseCase
import com.mohaberabi.bluechat.generator.bluetoothDevice
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetPairedDevicesUseCaseTest {

    private val repository: PairedDeviceRepository = mockk()
    private lateinit var useCase: GetPairedDevicesUseCase

    @Before
    fun setup() {
        useCase = GetPairedDevicesUseCase(repository)
    }

    @Test
    fun `invoke should return paired devices when successful`() = runTest {
        val pairedDevices = setOf(
            bluetoothDevice()
        )
        coEvery { repository.getPairedDevices() } returns AppResult.Done(pairedDevices)

        val result = useCase()

        assertTrue(result is AppResult.Done)
        assertEquals(pairedDevices, (result as AppResult.Done).data)
        coVerify { repository.getPairedDevices() }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val error = BluetoothError.Unknown
        coEvery { repository.getPairedDevices() } returns AppResult.Error(error)
        val result = useCase()
        assertTrue(result is AppResult.Error)
        assertEquals(error, (result as AppResult.Error).error)
        coVerify { repository.getPairedDevices() }
    }

}