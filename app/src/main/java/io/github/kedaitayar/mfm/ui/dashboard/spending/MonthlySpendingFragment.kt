package io.github.kedaitayar.mfm.ui.dashboard.spending

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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMonthlySpendingBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.OffsetDateTime
import java.util.*

@AndroidEntryPoint
class MonthlySpendingFragment : Fragment(R.layout.fragment_monthly_spending) {
    private val binding: FragmentMonthlySpendingBinding by viewBinding()
    private val monthlySpendingViewModel: MonthlySpendingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                monthlySpendingViewModel.monthlySpendingGraph.collect {
                    binding.barChart.apply {
                        val barDataSet = BarDataSet(it, "bardataset label")
                        val typedValue = TypedValue()
                        requireContext().theme.resolveAttribute(
                            R.attr.colorPrimary,
                            typedValue,
                            true
                        )
                        barDataSet.color = typedValue.data
                        requireContext().theme.resolveAttribute(
                            R.attr.colorOnSurface,
                            typedValue,
                            true
                        )
                        val colorOnSurface =
                            ContextCompat.getColor(requireContext(), typedValue.resourceId)
                        barDataSet.valueTextSize = 10f
                        barDataSet.valueTextColor = colorOnSurface
                        val barData = BarData(barDataSet)
                        setTouchEnabled(false)
                        description.isEnabled = false
                        setDrawGridBackground(false)
                        legend.isEnabled = false
                        xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
                        xAxis.setDrawGridLines(false)
                        xAxis.textColor = colorOnSurface
                        val monthsMap = mutableMapOf<Int, String>(
                            1 to "Jan",
                            2 to "Feb",
                            3 to "Mar",
                            4 to "Apr",
                            5 to "May",
                            6 to "Jun",
                            7 to "Jul",
                            8 to "Aug",
                            9 to "Sep",
                            10 to "Oct",
                            11 to "Nov",
                            12 to "Dec"
                        )
                        val xAxisLabel = mutableListOf<String>()
                        for (index in 0..11) {
                            xAxisLabel.add(
                                index,
                                monthsMap[OffsetDateTime.now()
                                    .minusMonths(index.toLong()).monthValue] ?: "error"
                            )
                        }
                        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)
                        axisLeft.setDrawGridLines(false)
                        axisLeft.setDrawZeroLine(true)
                        axisLeft.textColor = colorOnSurface
                        axisRight.setDrawGridLines(false)
                        axisRight.textColor = colorOnSurface

                        data = barData
                        notifyDataSetChanged()
                        invalidate()
                    }
                }
            }
        }
    }
}