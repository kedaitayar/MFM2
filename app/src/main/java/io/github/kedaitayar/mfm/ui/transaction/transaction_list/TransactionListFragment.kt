package io.github.kedaitayar.mfm.ui.transaction.transaction_list

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentTransactionListBinding
import io.github.kedaitayar.mfm.ui.main.MainFragment
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import io.github.kedaitayar.mfm.ui.transaction.MainTransactionFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionListFragment : Fragment(R.layout.fragment_transaction_list) {
    private val transactionListViewModel: TransactionListViewModel by viewModels()
    private var _binding: FragmentTransactionListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TransactionListAdapter()
        setupAdapter(adapter)
        setupRecyclerView(adapter)
        setupHideFABOnScroll()
    }

    private fun setupAdapter(adapter: TransactionListAdapter) {
        adapter.addLoadStateListener { loadState ->
//            val isLoading = loadState.mediator?.refresh is LoadState.Loading
            val isLoading = loadState.refresh is LoadState.Loading
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

    private fun setupHideFABOnScroll() {
        binding.recyclerViewTransactionList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
    }

    private fun setupRecyclerView(adapter: TransactionListAdapter) {
        binding.recyclerViewTransactionList.adapter = adapter
        binding.recyclerViewTransactionList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTransactionList.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        viewLifecycleOwner.lifecycleScope.launch {
            transactionListViewModel.allTransactionListAdapterData.collectLatest {
                adapter.submitData(it)
            }
        }
        popupMenuSetup(adapter)
    }

    private fun popupMenuSetup(adapter: TransactionListAdapter) {
        adapter.setOnItemClickListener(object : TransactionListAdapter.OnItemClickListener {

            override fun onClick(
                transactionListAdapterData: TransactionListAdapterData,
                transactionCard: ConstraintLayout
            ) {
                val action =
                    MainFragmentDirections.actionMainFragmentToEditTransactionFragment(transactionListAdapterData.toTransaction())
                val extras =
                    FragmentNavigatorExtras(transactionCard to "edit_transaction_${transactionListAdapterData.transactionId}")
//                findNavController().navigate(action, extras)
                findNavController().navigate(action)
            }
//            override fun onPopupMenuButtonClick(
//                transactionListAdapterData: TransactionListAdapterData,
//                popupMenuButton: Button
//            ) {
//                val popupMenu = PopupMenu(this@TransactionListFragment.context, popupMenuButton)
//                popupMenu.inflate(R.menu.menu_transaction_list_item)
//                popupMenu.setOnMenuItemClickListener {
//                    when (it.itemId) {
//                        R.id.edit -> {
//                            val action = MainFragmentDirections.actionMainFragmentToEditTransactionFragment(
//                                transactionListAdapterData.toTransaction()
//                            )
//                            findNavController().navigate(action)
//                            true
//                        }
//                        else -> false
//                    }
//                }
//                popupMenu.show()
//            }
        })
    }

    private fun TransactionListAdapterData.toTransaction(): Transaction {
        return Transaction(
            transactionId = transactionId,
            transactionAmount = transactionAmount,
            transactionTime = transactionTime,
            transactionType = transactionTypeId,
            transactionAccountId = transactionAccountId,
            transactionBudgetId = transactionBudgetId,
            transactionAccountTransferTo = transactionAccountTransferTo,
            transactionNote = transactionNote
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}