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
import io.github.kedaitayar.mfm.databinding.FragmentAccountTransactionGraphBinding
import io.github.kedaitayar.mfm.viewmodels.AccountViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

private const val ARG_ACCOUNT_ID =
    "io.github.kedaitayar.mfm.ui.account.AccountTransactionGraphFragment.accountId"
private const val TAG = "AccountTransactionGraph"

@AndroidEntryPoint
class AccountTransactionGraphFragment : Fragment(R.layout.fragment_account_transaction_graph) {
    private val accountViewModel: AccountViewModel by viewModels()
    private var _binding: FragmentAccountTransactionGraphBinding? = null
    private val binding get() = _binding!!
    private var accountId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accountId = it.getLong(ARG_ACCOUNT_ID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountTransactionGraphBinding.inflate(inflater, container, false)
        context ?: return binding.root
        setupCombinedGraph()

        return binding.root
    }

    private fun setupCombinedGraph() {
        binding.combinedChart.apply {
            description.isEnabled = false
            drawOrder = arrayOf(CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER)
            setDrawGridBackground(false)
            legend.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
            xAxis.valueFormatter = object : ValueFormatter(){
                override fun getFormattedValue(value: Float): String {
                    return when (value) {
                        0f -> {
                            ""
                        }
                        else -> {
                            "day ${value.toInt()}"
                        }
                    }
                }
            }
            extraBottomOffset = 5f
            extraTopOffset = 5f
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)
            axisLeft.setDrawZeroLine(true)
        }

        val dataMap: MutableMap<Int, AccountTransactionChartData> = mutableMapOf()
        val barEntries = mutableListOf<BarEntry>()
        val lineEntries = mutableListOf<Entry>()
        dataMap.clear()
        CoroutineScope(Dispatchers.IO).launch {
            val accountTransactionChartData = accountViewModel.getAccountTransactionChartData(
                accountId,
                OffsetDateTime.now().monthValue,
                OffsetDateTime.now().year
            )
            Log.i(TAG, "setupCombinedGraph: ${accountTransactionChartData}")

            //map data to its day
            for (item in accountTransactionChartData) {
                dataMap[item.accountTransactionDate.substring(8).toInt()] = item
            }

            // for line graph
            var total =
                (accountTransactionChartData.firstOrNull()?.accountTransactionIncomePrevMonth ?: 0.0) -
                        (accountTransactionChartData.firstOrNull()?.accountTransactionExpensePrevMonth ?: 0.0) -
                        (accountTransactionChartData.firstOrNull()?.accountTransactionTransferOutPrevMonth ?: 0.0) +
                        (accountTransactionChartData.firstOrNull()?.accountTransactionTransferInPrevMonth ?: 0.0)

            val now = OffsetDateTime.now()
            // insert data to both entries according to its day
            for (day in 1 until OffsetDateTime.now().month.maxLength()) {
                var moneyIn: Double = (dataMap[day]?.accountTransactionIncome ?: 0.0) +
                        (dataMap[day]?.accountTransactionTransferIn ?: 0.0)
                var moneyOut: Double = (dataMap[day]?.accountTransactionExpense ?: 0.0) +
                        (dataMap[day]?.accountTransactionTransferOut ?: 0.0)
                barEntries.add(
                    BarEntry(
                        day - 1f,
                        floatArrayOf(moneyIn.toFloat(), -moneyOut.toFloat())
                    )
                )
                total += moneyIn
                total -= moneyOut
                if (day <= now.dayOfMonth) {
                    lineEntries.add(Entry(day - 1f, total.toFloat()))
                }
            }

            val barDataSet = BarDataSet(barEntries, "bar label")
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(accountId: Long) =
            AccountTransactionGraphFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ACCOUNT_ID, accountId)
                }
            }
    }
}