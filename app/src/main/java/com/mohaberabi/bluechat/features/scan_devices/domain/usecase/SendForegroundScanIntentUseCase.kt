package com.mohaberabi.bluechat.features.scan_devices.domain.usecase

import com.mohaberabi.bluechat.core.data.services.BluetoothScanService
import com.mohaberabi.bluechat.core.domain.model.AppIntent
import com.mohaberabi.bluechat.core.domain.source.SimpleAppIntentInvoker
import javax.inject.Inject

class SendForegroundScanIntentUseCase @Inject constructor(
    private val invoker: SimpleAppIntentInvoker
) {
    operator fun invoke(
        action: String,
    ) {
        val intent = AppIntent(
            BluetoothScanService::class,
            action,
        )
        invoker.invoke(intent)
    }
}