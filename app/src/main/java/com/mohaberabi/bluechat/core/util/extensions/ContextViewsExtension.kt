package com.mohaberabi.bluechat.core.util.extensions

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.mohaberabi.bluechat.databinding.LoadingDialogueBinding

/**
 * Creates and returns a custom loading dialog with a cancel button.
 *
 * @param onCancel A callback triggered when the cancel button is clicked.
 * @return An [AlertDialog] instance for loading.
 */

fun Context.createLoadingDialog(
    onCancel: (AlertDialog) -> Unit,
): AlertDialog {
    val builder = AlertDialog.Builder(this)
    val inflater = LayoutInflater.from(this)
    val binding = LoadingDialogueBinding.inflate(inflater)
    builder.setView(binding.root)
    builder.setCancelable(false)
    val dialog = builder.create()
    binding.cancelButton.setOnClickListener {
        onCancel(dialog)
    }
    return builder.create()
}