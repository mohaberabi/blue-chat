package com.mohaberabi.bluechat.core.util.extensions


import android.view.View
import androidx.viewbinding.ViewBinding

/**
 * Sets the view's visibility to [View.GONE].
 */
fun View.hide() {
    visibility = View.GONE
}


/**
 * Sets the view's visibility to [View.INVISIBLE], keeping it on the layout but hidden.
 */
fun View.hideAndKeep() {
    visibility = View.INVISIBLE
}

/**
 * Sets the view's visibility to [View.VISIBLE].
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Conditionally sets the view's visibility to [View.VISIBLE] or [View.GONE].
 * @param visible If true, the view is set to [View.VISIBLE], otherwise [View.GONE].
 */
fun View.setVisible(
    visible: Boolean
) {
    visibility = if (visible) View.VISIBLE else View.GONE
}


/**
 * Makes all the provided views visible.
 * @param views A list of [View]s to set to [View.VISIBLE].
 */
fun ViewBinding.showAll(
    vararg views: View
) {
    views.forEach {
        it.show()
    }
}

/**
 * Hides all the provided views by setting their visibility to [View.GONE].
 * @param views A list of [View]s to set to [View.GONE].
 */
fun ViewBinding.hideAll(
    vararg views: View
) {
    views.forEach {
        it.hide()
    }
}


/**
 * adds a view into the view hierarchicy on predicate allowing access to its propertis
 * using the builder pattern as well with the help of DSL.
 * @param predicate If true, the view is shown; otherwise, it is hidden.
 * @param keepOnTree If true, hides the view using [View.INVISIBLE] instead of [View.GONE].
 * @param action An optional lambda to apply to the view when shown.
 * @return The view itself.
 */
inline fun <reified T : View> T.showIf(
    predicate: Boolean,
    keepOnTree: Boolean = false,
    action: T.() -> Unit = {},
): T {
    if (!predicate) {
        if (keepOnTree) {
            hideAndKeep()
        } else {
            hide()
        }
    } else {
        show()
        this.apply(action)
    }
    return this
}
