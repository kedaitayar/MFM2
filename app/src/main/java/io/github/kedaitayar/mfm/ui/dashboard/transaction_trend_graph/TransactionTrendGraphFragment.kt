package io.github.kedaitayar.mfm.ui.dashboard.transaction_trend_graph

import android.graphics.*
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.*
import com.github.mikephil.charting.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentTransactionTrendGraphBinding
import kotlinx.coroutines.launch

private const val TAG = "TransactionTrendGraphFr"

@AndroidEntryPoint
class TransactionTrendGraphFragment : Fragment(R.layout.fragment_transaction_trend_graph) {
    private val transactionTrendGraphViewModel: TransactionTrendGraphViewModel by viewModels()
    private val binding: FragmentTransactionTrendGraphBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModelColor()
        setupCombinedGraph()
    }

    private fun initViewModelColor() {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.gGreen, typedValue, true)
        transactionTrendGraphViewModel.green =
            ContextCompat.getColor(requireContext(), typedValue.resourceId)
        requireContext().theme.resolveAttribute(R.attr.gRed, typedValue, true)
        transactionTrendGraphViewModel.red =
            ContextCompat.getColor(requireContext(), typedValue.resourceId)
        requireContext().theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
        transactionTrendGraphViewModel.colorOnSurface =
            ContextCompat.getColor(requireContext(), typedValue.resourceId)
        requireContext().theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        transactionTrendGraphViewModel.colorPrimary =
            ContextCompat.getColor(requireContext(), typedValue.resourceId)
    }

    private fun setupCombinedGraph() {

        // adding border radius to bar chart
        val customCombinedChartRenderer = object : CombinedChartRenderer(
            binding.combinedChart,
            binding.combinedChart.animator,
            binding.combinedChart.viewPortHandler
        ) {
            override fun createRenderers() {
                super.createRenderers()
                mRenderers.clear()

                val chart = mChart.get() as CombinedChart? ?: return

                val orders = chart.drawOrder

                for (order in orders) {
                    when (order) {
                        DrawOrder.BAR -> if (chart.barData != null) mRenderers.add(
                            object : BarChartRenderer(
                                chart,
                                mAnimator,
                                mViewPortHandler
                            ) {
                                var mRadius = 4f
                                private val mBarShadowRectBuffer = RectF()
                                private val path = Path()

                                override fun drawDataSet(
                                    c: Canvas?,
                                    dataSet: IBarDataSet?,
                                    index: Int
                                ) {
//                                    super.drawDataSet(c, dataSet, index)
                                    val trans = mChart.getTransformer(dataSet!!.axisDependency)
                                    mBarBorderPaint.color = dataSet.barBorderColor
                                    mBarBorderPaint.strokeWidth =
                                        Utils.convertDpToPixel(dataSet.barBorderWidth)

                                    val drawBorder = dataSet.barBorderWidth > 0f
                                    val phaseX = mAnimator.phaseX
                                    val phaseY = mAnimator.phaseY

                                    // draw the bar shadow before the values

                                    // draw the bar shadow before the values
                                    if (mChart.isDrawBarShadowEnabled) {
                                        mShadowPaint.color = dataSet.barShadowColor
                                        val barData = mChart.barData
                                        val barWidth = barData.barWidth
                                        val barWidthHalf = barWidth / 2.0f
                                        var x: Float
                                        var i = 0
                                        val count = Math.min(
                                            Math.ceil((dataSet.entryCount.toFloat() * phaseX).toDouble())
                                                .toInt(), dataSet.entryCount
                                        )
                                        while (i < count) {
                                            val e = dataSet.getEntryForIndex(i)
                                            x = e.x
                                            mBarShadowRectBuffer.left = x - barWidthHalf
                                            mBarShadowRectBuffer.right = x + barWidthHalf
                                            trans.rectValueToPixel(mBarShadowRectBuffer)
                                            if (!mViewPortHandler.isInBoundsLeft(
                                                    mBarShadowRectBuffer.right
                                                )
                                            ) {
                                                i++
                                                continue
                                            }
                                            if (!mViewPortHandler.isInBoundsRight(
                                                    mBarShadowRectBuffer.left
                                                )
                                            ) break
                                            mBarShadowRectBuffer.top = mViewPortHandler.contentTop()
                                            mBarShadowRectBuffer.bottom =
                                                mViewPortHandler.contentBottom()
                                            if (mRadius > 0) {
                                                c!!.drawRoundRect(
                                                    mBarShadowRectBuffer,
                                                    mRadius,
                                                    mRadius,
                                                    mShadowPaint
                                                )
                                            } else {
                                                c!!.drawRect(mBarShadowRectBuffer, mShadowPaint)
                                            }
                                            i++
                                        }
                                    }

                                    val buffer = mBarBuffers[index]
                                    buffer.setPhases(phaseX, phaseY)
                                    buffer.setDataSet(index)
                                    buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
                                    buffer.setBarWidth(mChart.barData.barWidth)

                                    buffer.feed(dataSet)

                                    trans.pointValuesToPixel(buffer.buffer)

                                    val isSingleColor = dataSet.colors.size == 1

                                    if (isSingleColor) {
                                        mRenderPaint.color = dataSet.color
                                    }

                                    var j = 0
                                    while (j < buffer.size()) {
                                        if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                                            j += 4
                                            continue
                                        }
                                        if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
                                        if (!isSingleColor) {
                                            // Set the color for the currently drawn value. If the index
                                            // is out of bounds, reuse colors.
                                            mRenderPaint.color = dataSet.getColor(j / 4)
                                        }
                                        if (dataSet.gradientColor != null) {
                                            val gradientColor = dataSet.gradientColor
                                            mRenderPaint.shader = LinearGradient(
                                                buffer.buffer[j],
                                                buffer.buffer[j + 3],
                                                buffer.buffer[j],
                                                buffer.buffer[j + 1],
                                                gradientColor.startColor,
                                                gradientColor.endColor,
                                                Shader.TileMode.MIRROR
                                            )
                                        }
                                        if (dataSet.gradientColors != null) {
                                            mRenderPaint.shader = LinearGradient(
                                                buffer.buffer[j],
                                                buffer.buffer[j + 3],
                                                buffer.buffer[j],
                                                buffer.buffer[j + 1],
                                                dataSet.getGradientColor(j / 4).startColor,
                                                dataSet.getGradientColor(j / 4).endColor,
                                                Shader.TileMode.MIRROR
                                            )
                                        }
                                        if (mRadius > 0) {
                                            c!!.drawRoundRect(
                                                buffer.buffer[j],
                                                buffer.buffer[j + 1],
                                                buffer.buffer[j + 2],
                                                buffer.buffer[j + 3],
                                                mRadius,
                                                mRadius,
                                                mRenderPaint
                                            )
                                        } else {
                                            c!!.drawRect(
                                                buffer.buffer[j],
                                                buffer.buffer[j + 1],
                                                buffer.buffer[j + 2],
                                                buffer.buffer[j + 3],
                                                mRenderPaint
                                            )
                                        }
                                        if (drawBorder) {
                                            if (mRadius > 0) {
                                                c.drawRoundRect(
                                                    buffer.buffer[j],
                                                    buffer.buffer[j + 1],
                                                    buffer.buffer[j + 2],
                                                    buffer.buffer[j + 3],
                                                    mRadius,
                                                    mRadius,
                                                    mBarBorderPaint
                                                )
                                            } else {
                                                c.drawRect(
                                                    buffer.buffer[j],
                                                    buffer.buffer[j + 1],
                                                    buffer.buffer[j + 2],
                                                    buffer.buffer[j + 3],
                                                    mBarBorderPaint
                                                )
                                            }
                                        }
                                        j += 4
                                    }
                                }
                            }
                        )
                        DrawOrder.BUBBLE -> if (chart.bubbleData != null) mRenderers.add(
                            BubbleChartRenderer(chart, mAnimator, mViewPortHandler)
                        )
                        DrawOrder.LINE -> if (chart.lineData != null) mRenderers.add(
                            LineChartRenderer(chart, mAnimator, mViewPortHandler)
                        )
                        DrawOrder.CANDLE -> if (chart.candleData != null) mRenderers.add(
                            CandleStickChartRenderer(chart, mAnimator, mViewPortHandler)
                        )
                        DrawOrder.SCATTER -> if (chart.scatterData != null) mRenderers.add(
                            ScatterChartRenderer(chart, mAnimator, mViewPortHandler)
                        )
                    }
                }
            }

        }

        binding.combinedChart.apply {
            renderer = customCombinedChartRenderer
            setTouchEnabled(false)
            description.isEnabled = false
            drawOrder = arrayOf(
                DrawOrder.BAR,
                DrawOrder.BUBBLE,
                DrawOrder.CANDLE,
                DrawOrder.LINE,
                DrawOrder.SCATTER
            )
            setDrawGridBackground(false)
            legend.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
//            xAxis.setDrawGridLines(false)
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
            val colorOnSurface = ContextCompat.getColor(requireContext(), typedValue.resourceId)
            xAxis.textColor = colorOnSurface
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    if (value == 0f) {
                        return "now"
                    }
                    return "week ${value.toInt()}"
                }
            }
            extraBottomOffset = 5f
            extraTopOffset = 5f
            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawZeroLine(true)
            axisLeft.textColor = colorOnSurface
            axisRight.setDrawGridLines(false)
            axisRight.textColor = colorOnSurface
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                transactionTrendGraphViewModel.transactionGraphCombinedData.collect {
                    binding.combinedChart.apply {
                        data = it
                        notifyDataSetChanged()
                        invalidate()
                    }
                }
            }
        }
    }
}