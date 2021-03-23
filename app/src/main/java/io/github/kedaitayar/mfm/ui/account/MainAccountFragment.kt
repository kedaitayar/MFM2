package io.github.kedaitayar.mfm.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMainAccountBinding
import io.github.kedaitayar.mfm.ui.main.MainFragment
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import io.github.kedaitayar.mfm.viewmodels.AccountViewModel
import io.github.kedaitayar.mfm.viewmodels.SharedViewModel

@AndroidEntryPoint
class MainAccountFragment : Fragment(R.layout.fragment_main_account) {
    private val accountViewModel: AccountViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentMainAccountBinding? = null
    private val binding get() = _binding!!
    private var totalIncome: Double = 0.0
    private var totalBudgeted: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainAccountBinding.inflate(inflater, container, false)
        context ?: return binding.root

        childFragmentManager.beginTransaction().apply {
            replace(R.id.account_list_fragment_container, AccountListFragment())
            replace(R.id.fragment_container_transaction_trend, TransactionTrendGraphFragment())
        }.commit()

        setupAddAccountButton()
        setupDashboardInfo()
        setupHideFABOnScroll()

        return binding.root
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

    //TODO: extract string
    private fun setupDashboardInfo() {
        accountViewModel.thisMonthSpending.observe(viewLifecycleOwner) {
            it?.let {
                binding.textViewSpendingThisMonthAmount.text = "RM $it"
            }
        }
        accountViewModel.nextMonthBudgeted.observe(viewLifecycleOwner) {
            it?.let {
                binding.textViewBudgetedNextMonthAmount.text = "RM $it"
            }
        }
        accountViewModel.totalBudgetedAndGoal.observe(viewLifecycleOwner) {
            it?.let {
                binding.textViewUncompletedBudget.text =
                    "RM ${it.budgetGoal - it.budgetTransactionAmount}"
                binding.ringView.setRingProgress((it.budgetTransactionAmount / it.budgetGoal * 100).toInt())
            }
        }
        accountViewModel.totalIncome.observe(viewLifecycleOwner, Observer {
            it?.let {
                setAccountIncome(it)
            }
        })
        accountViewModel.totalBudgetedAmount.observe(viewLifecycleOwner, Observer {
            it?.let {
                setTotalBudgeted(it)
            }
        })
    }

    private fun setupAddAccountButton() {
        binding.buttonAddAccount.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddEditAccountFragment()
            findNavController().navigate(action)
        }
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