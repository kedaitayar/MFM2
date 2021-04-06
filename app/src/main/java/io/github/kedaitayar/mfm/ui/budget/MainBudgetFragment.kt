package io.github.kedaitayar.mfm.ui.budget

import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMainBudgetBinding
import io.github.kedaitayar.mfm.ui.budget.budget_list.BudgetListFragment
import io.github.kedaitayar.mfm.ui.budget.not_budgeted.NotBudgetedFragment
import io.github.kedaitayar.mfm.ui.main.MainFragment
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections


@AndroidEntryPoint
class MainBudgetFragment : Fragment(R.layout.fragment_main_budget) {
    private val mainBudgetViewModel: MainBudgetViewModel by viewModels()
    private var _binding: FragmentMainBudgetBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBudgetBinding.bind(view)

        childFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_not_budgeted, NotBudgetedFragment())
            replace(R.id.fragment_container_date, MonthYearScrollFragment())
            replace(R.id.fragment_container_budget_list_type1, BudgetListFragment.newInstance(1))
            replace(R.id.fragment_container_budget_list_type2, BudgetListFragment.newInstance(2))
        }.commit()

        binding.buttonAddBudget.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddBudgetFragment()
            findNavController().navigate(action)
        }
        binding.buttonBudgeting.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToBudgetingFragment()
            findNavController().navigate(action)
        }

        setupReclaimUnusedBudgetButton()
        setupHideFABOnScroll()
    }

    private fun setupReclaimUnusedBudgetButton() {
        binding.buttonReclaimUnused.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Reclaim Unused Budget")
                .setMessage("Reclaim unused budget from previous month?")
                .setNegativeButton("Cancel") { dialog, which -> }
                .setPositiveButton("Ok") { dialog, which ->
                    mainBudgetViewModel.onReclaimUnusedBudgetClick()
                }.show()
        }
    }

    private fun setupHideFABOnScroll() {
        if (parentFragment is MainFragment) {
            val parentFragment = (parentFragment as MainFragment)
            binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY > oldScrollY + 12 && parentFragment.isFABShown()) {
                    parentFragment.hideFAB()
                }
                if (scrollY < oldScrollY - 12 && !parentFragment.isFABShown()) {
                    parentFragment.showFAB()
                }
                if (scrollY == 0) {
                    parentFragment.showFAB()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}