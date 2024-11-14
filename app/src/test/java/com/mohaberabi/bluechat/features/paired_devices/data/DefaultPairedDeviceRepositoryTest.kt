package com.mohaberabi.bluechat.features.paired_devices.data

import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.features.paired_devices.data.repository.DefaultPairedDeviceRepository
import com.mohaberabi.bluechat.features.paired_devices.domain.source.PairedDeviceSource
import com.mohaberabi.bluechat.generator.bluetoothDevice
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultPairedDeviceRepositoryTest {


    private val pairedDeviceSource: PairedDeviceSource = mockk()
    private lateinit var repository: DefaultPairedDeviceRepository

    @Before
    fun setup() {
        repository = DefaultPairedDeviceRepository(pairedDeviceSource)
    }

    @Test
    fun `getPairedDevices should return paired devices when source is successful`() = runTest {
        val pairedDevices = setOf(
            bluetoothDevice()
        )
        coEvery { pairedDeviceSource.getAllPairedDevices() } returns AppResult.Done(pairedDevices)

        val result = repository.getPairedDevices()

        assertTrue(result is AppResult.Done)
        assertEquals(pairedDevices, (result as AppResult.Done).data)
        coVerify { pairedDeviceSource.getAllPairedDevices() }
    }

    @Test
    fun `getPairedDevices should return error when source fails`() = runTest {
        val error = BluetoothError.Unknown
        coEvery { pairedDeviceSource.getAllPairedDevices() } returns AppResult.Error(error)
        val result = repository.getPairedDevices()
        assertTrue(result is AppResult.Error)
        assertEquals(error, (result as AppResult.Error).error)
        coVerify { pairedDeviceSource.getAllPairedDevices() }
    }
}