package com.mohaberabi.bluechat.core.data.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.mohaberabi.bluechat.core.domain.model.AppNotificationChannels
import com.mohaberabi.bluechat.core.domain.model.MessageModel
import com.mohaberabi.bluechat.core.domain.usecase.notification.ShowNotificationUseCase
import com.mohaberabi.bluechat.core.util.DispatcherProvider
import com.mohaberabi.bluechat.features.chat.domain.usecase.ListenToChatMessagesUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


/**
 * foreground service that listens to incoming chat messages and displays
 * notifications for any new messages received.
 * - Starts a foreground service to listen for messages.
 * - Notifies the user when a new message arrives.
 * - Stops gracefully when instructed.
 */
@AndroidEntryPoint
class ChatService : Service() {


    /**
     * [NotificationCompat.Builder] injected in hilt
     */
    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    /**
     * [ListenToChatMessagesUseCase] injected in hilt
     */
    @Inject
    lateinit var listenToMessages: ListenToChatMessagesUseCase

    /**
     * [DispatcherProvider] injected in hilt
     */
    @Inject
    lateinit var dispatchers: DispatcherProvider

    /**
     * [ShowNotificationUseCase] injected in hilt
     */
    @Inject
    lateinit var showNotification: ShowNotificationUseCase

    /**
     * creating a [SupervisorJob] to allow the scope to execute background work wich is
     * each and any coroutine  manage its failure without having any effect on siblings
     * we used the IO thread because  the Service runs on the main thread and we are doing a relatively
     * hard work on the main thread
     */
    private val chatScope by lazy {
        CoroutineScope(dispatchers.io + SupervisorJob())
    }

    companion object {
        const val ACTION_START = "ACTION_START_CHAT_SERVICE"
        const val ACTION_STOP = "ACTION_STOP_CHAT_SERVICE"
        private const val CHAT_NOTIFICATION_ID = 201098
        private const val DEFAULT_NOTFICT_TTL = "Local Chat Messages !"
        private const val DEFAULT_NOTFICT_BODY = "You will receive chat notifications"
    }

    /**
     * No need for binding the service for this UseCase
     * but the binder allows the app , component or even another processes [Application] to communicate with the
     * service in IPC Manner . which is not the scope of the current use case
     */

    override fun onBind(p0: Intent?): IBinder? = null

    /**
     * Handles the start and stop commands for the service.
     * this function is called and invoked after the service
     * is created by android os , fires when an intent is sent so we can start operations
     */
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        when (intent?.action) {
            ACTION_START -> startNotifyingOfMessages()
            ACTION_STOP -> stop()
        }
        return START_NOT_STICKY
    }

    /**
     * showing notifications only if the messages received on the stream of data
     * in the socket is not sent by me
     */
    private fun startNotifyingOfMessages() {
        listenToMessages()
            .onEach { newMessage ->
                if (!newMessage.sentByMe) {
                    showMessageNotification(newMessage)
                }
            }.catch {
                it.printStackTrace()
                stop()
            }.launchIn(chatScope)
        startForeground(
            CHAT_NOTIFICATION_ID,
            chatEntryNotification.build(),
        )
    }

    /**
     *shows  notifications  tied to the foreground service
     */
    private fun showMessageNotification(
        newMessage: MessageModel,
    ) {
        showNotification(
            id = CHAT_NOTIFICATION_ID,
            body = newMessage.text,
            title = "${newMessage.senderName} Sent you a new message ",
            channel = AppNotificationChannels.Chat
        )

    }


    /**
     *allowing the service to stop itself and removing the notification
     */
    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    /**
     *freeing up resources once the service killed
     */
    override fun onDestroy() {
        super.onDestroy()
        chatScope.cancel()
    }

    /**
     * provides the default entry point notification  required by the foreground service  to start
     */
    private val chatEntryNotification
        get() = notificationBuilder
            .setContentText(DEFAULT_NOTFICT_BODY)
            .setContentTitle(DEFAULT_NOTFICT_TTL)
}