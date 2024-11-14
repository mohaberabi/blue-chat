package com.mohaberabi.bluechat.core.domain.source

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.ConnectionSocketResult
import com.mohaberabi.bluechat.core.domain.model.EmptyResult
import com.mohaberabi.bluechat.core.domain.model.errros.AppError
import kotlinx.coroutines.flow.Flow

interface SocketConnectionManager {
    fun openConnectionSocket(): Flow<ConnectionSocketResult>
    fun closeConnection()
    fun listenToIncomingDataOnConnection(): Flow<ByteArray>
    suspend fun sendDataToCurrentSocket(bytes: ByteArray): AppResult<ByteArray, AppError>
    suspend fun connectToDevice(device: AppBluetoothDevice): EmptyResult

}