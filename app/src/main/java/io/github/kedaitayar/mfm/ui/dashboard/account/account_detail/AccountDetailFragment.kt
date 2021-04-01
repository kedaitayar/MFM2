package io.github.kedaitayar.mfm.ui.dashboard.account.account_detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentAccountDetailBinding

@AndroidEntryPoint
class AccountDetailFragment : Fragment(R.layout.fragment_account_detail) {
    private var _binding: FragmentAccountDetailBinding? = null
    private val binding get() = _binding!!
    private val args: AccountDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAccountDetailBinding.bind(view)

        childFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_account_budget_chart,
                AccountBudgetChartFragment.newInstance(args.account)
            )
            replace(R.id.fragment_container_account_transaction_graph,
                AccountTransactionGraphFragment.newInstance(args.account)
            )
        }.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ACCOUNT_STATE_KEY =
            "io.github.kedaitayar.mfm.ui.dashboard.account.account_detail.account"
    }
}