package com.mohaberabi.bluechat.features.scan_devices.presentation.usecase

import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.features.scan_devices.domain.ScanDeviceRepository
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.StopScanningForDevicesUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class StopScanningForDevieUseCaseTest {


    private lateinit var repository: ScanDeviceRepository
    private lateinit var stopScan: StopScanningForDevicesUseCase


    @Before
    fun setup() {
        repository = mockk()
        stopScan = StopScanningForDevicesUseCase(repository)
    }

    @Test
    fun `should stop scanning and return done when reppository returns done `() = runTest {
        val actual = AppResult.Done(Unit)
        every { repository.stopScanningForDevices() } returns actual

        val result = stopScan()
        assertEquals(result, actual)
        verify { repository.stopScanningForDevices() }
    }

    @Test
    fun ` should return app erorr when repository could not stop scanning `() = runTest {
        val actual = AppResult.Error(BluetoothError.Disabled)
        every { repository.stopScanningForDevices() } returns actual

        val result = stopScan()
        assertEquals(result, actual)
        verify { repository.stopScanningForDevices() }
    }
}