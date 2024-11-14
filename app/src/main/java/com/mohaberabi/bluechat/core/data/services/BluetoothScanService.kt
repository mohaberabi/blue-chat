package com.mohaberabi.bluechat.core.data.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.mohaberabi.bluechat.core.domain.model.AppNotificationChannels
import com.mohaberabi.bluechat.core.domain.model.ScanDeviceResult
import com.mohaberabi.bluechat.core.domain.model.onDone
import com.mohaberabi.bluechat.core.domain.model.onFailed
import com.mohaberabi.bluechat.core.domain.usecase.notification.ShowNotificationUseCase
import com.mohaberabi.bluechat.core.util.DispatcherProvider
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.GetScannedDevicesUseCase
import com.mohaberabi.bluechat.features.scan_devices.domain.usecase.ScanForDevicesUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * BluetoothScanService manages Bluetooth device scanning as a foreground service.
 * It handles device discovery, notifications, and connection updates.
 *
 * - Starts and stops scanning for Bluetooth devices.
 * - Displays notifications for discovered devices.
 * - Operates as a foreground service with a persistent notification.
 */
@AndroidEntryPoint
class BluetoothScanService : Service() {


    /**
     * the primary [ NotificationCompat.Builder] used for sharing same builded notiifcaiton
     * across the app injected in hilt
     */
    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    /**
     * [GetScannedDevicesUseCase] injected in hilt
     */
    @Inject
    lateinit var getScannedDevices: GetScannedDevicesUseCase

    /**
     * [ScanForDevicesUseCase] injected in hilt
     */
    @Inject
    lateinit var requestToScanUseCase: ScanForDevicesUseCase

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
    private val scanScope by lazy {
        CoroutineScope(dispatchers.io + SupervisorJob())
    }

    companion object {
        private const val SCAN_NOTIFICATION_ID = 981020
        const val ACTION_START = "ACTION_START_SCAN_SERVICE"
        const val ACTION_STOP = "ACTION_STOP_SCAN_SERVICE"
        const val TAG = "BluetoothScanService"
        private const val DEFAULT_NOTFICT_TTL =
            "Nearby Devices!"
        private const val DEFAULT_NOTFICT_BODY =
            "You will receive notifications about nearby devices"
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
            ACTION_START -> requestToScan()

            ACTION_STOP -> stop()
        }
        return START_NOT_STICKY
    }

    /**
     * Request to start scanning using the provided use case.
     * If the request succeeds, it starts listening for scanned devices.
     */
    private fun requestToScan() {
        requestToScanUseCase()
            .onDone { startScanWhenReady() }
            .onFailed {
                Log.e(TAG, it.toString())
                stop()
            }
    }

    /**
     *Staring scanning devices once ready
     */
    private fun startScanWhenReady() {
        getScannedDevices()
            .distinctUntilChanged()
            .onEach { result ->
                if (result is ScanDeviceResult.DeviceFound) {
                    showMessageNotification(result.device.name)
                }
            }.catch {
                it.printStackTrace()
                stop()
            }.launchIn(scanScope)
        startForeground(
            SCAN_NOTIFICATION_ID,
            scanEntryNotification.build()
        )
    }


    /**
     *shows  notifications  tied to the foreground service
     */
    private fun showMessageNotification(
        device: String,
    ) {
        showNotification(
            id = SCAN_NOTIFICATION_ID,
            body = "Found $device nearby waiting for connection",
            title = "Found a new device nearby",
            channel = AppNotificationChannels.ScanDevices
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
        scanScope.cancel()
    }

    /**
     * provides the default entry point notification  required by the foreground service  to start
     */
    private val scanEntryNotification
        get() = notificationBuilder
            .setChannelId(AppNotificationChannels.ScanDevices.id)
            .setContentText(DEFAULT_NOTFICT_BODY)
            .setContentTitle(DEFAULT_NOTFICT_TTL)
}