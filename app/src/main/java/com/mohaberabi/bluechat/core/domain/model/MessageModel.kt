package com.mohaberabi.bluechat.core.domain.model

data class MessageModel(
    val sentByMe: Boolean,
    val text: String,
    val senderName: String,
) {
    val toByteArray: ByteArray
        get() = "$sentByMe|$text|$senderName".encodeToByteArray()

    companion object {
        fun fromByteArray(bytes: ByteArray): MessageModel {
            val decodedString = bytes.decodeToString()
            val parts = decodedString.split("|")
            return if (parts.size < 3) {
                MessageModel(
                    sentByMe = false,
                    text = "Message Data Corrupted",
                    senderName = "Sender Name Corrupted"
                )
            } else {
                MessageModel(
                    sentByMe = parts[0].toBoolean(),
                    text = parts[1],
                    senderName = parts[2]
                )
            }

        }
    }
}
