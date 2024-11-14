package com.mohaberabi.bluechat.features.paired_devices.data.repository

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.errros.AppError
import com.mohaberabi.bluechat.features.paired_devices.domain.repository.PairedDeviceRepository
import com.mohaberabi.bluechat.features.paired_devices.domain.source.PairedDeviceSource
import javax.inject.Inject

/**
 * Default  implementation for retrieving paired Bluetooth devices.
 */
class DefaultPairedDeviceRepository @Inject constructor(
    private val pairedDeviceSource: PairedDeviceSource
) : PairedDeviceRepository {
    /**
     * Gets all paired Bluetooth devices. if the source returns correctly and now errors
     * else it returns errors with the reason as well
     * @return A result containing a set of paired Bluetooth devices or an error if the operation fails.
     */
    override suspend fun getPairedDevices(
    ): AppResult<Set<AppBluetoothDevice>, AppError> = pairedDeviceSource.getAllPairedDevices()
}