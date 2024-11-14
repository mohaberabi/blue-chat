package com.mohaberabi.bluechat.core.data.source

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.UserModel
import com.mohaberabi.bluechat.core.domain.model.errros.handleBluetoothOperation
import com.mohaberabi.bluechat.core.domain.source.UserLocalDataSource
import javax.inject.Inject


/**
 * A BluetoothUserLocalDataSource as a default impl for the [UserLocalDataSource]
 * used for getting the current device name to be appeared for the other user in action with current user
 *
 */
class BluetoothUserLocalDataSource @Inject constructor(
    private val bluetoothManager: BluetoothManager
) : UserLocalDataSource {
    private val adapter by lazy { bluetoothManager.adapter }

    @SuppressLint("MissingPermission")
    override fun getCurrentUser(): UserModel {
        val result = handleBluetoothOperation {
            adapter?.name ?: "Unknown"
        }
        val name = when (result) {
            is AppResult.Done -> result.data
            is AppResult.Error -> ""
        }
        return UserModel(name)
    }
}