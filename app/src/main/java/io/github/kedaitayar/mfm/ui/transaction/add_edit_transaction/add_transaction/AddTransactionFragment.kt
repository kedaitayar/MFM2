package io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentAddTransactionBinding
import io.github.kedaitayar.mfm.ui.main.MainViewModel
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.AddEditTransactionViewModel
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.util.exhaustive
import io.github.kedaitayar.mfm.util.themeColor
import kotlinx.coroutines.flow.collect
import java.lang.ref.WeakReference

@AndroidEntryPoint
class AddTransactionFragment : Fragment(R.layout.fragment_add_transaction) {
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val addEditTransactionViewModel: AddEditTransactionViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    // to get the current viewpager2 fragment, solution get from - https://stackoverflow.com/a/57495777/12528485 (no idea if this is good solution)
    private var _currentPage: WeakReference<AddTransactionChild>? = null
    private val currentPage get() = _currentPage?.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
            .apply {
                drawingViewId = R.id.nav_host
                duration = 300
                scrimColor = Color.TRANSPARENT
                setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
                interpolator = FastOutSlowInInterpolator()
                fadeMode = MaterialContainerTransform.FADE_MODE_IN
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddTransactionBinding.bind(view)

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = AddTransactionPagerAdapter(this)

        binding.topAppBar.apply {
            setNavigationIcon(R.drawable.ic_baseline_close_24)
            setNavigationOnClickListener {
                hideKeyboard()
//                findNavController().navigateUp()
                requireActivity().onBackPressed()
            }
            title = "Add Transaction"
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        binding.buttonSave.setOnClickListener {
            currentPage?.onButtonAddClick()
        }
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
//                        findNavController().navigateUp()
                        requireActivity().onBackPressed()
                    }
                    is AddEditTransactionViewModel.AddEditTransactionEvent.NavigateBackWithDeleteResult -> {
                        if (event.result == 1) {
                            mainViewModel.showSnackbar("Transaction deleted", Snackbar.LENGTH_SHORT)
                        } else {
                            mainViewModel.showSnackbar("Transaction delete failed", Snackbar.LENGTH_SHORT)
                        }
                        hideKeyboard()
//                        findNavController().navigateUp()
                        requireActivity().onBackPressed()
                    }
                    is AddEditTransactionViewModel.AddEditTransactionEvent.NavigateBackWithEditResult -> {
                        if (event.result == 1) {
                            mainViewModel.showSnackbar("Transaction updated", Snackbar.LENGTH_SHORT)
                        } else {
                            mainViewModel.showSnackbar("Transaction update failed", Snackbar.LENGTH_SHORT)
                        }
                        hideKeyboard()
//                        findNavController().navigateUp()
                        requireActivity().onBackPressed()
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