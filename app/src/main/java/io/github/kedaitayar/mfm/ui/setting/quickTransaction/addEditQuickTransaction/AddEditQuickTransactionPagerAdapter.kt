package io.github.kedaitayar.mfm.ui.setting.quickTransaction.addEditQuickTransaction

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.kedaitayar.mfm.ui.setting.quickTransaction.addEditQuickTransaction.commons.QuickTransactionExpenseFragment
import io.github.kedaitayar.mfm.ui.setting.quickTransaction.addEditQuickTransaction.commons.QuickTransactionIncomeFragment
import io.github.kedaitayar.mfm.ui.setting.quickTransaction.addEditQuickTransaction.commons.QuickTransactionTransferFragment
import java.lang.IndexOutOfBoundsException

class AddEditQuickTransactionPagerAdapter(val fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val tabFragmentCreator: Map<Int, () -> Fragment> = mapOf(
        QuickTransactionTabType.EXPENSE_PAGE_INDEX.index to { QuickTransactionExpenseFragment() },
        QuickTransactionTabType.INCOME_PAGE_INDEX.index to { QuickTransactionIncomeFragment() },
        QuickTransactionTabType.TRANSFER_PAGE_INDEX.index to { QuickTransactionTransferFragment() },
    )

    override fun getItemCount(): Int {
        return tabFragmentCreator.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabFragmentCreator[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}

enum class QuickTransactionTabType(val index: Int) {
    EXPENSE_PAGE_INDEX(0), INCOME_PAGE_INDEX(1), TRANSFER_PAGE_INDEX(2)
}