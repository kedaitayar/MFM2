package io.github.kedaitayar.mfm.ui

import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.renderer.*
import com.github.mikephil.charting.utils.ViewPortHandler

class CustomCombinedChartRenderer(
    chart: CombinedChart?, animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : CombinedChartRenderer(
    chart,
    animator, viewPortHandler,
) {
    override fun createRenderers() {
//        super.createRenderers()
        mRenderers.clear()

        val chart = mChart.get() as CombinedChart? ?: return

        val orders = chart.drawOrder

        for (order in orders) {
            when (order) {
                CombinedChart.DrawOrder.BAR -> if (chart.barData != null) mRenderers.add(
                    CustomBarChartRenderer(chart, mAnimator, mViewPortHandler, 4f)
                )
                CombinedChart.DrawOrder.BUBBLE -> if (chart.bubbleData != null) mRenderers.add(
                    BubbleChartRenderer(chart, mAnimator, mViewPortHandler)
                )
                CombinedChart.DrawOrder.LINE -> if (chart.lineData != null) mRenderers.add(
                    LineChartRenderer(chart, mAnimator, mViewPortHandler)
                )
                CombinedChart.DrawOrder.CANDLE -> if (chart.candleData != null) mRenderers.add(
                    CandleStickChartRenderer(chart, mAnimator, mViewPortHandler)
                )
                CombinedChart.DrawOrder.SCATTER -> if (chart.scatterData != null) mRenderers.add(
                    ScatterChartRenderer(chart, mAnimator, mViewPortHandler)
                )
            }
        }
    }
}