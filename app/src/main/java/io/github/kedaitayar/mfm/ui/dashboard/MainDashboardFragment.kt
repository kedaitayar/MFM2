package io.github.kedaitayar.mfm.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMainDashboardBinding
import io.github.kedaitayar.mfm.ui.dashboard.account.main_account.AccountDashboardFragment
import io.github.kedaitayar.mfm.ui.dashboard.budget_transaction_list.BudgetTransactionListFragment
import io.github.kedaitayar.mfm.ui.dashboard.spending.MonthlySpendingFragment
import io.github.kedaitayar.mfm.ui.dashboard.transaction_trend_graph.TransactionTrendGraphFragment
import io.github.kedaitayar.mfm.ui.main.MainFragment

@AndroidEntryPoint
class MainDashboardFragment : Fragment(R.layout.fragment_main_dashboard) {
    private val binding: FragmentMainDashboardBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_main_account, AccountDashboardFragment())
            replace(R.id.fragment_container_transaction_trend, TransactionTrendGraphFragment())
            replace(R.id.fragment_container_monthly_spending, MonthlySpendingFragment())
            replace(R.id.fragment_container_budget_detail, BudgetTransactionListFragment())
        }.commit()

        setupHideFABOnScroll()
    }

    private fun setupHideFABOnScroll() {
        if (parentFragment is MainFragment) {
            val parentFragment = (parentFragment as MainFragment)
            binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY > oldScrollY + 12 && parentFragment.isFABShown()) {
                    parentFragment.hideFAB()
                }
                if (scrollY < oldScrollY - 12 && !parentFragment.isFABShown()) {
                    parentFragment.showFAB()
                }
                if (scrollY == 0) {
                    parentFragment.showFAB()
                }
            })
        }
    }
}