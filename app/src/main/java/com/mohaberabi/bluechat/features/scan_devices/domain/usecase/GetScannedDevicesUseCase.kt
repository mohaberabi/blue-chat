package com.mohaberabi.bluechat.features.scan_devices.domain.usecase

import com.mohaberabi.bluechat.core.domain.model.ScanDeviceResult
import com.mohaberabi.bluechat.features.scan_devices.domain.ScanDeviceRepository
import kotlinx.coroutines.flow.Flow

class GetScannedDevicesUseCase(
    private val repository: ScanDeviceRepository
) {
    operator fun invoke(): Flow<ScanDeviceResult> = repository.getScannedDevices()
}