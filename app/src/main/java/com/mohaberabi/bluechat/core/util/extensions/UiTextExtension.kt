package com.mohaberabi.bluechat.core.util.extensions

import android.content.Context
import com.mohaberabi.bluechat.core.domain.model.UiText
import com.mohaberabi.bluechat.core.domain.model.UiText.Dynamic
import com.mohaberabi.bluechat.core.domain.model.UiText.Empty
import com.mohaberabi.bluechat.core.domain.model.UiText.StringResources


/**
 * Converts a [UiText] object into a string.
 *
 * @param context The [Context] to map into  string resources.
 * @return The string value of the [UiText].
 */
fun UiText.asString(
    context: Context,
): String {
    return when (this) {
        is Dynamic -> this.value
        Empty -> ""
        is StringResources -> context.getString(this.id, this.formatArgs.toTypedArray())
    }
}