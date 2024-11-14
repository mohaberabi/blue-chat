package com.mohaberabi.bluechat.features.scan_devices.presentation.viewmodel

import app.cash.turbine.test
import com.mohaberabi.bluechat.core.data.services.BluetoothScanService
import com.mohaberabi.bluechat.core.domain.model.*
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.core.domain.usecase.connection.SocketConnectionUseCases
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.ScannerUseCase
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.SendForegroundScanIntentUseCase
import com.mohaberabi.bluechat.features.scan_devices.presentation.viewmodel.ScanDeviceAction
import com.mohaberabi.bluechat.features.scan_devices.presentation.viewmodel.ScanDeviceEvent
import com.mohaberabi.bluechat.features.scan_devices.presentation.viewmodel.ScanDeviceState
import com.mohaberabi.bluechat.features.scan_devices.presentation.viewmodel.ScanDeviceViewModel
import com.mohaberabi.bluechat.generator.bluetoothDevice
import com.mohaberabi.bluechat.util.MainDispatcherRule
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class ScanDeviceViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val scannerUseCase: ScannerUseCase = mockk()
    private val connectionUseCases: SocketConnectionUseCases = mockk()
    private val sendForegroundScanIntent: SendForegroundScanIntentUseCase = mockk(relaxed = true)
    private lateinit var viewModel: ScanDeviceViewModel


    @Before
    fun setup() {
        viewModel =
            ScanDeviceViewModel(
                scannerUseCase,
                connectionUseCases,
                sendForegroundScanIntent
            )
    }


    @Test

    fun `toggleScanning should start scanning when not scanning updates scanned set with one device as start getting scanned is ready to emit  `() =
        runTest {
            every { sendForegroundScanIntent(BluetoothScanService.ACTION_START) } returns Unit
            every { sendForegroundScanIntent(BluetoothScanService.ACTION_STOP) } returns Unit
            every { scannerUseCase.stopScanning() } returns AppResult.Done(Unit)

            every { scannerUseCase.requestToScanDevices() } returns AppResult.Done(Unit)
            every { scannerUseCase.getScannedDevices() } returns flowOf(
                ScanDeviceResult.DeviceFound(
                    bluetoothDevice()
                )
            )
            viewModel.onAction(ScanDeviceAction.ToggleScanning)
            advanceUntilIdle()
            assertEquals(true, viewModel.state.value.isScanning)
            assertEquals(1, viewModel.state.value.scannedDevices.size)
            verify { sendForegroundScanIntent(BluetoothScanService.ACTION_START) }
        }

    @Test
    fun `toggleScanning should stop scanning when already scanning`() = runTest {
        every { scannerUseCase.requestToScanDevices() } returns AppResult.Done(Unit)
        every { scannerUseCase.getScannedDevices() } returns flowOf(
            ScanDeviceResult.DeviceFound(
                bluetoothDevice()
            )
        )
        every { sendForegroundScanIntent(BluetoothScanService.ACTION_START) } returns Unit
        every { sendForegroundScanIntent(BluetoothScanService.ACTION_STOP) } returns Unit
        every { scannerUseCase.stopScanning() } returns AppResult.Done(Unit)

        viewModel.onAction(ScanDeviceAction.ToggleScanning)
        assertEquals(false, viewModel.state.value.isScanning)
        advanceUntilIdle()
        assertEquals(true, viewModel.state.value.isScanning)
        viewModel.onAction(ScanDeviceAction.ToggleScanning)
        advanceUntilIdle()
        assertEquals(false, viewModel.state.value.isScanning)

        verify { sendForegroundScanIntent(BluetoothScanService.ACTION_START) }
        verify { sendForegroundScanIntent(BluetoothScanService.ACTION_STOP) }
    }

    @Test

    fun `openConnectionSocket sends connected when done `() = runTest {
        every { connectionUseCases.openSocket() } returns flowOf(
            ConnectionSocketResult.Connected(
                bluetoothDevice()
            )
        )
        viewModel.onAction(ScanDeviceAction.OpenConnectionSocket)
        assertEquals(false, viewModel.state.value.isConnecting)
        advanceUntilIdle()
        assertEquals(true, viewModel.state.value.isConnecting)
        assert(viewModel.events.first() is ScanDeviceEvent.Connected)
        coVerify { connectionUseCases.openSocket() }
    }

    @Test

    fun `openConnectionSocket sends error when flow emites erorr  `() = runTest {
        every { connectionUseCases.openSocket() } returns flowOf(
            ConnectionSocketResult.Error(
                BluetoothError.PermissionNotGranted
            )
        )
        viewModel.onAction(ScanDeviceAction.OpenConnectionSocket)
        assertEquals(false, viewModel.state.value.isConnecting)
        advanceUntilIdle()
        assertEquals(true, viewModel.state.value.isConnecting)
        assert(viewModel.events.first() is ScanDeviceEvent.Error)
        coVerify { connectionUseCases.openSocket() }
    }

    @Test

    fun `connectToDevice should send connected when done `() =
        runTest() {
            val device = bluetoothDevice()
            coEvery { connectionUseCases.connectToDevice(device) } coAnswers { AppResult.Done(Unit) }
            viewModel.onAction(ScanDeviceAction.ConnectToDevice(device))
            advanceUntilIdle()
            assert(viewModel.events.first() is ScanDeviceEvent.Connected)
        }

    @Test
    fun `connectToDevice should sends  erorr when failed`() = runTest(UnconfinedTestDispatcher()) {
        val device = bluetoothDevice()
        val error = BluetoothError.Disabled
        coEvery { connectionUseCases.connectToDevice(device) } coAnswers { AppResult.Error(error) }
        viewModel.onAction(ScanDeviceAction.ConnectToDevice(device))
        assertEquals(false, viewModel.state.value.isConnecting)
        assert(viewModel.events.first() is ScanDeviceEvent.Error)
    }
}