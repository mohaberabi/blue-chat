package com.mohaberabi.bluechat.features.paired_devices.domain.usecase

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.errros.AppError
import com.mohaberabi.bluechat.features.paired_devices.domain.repository.PairedDeviceRepository
import javax.inject.Inject

class GetPairedDevicesUseCase @Inject constructor(
    private val repository: PairedDeviceRepository
) {
    suspend operator fun invoke(
    ): AppResult<Set<AppBluetoothDevice>, AppError> =
        repository.getPairedDevices()
}