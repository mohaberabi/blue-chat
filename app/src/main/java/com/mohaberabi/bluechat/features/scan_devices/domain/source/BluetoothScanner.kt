package com.mohaberabi.bluechat.features.scan_devices.domain.source

import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.EmptyResult
import com.mohaberabi.bluechat.core.domain.model.ScanDeviceResult
import com.mohaberabi.bluechat.core.domain.model.errros.AppError
import kotlinx.coroutines.flow.Flow

interface BluetoothScanner {
    fun requestToScanForDevices(): AppResult<Unit, AppError>
    fun stopScanning(): EmptyResult
    fun getFoundDevices(): Flow<ScanDeviceResult>
}