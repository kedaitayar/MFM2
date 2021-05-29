package io.github.kedaitayar.mfm.util

import android.text.Editable

val <T> T.exhaustive: T
    get() = this

fun Editable?.toStringOrBlank(): String {
    if (this == null) {
        return ""
    }
    return this.toString()
}