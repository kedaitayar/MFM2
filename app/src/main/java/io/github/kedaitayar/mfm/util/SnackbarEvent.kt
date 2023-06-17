package io.github.kedaitayar.mfm.util

import androidx.lifecycle.Observer

class SnackbarEvent<out T>(private val content: T) {
    private var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}

//class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<SnackbarEvent<T>> {
//    override fun onChanged(event: SnackbarEvent<T>?) {
//        event?.getContentIfNotHandled()?.let {
//            onEventUnhandledContent(it)
//        }
//    }
//}