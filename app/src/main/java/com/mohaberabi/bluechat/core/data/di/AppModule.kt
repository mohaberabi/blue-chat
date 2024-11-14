package com.mohaberabi.bluechat.core.data.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.mohaberabi.bluechat.MainActivity
import com.mohaberabi.bluechat.R
import com.mohaberabi.bluechat.core.data.repository.DefaultSocketConnectionRepository
import com.mohaberabi.bluechat.core.data.source.AndroidNotificationManager
import com.mohaberabi.bluechat.core.data.source.AndroidServiceIntentInvoker
import com.mohaberabi.bluechat.core.data.source.BluetoothConnectionTracker
import com.mohaberabi.bluechat.core.data.source.BluetoothSocketConnectionManager
import com.mohaberabi.bluechat.core.domain.model.AppNotificationChannels
import com.mohaberabi.bluechat.core.domain.repository.SocketConnectionRepository
import com.mohaberabi.bluechat.core.domain.source.AppNotificationManager
import com.mohaberabi.bluechat.core.domain.source.ConnectionStateTracker
import com.mohaberabi.bluechat.core.domain.source.SimpleAppIntentInvoker
import com.mohaberabi.bluechat.core.domain.source.SocketConnectionManager
import com.mohaberabi.bluechat.core.domain.usecase.connection.CheckConnectionLossUseCase
import com.mohaberabi.bluechat.core.domain.usecase.connection.CloseConnectionUseCase
import com.mohaberabi.bluechat.core.domain.usecase.connection.ConnectToDeviceUseCase
import com.mohaberabi.bluechat.core.domain.usecase.connection.OpenConnectionSocketUseCase
import com.mohaberabi.bluechat.core.domain.usecase.connection.SocketConnectionUseCases
import com.mohaberabi.bluechat.core.util.AppSupervisorScope
import com.mohaberabi.bluechat.core.util.DefaultAppSupervisorScope
import com.mohaberabi.bluechat.core.util.DefaultDispatcherProvider
import com.mohaberabi.bluechat.core.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDispatchersProvider(
    ): DispatcherProvider = DefaultDispatcherProvider()


    @Singleton
    @Provides
    fun provideSocketManager(
        @ApplicationContext context: Context,
        dispatchers: DispatcherProvider
    ): SocketConnectionManager = BluetoothSocketConnectionManager(
        dispatchers = dispatchers,
        context = context,
    )

    @Singleton
    @Provides
    fun provideConnectionRepository(
        manager: SocketConnectionManager
    ): SocketConnectionRepository = DefaultSocketConnectionRepository(manager)


    @Singleton
    @Provides
    fun provideConnectionUseCases(
        repository: SocketConnectionRepository,
    ) = SocketConnectionUseCases(
        closeConnection = CloseConnectionUseCase(repository),
        openSocket = OpenConnectionSocketUseCase(repository),
        connectToDevice = ConnectToDeviceUseCase(repository)
    )


    @Singleton
    @Provides
    fun provideAppSuperVisorScope(
        dispatchers: DispatcherProvider
    ): AppSupervisorScope = DefaultAppSupervisorScope(dispatchers)

    @Singleton
    @Provides
    fun provideConnectionTracker(
        @ApplicationContext context: Context,
        dispatchers: DispatcherProvider,
        supervisorScope: AppSupervisorScope
    ): ConnectionStateTracker = BluetoothConnectionTracker(
        context,
        dispatchers,
        supervisorScope
    )

    @Singleton
    @Provides
    fun provideCheckConnectionLossUseCase(
        tracker: ConnectionStateTracker
    ) = CheckConnectionLossUseCase(tracker)


    @Singleton
    @Provides
    fun provideAppNotificationManager(
        @ApplicationContext context: Context,
        builder: NotificationCompat.Builder
    ): AppNotificationManager = AndroidNotificationManager(context, builder)

    @Singleton
    @Provides
    fun provideNotificationBuilder(
        @ApplicationContext context: Context,
        defaultContentIntent: PendingIntent
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(
            context,
            AppNotificationChannels.ScanDevices.id
        ).setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(defaultContentIntent)

    @Singleton
    @Provides
    fun provideAppIntentInvoker(
        @ApplicationContext context: Context
    ): SimpleAppIntentInvoker = AndroidServiceIntentInvoker(
        context = context
    )


    @Singleton
    @Provides
    fun provideDefaultNotificationContent(
        @ApplicationContext context: Context,
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return pendingIntent
    }
}

