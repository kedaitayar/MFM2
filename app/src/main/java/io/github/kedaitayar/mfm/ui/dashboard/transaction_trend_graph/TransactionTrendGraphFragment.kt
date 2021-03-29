package io.github.kedaitayar.mfm.ui.dashboard.transaction_trend_graph

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentTransactionTrendGraphBinding

@AndroidEntryPoint
class TransactionTrendGraphFragment : Fragment(R.layout.fragment_transaction_trend_graph) {
    private val transactionTrendGraphViewModel: TransactionTrendGraphViewModel by viewModels()
    private var _binding: FragmentTransactionTrendGraphBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTransactionTrendGraphBinding.bind(view)
        setupCombinedGraph()
    }

    private fun setupCombinedGraph() {
        binding.combinedChart.apply {
            description.isEnabled = false
            drawOrder = arrayOf(
                CombinedChart.DrawOrder.BAR,
                CombinedChart.DrawOrder.BUBBLE,
                CombinedChart.DrawOrder.CANDLE,
                CombinedChart.DrawOrder.LINE,
                CombinedChart.DrawOrder.SCATTER
            )
            setDrawGridBackground(false)
            legend.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
            val colorOnSurface = ContextCompat.getColor(requireContext(), typedValue.resourceId)
            xAxis.textColor = colorOnSurface
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
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

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.gGreen, typedValue, true)
        val green = ContextCompat.getColor(requireContext(), typedValue.resourceId)
        requireContext().theme.resolveAttribute(R.attr.gRed, typedValue, true)
        val red = ContextCompat.getColor(requireContext(), typedValue.resourceId)

        // initial empty data
        val barEntries = mutableListOf<BarEntry>()
        val lineEntries = mutableListOf<Entry>()

        val barDataSet = BarDataSet(barEntries, "bardataset label")
        barDataSet.colors = arrayListOf(
            green,
            red
        )
        barDataSet.setDrawValues(false)
        val barData = BarData(barDataSet)

        requireContext().theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
        val colorOnSurface = ContextCompat.getColor(requireContext(), typedValue.resourceId)

        val lineDataSet = LineDataSet(lineEntries, "line label")
        lineDataSet.apply {
            setDrawValues(false)
            mode = LineDataSet.Mode.LINEAR
            color = colorOnSurface
            setCircleColor(colorOnSurface)
            lineWidth = 2f
            circleRadius = 2f
        }
        val lineData = LineData(lineDataSet)

        val combinedData = CombinedData().apply {
            setData(barData)
            setData(lineData)
            notifyDataChanged()
        }

        binding.combinedChart.apply {
            data = combinedData
            notifyDataSetChanged()
            invalidate()
        }

        //get data and update view
        transactionTrendGraphViewModel.transactionGraphBarEntries.observe(viewLifecycleOwner) {
            it?.let {
                barDataSet.values = it
                barData.clearValues()
                barData.addDataSet(barDataSet)
                binding.combinedChart.apply {
                    data.setData(barData)
                    notifyDataSetChanged()
                    postInvalidate()
                    animateX(500)
                }
            }
        }

        transactionTrendGraphViewModel.transactionGraphLineEntries.observe(viewLifecycleOwner) {
            it?.let {
                lineDataSet.values = it
                lineData.clearValues()
                lineData.addDataSet(lineDataSet)
                binding.combinedChart.apply {
                    data.setData(lineData)
                    notifyDataSetChanged()
                    postInvalidate()
                    animateX(500)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}