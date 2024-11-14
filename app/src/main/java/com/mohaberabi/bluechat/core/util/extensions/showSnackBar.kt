package com.mohaberabi.bluechat.core.util.extensions

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.mohaberabi.bluechat.core.domain.model.UiText

/**
 * Shows a [Snackbar] with the provided message string.
 *
 * @param message The message to display in the Snackbar.
 */

fun View.showSnackBar(
    message: String,
) {
    Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_LONG
    ).also {
        it.show()
    }
}

/**
 * Shows a [Snackbar] with a message from a [UiText] object. when mapped into string
 *
 * @param message The [UiText] message to display in the Snackbar.
 */

fun View.showSnackBar(
    message: UiText,
) {
    Snackbar.make(
        this,
        message.asString(this.context),
        Snackbar.LENGTH_LONG
    ).also {
        it.show()
    }
}