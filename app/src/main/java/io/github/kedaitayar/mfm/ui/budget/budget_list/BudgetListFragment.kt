package io.github.kedaitayar.mfm.ui.budget.budget_list

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentBudgetListBinding
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BudgetListFragment : Fragment(R.layout.fragment_budget_list) {
    private val budgetListViewModel: BudgetListViewModel by viewModels()
    private val binding: FragmentBudgetListBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = BudgetListAdapter()
        adapter.setBudgetType(budgetListViewModel.budgetType ?: 0)
        setupRecyclerView(adapter)
    }

    private fun setupRecyclerView(adapter: BudgetListAdapter) {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val from = viewHolder.bindingAdapterPosition
                    val to = target.bindingAdapterPosition
                    (recyclerView.adapter as BudgetListAdapter).moveItem(from, to)

                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    //not handled
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        (viewHolder as BudgetListAdapter.BudgetListViewHolder).onDragHandler()
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    val currentList = (recyclerView.adapter as BudgetListAdapter).currentList
                    (viewHolder as BudgetListAdapter.BudgetListViewHolder).onClearViewHandler()
                    budgetListViewModel.updateBudgetListPosition(currentList)
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                budgetListViewModel.budgetList.collect {
                    if (it.isEmpty()) {
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
                        R.id.detail -> {
                            val action =
                                MainFragmentDirections.actionMainFragmentToBudgetDetailFragment(
                                    budgetListAdapterData.budgetId,
                                    budgetListAdapterData.budgetName
                                )
                            findNavController().navigate(action)
                            true
                        }
                        R.id.edit -> {
                            val action =
                                MainFragmentDirections.actionMainFragmentToEditBudgetFragment(
                                    budgetListAdapterData
                                )
                            findNavController().navigate(action)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }

            override fun onBudgetedPillClick(budgetListAdapterData: BudgetListAdapterData) {
                val action = MainFragmentDirections.actionMainFragmentToSingleBudgetingFragment(
                    budgetListAdapterData
                )
                findNavController().navigate(action)
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