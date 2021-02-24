package io.github.kedaitayar.mfm.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMainBinding
import io.github.kedaitayar.mfm.databinding.FragmentMainBudgetBinding
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections

@AndroidEntryPoint
class MainBudgetFragment: Fragment(R.layout.fragment_main_budget) {
    private var _binding: FragmentMainBudgetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBudgetBinding.inflate(inflater, container, false)
        context ?: return binding.root

        childFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_date, MonthYearScrollFragment())
            replace(R.id.fragment_container_budget_list_type1, BudgetListFragment.newInstance(-1))
        }.commit()

        binding.buttonAddBudget.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddBudgetFragment()
            findNavController().navigate(action)
        }
        binding.buttonBudgeting.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToBudgetingFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}