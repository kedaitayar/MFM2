package io.github.kedaitayar.mfm.ui.setting.quickTransaction.addEditQuickTransaction.commons

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentQuickTransactionIncomeBinding
import io.github.kedaitayar.mfm.ui.setting.quickTransaction.addEditQuickTransaction.AddEditQuickTransactionFragment
import io.github.kedaitayar.mfm.ui.setting.quickTransaction.addEditQuickTransaction.AddEditQuickTransactionViewModel
import io.github.kedaitayar.mfm.ui.setting.quickTransaction.addEditQuickTransaction.AddQuickTransactionChild
import io.github.kedaitayar.mfm.ui.setting.quickTransaction.utils.AccountListArrayAdapter
import io.github.kedaitayar.mfm.ui.setting.quickTransaction.utils.BudgetListArrayAdapter
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.AddEditTransactionViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class QuickTransactionIncomeFragment : Fragment(R.layout.fragment_quick_transaction_income),
                                       AddQuickTransactionChild {
    private val binding: FragmentQuickTransactionIncomeBinding by viewBinding()
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
            }
        }
    }

    override fun onButtonAddClick() {
        addEditQuickTransactionViewModel.onButtonAddClick(AddEditTransactionViewModel.TransactionType.INCOME)
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
            textInputEditAmount.setText(addEditQuickTransactionViewModel.inputAmount?.toString())
        }
    }
}