package com.mohaberabi.bluechat.features.paired_devices.presentation.viewmodel

import app.cash.turbine.test
import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.UiText
import com.mohaberabi.bluechat.core.domain.model.asUiText
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.core.domain.usecase.connection.SocketConnectionUseCases
import com.mohaberabi.bluechat.features.paired_devices.domain.usecase.GetPairedDevicesUseCase
import com.mohaberabi.bluechat.generator.bluetoothDevice
import com.mohaberabi.bluechat.util.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PairedDevicesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: PairedDevicesViewModel

    private val getPairedDevicesUseCase: GetPairedDevicesUseCase = mockk()
    private val connectionUseCases: SocketConnectionUseCases = mockk()

    @Before
    fun setup() {
        viewModel = PairedDevicesViewModel(
            getPairedDevicesUseCase = getPairedDevicesUseCase,
            connectionUseCases = connectionUseCases
        )
    }

    @Test
    fun `onAction GetPairedDevices  update state with paired devices`() = runTest {
        val devices = setOf(bluetoothDevice())
        coEvery { getPairedDevicesUseCase() } coAnswers { AppResult.Done(devices) }
        viewModel.state.test {
            viewModel.onActin(PairedDeviceActions.GetPairedDevices)
            assertEquals(awaitItem().loading, false)
            val newState = awaitItem()
            assertEquals(newState.loading, true)
            assertEquals(newState.pairedDevices, devices)
            assertEquals(newState.error, UiText.Empty)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { getPairedDevicesUseCase() }
    }

    @Test
    fun `onAction GetPairedDevices  update state with error on failure`() = runTest {
        val errorr = BluetoothError.Disabled
        coEvery { getPairedDevicesUseCase() } coAnswers { AppResult.Error(errorr) }
        viewModel.state.test {
            viewModel.onActin(PairedDeviceActions.GetPairedDevices)
            assertEquals(awaitItem().loading, false)
            val newState = awaitItem()
            assertEquals(newState.loading, true)
            assertEquals(newState.pairedDevices, emptySet<AppBluetoothDevice>())
            assertEquals(newState.error, UiText.Empty)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { getPairedDevicesUseCase() }
    }

    @Test
    fun `onAction ConnectToDevice  emit Connected event on success`() = runTest {
        val device = bluetoothDevice()
        coEvery {
            connectionUseCases.connectToDevice(device)
        } returns AppResult.Done(
            Unit
        )
        viewModel.events.test {
            viewModel.onActin(PairedDeviceActions.ConnectToDevice(device))
            val event = awaitItem()
            assertTrue(event is PairedDeviceEvents.Connected)
            assertEquals((event as PairedDeviceEvents.Connected).connectedName, device.name)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { connectionUseCases.connectToDevice(device) }
    }

    @Test
    fun `onAction ConnectToDevice  emit Error event on failure`() = runTest {
        val device = bluetoothDevice()
        val error = BluetoothError.Disabled
        coEvery { connectionUseCases.connectToDevice(device) } coAnswers { AppResult.Error(error) }
        viewModel.events.test {
            viewModel.onActin(PairedDeviceActions.ConnectToDevice(device))
            val event = awaitItem()
            assertTrue(event is PairedDeviceEvents.Error)
            assertEquals((event as PairedDeviceEvents.Error).error, error.asUiText())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { connectionUseCases.connectToDevice(device) }
    }

    @Test
    fun `onAction CancelConnection  cancel job and reset state`() = runTest {
        coEvery { connectionUseCases.closeConnection() } just Runs
        viewModel.state.test {
            viewModel.onActin(PairedDeviceActions.CancelConnection)
            val updatedState = awaitItem()
            assertEquals(updatedState.connecting, false)
            coVerify { connectionUseCases.closeConnection() }
            cancelAndIgnoreRemainingEvents()
        }

    }


}