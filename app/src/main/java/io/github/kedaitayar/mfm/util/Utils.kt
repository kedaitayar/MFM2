package io.github.kedaitayar.mfm.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Editable
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use

val <T> T.exhaustive: T
    get() = this

fun Editable?.toStringOrBlank(): String {
    if (this == null) {
        return ""
    }
    return this.toString()
}

@ColorInt
@SuppressLint("Recycle")
fun Context.themeColor(
    @AttrRes themeAttrId: Int
): Int {
    return obtainStyledAttributes(
        intArrayOf(themeAttrId)
    ).use {
        it.getColor(0, Color.MAGENTA)
    }
}