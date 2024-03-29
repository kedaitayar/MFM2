package io.github.kedaitayar.mfm.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import io.github.kedaitayar.mfm.R

class PillPieCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private val path = Path()
    private var radius = 0f
    var piePercent: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var bgColor = 0
        set(value) {
            field = value
            invalidate()
        }
    private var pieColor = 0
        set(value) {
            field = value
            invalidate()
        }


    init {
        this.setPadding(
            paddingLeft,
            paddingTop,
            paddingRight + (textSize * 1.75).toInt(),
            paddingBottom
        )
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.PillPieCustomView)
        if (isInEditMode) {
            this.text = "Placeholder"
            bgColor = Color.parseColor("#4CAF50")
            pieColor = Color.parseColor("#777777")
            piePercent = 80
        } else {
            piePercent = typeArray.getInt(R.styleable.PillPieCustomView_piePercentage, 0)
            bgColor = typeArray.getColor(
                R.styleable.PillPieCustomView_bgColor,
                Color.parseColor("#4CAF50")
            )
            pieColor = typeArray.getColor(
                R.styleable.PillPieCustomView_pieColor,
                Color.parseColor("#777777")
            )
        }
        typeArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        drawPieAndBG(canvas)
        canvas?.save()
        canvas?.translate(height.toFloat(), height * -0.02f)
        super.onDraw(canvas)
        canvas?.restore()
    }


    private fun drawPieAndBG(canvas: Canvas?) {
        canvas?.save()
        paint.apply {
            strokeWidth = 4f
            color = this@PillPieCustomView.bgColor
            style = Paint.Style.FILL
        }

        radius = height / 2f
        path.rewind()
        path.addCircle(radius, radius, radius * 0.75f, Path.Direction.CCW)
        val clipRect01 = RectF(radius, 0f, radius * 2, height.toFloat())
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            canvas?.clipPath(path, Region.Op.DIFFERENCE)
            canvas?.save()
            canvas?.clipRect(clipRect01, Region.Op.DIFFERENCE)
        } else {
            canvas?.clipOutPath(path)
            canvas?.save()
            canvas?.clipOutRect(clipRect01)
        }
        canvas?.drawCircle(radius, radius, radius, paint)
        canvas?.restore()

        val rightRadius = 8 * resources.displayMetrics.density
        canvas?.drawRect(radius, 0f, height.toFloat(), height.toFloat(), paint)
        val rect01 = RectF(radius, 0f, width.toFloat(), height.toFloat())

        canvas?.save()
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            canvas?.clipRect(clipRect01, Region.Op.DIFFERENCE)
        } else {
            canvas?.clipOutRect(clipRect01)
        }
        canvas?.drawRoundRect(rect01, rightRadius, rightRadius, paint)
        canvas?.restore()

        canvas?.restore()

        paint.color = this@PillPieCustomView.pieColor
        val rect02 = RectF(height * 0.2f, height * 0.2f, height * 0.8f, height * 0.8f)
        canvas?.drawArc(rect02, -90f, piePercent.toPiePercentageDiscrete(), true, paint)
    }

    private fun Int.toPiePercentageDiscrete(): Float {
        return (this.div(10).times(10)) * 360 * 0.01f
    }
}