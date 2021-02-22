package io.github.kedaitayar.mfm.ui.transaction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentTransactionListBinding
import io.github.kedaitayar.mfm.viewmodels.TransactionViewModel

private const val TAG = "TransactionListFragment"

@AndroidEntryPoint
class TransactionListFragment : Fragment(R.layout.fragment_transaction_list) {
    private val transactionViewModel: TransactionViewModel by viewModels()
    private var _binding: FragmentTransactionListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val recyclerView = binding.recyclerView
        val adapter = TransactionListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        setupRecyclerView(adapter)

        return binding.root
    }

    private fun setupRecyclerView(adapter: TransactionListAdapter){
        transactionViewModel.allTransactionListAdapterData.observe(viewLifecycleOwner, Observer {
            it?.let { list ->
                for (item in list) {
                    Log.i(TAG, "setupRecyclerView: $item")
                }
                adapter.submitList(list)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}