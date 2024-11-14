package com.mohaberabi.bluechat.core.data.constants

import java.util.UUID

/**
 * Holds constants for the app
 */
object ConnectionSocketConst {

    /**
     * The name of the socket we used across the whole app
     */
    const val SOCKET_NAME = "BlueChat"

    /**
     * The unique id  for the Bluetooth socket connection. it must be unique and in
     * the form of uuid as android requires it
     * the app only listens and connects on socket of that uuid
     */
    private const val SOCKET_ID = "f180c4e2-4384-4f0e-8f83-beccb1bd934a"

    /**
     * A [UUID]  from the [SOCKET_UUID] constant
     */
    val SOCKET_UUID: UUID = UUID.fromString(SOCKET_ID)
}