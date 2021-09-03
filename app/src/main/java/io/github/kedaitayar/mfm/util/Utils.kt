package io.github.kedaitayar.mfm.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.text.Editable
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

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

fun Double.toCurrency(context: Context): String {
    val resources = context.resources
    val format = DecimalFormatSymbols().apply {
        groupingSeparator = ' '
    }
    val formatter = DecimalFormat(resources.getString(R.string.currency_format)).apply {
        decimalFormatSymbols = format
    }
    return resources.getString(R.string.currency_symbol, formatter.format(this))
}

fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
fun Context.dpToPx(dp: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

val Int.toDp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.toPx: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * if value is null, return 0.0, if not return the original value
 */
fun Double?.notNull(): Double {
    return (this ?: 0.0)
}

fun BudgetListAdapterData.toBudget(): Budget {
    return Budget(
        budgetId = this.budgetId,
        budgetPosition = this.budgetPosition,
        budgetGoal = this.budgetGoal,
        budgetName = this.budgetName,
        budgetType = this.budgetTypeId.toInt()
    )
}