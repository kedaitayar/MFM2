package io.github.kedaitayar.mfm.ui.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentAddTransactionBinding
import java.lang.ref.WeakReference

@AndroidEntryPoint
class AddTransactionFragment : Fragment(R.layout.fragment_add_transaction) {
//    private val transactionViewModel: TransactionViewModel by viewModels()
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    // to get the current viewpager2 fragment, solution get from - https://stackoverflow.com/a/57495777/12528485 (no idea if this is good solution)
    private var _currentPage: WeakReference<AddTransactionChild>? = null
    val currentPage get() = _currentPage?.get()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = AddTransactionPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        binding.buttonSave.setOnClickListener {
            currentPage?.onButtonAddClick()
        }

        return binding.root
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            EXPENSE_PAGE_INDEX -> "Expense"
            INCOME_PAGE_INDEX -> "Income"
            TRANSFER_PAGE_INDEX -> "Transfer"
            else -> null
        }
    }

    // to get the current viewpager2 fragment, solution get from - https://stackoverflow.com/a/57495777/12528485 (no idea if this is good solution)
    // to set the ref to current page
    fun setCurrentPage(page: AddTransactionChild) {
        _currentPage = WeakReference(page)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}