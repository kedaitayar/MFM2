package io.github.kedaitayar.mfm.ui.dashboard.account.main_account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentAccountListBinding
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import io.github.kedaitayar.mfm.util.exhaustive
import io.github.kedaitayar.mfm.viewmodels.AccountViewModel
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AccountListFragment : Fragment(R.layout.fragment_account_list) {
    private val accountViewModel: AccountViewModel by viewModels()
    private val accountListViewModel: AccountListViewModel by viewModels()
    private var _binding: FragmentAccountListBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAccountListBinding.bind(view)
        val adapter = AccountListAdapter()
        recyclerViewSetup(adapter)
        setupEventListener()
    }


    private fun recyclerViewSetup(adapter: AccountListAdapter) {
        binding.recyclerViewAccountList.adapter = adapter
        // to half the recycler itemview width - https://stackoverflow.com/a/51224889
//        binding.recyclerViewAccountList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewAccountList.layoutManager = object : GridLayoutManager(requireContext(), 2) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                lp?.width = width / 2
                return true
            }
        }
        accountViewModel.accountListAdapterData.observe(viewLifecycleOwner, Observer {
            if (it == null || it.isEmpty()) {
                binding.linearLayoutEmptyView.visibility = View.VISIBLE
            } else {
                binding.linearLayoutEmptyView.visibility = View.GONE
                adapter.submitList(it)
            }
        })
        popupMenuSetup(adapter)
    }

    private fun popupMenuSetup(adapter: AccountListAdapter) {
        adapter.setOnItemClickListener(object : AccountListAdapter.OnItemClickListener {
            override fun onPopupMenuButtonClick(
                accountListAdapterData: AccountListAdapterData,
                popupMenuButton: Button
            ) {
                val account = accountListAdapterData
                val popupMenu = PopupMenu(this@AccountListFragment.context, popupMenuButton)
                popupMenu.inflate(R.menu.menu_account_list_item)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.detail -> {
                            accountListViewModel.onAccountDetailClick(accountListAdapterData.toAccount())
                            true
                        }
                        R.id.edit -> {
                            accountListViewModel.onEditAccountClick(accountListAdapterData.toAccount())
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        })
    }

    private fun setupEventListener() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            accountListViewModel.accountListEvent.collect { event ->
                when (event) {
                    is AccountListViewModel.AccountListEvent.NavigateToAccountDetail -> {
                        val action = MainFragmentDirections.actionMainFragmentToAccountDetailFragment(event.account)
                        findNavController().navigate(action)
                    }
                    is AccountListViewModel.AccountListEvent.NavigateToEditAccount ->{
                        val action = MainFragmentDirections.actionMainFragmentToAddEditAccountFragment(event.account)
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }
    }

    private fun AccountListAdapterData.toAccount() : Account {
        return Account(
            accountId = accountId,
            accountName = accountName
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}