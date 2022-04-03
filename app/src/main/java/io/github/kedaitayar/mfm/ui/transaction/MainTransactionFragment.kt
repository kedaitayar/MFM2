package io.github.kedaitayar.mfm.ui.transaction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMainTransactionBinding
import io.github.kedaitayar.mfm.ui.transaction.transaction_list.TransactionListFragment

@AndroidEntryPoint
class MainTransactionFragment : Fragment(R.layout.fragment_main_transaction) {
    private val binding: FragmentMainTransactionBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_transaction_list, TransactionListFragment())
        }.commit()
    }

    fun showEmptyView() {
        binding.linearLayoutEmptyView.visibility = View.VISIBLE
    }

    fun hideEmptyView() {
        binding.linearLayoutEmptyView.visibility = View.GONE
    }

    fun showLoadingView() {
        binding.progressBar.visibility = View.VISIBLE
    }

    fun hideLoadingView() {
        binding.progressBar.visibility = View.GONE
    }
}