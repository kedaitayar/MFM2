package io.github.kedaitayar.mfm.ui.budget

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.BudgetTransaction
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentBudgetingBinding
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.viewmodels.BudgetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.format.TextStyle
import java.util.*

private const val TAG = "BudgetingFragment"

@AndroidEntryPoint
class BudgetingFragment : Fragment(R.layout.fragment_budgeting) {
    private val budgetViewModel: BudgetViewModel by viewModels()
    private var _binding: FragmentBudgetingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetingBinding.inflate(inflater, container, false)
        context ?: return binding.root
        val adapter1 = BudgetingListAdapter()
        val adapter2 = BudgetingListAdapter()
        setupToolbar(adapter1, adapter2)
        setupBudgetingListRecyclerView(adapter1, adapter2)

        return binding.root
    }

    private fun setupToolbar(monthlyAdapter: BudgetingListAdapter, yearlyAdapter: BudgetingListAdapter) {
        binding.topAppBar.inflateMenu(R.menu.menu_budgeting)
        binding.topAppBar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        binding.topAppBar.setNavigationOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.save_budgeting -> {
                    val monthlyBudgetingList = monthlyAdapter.currentList
                    val monthlyBudgetingAmountList = monthlyAdapter.getBudgetingAmountList()
                    val date = budgetViewModel.selectedDate.value!!
                    for ((index, budgeting) in monthlyBudgetingList.withIndex()) {
                        val budgetTransaction = BudgetTransaction(
                            budgetTransactionAmount = ((monthlyBudgetingAmountList[index])?.toDouble() ?: 0.0),
                            budgetTransactionBudgetId = budgeting.budgetId,
                            budgetTransactionMonth = date.monthValue,
                            budgetTransactionYear = date.year
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            budgetViewModel.insert(budgetTransaction)
                        }
                    }
                    val yearlyBudgetingList = yearlyAdapter.currentList
                    val yearlyBudgetingAmountList = yearlyAdapter.getBudgetingAmountList()
                    for ((index, budgeting) in yearlyBudgetingList.withIndex()) {
                        val budgetTransaction = BudgetTransaction(
                            budgetTransactionAmount = ((yearlyBudgetingAmountList[index])?.toDouble() ?: 0.0),
                            budgetTransactionBudgetId = budgeting.budgetId,
                            budgetTransactionMonth = date.monthValue,
                            budgetTransactionYear = date.year
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            budgetViewModel.insert(budgetTransaction)
                        }
                    }

                    hideKeyboard()
                    findNavController().navigateUp()
                    true
                }
                else -> {
                    false
                }
            }
        }

        budgetViewModel.selectedDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.i(TAG, "setupToolbar selectedDate: $it")
                binding.topAppBar.title = it.month.getDisplayName(
                    TextStyle.FULL,
                    Locale.ENGLISH
                ) + ", " + it.year    //TODO: extract string
            }
        })
    }

    private fun setupBudgetingListRecyclerView(
        monthlyAdapter: BudgetingListAdapter,
        yearlyAdapter: BudgetingListAdapter
    ) {
        binding.recyclerViewBudgeting.adapter = monthlyAdapter
        binding.recyclerViewBudgeting.layoutManager = LinearLayoutManager(requireContext())
        budgetViewModel.monthlyBudgetingListData.observe(viewLifecycleOwner) {
            it?.let {
                monthlyAdapter.submitList(it)
            }
        }

        binding.recyclerViewBudgeting2.adapter = yearlyAdapter
        binding.recyclerViewBudgeting2.layoutManager = LinearLayoutManager(requireContext())
        budgetViewModel.yearlyBudgetingListData.observe(viewLifecycleOwner) {
            it?.let {
                yearlyAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}