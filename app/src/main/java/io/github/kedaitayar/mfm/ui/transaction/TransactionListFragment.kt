package io.github.kedaitayar.mfm.ui.transaction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentTransactionListBinding
import io.github.kedaitayar.mfm.ui.main.MainFragment
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
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
    ): View {
        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val adapter = TransactionListAdapter()
        setupRecyclerView(adapter)

        return binding.root
    }

    private fun setupRecyclerView(adapter: TransactionListAdapter) {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        transactionViewModel.allTransactionListAdapterData.observe(viewLifecycleOwner, Observer {
            if (it == null || it.isEmpty()) {
                if (parentFragment is MainTransactionFragment) {
                    (parentFragment as MainTransactionFragment).showEmptyView()
                }
            } else {
                if (parentFragment is MainTransactionFragment) {
                    (parentFragment as MainTransactionFragment).hideEmptyView()
                }
                for (item in it) {
                    Log.i(TAG, "setupRecyclerView: $item")
                }
                adapter.submitList(it)
            }
        })
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val mainFragment = parentFragment?.parentFragment
                if (mainFragment is MainFragment) {
                    if (dy > 12 && mainFragment.isFABShown()) {
                        mainFragment.hideFAB()
                    }
                    if (dy < -12 && !mainFragment.isFABShown()) {
                        mainFragment.showFAB()
                    }
                    if (dy == 0) {
                        mainFragment.showFAB()
                    }
                }
            }
        })
        popupMenuSetup(adapter)
    }

    private fun popupMenuSetup(adapter: TransactionListAdapter) {
        adapter.setOnItemClickListener(object : TransactionListAdapter.OnItemClickListener {

            override fun onPopupMenuButtonClick(
                transactionListAdapterData: TransactionListAdapterData,
                popupMenuButton: Button
            ) {
                val popupMenu = PopupMenu(this@TransactionListFragment.context, popupMenuButton)
                popupMenu.inflate(R.menu.menu_transaction_list_item)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.edit -> {
                            val action = MainFragmentDirections.actionMainFragmentToEditTransactionFragment(
                                transactionListAdapterData.transactionId
                            )
                            findNavController().navigate(action)
                            true
                        }
//                        R.id.delete -> {
//                            CoroutineScope(Dispatchers.IO).launch {
//                                val account =
//                                    Transaction(transactionId = transactionListAdapterData.transactionId)
//                                val result = transactionViewModel.delete(account)
//                                Log.i(TAG, "onPopupMenuButtonClick: delete result: $result")
//                            }
//                            true
//                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}