package io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.edit_transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentEditTransactionBinding
import io.github.kedaitayar.mfm.ui.BlankFragment
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.ui.main.MainViewModel
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.AddEditTransactionViewModel
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.common.ExpenseTransactionFragment
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.common.IncomeTransactionFragment
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.common.TransferTransactionFragment
import io.github.kedaitayar.mfm.util.exhaustive
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class EditTransactionFragment : Fragment(R.layout.fragment_edit_transaction) {
    private val addEditTransactionViewModel: AddEditTransactionViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentEditTransactionBinding? = null
    private val binding get() = _binding!!
    private val args: EditTransactionFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addEditTransactionViewModel.transaction  // need this to instantiate transaction i think, if not, the transaction will be null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditTransactionBinding.bind(view)
        setupToolbar()
        setupFragment()
        setupEventListener()
    }

    private fun setupEventListener() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            addEditTransactionViewModel.addEditTransactionEvent.collect { event: AddEditTransactionViewModel.AddEditTransactionEvent ->
                when (event) {
                    is AddEditTransactionViewModel.AddEditTransactionEvent.NavigateBackWithAddResult -> {
                        if (event.result > 0L) {
                            mainViewModel.showSnackbar("Transaction added", Snackbar.LENGTH_SHORT)
                        } else {
                            mainViewModel.showSnackbar("Transaction add failed", Snackbar.LENGTH_SHORT)
                        }
                        hideKeyboard()
                        findNavController().navigateUp()
                    }
                    is AddEditTransactionViewModel.AddEditTransactionEvent.NavigateBackWithDeleteResult -> {
                        if (event.result == 1) {
                            mainViewModel.showSnackbar("Transaction deleted", Snackbar.LENGTH_SHORT)
                        } else {
                            mainViewModel.showSnackbar("Transaction delete failed", Snackbar.LENGTH_SHORT)
                        }
                        hideKeyboard()
                        findNavController().navigateUp()
                    }
                    is AddEditTransactionViewModel.AddEditTransactionEvent.NavigateBackWithEditResult -> {
                        if (event.result == 1) {
                            mainViewModel.showSnackbar("Transaction updated", Snackbar.LENGTH_SHORT)
                        } else {
                            mainViewModel.showSnackbar("Transaction update failed", Snackbar.LENGTH_SHORT)
                        }
                        hideKeyboard()
                        findNavController().navigateUp()
                    }
                    is AddEditTransactionViewModel.AddEditTransactionEvent.ShowSnackbar -> {
                        Snackbar.make(requireView(), event.msg, event.length)
                            .setAnchorView(binding.buttonSave)
                            .show()
                    }
                }.exhaustive
            }
        }
    }

    private fun setupFragment() {
        val fragment = when (args.transaction.transactionType) {
            1 -> {
                ExpenseTransactionFragment.newInstance(args.transaction)
            }
            2 -> {
                IncomeTransactionFragment.newInstance(args.transaction)
            }
            3 -> {
                TransferTransactionFragment.newInstance(args.transaction)
            }
            else -> {
//                Fragment()
                BlankFragment()
            }
        }
        childFragmentManager.beginTransaction().replace(R.id.fragment_container_edit_transaction, fragment).commit()
        binding.buttonSave.setOnClickListener {
            if (fragment is EditTransactionChild) {
                (fragment as EditTransactionChild).onButtonSaveClick()
            }
        }
    }

    private fun setupToolbar() {
        binding.topAppBar.apply {
            inflateMenu(R.menu.menu_delete)
            setNavigationIcon(R.drawable.ic_baseline_close_24)
            setNavigationOnClickListener {
                hideKeyboard()
                findNavController().navigateUp()
            }
            title = "Edit Transaction"
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage("Delete transaction?")
                            .setNegativeButton("Cancel") { dialog, which -> }
                            .setPositiveButton("Delete") { dialog, which ->
                                addEditTransactionViewModel.onDeleteClick()
                            }.show()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }
    }

    @Deprecated("use MainViewModel.showSnackbar()")
    fun showSnackbar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.buttonSave)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}