package io.github.kedaitayar.mfm.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMainTransactionBinding
import io.github.kedaitayar.mfm.ui.transaction.transaction_list.TransactionListFragment

@AndroidEntryPoint
class MainTransactionFragment: Fragment(R.layout.fragment_main_transaction) {
    private var _binding: FragmentMainTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainTransactionBinding.bind(view)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}