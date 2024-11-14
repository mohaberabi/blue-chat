package com.mohaberabi.bluechat.features.scan_devices.data.repository

import com.mohaberabi.bluechat.core.domain.model.EmptyResult
import com.mohaberabi.bluechat.core.domain.model.ScanDeviceResult
import com.mohaberabi.bluechat.features.scan_devices.domain.ScanDeviceRepository
import com.mohaberabi.bluechat.features.scan_devices.domain.source.BluetoothScanner
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Default implementation of the `ScanDeviceRepository` interface.
 * @property scanner The `BluetoothScanner` interface that injected which implementation handles the low level andorid
 * scanning process
 */
class DefaultScanDeviceRepository @Inject constructor(
    private val scanner: BluetoothScanner,
) : ScanDeviceRepository {


    /**
     * simple flow that emits the current found nearby device
     */
    override fun getScannedDevices(): Flow<ScanDeviceResult> =
        scanner.getFoundDevices()

    /**
     * Request to scan devices before actually invoking the android low level api to scan devices if it
     * is done it the turn  for  the invoker to request the scanning process
     */
    override fun scanForDevices(): EmptyResult = scanner.requestToScanForDevices()

    /**
     * Stops the  scanning process.
     */
    override fun stopScanningForDevices(): EmptyResult = scanner.stopScanning()

}