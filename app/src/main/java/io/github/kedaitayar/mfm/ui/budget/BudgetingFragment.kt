package io.github.kedaitayar.mfm.ui.budget

import android.os.Bundle
import android.text.Editable
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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

@AndroidEntryPoint
class BudgetingFragment : Fragment(R.layout.fragment_budgeting), BudgetingListAdapter.OnBudgetingListAdapterListener {
    private val budgetViewModel: BudgetViewModel by viewModels()
    private var _binding: FragmentBudgetingBinding? = null
    private val binding get() = _binding!!
    private var totalIncome: Double = 0.0
    private var totalBudgeted: Double = 0.0
    private var totalMonthlyBudgetedThisMonth: Double = 0.0
    private var totalYearlyBudgetedThisMonth: Double = 0.0
    private var budgetingAmountListMonthly = mutableMapOf<Long, String>()
    private var budgetingAmountListYearly = mutableMapOf<Long, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetingBinding.inflate(inflater, container, false)
        context ?: return binding.root
        val adapter1 = BudgetingListAdapter(this)
        val adapter2 = BudgetingListAdapter(this)
        setupToolbar(adapter1, adapter2)
        setupBudgetingListRecyclerView(adapter1, adapter2)

//        setupNotBudgeted()

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
                    val date = budgetViewModel.selectedDate.value!!
                    for (budgeting in monthlyBudgetingList) {
                        val budgetTransaction = BudgetTransaction(
                            budgetTransactionAmount = ((budgetingAmountListMonthly[budgeting.budgetId])?.toDouble()
                                ?: 0.0),
                            budgetTransactionBudgetId = budgeting.budgetId,
                            budgetTransactionMonth = date.monthValue,
                            budgetTransactionYear = date.year
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            budgetViewModel.insert(budgetTransaction)
                        }
                    }
                    val yearlyBudgetingList = yearlyAdapter.currentList
                    for (budgeting in yearlyBudgetingList) {
                        val budgetTransaction = BudgetTransaction(
                            budgetTransactionAmount = ((budgetingAmountListYearly[budgeting.budgetId])?.toDouble()
                                ?: 0.0),
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
                totalMonthlyBudgetedThisMonth = it.map { item -> item.budgetAllocation }.sum()
                monthlyAdapter.submitTotalBudgetedThisMonth(totalMonthlyBudgetedThisMonth + totalYearlyBudgetedThisMonth)
            }
        }

        binding.recyclerViewBudgeting2.adapter = yearlyAdapter
        binding.recyclerViewBudgeting2.layoutManager = LinearLayoutManager(requireContext())
        budgetViewModel.yearlyBudgetingListData.observe(viewLifecycleOwner) {
            it?.let {
                yearlyAdapter.submitList(it)
                totalYearlyBudgetedThisMonth = it.map { item -> item.budgetAllocation }.sum()
                monthlyAdapter.submitTotalBudgetedThisMonth(totalMonthlyBudgetedThisMonth + totalYearlyBudgetedThisMonth)
            }
        }
    }

//    private fun setupNotBudgeted() {
//        budgetViewModel.totalIncome.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                setAccountIncome(it)
//            }
//        })
//        budgetViewModel.totalBudgetedAmount.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                setTotalBudgeted(it)
//            }
//        })
//    }

    private fun setAccountIncome(totalIncome: Double) {
        this.totalIncome = totalIncome
        binding.textViewNotBudgetedAmount.text = "RM " + (this.totalIncome - this.totalBudgeted).toString()
    }

    private fun setTotalBudgeted(totalBudgeted: Double) {
        this.totalBudgeted = totalBudgeted
        binding.textViewNotBudgetedAmount.text = "RM " + (this.totalIncome - this.totalBudgeted).toString()
    }

    private fun updateNotBudgetedValue() {
        val budgetingAmountMonthly =
            budgetingAmountListMonthly.map { it.value.toDoubleOrNull() ?: 0.0 }.sumByDouble { it }
        val budgetingAmountYearly =
            budgetingAmountListYearly.map { it.value.toDoubleOrNull() ?: 0.0 }.sumByDouble { it }
        val totalUnbudgeted =
            totalIncome - totalBudgeted - (budgetingAmountMonthly - totalMonthlyBudgetedThisMonth) - (budgetingAmountYearly - totalYearlyBudgetedThisMonth)
        binding.textViewNotBudgetedAmount.text = "RM $totalUnbudgeted"

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.gGreen, typedValue, true)
        val green = ContextCompat.getColor(requireContext(), typedValue.resourceId)
        requireContext().theme.resolveAttribute(R.attr.gRed, typedValue, true)
        val red = ContextCompat.getColor(requireContext(), typedValue.resourceId)

        if (totalUnbudgeted < 0) {
            binding.constraintLayoutNotBudgeted.setBackgroundColor(red)
        } else {
            binding.constraintLayoutNotBudgeted.setBackgroundColor(green)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAfterTextChanged(item: BudgetListAdapterData, editable: Editable?) {
        when (item.budgetTypeId) {
            1L -> {
                budgetingAmountListMonthly[item.budgetId] = editable.toString()
            }
            2L -> {
                budgetingAmountListYearly[item.budgetId] = editable.toString()
            }
        }
        updateNotBudgetedValue()
    }
}