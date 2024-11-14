package com.mohaberabi.bluechat.core.data.di

import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.core.content.getSystemService
import com.mohaberabi.bluechat.core.data.source.BluetoothUserLocalDataSource
import com.mohaberabi.bluechat.core.domain.source.SimpleAppIntentInvoker
import com.mohaberabi.bluechat.features.chat.data.repository.DefaultChatRepository
import com.mohaberabi.bluechat.features.chat.domain.repository.ChatRepository
import com.mohaberabi.bluechat.core.domain.source.SocketConnectionManager
import com.mohaberabi.bluechat.core.domain.source.UserLocalDataSource
import com.mohaberabi.bluechat.features.chat.domain.usecase.ListenToChatMessagesUseCase
import com.mohaberabi.bluechat.features.chat.domain.usecase.SendMessageUseCase
import com.mohaberabi.bluechat.features.chat.domain.usecase.SendChatForegroundIntentUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ChatModule {


    @Provides
    @Singleton
    fun provideUserLocalDataSource(
        @ApplicationContext context: Context,
    ): UserLocalDataSource = BluetoothUserLocalDataSource(
        requireNotNull(context.getSystemService<BluetoothManager>()),
    )

    @Provides
    @Singleton
    fun provideChatRepository(
        manager: SocketConnectionManager,
        source: UserLocalDataSource,
    ): ChatRepository = DefaultChatRepository(
        manager,
        userLocalDataSource = source
    )

  
    @Singleton
    @Provides
    fun provideSendMessageUseCase(
        repository: ChatRepository
    ) = SendMessageUseCase(repository)

    @Singleton
    @Provides
    fun provideListenToMessagesUseCase(
        repository: ChatRepository
    ) = ListenToChatMessagesUseCase(repository)


    @Singleton
    @Provides
    fun sendChatForegroundIntentUseCase(
        invoker: SimpleAppIntentInvoker
    ) = SendChatForegroundIntentUseCase(invoker)


}