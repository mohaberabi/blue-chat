package com.mohaberabi.bluechat.core.data.repository

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.ConnectionSocketResult
import com.mohaberabi.bluechat.core.domain.repository.SocketConnectionRepository
import com.mohaberabi.bluechat.core.domain.source.SocketConnectionManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * Default implementation of the [SocketConnectionRepository]. interface
 */
class DefaultSocketConnectionRepository @Inject constructor(
    private val socketManager: SocketConnectionManager
) : SocketConnectionRepository {


    /**
     * Opens a socket for incoming connections to await for other side of connections .
     */

    override fun openConnectionSocket(): Flow<ConnectionSocketResult> =
        socketManager.openConnectionSocket()


    /**
     * Connects to a given device with the help of [socketManager].
     */
    override suspend fun connectToDevice(device: AppBluetoothDevice) =
        socketManager.connectToDevice(device)

    /**
     * Closes the current Bluetooth connection. with the help of the [socketManager]
     */
    override fun closeConnection() = socketManager.closeConnection()
}