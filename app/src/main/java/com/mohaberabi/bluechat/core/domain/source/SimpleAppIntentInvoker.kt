package com.mohaberabi.bluechat.core.domain.source

import com.mohaberabi.bluechat.core.domain.model.AppIntent

interface SimpleAppIntentInvoker {
    fun invoke(intent: AppIntent)
}