package io.github.kedaitayar.mfm.ui.budget.budget_detail

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentBudgetDetailBinding
import io.github.kedaitayar.mfm.ui.CustomBarChartRenderer
import io.github.kedaitayar.mfm.ui.transaction.transaction_list.TransactionListAdapter
import io.github.kedaitayar.mfm.util.safeCollection
import java.time.OffsetDateTime

@AndroidEntryPoint
class BudgetDetailFragment : Fragment(R.layout.fragment_budget_detail) {
    private val budgetDetailViewModel: BudgetDetailViewModel by viewModels()
    private val binding: FragmentBudgetDetailBinding by viewBinding()
    private val args: BudgetDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTopAppBar()
        setupMonthlyBudgetSpending()
        setupTransactionList()
    }

    private fun setupTopAppBar() {
        binding.topAppBar.apply {
            setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            title = args.budgetName
        }
    }

    private fun setupTransactionList() {
        val adapter = TransactionListAdapter()
        binding.apply {
            recyclerViewTransactionList.adapter = adapter
            recyclerViewTransactionList.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewTransactionList.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        budgetDetailViewModel.transactionListByBudget.safeCollection(viewLifecycleOwner) {
            adapter.submitData(it)
        }
    }

    private fun setupMonthlyBudgetSpending() {
        budgetDetailViewModel.monthlyBudgetSpendingGraph.safeCollection(viewLifecycleOwner) {
            binding.barChart.apply {
                binding.barChart.renderer = CustomBarChartRenderer(
                    binding.barChart,
                    binding.barChart.animator,
                    binding.barChart.viewPortHandler,
                    8f
                )
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