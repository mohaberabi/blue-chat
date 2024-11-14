package com.mohaberabi.bluechat.core.domain.repository

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.ConnectionSocketResult
import com.mohaberabi.bluechat.core.domain.model.EmptyResult
import kotlinx.coroutines.flow.Flow

interface SocketConnectionRepository {
    fun openConnectionSocket(): Flow<ConnectionSocketResult>
    suspend fun connectToDevice(device: AppBluetoothDevice): EmptyResult
    fun closeConnection()
}