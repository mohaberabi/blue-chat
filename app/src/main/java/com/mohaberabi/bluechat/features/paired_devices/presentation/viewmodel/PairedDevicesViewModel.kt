package com.mohaberabi.bluechat.features.paired_devices.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.UiText
import com.mohaberabi.bluechat.core.domain.model.asUiText
import com.mohaberabi.bluechat.core.domain.model.onDone
import com.mohaberabi.bluechat.core.domain.model.onFailed
import com.mohaberabi.bluechat.core.domain.usecase.connection.SocketConnectionUseCases
import com.mohaberabi.bluechat.features.paired_devices.domain.usecase.GetPairedDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the stat related to paired Bluetooth devices.
 */

@HiltViewModel
class PairedDevicesViewModel @Inject constructor(
    private val getPairedDevicesUseCase: GetPairedDevicesUseCase,
    private val connectionUseCases: SocketConnectionUseCases,
) : ViewModel() {
    /**
     * on coroutine job handles connectivity operations logic
     */

    private var connectToDeviceJob: Job? = null

    private val _state = MutableStateFlow(PairedDeviceState())
    val state = _state
        .onStart { getPairedDevices() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            PairedDeviceState()
        )


    private val _events = Channel<PairedDeviceEvents>()
    val events = _events.receiveAsFlow()
    fun onActin(action: PairedDeviceActions) {
        when (action) {
            PairedDeviceActions.CancelConnection -> cancelConnection()
            is PairedDeviceActions.ConnectToDevice -> connectToDevice(action.device)
            PairedDeviceActions.GetPairedDevices -> getPairedDevices()
        }
    }

    /**
     * Attempts to connect to a specified Bluetooth device.
     *
     * Updates the state and emits events based on the result of the connection attempt.
     *
     * @param device The device to connect to using its address.
     */
    private fun connectToDevice(
        device: AppBluetoothDevice,
    ) {
        connectToDeviceJob = viewModelScope.launch {
            _state.update { it.copy(connecting = true) }
            connectionUseCases.connectToDevice(device)
                .onDone {
                    _events.send(PairedDeviceEvents.Connected(device.name))
                    _state.update { it.copy(connecting = false) }
                }.onFailed { err ->
                    _events.send(PairedDeviceEvents.Error(err.asUiText()))
                    _state.update { it.copy(connecting = false) }
                }
        }
    }

    /**
     * Cancels an ongoing connection attempt. and closes any connections
     */
    private fun cancelConnection() {
        connectionUseCases.closeConnection()
        connectToDeviceJob?.cancel()
        _state.update { it.copy(connecting = false) }
    }

    /**
     * Fetches the list of paired Bluetooth devices. if the usecase is done updating the state
     * whith fetched paired devcices
     * else it should update the state as error
     */
    private fun getPairedDevices() {
        _state.update { it.copy(loading = true, error = UiText.Empty) }
        viewModelScope.launch {
            getPairedDevicesUseCase()
                .onDone { pair ->
                    _state.update {
                        it.copy(
                            pairedDevices = pair,
                            loading = false,
                            error = UiText.Empty
                        )
                    }
                }.onFailed { error ->
                    _state.update {
                        it.copy(
                            loading = false,
                            error = error.asUiText()
                        )
                    }

                }
        }
    }

    /**
     * Cleans up resources when the ViewModel is cleared. when popped from the backstack or any reason of killing
     * the viewmodel so if any and the callback invoked we should clean  resources
     */
    override fun onCleared() {
        super.onCleared()
        connectionUseCases.closeConnection()
    }

}