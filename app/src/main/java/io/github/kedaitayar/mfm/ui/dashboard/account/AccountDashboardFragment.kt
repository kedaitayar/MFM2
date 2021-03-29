package io.github.kedaitayar.mfm.ui.dashboard.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentAccountDashboardBinding
import io.github.kedaitayar.mfm.ui.account.AccountListFragment
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import io.github.kedaitayar.mfm.viewmodels.SharedViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@AndroidEntryPoint
class AccountDashboardFragment : Fragment(R.layout.fragment_account_dashboard) {
    private val accountDashboardViewModel: AccountDashboardViewModel by viewModels()
    private var _binding: FragmentAccountDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAccountDashboardBinding.bind(view)

        childFragmentManager.beginTransaction().replace(R.id.account_list_fragment_container, AccountListFragment()).commit()

        setupAddAccountButton()
        setupDashboardInfo()
    }

    //TODO: extract string
    private fun setupDashboardInfo() {
        val resources = requireContext().resources
        val format = DecimalFormatSymbols().apply {
            groupingSeparator = ' '
        }
        val formatter = DecimalFormat(resources.getString(R.string.currency_format)).apply {
            decimalFormatSymbols = format
        }
        accountDashboardViewModel.thisMonthSpending.observe(viewLifecycleOwner) {
            it?.let {
                binding.textViewSpendingThisMonthAmount.text = resources.getString(R.string.currency_symbol, formatter.format(it))
            }
        }
        accountDashboardViewModel.nextMonthBudgeted.observe(viewLifecycleOwner) {
            it?.let {
                binding.textViewBudgetedNextMonthAmount.text = resources.getString(R.string.currency_symbol, formatter.format(it))
            }
        }
        accountDashboardViewModel.totalBudgetedAndGoal.observe(viewLifecycleOwner) {
            it?.let {
                binding.textViewUncompletedBudget.text = resources.getString(R.string.currency_symbol, formatter.format(it.budgetGoal - it.budgetTransactionAmount))
                binding.ringView.setRingProgress((it.budgetTransactionAmount / it.budgetGoal * 100).toInt())
            }
        }
        accountDashboardViewModel.notBudgetedAmount.observe(viewLifecycleOwner) {
            it?.let {
                binding.textViewNotBudgetedAmount.text = resources.getString(R.string.currency_symbol, formatter.format(it))
            }
        }
    }

    private fun setupAddAccountButton() {
        binding.buttonAddAccount.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddEditAccountFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}