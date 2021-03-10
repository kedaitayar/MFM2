package io.github.kedaitayar.mfm.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentAccountDetailBinding
import io.github.kedaitayar.mfm.viewmodels.AccountViewModel

@AndroidEntryPoint
class AccountDetailFragment : Fragment() {
    private val accountViewModel: AccountViewModel by viewModels()
    private var _binding: FragmentAccountDetailBinding? = null
    private val binding get() = _binding!!
    private val args: AccountDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountDetailBinding.inflate(inflater, container, false)
        context ?: return binding.root

        childFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_account_budget_chart, AccountBudgetChartFragment.newInstance(args.accountId))
            replace(R.id.fragment_container_account_transaction_graph, AccountTransactionGraphFragment.newInstance(args.accountId))
        }.commit()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}