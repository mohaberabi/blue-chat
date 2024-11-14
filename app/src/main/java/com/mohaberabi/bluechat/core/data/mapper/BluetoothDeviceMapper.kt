package com.mohaberabi.bluechat.core.data.mapper

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice


@SuppressLint("MissingPermission")
fun BluetoothDevice.toAppBluetoothDevice() = AppBluetoothDevice(
    name = name ?: "No Name",
    macAddress = address
)
