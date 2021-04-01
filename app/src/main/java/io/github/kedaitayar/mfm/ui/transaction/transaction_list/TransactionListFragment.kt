package io.github.kedaitayar.mfm.ui.transaction.transaction_list

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentTransactionListBinding
import io.github.kedaitayar.mfm.ui.main.MainFragment
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import io.github.kedaitayar.mfm.ui.transaction.MainTransactionFragment
import io.github.kedaitayar.mfm.ui.transaction.TransactionListAdapter
import io.github.kedaitayar.mfm.viewmodels.TransactionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "TransactionListFragment"

@AndroidEntryPoint
class TransactionListFragment : Fragment(R.layout.fragment_transaction_list) {
    private val transactionListViewModel: TransactionListViewModel by viewModels()
    private var _binding: FragmentTransactionListBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTransactionListBinding.bind(view)
        val adapter = TransactionListAdapter()
        setupAdapter(adapter)
        setupRecyclerView(adapter)
    }

    private fun setupAdapter(adapter: TransactionListAdapter) {
        adapter.addLoadStateListener { loadState ->
            val isLoading = loadState.mediator?.refresh is LoadState.Loading
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0


            if (parentFragment is MainTransactionFragment) {
                val mainTransactionFragment = parentFragment as MainTransactionFragment
                if (isLoading) {
                    mainTransactionFragment.showLoadingView()
                } else {
                    mainTransactionFragment.hideLoadingView()
                }

                if (isListEmpty) {
                    mainTransactionFragment.showEmptyView()
                } else {
                    mainTransactionFragment.hideEmptyView()
                }
            }
        }
    }

    private fun setupRecyclerView(adapter: TransactionListAdapter) {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        viewLifecycleOwner.lifecycleScope.launch {

            transactionListViewModel.allTransactionListAdapterData.collectLatest {
                adapter.submitData(it)
            }
        }
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