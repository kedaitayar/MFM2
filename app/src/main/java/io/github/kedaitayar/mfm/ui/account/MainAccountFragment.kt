package io.github.kedaitayar.mfm.ui.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMainAccountBinding
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import io.github.kedaitayar.mfm.util.NavigationResult.getNavigationResultLiveData
import io.github.kedaitayar.mfm.viewmodels.AccountViewModel

private const val TAG = "MainAccountFragment"

@AndroidEntryPoint
class MainAccountFragment : Fragment(R.layout.fragment_main_account) {
    private val accountViewModel: AccountViewModel by viewModels()
    private var _binding: FragmentMainAccountBinding? = null
    private val binding get() = _binding!!
    private var totalIncome: Double = 0.0
    private var totalBudgeted: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainAccountBinding.inflate(inflater, container, false)
        context ?: return binding.root

        childFragmentManager.beginTransaction().apply {
            replace(R.id.account_list_fragment_container, AccountListFragment())
        }.commit()

        setupAddAccountButton()
        setupAddAccountFragmentResultObserver()
        setupDashboardInfo()

        return binding.root
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

    private fun setupAddAccountFragmentResultObserver() {
        val result = getNavigationResultLiveData<Long>(AddAccountFragment.ADD_ACCOUNT_RESULT_KEY)
        result?.observe(viewLifecycleOwner) {
            Log.i(
                TAG,
                "add account result: $it"
            )   //TODO: snackbar to show outcome of account insert
        }
    }

    private fun setupAddAccountButton() {
        binding.buttonAddAccount.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddAccountFragment()
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