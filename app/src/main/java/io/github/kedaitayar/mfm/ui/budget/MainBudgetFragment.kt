package io.github.kedaitayar.mfm.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMainBinding
import io.github.kedaitayar.mfm.databinding.FragmentMainBudgetBinding
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import io.github.kedaitayar.mfm.viewmodels.BudgetViewModel

@AndroidEntryPoint
class MainBudgetFragment: Fragment(R.layout.fragment_main_budget) {
    private val budgetViewModel: BudgetViewModel by viewModels()
    private var _binding: FragmentMainBudgetBinding? = null
    private val binding get() = _binding!!
    private var totalIncome: Double = 0.0
    private var totalBudgeted: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBudgetBinding.inflate(inflater, container, false)
        context ?: return binding.root

        childFragmentManager.beginTransaction().apply {
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

        setupNotBudgeted()

        return binding.root
    }

    private fun setupNotBudgeted(){
        budgetViewModel.totalIncome.observe(viewLifecycleOwner, Observer {
            it?.let {
                setAccountIncome(it)
            }
        })
        budgetViewModel.totalBudgetedAmount.observe(viewLifecycleOwner, Observer {
            it?.let {
                setTotalBudgeted(it)
            }
        })
    }

    private fun setAccountIncome(totalIncome: Double) {
        this.totalIncome = totalIncome
        binding.textViewNotBudgetedAmount.text = "RM " + (this.totalIncome - this.totalBudgeted).toString()
    }

    private fun setTotalBudgeted(totalBudgeted: Double) {
        this.totalBudgeted = totalBudgeted
        binding.textViewNotBudgetedAmount.text = "RM " + (this.totalIncome - this.totalBudgeted).toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}