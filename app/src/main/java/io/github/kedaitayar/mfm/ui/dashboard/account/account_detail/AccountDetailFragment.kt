package io.github.kedaitayar.mfm.ui.dashboard.account.account_detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentAccountDetailBinding

@AndroidEntryPoint
class AccountDetailFragment : Fragment(R.layout.fragment_account_detail) {
    private val binding: FragmentAccountDetailBinding by viewBinding()
    private val args: AccountDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.commit {
                add(R.id.fragment_container_account_budget_chart, AccountBudgetChartFragment.newInstance(args.account))
                add(R.id.fragment_container_account_transaction_graph, AccountTransactionGraphFragment.newInstance(args.account))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topAppBar.apply {
            setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            title = "${args.account.accountName} Detail"
        }
    }

    companion object {
        const val ACCOUNT_STATE_KEY =
            "io.github.kedaitayar.mfm.ui.dashboard.account.account_detail.account"
    }
}