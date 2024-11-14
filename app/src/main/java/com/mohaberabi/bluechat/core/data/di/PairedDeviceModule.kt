package com.mohaberabi.bluechat.core.data.di

import android.content.Context
import com.mohaberabi.bluechat.core.util.DispatcherProvider
import com.mohaberabi.bluechat.features.paired_devices.data.repository.DefaultPairedDeviceRepository
import com.mohaberabi.bluechat.features.paired_devices.data.source.AndroidPairedDeviceSource
import com.mohaberabi.bluechat.features.paired_devices.domain.repository.PairedDeviceRepository
import com.mohaberabi.bluechat.features.paired_devices.domain.source.PairedDeviceSource
import com.mohaberabi.bluechat.features.paired_devices.domain.usecase.GetPairedDevicesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)


object PairedDeviceModule {


    @Singleton
    @Provides
    fun providePairedDeviceSource(
        dispatchers: DispatcherProvider,
        @ApplicationContext context: Context
    ): PairedDeviceSource = AndroidPairedDeviceSource(
        dispatchers = dispatchers,
        context = context
    )

    @Singleton
    @Provides
    fun providePairedDeviceRepository(
        pairedDeviceSource: PairedDeviceSource
    ): PairedDeviceRepository = DefaultPairedDeviceRepository(pairedDeviceSource)

    @Singleton
    @Provides
    fun provideGetPairedDevicesUseCase(
        repository: PairedDeviceRepository
    ): GetPairedDevicesUseCase = GetPairedDevicesUseCase(repository)

}