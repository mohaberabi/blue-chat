package com.mohaberabi.bluechat.features.scan_devices.presentation.usecase

import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.features.scan_devices.domain.ScanDeviceRepository
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.ScanForDevicesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RequestToScanDevicesUseCaseTest {

    private lateinit var repository: ScanDeviceRepository
    private lateinit var useCase: ScanForDevicesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ScanForDevicesUseCase(repository)
    }

    @Test
    fun ` should return Done when scan starts successfully`() = runTest {
        coEvery { repository.scanForDevices() } returns AppResult.Done(Unit)
        val result = useCase()
        assertEquals(AppResult.Done(Unit), result)
    }

    @Test
    fun ` should return Error when repository fails`() = runTest {
        coEvery { repository.scanForDevices() } returns AppResult.Error(BluetoothError.PermissionNotGranted)
        val result = useCase()
        assertEquals(AppResult.Error(BluetoothError.PermissionNotGranted), result)
    }
}