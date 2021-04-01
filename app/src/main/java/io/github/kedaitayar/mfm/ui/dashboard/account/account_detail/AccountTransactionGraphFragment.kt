package io.github.kedaitayar.mfm.ui.dashboard.account.account_detail

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.podata.AccountTransactionChartData
import io.github.kedaitayar.mfm.databinding.FragmentAccountTransactionGraphBinding
import io.github.kedaitayar.mfm.viewmodels.AccountViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

private const val ARG_ACCOUNT =
    "io.github.kedaitayar.mfm.ui.dashboard.account.account_detail.AccountTransactionGraphFragment.account"

@AndroidEntryPoint
class AccountTransactionGraphFragment : Fragment(R.layout.fragment_account_transaction_graph) {
    private val accountViewModel: AccountViewModel by viewModels()
    private val accountTransactionGraphViewModel: AccountTransactionGraphViewModel by viewModels()
    private var _binding: FragmentAccountTransactionGraphBinding? = null
    private val binding get() = _binding!!
    private var accountId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accountTransactionGraphViewModel.account.value = it.getParcelable(ARG_ACCOUNT) ?: Account(accountId = -1)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAccountTransactionGraphBinding.bind(view)
        setupCombinedGraph()
    }

    private fun setupCombinedGraph() {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.gGreen, typedValue, true)
        val green = ContextCompat.getColor(requireContext(), typedValue.resourceId)
        requireContext().theme.resolveAttribute(R.attr.gRed, typedValue, true)
        val red = ContextCompat.getColor(requireContext(), typedValue.resourceId)
        requireContext().theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
        val colorOnSurface = ContextCompat.getColor(requireContext(), typedValue.resourceId)

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
            xAxis.textColor = colorOnSurface
            xAxis.valueFormatter = object : ValueFormatter() {
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
            axisLeft.setDrawZeroLine(true)
            axisLeft.textColor = colorOnSurface
            axisRight.setDrawGridLines(false)
            axisRight.textColor = colorOnSurface
        }

        val barDataSet = BarDataSet(listOf<BarEntry>(), "bar label")
        barDataSet.colors = arrayListOf(
            green,
            red
        )
        barDataSet.setDrawValues(false)
        val barData = BarData(barDataSet)

        val lineDataSet = LineDataSet(listOf<Entry>(), "line label")
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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            accountTransactionGraphViewModel.accountTransactionGraphBarEntries.observe(viewLifecycleOwner) {
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
            accountTransactionGraphViewModel.accountTransactionGraphLineEntries.observe(viewLifecycleOwner) {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(account: Account) =
            AccountTransactionGraphFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ACCOUNT, account)
                }
            }
    }
}