package io.github.kedaitayar.mfm.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMainTransactionBinding

@AndroidEntryPoint
class MainTransactionFragment: Fragment(R.layout.fragment_main_transaction) {
    private var _binding: FragmentMainTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainTransactionBinding.inflate(inflater, container, false)
        context ?: return binding.root

        childFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_transaction_list, TransactionListFragment())
        }.commit()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}