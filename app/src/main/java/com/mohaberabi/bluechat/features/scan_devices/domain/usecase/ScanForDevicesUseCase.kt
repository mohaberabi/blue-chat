package com.mohaberabi.bluechat.features.scan_devices.domain.usecase

import com.mohaberabi.bluechat.features.scan_devices.domain.ScanDeviceRepository

class ScanForDevicesUseCase(
    private val repository: ScanDeviceRepository
) {
    operator fun invoke() = repository.scanForDevices()
}