package com.mohaberabi.bluechat.features.scan_devices.domain.repository

import app.cash.turbine.test
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.ScanDeviceResult
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.features.scan_devices.data.repository.DefaultScanDeviceRepository
import com.mohaberabi.bluechat.features.scan_devices.domain.ScanDeviceRepository
import com.mohaberabi.bluechat.features.scan_devices.domain.source.BluetoothScanner
import com.mohaberabi.bluechat.generator.bluetoothDevice
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DefaultScanDeviceRepositoryTest {

    private lateinit var scanner: BluetoothScanner
    private lateinit var repository: ScanDeviceRepository

    @Before
    fun setup() {
        scanner = mockk()
        repository = DefaultScanDeviceRepository(scanner)
    }

    @Test
    fun `getScannedDevices should return flow of ScanDeviceResult`() = runTest {

        val result = ScanDeviceResult.DeviceFound(
            bluetoothDevice()
        )
        coEvery { scanner.getFoundDevices() } returns flowOf(
            result
        )
        assertEquals(repository.getScannedDevices().first(), result)
    }

    @Test
    fun `scanForDevices should return Done on success`() = runTest {
        coEvery { scanner.requestToScanForDevices() } returns AppResult.Done(Unit)
        val result = repository.scanForDevices()
        assert(result is AppResult.Done)
    }

    @Test
    fun `scanForDevices should return Error on failure`() = runTest {
        coEvery { scanner.requestToScanForDevices() } returns AppResult.Error(
            BluetoothError.PermissionNotGranted
        )
        val result = repository.scanForDevices()
        assert(result is AppResult.Error)
    }

    @Test
    fun `stopScanningForDevices should return Done on scanner doen `() = runTest {
        coEvery { scanner.stopScanning() } returns AppResult.Done(Unit)
        val result = repository.stopScanningForDevices()
        assert(result is AppResult.Done)
    }

    @Test
    fun `stopScanningForDevices should return Error on scanner failure `() = runTest {
        coEvery { scanner.stopScanning() } returns AppResult.Error(
            BluetoothError.Unknown
        )
        val result = repository.stopScanningForDevices()
        assert(result is AppResult.Error)
    }
}