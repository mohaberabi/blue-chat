package com.mohaberabi.bluechat.core.data.di

import android.content.Context
import com.mohaberabi.bluechat.features.scan_devices.domain.ScanDeviceRepository
import com.mohaberabi.bluechat.core.domain.source.SimpleAppIntentInvoker
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.GetScannedDevicesUseCase
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.ScanForDevicesUseCase
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.StopScanningForDevicesUseCase
import com.mohaberabi.bluechat.features.scan_devices.data.source.DefaultBluetoothScanner
import com.mohaberabi.bluechat.features.scan_devices.data.repository.DefaultScanDeviceRepository
import com.mohaberabi.bluechat.features.scan_devices.domain.source.BluetoothScanner
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.ScannerUseCase
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.SendForegroundScanIntentUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ScanDeviceModule {


    @Singleton
    @Provides
    fun provideBluetoothScanner(
        @ApplicationContext context: Context
    ): BluetoothScanner = DefaultBluetoothScanner(
        context = context
    )


    @Singleton
    @Provides
    fun provideScanRepository(
        scanner: BluetoothScanner
    ): ScanDeviceRepository = DefaultScanDeviceRepository(
        scanner = scanner
    )


    @Singleton
    @Provides
    fun provideGetScannedDevicesUseCase(
        repository: ScanDeviceRepository
    ): GetScannedDevicesUseCase = GetScannedDevicesUseCase(repository)


    @Singleton
    @Provides
    fun provideScanForDevicesUseCase(
        repository: ScanDeviceRepository
    ): ScanForDevicesUseCase = ScanForDevicesUseCase(repository)

    @Singleton
    @Provides
    fun provideStopScanningForDevicesUseCase(
        repository: ScanDeviceRepository
    ): StopScanningForDevicesUseCase = StopScanningForDevicesUseCase(repository)

    @Singleton
    @Provides
    fun provideScannerUserCases(
        repository: ScanDeviceRepository,
    ) = ScannerUseCase(
        requestToScanDevices = ScanForDevicesUseCase(repository),
        getScannedDevices = GetScannedDevicesUseCase(repository),
        stopScanning = StopScanningForDevicesUseCase(repository)
    )

    @Singleton
    @Provides
    fun provideSendChatIntentUseCase(
        invoker: SimpleAppIntentInvoker
    ) = SendForegroundScanIntentUseCase(invoker)


}