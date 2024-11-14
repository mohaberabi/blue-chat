package com.mohaberabi.bluechat.core.domain.model

import kotlin.reflect.KClass

data class AppIntent(
    val kClass: KClass<*>,
    val intentAction: String,
)
