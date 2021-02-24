package io.github.kedaitayar.mfm.ui.account

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentAccountListBinding
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import io.github.kedaitayar.mfm.viewmodels.AccountViewModel
import io.github.kedaitayar.mfm.data.entity.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "AccountListFragment"

@AndroidEntryPoint
class AccountListFragment : Fragment(R.layout.fragment_account_list) {
    private val accountViewModel: AccountViewModel by viewModels()
    private var _binding: FragmentAccountListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountListBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val adapter = AccountListAdapter()
        recyclerViewSetup(adapter)

        return binding.root
    }


    private fun recyclerViewSetup(adapter: AccountListAdapter) {
        binding.recyclerViewAccountList.adapter = adapter
        binding.recyclerViewAccountList.layoutManager = LinearLayoutManager(requireContext())
        accountViewModel.accountListAdapterData.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        popupMenuSetup(adapter)
    }

    private fun popupMenuSetup(adapter: AccountListAdapter) {
        Log.i(TAG, "popupMenuSetup: ")
        adapter.setOnItemClickListener(object : AccountListAdapter.OnItemClickListener {
            override fun onPopupMenuButtonClick(
                accountListAdapterData: AccountListAdapterData,
                popupMenuButton: Button
            ) {
                Log.i(TAG, "onPopupMenuButtonClick: ")
                val popupMenu = PopupMenu(this@AccountListFragment.context, popupMenuButton)
                popupMenu.inflate(R.menu.menu_account_list_item)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.detail -> {
                            val action = MainFragmentDirections.actionMainFragmentToAccountDetailFragment(accountListAdapterData.accountId)
                            findNavController().navigate(action)
                            true
                        }
                        R.id.delete -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                val account = Account(accountId = accountListAdapterData.accountId)
                                val result = accountViewModel.delete(account)
                                Log.i(TAG, "onPopupMenuButtonClick: delete result: $result")
                            }
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