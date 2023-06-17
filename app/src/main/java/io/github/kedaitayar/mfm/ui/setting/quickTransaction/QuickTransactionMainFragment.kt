package io.github.kedaitayar.mfm.ui.setting.quickTransaction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.QuickTransactionListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentQuickTransactionMainBinding
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.util.safeCollection

@AndroidEntryPoint
class QuickTransactionMainFragment : Fragment(R.layout.fragment_quick_transaction_main) {
    private val quickTransactionMainViewModel: QuickTransactionMainViewModel by viewModels()
    private val binding: FragmentQuickTransactionMainBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTopbar()
        val adapter = QuickTransactionListAdapter()
        setupRecyclerView(adapter)
        setupButton()
    }

    private fun setupButton() {
        binding.buttonAddQuickTransaction.setOnClickListener {
            val action =
                QuickTransactionMainFragmentDirections.actionQuickTransactionMainFragmentToAddEditQuickTransactionFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView(adapter: QuickTransactionListAdapter) {
        binding.apply {
            recyclerViewQuickTransaction.adapter = adapter
            recyclerViewQuickTransaction.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewQuickTransaction.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        quickTransactionMainViewModel.quickTransactionList.safeCollection(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        adapter.setOnItemClickListener(object : QuickTransactionListAdapter.OnItemClickListener {
            override fun onClick(quickTransactionListAdapterData: QuickTransactionListAdapterData) {
                val action =
                    QuickTransactionMainFragmentDirections.actionQuickTransactionMainFragmentToAddEditQuickTransactionFragment(
                        quickTransactionListAdapterData
                    )
                findNavController().navigate(action)
            }
        })
    }

    private fun setupTopbar() {
        binding.topAppBar.apply {
            setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener {
                hideKeyboard()
                requireActivity().onBackPressed()
            }
            title = "Quick Transaction"
        }
    }
}