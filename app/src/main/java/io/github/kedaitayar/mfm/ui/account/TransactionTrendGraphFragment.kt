package io.github.kedaitayar.mfm.ui.account

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.TransactionGraphData
import io.github.kedaitayar.mfm.databinding.FragmentTransactionTrendGraphBinding
import io.github.kedaitayar.mfm.viewmodels.TransactionViewModel
import java.time.OffsetDateTime
import java.time.temporal.WeekFields
import java.util.*

@AndroidEntryPoint
class TransactionTrendGraphFragment : Fragment(R.layout.fragment_transaction_trend_graph) {
    private val transactionViewModel: TransactionViewModel by viewModels()
    private var _binding: FragmentTransactionTrendGraphBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionTrendGraphBinding.inflate(inflater, container, false)
        context ?: return binding.root
        setupCombinedGraph()
        return binding.root
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
        transactionViewModel.transactionYearlyTrendGraph.observe(viewLifecycleOwner) {
            it?.let {
                updateGraphData(it)
            }
        }
    }

    private fun updateGraphData(transactionGraphDataList: List<TransactionGraphData>) {
        val barEntries = mutableListOf<BarEntry>()
        val lineEntries = mutableListOf<Entry>()
        val dataMapIncome = mutableMapOf<Int, TransactionGraphData>()
        val dataMapExpense = mutableMapOf<Int, TransactionGraphData>()
        val now = OffsetDateTime.now()
        val weekField = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()
        val weekNow = now.get(weekField)
        var total = 0.0

        for (item in transactionGraphDataList) {
            when (item.transactionType) {
                1 -> {
                    dataMapExpense[item.transactionWeek] = item
                }
                2 -> {
                    dataMapIncome[item.transactionWeek] = item
                }
            }
        }
        total = (dataMapIncome[-1]?.transactionAmountPrevYear ?: 0.0) - (dataMapExpense[-1]?.transactionAmountPrevYear
            ?: 0.0)

        for (week in 1 until 53) {
            barEntries.add(
                BarEntry(
                    week.toFloat(),
                    floatArrayOf(
                        dataMapIncome[week]?.transactionAmount?.toFloat() ?: 0f,
                        -(dataMapExpense[week]?.transactionAmount?.toFloat() ?: 0f)
                    )
                )
            )
            total = total + (dataMapIncome[week]?.transactionAmount ?: 0.0) - (dataMapExpense[week]?.transactionAmount
                ?: 0.0)
            if (week <= weekNow) {
                lineEntries.add(
                    Entry(
                        week.toFloat(),
                        total.toFloat()
                    )
                )
            }
        }
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.gGreen, typedValue, true)
        val green = ContextCompat.getColor(requireContext(), typedValue.resourceId)
        requireContext().theme.resolveAttribute(R.attr.gRed, typedValue, true)
        val red = ContextCompat.getColor(requireContext(), typedValue.resourceId)

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}