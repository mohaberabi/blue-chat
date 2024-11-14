package com.mohaberabi.bluechat.core.data.source

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.mohaberabi.bluechat.core.domain.model.AppIntent
import com.mohaberabi.bluechat.core.domain.source.SimpleAppIntentInvoker
import javax.inject.Inject


/**
 * The AndroidServiceIntentInvoker Default Implementation fro the [SimpleAppIntentInvoker]
 * Handles starting Android services with intents. that are passed from any domain layer
 * then it is mapped to the android intent
 */
class AndroidServiceIntentInvoker @Inject constructor(
    private val context: Context
) : SimpleAppIntentInvoker {

    /**
     * Invokes a service intent and starts it as a foreground service.
     * @param intent The [AppIntent] containing the [KClass] and action for the service. which
     * in return is mapped into the android intent
     */
    override fun invoke(
        intent: AppIntent,
    ) {
        val androidIntent = Intent(
            context,
            intent.kClass.java
        ).apply {
            action = intent.intentAction
        }
        ContextCompat.startForegroundService(context, androidIntent)
    }
}