package io.github.kedaitayar.mfm.ui.transaction

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.kedaitayar.mfm.ui.account.MainAccountFragment
import io.github.kedaitayar.mfm.ui.budget.MainBudgetFragment
import io.github.kedaitayar.mfm.ui.main.ACCOUNT_PAGE_INDEX
import io.github.kedaitayar.mfm.ui.main.BUDGET_PAGE_INDEX
import io.github.kedaitayar.mfm.ui.main.TRANSACTION_PAGE_INDEX
import java.lang.IndexOutOfBoundsException

const val EXPENSE_PAGE_INDEX = 0
const val INCOME_PAGE_INDEX = 1
const val TRANSFER_PAGE_INDEX = 2

class AddTransactionPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val tabFragmentsCreator: Map<Int, () -> Fragment> = mapOf(
        EXPENSE_PAGE_INDEX to { ExpenseTransactionFragment() },
        INCOME_PAGE_INDEX to { IncomeTransactionFragment() },
        TRANSFER_PAGE_INDEX to { TransferTransactionFragment() }
    )

    override fun getItemCount(): Int {
        return tabFragmentsCreator.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreator[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}