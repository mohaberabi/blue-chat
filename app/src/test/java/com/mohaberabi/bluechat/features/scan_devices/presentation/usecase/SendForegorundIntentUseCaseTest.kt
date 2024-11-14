package com.mohaberabi.bluechat.features.scan_devices.presentation.usecase

import com.mohaberabi.bluechat.core.data.services.BluetoothScanService
import com.mohaberabi.bluechat.core.domain.model.AppIntent
import com.mohaberabi.bluechat.core.domain.model.ScanDeviceResult
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.core.domain.source.SimpleAppIntentInvoker
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.SendForegroundScanIntentUseCase
import com.mohaberabi.bluechat.generator.bluetoothDevice
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SendForegorundIntentUseCaseTest {


    private lateinit var sendForeground: SendForegroundScanIntentUseCase


    private lateinit var invoker: SimpleAppIntentInvoker


    private val intent = AppIntent(BluetoothScanService::class, "start")

    @Before
    fun setup() {

        invoker = mockk()
        sendForeground = SendForegroundScanIntentUseCase(invoker)
    }


    @Test
    fun `invoekr should be invoked with the application intent once `() = runTest {

        every { invoker.invoke(intent) } returns Unit

        sendForeground("start")
        verify { invoker.invoke(intent) }
    }

}