package com.mohaberabi.bluechat.features.paired_devices.domain.source

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.errros.AppError

interface PairedDeviceSource {

    suspend fun getAllPairedDevices(): AppResult<Set<AppBluetoothDevice>, AppError>
}