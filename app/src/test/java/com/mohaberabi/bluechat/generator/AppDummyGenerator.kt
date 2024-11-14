package com.mohaberabi.bluechat.generator

import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppIntent
import com.mohaberabi.bluechat.core.domain.model.MessageModel
import com.mohaberabi.bluechat.core.domain.model.UserModel
import kotlin.reflect.KClass


class DummyClass {}

fun bluetoothDevice() = AppBluetoothDevice("Loser", "loser-123")
fun userModel() = UserModel("")

fun appIntent(
    kclass: KClass<*> = DummyClass::class,
    action: String = "Some"
) = AppIntent(
    kclass,
    intentAction = action
)

fun messageModel(
    sentByMe: Boolean = true,
) = MessageModel(sentByMe, "asdsadsadasd", "asdasdsadsad")