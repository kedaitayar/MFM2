package io.github.kedaitayar.mfm.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import io.github.kedaitayar.mfm.R
import kotlin.math.min

class RingCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var rectF = RectF()
    private var startAngle = 120f
    private var fullRingAngle = 300f
    private var ringStrokeWidth = 20f
    private var ringColor = Color.GREEN
    private var size = 0
    private var ringProgress = 0f

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.RingCustomView)
        if (isInEditMode) {
            this.ringProgress = 200f
        } else {
            this.setRingProgress(typeArray.getInt(R.styleable.RingCustomView_ringProgress, 0))
            this.ringColor = typeArray.getColor(R.styleable.RingCustomView_ringColor, Color.GREEN)
            this.ringStrokeWidth =
                typeArray.getFloat(R.styleable.RingCustomView_ringStrokeWidth, 20f)
        }
        typeArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            ringPaint.apply {
                color = Color.LTGRAY
                strokeWidth = ringStrokeWidth
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
            }
            canvas.drawArc(rectF, startAngle, fullRingAngle, false, ringPaint)
            ringPaint.color = ringColor
            canvas.drawArc(rectF, startAngle, ringProgress, false, ringPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size = min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        setMeasuredDimension(size, size)
        rectF.set(
            ringStrokeWidth / 2f,
            ringStrokeWidth / 2f,
            size - (ringStrokeWidth / 2f),
            size - (ringStrokeWidth / 2f)
        )
    }

    fun setRingProgress(ringProgress: Int) {
        this.ringProgress = (fullRingAngle / 100) * ringProgress
        invalidate()
    }
}