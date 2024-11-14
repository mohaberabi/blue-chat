package com.mohaberabi.bluechat.core.domain.model

import androidx.annotation.StringRes
import com.mohaberabi.bluechat.R
import com.mohaberabi.bluechat.core.domain.model.errros.AppError
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.core.domain.model.errros.SocketConnectionError

sealed class UiText {


    data object Empty : UiText()

    data class Dynamic(val value: String) : UiText()
    data class StringResources(
        @StringRes val id: Int,
        val formatArgs: List<Any> = listOf()
    ) : UiText()

    val isEmpty: Boolean
        get() = this == Empty

}


fun AppError.asUiText(): UiText {
    val id = when (this) {
        is BluetoothError -> {
            when (this) {
                BluetoothError.NotSupported -> R.string.bluetooth_not_supported
                BluetoothError.Disabled -> R.string.bluetooth_disabled
                BluetoothError.PermissionNotGranted -> R.string.bluetooth_permission_not_granted
                BluetoothError.AlreadyUnregisteredResources -> R.string.bluetooth_already_unregistered_resources
                BluetoothError.Unknown -> R.string.bluetooth_unknown_error
            }
        }

        is SocketConnectionError -> {
            when (this) {
                SocketConnectionError.NoClientConnected -> R.string.socket_no_client_connected
                SocketConnectionError.DataTransferError -> R.string.socket_data_transfer_error
                SocketConnectionError.ConnectToDeviceError -> R.string.socket_connect_to_device_error
            }
        }

        else -> R.string.unknown_error
    }
    return UiText.StringResources(id)
}


