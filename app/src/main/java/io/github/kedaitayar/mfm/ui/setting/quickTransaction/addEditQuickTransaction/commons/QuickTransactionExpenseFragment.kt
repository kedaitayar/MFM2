package io.github.kedaitayar.mfm.ui.setting.quickTransaction.addEditQuickTransaction.commons

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.databinding.FragmentQuickTransactionExpenseBinding
import io.github.kedaitayar.mfm.ui.setting.quickTransaction.addEditQuickTransaction.AddEditQuickTransactionFragment
import io.github.kedaitayar.mfm.ui.setting.quickTransaction.addEditQuickTransaction.AddEditQuickTransactionViewModel
import io.github.kedaitayar.mfm.ui.setting.quickTransaction.addEditQuickTransaction.AddQuickTransactionChild
import io.github.kedaitayar.mfm.ui.setting.quickTransaction.utils.AccountListArrayAdapter
import io.github.kedaitayar.mfm.ui.setting.quickTransaction.utils.BudgetListArrayAdapter
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.AddEditTransactionViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuickTransactionExpenseFragment : Fragment(R.layout.fragment_quick_transaction_expense),
                                        AddQuickTransactionChild {
    private val binding: FragmentQuickTransactionExpenseBinding by viewBinding()
    private val addEditQuickTransactionViewModel: AddEditQuickTransactionViewModel by viewModels(
        ownerProducer = { requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            textInputEditName.addTextChangedListener {
                addEditQuickTransactionViewModel.inputName = it.toString()
            }
            autoCompleteAccount.setOnItemClickListener { parent, _, position, _ ->
                addEditQuickTransactionViewModel.inputAccountFrom =
                    parent.getItemAtPosition(position) as Account?
            }
            autoCompleteBudget.setOnItemClickListener { parent, _, position, _ ->
                addEditQuickTransactionViewModel.inputBudget =
                    parent.getItemAtPosition(position) as Budget?
            }
            textInputEditAmount.addTextChangedListener {
                addEditQuickTransactionViewModel.inputAmount = it.toString().toDoubleOrNull()
            }
            textInputEditNote.addTextChangedListener {
                addEditQuickTransactionViewModel.inputNote = it.toString()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    addEditQuickTransactionViewModel.allAccount.collect {
                        val adapter = AccountListArrayAdapter(
                            requireContext(),
                            R.layout.support_simple_spinner_dropdown_item,
                            it
                        )
                        binding.autoCompleteAccount.setAdapter(adapter)
                    }
                }
                launch {
                    addEditQuickTransactionViewModel.allBudget.collect {
                        val adapter = BudgetListArrayAdapter(
                            requireContext(),
                            R.layout.support_simple_spinner_dropdown_item,
                            it
                        )
                        binding.autoCompleteBudget.setAdapter(adapter)
                    }
                }
            }
        }
    }

    override fun onButtonAddClick() {
        addEditQuickTransactionViewModel.onButtonAddClick(AddEditTransactionViewModel.TransactionType.EXPENSE)
    }

    override fun onResume() {
        super.onResume()
        if (parentFragment is AddEditQuickTransactionFragment) {
            (parentFragment as AddEditQuickTransactionFragment).setCurrentPage(this)
        }
        binding.apply {
            textInputEditName.setText(addEditQuickTransactionViewModel.inputName)
            addEditQuickTransactionViewModel.inputAccountFrom?.let {
                autoCompleteAccount.setText(it.accountName, false)
            }
            addEditQuickTransactionViewModel.inputBudget?.let {
                autoCompleteBudget.setText(it.budgetName, false)
            }
            textInputEditAmount.setText(addEditQuickTransactionViewModel.inputAmount?.toString())
            textInputEditNote.setText(addEditQuickTransactionViewModel.inputNote ?: "")
        }
    }
}