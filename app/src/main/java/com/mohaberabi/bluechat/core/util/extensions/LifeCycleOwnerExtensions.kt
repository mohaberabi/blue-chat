package com.mohaberabi.bluechat.core.util.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


/**
 * Collects emissions from a [Flow] and handles them within the lifecycle of a [LifecycleOwner].
 * This function collects the flows  only happens when the lifecycle is at least in the `STARTED` state,
 * which is typically useful as it make sure  app has the focus so we can consume resources to collect the flows
 * automatically stopping collection when the lifecycle is in a lower priority state
 * @param flow The [Flow] to collect from.
 * @param collector A lambda function to handle each emitted value from the flow.
 */

fun <T> LifecycleOwner.collectWithLifeCycle(
    flow: Flow<T>,
    collector: (T) -> Unit,
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect {
                collector(it)
            }
        }
    }

}

fun <T> Fragment.collectWithLifeCycle(
    flow: Flow<T>,
    collector: (T) -> Unit,
) = viewLifecycleOwner.collectWithLifeCycle(flow, collector)