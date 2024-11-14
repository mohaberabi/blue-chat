package com.mohaberabi.bluechat.core.domain.model.errros


enum class BluetoothError : AppError {
    NotSupported,
    Disabled,
    PermissionNotGranted,
    AlreadyUnregisteredResources,
    Unknown,
}


enum class SocketConnectionError : AppError {
    NoClientConnected,
    DataTransferError,
    ConnectToDeviceError,
}