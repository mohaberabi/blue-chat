package com.mohaberabi.bluechat.features.scan_devices.presentation.usecase

import com.mohaberabi.bluechat.core.domain.model.ScanDeviceResult
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.features.scan_devices.domain.ScanDeviceRepository
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.GetScannedDevicesUseCase
import com.mohaberabi.bluechat.generator.bluetoothDevice
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetScanneDeviceUseCaseTest {


    private lateinit var repository: ScanDeviceRepository
    private lateinit var getScanned: GetScannedDevicesUseCase


    @Before
    fun setup() {
        repository = mockk()
        getScanned = GetScannedDevicesUseCase(repository)
    }

    @Test
    fun ` should return a new device once repo founds nearby device `() = runTest {
        val actual = flowOf(
            ScanDeviceResult.DeviceFound(
                bluetoothDevice()
            )
        )
        every { repository.getScannedDevices() } returns actual

        val result = getScanned()
        assertEquals(result, actual)
        verify { repository.getScannedDevices() }
    }

    @Test
    fun ` should return a connection lost when connection repo emits ConnectionLost`() = runTest {
        val actual = flowOf(
            ScanDeviceResult.ConnectionLost(
                BluetoothError.Disabled
            )
        )
        every { repository.getScannedDevices() } returns actual

        val result = getScanned()
        assertEquals(result, actual)
        verify { repository.getScannedDevices() }

    }
}