package io.github.kedaitayar.mfm.ui.budget.budget_detail

import android.os.Bundle
import android.view.View
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.databinding.FragmentBudgetDetailBinding
import io.github.kedaitayar.mfm.util.dpToPx
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class BudgetDetailFragment : Fragment(R.layout.fragment_budget_detail) {
    private val binding: FragmentBudgetDetailBinding by viewBinding()
    private val budgetDetailViewModel: BudgetDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                budgetDetailViewModel.budgets.collect {
                    binding.chipGroupBudgetFilter.removeAllViews()
                    it.forEach { budget: Budget ->
                        val chip = Chip(requireContext())
                        chip.text = budget.budgetName
                        chip.isCheckedIconVisible = true
                        chip.isCheckable = true
                        chip.tag = budget
//                        chip.chipStartPadding = requireContext().dpToPx(16f).toFloat()
//                        chip.chipEndPadding = requireContext().dpToPx(16f).toFloat()
                        chip.ensureAccessibleTouchTarget(requireContext().dpToPx(40f))
                        chip.setOnCheckedChangeListener { compoundButton, b ->
                            Timber.i("${(compoundButton.tag as Budget).budgetName}: $b")
                        }
                        binding.chipGroupBudgetFilter.addView(chip)
                    }
                }
            }
        }
    }
}