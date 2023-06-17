package io.github.kedaitayar.mfm.ui.dashboard.account.account_detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.databinding.FragmentAccountBudgetChartBinding
import io.github.kedaitayar.mfm.util.safeCollection

private const val ARG_ACCOUNT =
    "io.github.kedaitayar.mfm.ui.dashboard.account.account_detail.AccountBudgetChartFragment.account"

@AndroidEntryPoint
class AccountBudgetChartFragment : Fragment(R.layout.fragment_account_budget_chart) {
    private val accountBudgetChartViewModel: AccountBudgetChartViewModel by viewModels()
    private val binding: FragmentAccountBudgetChartBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accountBudgetChartViewModel.account.value =
                it.getParcelable(ARG_ACCOUNT) ?: Account(accountId = -1)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPieChartAndBudgetList()
    }

    private fun setupPieChartAndBudgetList() {
        val adapter = AccountBudgetChartListAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.pieChart.apply {
            setDrawEntryLabels(false)
//            holeRadius = 60f
            isDrawHoleEnabled = false
            setTouchEnabled(false)
            description.isEnabled = false
        }

        binding.pieChart.legend.apply {
            isEnabled = false
        }

        val colors = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)

        accountBudgetChartViewModel.pieEntries.safeCollection(viewLifecycleOwner) { list ->
            val dataSet = PieDataSet(list, "dataset label")
            dataSet.colors = colors
            val data = PieData(dataSet)
            data.setDrawValues(false)
            binding.pieChart.data = data
            binding.pieChart.notifyDataSetChanged()
            binding.pieChart.invalidate()
        }
        accountBudgetChartViewModel.totalTransactionAmount.safeCollection(viewLifecycleOwner) {
            adapter.setTotalTransactionAmount(it)
        }
        accountBudgetChartViewModel.accountTransactionBudgetFlow.safeCollection(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(account: Account) =
            AccountBudgetChartFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ACCOUNT, account)
                }
            }
    }
}