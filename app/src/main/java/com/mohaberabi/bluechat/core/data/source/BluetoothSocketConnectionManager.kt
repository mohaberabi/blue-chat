package com.mohaberabi.bluechat.core.data.source

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.core.content.getSystemService
import com.mohaberabi.bluechat.core.data.constants.ConnectionSocketConst
import com.mohaberabi.bluechat.core.data.mapper.toAppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.ConnectionSocketResult
import com.mohaberabi.bluechat.core.domain.model.EmptyResult
import com.mohaberabi.bluechat.core.domain.model.errros.AppError
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.core.domain.model.errros.SocketConnectionError
import com.mohaberabi.bluechat.core.domain.model.errros.handleBluetoothOperation
import com.mohaberabi.bluechat.core.domain.source.SocketConnectionManager
import com.mohaberabi.bluechat.core.util.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

/**
 * A manager for handling Bluetooth socket connections.
 *
 * This class provides methods for managing Bluetooth server and client sockets,
 * including operations for connecting, sending, and receiving data between devices.
 */
@SuppressLint("MissingPermission")
class BluetoothSocketConnectionManager @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val context: Context
) : SocketConnectionManager {

    private val adapter by lazy {
        requireNotNull(context.getSystemService<BluetoothManager>()).adapter
    }
    private var currentClientSocket: BluetoothSocket? = null
    private var currentServerSocket: BluetoothServerSocket? = null

    /**
     * Opens a Bluetooth server socket and waits for incoming connections.
     * closes the socket is the bluetooth was disabled
     * emits error if no client was attached
     * emits done if client   was attached to the current socket
     * @return [Flow] emitting [ConnectionSocketResult]. the flow is main safe means it changes and switches
     *the dispatcher to background thread by the help of the injected dispatchers provider
     */
    override fun openConnectionSocket(): Flow<ConnectionSocketResult> = flow {
        if (!adapter.isEnabled) {
            emit(ConnectionSocketResult.Error(BluetoothError.Disabled))
            return@flow
        }
        currentServerSocket = adapter?.listenUsingRfcommWithServiceRecord(
            ConnectionSocketConst.SOCKET_NAME,
            ConnectionSocketConst.SOCKET_UUID
        )
        val client = try {
            currentServerSocket?.accept()
        } catch (e: IOException) {
            null
        }
        if (client != null) {
            currentClientSocket = client
            val device = currentClientSocket?.remoteDevice?.toAppBluetoothDevice()
            emit(ConnectionSocketResult.Connected(device))
            currentServerSocket?.close()
        } else {
            emit(ConnectionSocketResult.Error(SocketConnectionError.NoClientConnected))
        }
    }.flowOn(dispatchers.io)

    /**
     * Listens to incoming data from the connected Bluetooth socket.
     * on the current input stream which  receives bytes form the other side output stream
     * main safe on a background thread
     * @return [Flow] emitting byte arrays of received data.
     */
    override fun listenToIncomingDataOnConnection(): Flow<ByteArray> = flow {
        val buffer = ByteArray(1024)
        val socket = currentClientSocket ?: return@flow
        while (socket.isConnected) {
            val bytesRead = try {
                currentClientSocket?.inputStream?.read(buffer)
            } catch (e: IOException) {
                e.printStackTrace()
                break
            }
            bytesRead?.let {
                emit(buffer.copyOf(it))
            }
        }
    }.flowOn(dispatchers.io)

    /**
     * Connects to a specified Bluetooth device.
     * caching the  [currentClientSocket] to be used across the whole session unless  closed
     * @param device The [AppBluetoothDevice] to connect to using the mac address
     * if error happens while [BluetoothAdapter.connect] all errors are handled by the
     * help of [handleBluetoothOperation] wich  also hanldes the explicitly thrown IoException
     * @return [EmptyResult] indicating success or failure.
     */
    override suspend fun connectToDevice(
        device: AppBluetoothDevice
    ): EmptyResult {
        return withContext(dispatchers.io) {
            handleBluetoothOperation {
                val remoteDevice = adapter?.getRemoteDevice(device.macAddress)
                val client = remoteDevice?.createRfcommSocketToServiceRecord(
                    ConnectionSocketConst.SOCKET_UUID
                )
                currentClientSocket = client
                if (adapter.isDiscovering) {
                    adapter.cancelDiscovery()
                }
                client?.connect() ?: throw IOException()
            }
        }
    }

    /**
     * Sends data through the connected Bluetooth socket.
     * on the output stream
     * main safe
     * @param bytes The byte array to send.
     * @return [AppResult] containing the sent data or an error.
     */
    override suspend fun sendDataToCurrentSocket(
        bytes: ByteArray
    ): AppResult<ByteArray, AppError> {
        return withContext(dispatchers.io) {
            handleBluetoothOperation {
                currentClientSocket?.outputStream?.write(bytes)
                bytes
            }
        }
    }

    /**
     * Closes the current Bluetooth socket connection. and releases the resources
     * carried from the client and server  sockets
     */
    override fun closeConnection() {
        try {
            currentClientSocket?.close()
            currentServerSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            currentClientSocket = null
            currentServerSocket = null
        }
    }
}