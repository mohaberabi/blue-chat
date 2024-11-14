package com.mohaberabi.bluechat.features.scan_devices.domain.usecase

data class ScannerUseCase(
    val getScannedDevices: GetScannedDevicesUseCase,
    val requestToScanDevices: ScanForDevicesUseCase,
    val stopScanning: StopScanningForDevicesUseCase,
)
