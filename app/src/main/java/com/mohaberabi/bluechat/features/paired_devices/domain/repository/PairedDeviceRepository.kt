package com.mohaberabi.bluechat.features.paired_devices.domain.repository

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.errros.AppError

interface PairedDeviceRepository {
    suspend fun getPairedDevices(): AppResult<Set<AppBluetoothDevice>, AppError>

}