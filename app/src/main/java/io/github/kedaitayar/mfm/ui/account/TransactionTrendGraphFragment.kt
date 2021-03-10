package io.github.kedaitayar.mfm.ui.account

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import io.github.kedaitayar.mfm.data.podata.AccountTransactionChartData
import io.github.kedaitayar.mfm.data.podata.TransactionGraphData
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentTransactionTrendGraphBinding
import io.github.kedaitayar.mfm.viewmodels.TransactionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.roundToInt

private const val TAG = "TransactionTrendGraphFr"

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
            xAxis.valueFormatter = object : ValueFormatter(){
                override fun getFormattedValue(value: Float): String {
                    return "week ${value.toInt()}"
                }
            }
            extraBottomOffset = 5f
            extraTopOffset = 5f
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)
            axisLeft.setDrawZeroLine(true)
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
        total = (dataMapIncome[-1]?.transactionAmountPrevYear ?: 0.0) -(dataMapExpense[-1]?.transactionAmountPrevYear ?: 0.0)

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
            total = total + (dataMapIncome[week]?.transactionAmount ?: 0.0) - (dataMapExpense[week]?.transactionAmount ?: 0.0)
            if (week <= weekNow) {
                lineEntries.add(
                    Entry(
                        week.toFloat(),
                        total.toFloat()
                    )
                )
            }
        }

        val barDataSet = BarDataSet(barEntries, "bardataset label")
        barDataSet.colors = arrayListOf(
            ContextCompat.getColor(requireContext(), R.color.gGreen),
            ContextCompat.getColor(requireContext(), R.color.gRed)
        )
        barDataSet.setDrawValues(false)
        val barData = BarData(barDataSet)

        val lineDataSet = LineDataSet(lineEntries, "line label")
        lineDataSet.apply {
            setDrawValues(false)
            mode = LineDataSet.Mode.LINEAR
            color = Color.DKGRAY
            setCircleColor(Color.DKGRAY)
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