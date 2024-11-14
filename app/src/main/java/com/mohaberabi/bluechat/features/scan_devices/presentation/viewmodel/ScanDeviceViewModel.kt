package com.mohaberabi.bluechat.features.scan_devices.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohaberabi.bluechat.core.data.services.BluetoothScanService
import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.ConnectionSocketResult
import com.mohaberabi.bluechat.core.domain.model.ScanDeviceResult
import com.mohaberabi.bluechat.core.domain.model.UiText
import com.mohaberabi.bluechat.core.domain.model.asUiText
import com.mohaberabi.bluechat.core.domain.model.onDone
import com.mohaberabi.bluechat.core.domain.model.onFailed
import com.mohaberabi.bluechat.core.domain.usecase.connection.SocketConnectionUseCases
import com.mohaberabi.bluechat.core.util.extensions.toAppError
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.ScannerUseCase
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.SendForegroundScanIntentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling device scanning and connection logic.
 *
 * This class manages scanning for Bluetooth devices, connection with found device or open
 * the device to be waiting for connection
 *
 */

@HiltViewModel
class ScanDeviceViewModel @Inject constructor(
    private val scannerUseCase: ScannerUseCase,
    private val connectionUseCases: SocketConnectionUseCases,
    private val sendForegroundScanIntent: SendForegroundScanIntentUseCase
) : ViewModel() {
    /**
     * one coroutine  job for either of the operations
     */

    private var activeJob: Job? = null
    private val _state = MutableStateFlow(ScanDeviceState())
    val state = _state.asStateFlow()
    private val _events = Channel<ScanDeviceEvent>()
    val events = _events.receiveAsFlow()


    fun onAction(action: ScanDeviceAction) {
        when (action) {
            ScanDeviceAction.ToggleScanning -> toggleScanning()
            ScanDeviceAction.CancelOperations -> cancelAllOperations()
            ScanDeviceAction.OpenConnectionSocket -> openConnectionSocket()
            is ScanDeviceAction.ConnectToDevice -> connectToDevice(action.device)
        }
    }

    /**
     * Start or stop scanning based on the current state.
     */
    private fun toggleScanning() {
        if (state.value.isScanning) {
            cancelAllOperations()
        } else {
            scannerUseCase.requestToScanDevices()
                .onDone { startGettingScannedDevices() }
                .onFailed { error ->
                    viewModelScope.launch {
                        sendErrorEvent(error.asUiText())
                    }
                }
        }

    }

    /**
     * Start retrieving scanned devices and process the results.
     */
    private fun startGettingScannedDevices() {
        cancelCurrentJob()
        activeJob = scannerUseCase.getScannedDevices()
            .onStart {
                _state.update { it.copy(isConnecting = false, isScanning = true) }
                sendForegroundScanIntent(BluetoothScanService.ACTION_START)
            }.onEach { scanResult ->
                handleScanResult(scanResult)
            }.catch {
                sendErrorEvent(it.toAppError.asUiText())
            }.onCompletion {
                scannerUseCase.stopScanning()
                sendForegroundScanIntent(BluetoothScanService.ACTION_STOP)
            }.launchIn(viewModelScope)
    }

    /**
     * Handle individual scan results, like finding a device or losing connection.
     */
    private suspend fun handleScanResult(result: ScanDeviceResult) {
        when (result) {
            is ScanDeviceResult.ConnectionLost -> {
                sendErrorEvent(result.reason.asUiText())
            }

            is ScanDeviceResult.DeviceFound -> {
                _state.update { it.copy(scannedDevices = it.scannedDevices + result.device) }
            }

            ScanDeviceResult.Idle -> Unit
        }
    }

    /**
     * Open a socket connection and handle connection events.
     */
    private fun openConnectionSocket() {
        cancelCurrentJob()
        activeJob = connectionUseCases.openSocket()
            .onStart {
                _state.update { it.copy(isConnecting = true, isScanning = false) }
            }.onEach { result ->
                when (result) {
                    is ConnectionSocketResult.Connected -> sendConnectionDoneEvent(
                        device = result.device?.name ?: ""
                    )

                    is ConnectionSocketResult.Error -> sendErrorEvent(result.error.asUiText())
                }
            }.catch { err ->
                sendErrorEvent(err.toAppError.asUiText())
            }.launchIn(viewModelScope)
    }

    /**
     * Cancel all ongoing operations and reset state.
     */
    private fun cancelAllOperations() {
        cancelCurrentJob()
        setStateIdle()
    }

    /**
     * Connect to a specific Bluetooth device.
     */
    private fun connectToDevice(device: AppBluetoothDevice) {
        cancelCurrentJob()
        activeJob = viewModelScope.launch {
            _state.update { it.copy(isConnecting = true, isScanning = false) }
            connectionUseCases.connectToDevice(device)
                .onDone { sendConnectionDoneEvent(device.name) }
                .onFailed { error ->
                    sendErrorEvent(error.asUiText())
                }
        }
    }

    /**
     * Cancel the current active job.
     */
    private fun cancelCurrentJob() {
        activeJob?.cancel()
        activeJob = null
    }

    /**
     * Send an event for a done  connection.
     */
    private suspend fun sendConnectionDoneEvent(device: String) {
        _events.send(ScanDeviceEvent.Connected(device))
        setStateIdle()
    }

    /**
     * Send an error event with a specific message.
     */
    private suspend fun sendErrorEvent(error: UiText) {
        _events.send(ScanDeviceEvent.Error(error))
        setStateIdle()
    }

    /**
     * Reset state to idle.
     */
    private fun setStateIdle() {
        _state.update { it.copy(isConnecting = false, isScanning = false) }

    }

}

