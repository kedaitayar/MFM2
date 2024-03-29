package io.github.kedaitayar.mfm.ui.dashboard.transaction_trend_graph

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentTransactionTrendGraphBinding
import io.github.kedaitayar.mfm.ui.CustomCombinedChartRenderer
import io.github.kedaitayar.mfm.util.safeCollection


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
        binding.combinedChart.apply {
            renderer = CustomCombinedChartRenderer(
                binding.combinedChart,
                binding.combinedChart.animator,
                binding.combinedChart.viewPortHandler
            )
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

        transactionTrendGraphViewModel.transactionGraphCombinedData.safeCollection(
            viewLifecycleOwner
        ) {
            binding.combinedChart.apply {
                data = it
                notifyDataSetChanged()
                invalidate()
            }
        }
    }
}