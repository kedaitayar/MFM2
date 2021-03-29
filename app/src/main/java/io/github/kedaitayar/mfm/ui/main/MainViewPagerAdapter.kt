package io.github.kedaitayar.mfm.ui.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.kedaitayar.mfm.ui.budget.MainBudgetFragment
import io.github.kedaitayar.mfm.ui.dashboard.MainDashboardFragment
import io.github.kedaitayar.mfm.ui.transaction.MainTransactionFragment
import java.lang.IndexOutOfBoundsException

const val DASHBOARD_PAGE_INDEX = 0
const val TRANSACTION_PAGE_INDEX = 1
const val BUDGET_PAGE_INDEX = 2

class MainViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val tabFragmentsCreator: Map<Int, () -> Fragment> = mapOf(
        DASHBOARD_PAGE_INDEX to { MainDashboardFragment() },
        TRANSACTION_PAGE_INDEX to {MainTransactionFragment()},
        BUDGET_PAGE_INDEX to {MainBudgetFragment()}
    )

    override fun getItemCount(): Int {
        return tabFragmentsCreator.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreator[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}