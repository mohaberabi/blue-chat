package com.mohaberabi.bluechat.features.scan_devices.domain

import com.mohaberabi.bluechat.core.domain.model.EmptyResult
import com.mohaberabi.bluechat.core.domain.model.ScanDeviceResult
import kotlinx.coroutines.flow.Flow

interface ScanDeviceRepository {
    fun getScannedDevices(): Flow<ScanDeviceResult>
    fun scanForDevices(): EmptyResult
    fun stopScanningForDevices(): EmptyResult
}