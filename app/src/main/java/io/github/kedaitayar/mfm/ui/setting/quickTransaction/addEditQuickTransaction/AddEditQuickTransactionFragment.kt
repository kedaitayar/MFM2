package io.github.kedaitayar.mfm.ui.setting.quickTransaction.addEditQuickTransaction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentAddEditQuickTransactionBinding
import io.github.kedaitayar.mfm.ui.main.MainViewModel
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.EXPENSE_PAGE_INDEX
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.INCOME_PAGE_INDEX
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.TRANSFER_PAGE_INDEX
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.util.safeCollection
import java.lang.ref.WeakReference

@AndroidEntryPoint
class AddEditQuickTransactionFragment : Fragment(R.layout.fragment_add_edit_quick_transaction) {
    private val binding: FragmentAddEditQuickTransactionBinding by viewBinding()
    private val addEditQuickTransactionViewModel: AddEditQuickTransactionViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    // to get the current viewpager2 fragment, solution get from - https://stackoverflow.com/a/57495777/12528485 (no idea if this is good solution)
    private var _currentPage: WeakReference<AddQuickTransactionChild>? = null
    private val currentPage get() = _currentPage?.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTopBar()
        setupViewPagerAdapter()
        setupButton()
        setupEventListener()
    }

    private fun setupButton() {
        addEditQuickTransactionViewModel.transaction?.let {
            binding.buttonSave.text = "Save"
        }
        binding.buttonSave.setOnClickListener {
            currentPage?.onButtonAddClick()
        }
    }

    private fun setupEventListener() {
        addEditQuickTransactionViewModel.addEditQuickTransactionEvent.safeCollection(
            viewLifecycleOwner
        ) {
            when (it) {
                is AddEditQuickTransactionEvent.NavigateBackWithAddResult -> {
                    if (it.result > 0L) {
                        mainViewModel.showSnackbar(
                            "Quick Transaction added",
                            Snackbar.LENGTH_SHORT
                        )
                    } else {
                        mainViewModel.showSnackbar(
                            "Quick Transaction add failed",
                            Snackbar.LENGTH_SHORT
                        )
                    }
                    hideKeyboard()
                    requireActivity().onBackPressed()
                }

                is AddEditQuickTransactionEvent.NavigateBackWithDeleteResult -> {
                    if (it.result > 0) {
                        mainViewModel.showSnackbar(
                            "Quick Transaction deleted",
                            Snackbar.LENGTH_SHORT
                        )
                    } else {
                        mainViewModel.showSnackbar(
                            "Quick Transaction delete failed",
                            Snackbar.LENGTH_SHORT
                        )
                    }
                    hideKeyboard()
                    requireActivity().onBackPressed()
                }

                is AddEditQuickTransactionEvent.NavigateBackWithEditResult -> {
                    if (it.result > 0) {
                        mainViewModel.showSnackbar(
                            "Quick Transaction added",
                            Snackbar.LENGTH_SHORT
                        )
                    } else {
                        mainViewModel.showSnackbar(
                            "Quick Transaction add failed",
                            Snackbar.LENGTH_SHORT
                        )
                    }
                    hideKeyboard()
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    private fun setupViewPagerAdapter() {
        binding.apply {
            viewPager.adapter =
                AddEditQuickTransactionPagerAdapter(this@AddEditQuickTransactionFragment)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = getTabTitle(position)
            }.attach()
        }
        addEditQuickTransactionViewModel.transaction?.let {
            binding.viewPager.post {
                binding.viewPager.setCurrentItem(it.transactionType - 1, false)
            }
        }
    }

    private fun setupTopBar() {
        binding.topAppBar.apply {
            inflateMenu(R.menu.menu_delete)
            setNavigationIcon(R.drawable.ic_baseline_close_24)
            setNavigationOnClickListener {
                hideKeyboard()
                requireActivity().onBackPressed()
            }
            title =
                if (addEditQuickTransactionViewModel.transaction == null) "Add Quick Transaction" else "Edit Quick Transaction"
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage("Delete quick transaction?")
                            .setNegativeButton("Cancel") { dialog, which -> }
                            .setPositiveButton("Delete") { dialog, which ->
                                addEditQuickTransactionViewModel.onDelete()
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
    fun setCurrentPage(page: AddQuickTransactionChild) {
        _currentPage = WeakReference(page)
    }
}
