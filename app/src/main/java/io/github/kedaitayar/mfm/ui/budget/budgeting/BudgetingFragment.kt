package io.github.kedaitayar.mfm.ui.budget.budgeting

import android.os.Bundle
import android.text.Editable
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentBudgetingBinding
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import kotlinx.coroutines.flow.collect
import java.time.format.TextStyle
import java.util.*

@AndroidEntryPoint
class BudgetingFragment : Fragment(R.layout.fragment_budgeting), BudgetingListAdapter.OnBudgetingListAdapterListener {
    private val budgetingViewModel: BudgetingViewModel by viewModels()
    private var _binding: FragmentBudgetingBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBudgetingBinding.bind(view)
        val adapter1 = BudgetingListAdapter(this)
        val adapter2 = BudgetingListAdapter(this)
        setupEventListener()
        setupToolbar()
        setupBudgetingListRecyclerView(adapter1, adapter2)

        setupNotBudgeted()
    }

    private fun setupEventListener() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            budgetingViewModel.budgetingEvent.collect { event ->
                when (event) {
                    BudgetingViewModel.BudgetingEvent.NavigateBack -> {
                        hideKeyboard()
                        findNavController().navigateUp()
                    }
                    is BudgetingViewModel.BudgetingEvent.ShowSnackbar -> {
                        Snackbar.make(requireView(), event.msg, event.length)
//                            .setAnchorView(binding)
                            .show()
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.topAppBar.inflateMenu(R.menu.menu_budgeting)
        binding.topAppBar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        binding.topAppBar.setNavigationOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.save_budgeting -> {
                    budgetingViewModel.onSaveClick()
                    true
                }
                else -> {
                    false
                }
            }
        }

        budgetingViewModel.selectedDate.observe(viewLifecycleOwner) {
            it?.let {
                binding.topAppBar.title = it.month.getDisplayName(
                    TextStyle.FULL,
                    Locale.ENGLISH
                ) + ", " + it.year    //TODO: extract string
            }
        }
    }

    private fun setupBudgetingListRecyclerView(
        monthlyAdapter: BudgetingListAdapter,
        yearlyAdapter: BudgetingListAdapter
    ) {
        binding.recyclerViewBudgeting.adapter = monthlyAdapter
        binding.recyclerViewBudgeting.layoutManager = LinearLayoutManager(requireContext())
        budgetingViewModel.monthlyBudgetingListData.observe(viewLifecycleOwner) {
            it?.let {
                monthlyAdapter.submitList(it)
                budgetingViewModel.totalMonthlyBudgetedThisMonth = it.map { item -> item.budgetAllocation }.sum()
            }
        }

        binding.recyclerViewBudgeting2.adapter = yearlyAdapter
        binding.recyclerViewBudgeting2.layoutManager = LinearLayoutManager(requireContext())
        budgetingViewModel.yearlyBudgetingListData.observe(viewLifecycleOwner) {
            it?.let {
                yearlyAdapter.submitList(it)
                budgetingViewModel.totalYearlyBudgetedThisMonth = it.map { item -> item.budgetAllocation }.sum()
            }
        }
    }

    private fun updateNotBudgetedValue() {
        val budgetingAmountMonthly =
            budgetingViewModel.budgetingAmountListMonthly.map { it.value.toDoubleOrNull() ?: 0.0 }.sumOf { it }
        val budgetingAmountYearly =
            budgetingViewModel.budgetingAmountListYearly.map { it.value.toDoubleOrNull() ?: 0.0 }.sumOf { it }
        val totalUnbudgeted =
            (budgetingViewModel.totalNotBudgeted.value ?: 0.0) -
                    (budgetingAmountMonthly - budgetingViewModel.totalMonthlyBudgetedThisMonth) -
                    (budgetingAmountYearly - budgetingViewModel.totalYearlyBudgetedThisMonth)
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

    private fun setupNotBudgeted() {
        budgetingViewModel.totalNotBudgeted.observe(viewLifecycleOwner) {
            it?.let {
                updateNotBudgetedValue()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAfterTextChanged(item: BudgetListAdapterData, editable: Editable?) {
        when (item.budgetTypeId) {
            1L -> {
                budgetingViewModel.budgetingAmountListMonthly[item.budgetId] = editable.toString()
            }
            2L -> {
                budgetingViewModel.budgetingAmountListYearly[item.budgetId] = editable.toString()
            }
        }
        updateNotBudgetedValue()
    }
}