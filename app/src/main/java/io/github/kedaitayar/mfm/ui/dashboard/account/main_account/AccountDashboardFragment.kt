package io.github.kedaitayar.mfm.ui.dashboard.account.main_account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentAccountDashboardBinding
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import io.github.kedaitayar.mfm.util.exhaustive
import io.github.kedaitayar.mfm.util.notNull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

private const val TAG = "AccountDashboardFragmen"

@AndroidEntryPoint
class AccountDashboardFragment : Fragment(R.layout.fragment_account_dashboard) {
    private val accountDashboardViewModel: AccountDashboardViewModel by viewModels()
    private val binding: FragmentAccountDashboardBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction().replace(R.id.account_list_fragment_container, AccountListFragment())
            .commit()

        setupDashboardInfo()
        setupAddAccountButtonListener()
        setupEventListener()
    }

    private fun setupEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                accountDashboardViewModel.accountDashboardEvent.collect { event ->
                    when (event) {
                        AccountDashboardViewModel.AccountDashboardEvent.NavigateToAddAccount -> {
                            val action = MainFragmentDirections.actionMainFragmentToAddEditAccountFragment()
                            findNavController().navigate(action)
                        }
                    }.exhaustive
                }
            }
        }
    }

    private fun setupDashboardInfo() {
        val resources = requireContext().resources
        val format = DecimalFormatSymbols().apply {
            groupingSeparator = ' '
        }
        val formatter = DecimalFormat(resources.getString(R.string.currency_format)).apply {
            decimalFormatSymbols = format
        }
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    accountDashboardViewModel.thisMonthSpending.collect {
                        binding.textViewSpendingThisMonthAmount.text =
                            resources.getString(R.string.currency_symbol, formatter.format(it.notNull()))
                    }
                }
                launch {
                    accountDashboardViewModel.totalBudgetedAndGoal.collect {
                        binding.textViewUncompletedBudget.text = resources.getString(
                            R.string.currency_symbol,
                            formatter.format(it.uncompletedGoal.notNull())
                        )
                        binding.ringView.setRingProgress(((it.budgetGoal.notNull() - it.uncompletedGoal.notNull()) / it.budgetGoal.notNull() * 100).toInt())
                    }
                }
                launch {
                    accountDashboardViewModel.nextMonthBudgeted.collect {
                        binding.textViewBudgetedNextMonthAmount.text =
                            resources.getString(R.string.currency_symbol, formatter.format(it.notNull()))
                    }
                }
                launch {
                    accountDashboardViewModel.notBudgetedAmount.collect {
                        binding.textViewNotBudgetedAmount.text =
                            resources.getString(R.string.currency_symbol, formatter.format(it))
                    }
                }
            }
        }
    }

    private fun setupAddAccountButtonListener() {
        binding.buttonAddAccount.setOnClickListener {
            accountDashboardViewModel.onAddNewAccountClick()
        }
    }
}