package io.github.kedaitayar.mfm.ui.budget.budget_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentBudgetListBinding
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections

@AndroidEntryPoint
class BudgetListFragment : Fragment(R.layout.fragment_budget_list) {
    private val budgetListViewModel: BudgetListViewModel by viewModels()
    private var _binding: FragmentBudgetListBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBudgetListBinding.bind(view)
        val adapter = BudgetListAdapter()
        adapter.setBudgetType(budgetListViewModel.budgetType ?: 0)
        setupRecyclerView(adapter)
    }

    private fun setupRecyclerView(adapter: BudgetListAdapter) {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        budgetListViewModel.budgetList.observe(viewLifecycleOwner) {
            if (it == null || it.isEmpty()) {
                when (budgetListViewModel.budgetType) {
                    1 -> {
                        binding.textViewEmptyViewTopText.text = "Monthly budget is empty"
                    }
                    2 -> {
                        binding.textViewEmptyViewTopText.text = "Yearly budget is empty"
                    }
                    else -> {

                    }
                }
                binding.linearLayoutEmptyView.visibility = View.VISIBLE
            } else {
                binding.linearLayoutEmptyView.visibility = View.GONE
                adapter.submitList(it)
            }
        }
        popupMenuSetup(adapter)
    }

    private fun popupMenuSetup(adapter: BudgetListAdapter) {
        adapter.setOnItemClickListener(object : BudgetListAdapter.OnItemClickListener {

            override fun onPopupMenuButtonClick(
                budgetListAdapterData: BudgetListAdapterData,
                popupMenuButton: Button
            ) {
                val popupMenu = PopupMenu(this@BudgetListFragment.context, popupMenuButton)
                popupMenu.inflate(R.menu.menu_budget_list_item)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
//                        R.id.detail -> {
//                            true
//                        }
                        R.id.edit -> {
                            val action =
                                MainFragmentDirections.actionMainFragmentToEditBudgetFragment(budgetListAdapterData)
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

    companion object {
        const val ARG_BUDGET_TYPE =
            "io.github.kedaitayar.mfm.ui.budget.BudgetListFragment.budgetType"

        @JvmStatic
        fun newInstance(budgetType: Int) =
            BudgetListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_BUDGET_TYPE, budgetType)
                }
            }
    }
}