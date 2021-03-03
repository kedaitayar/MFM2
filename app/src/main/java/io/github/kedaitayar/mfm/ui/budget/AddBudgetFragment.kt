package io.github.kedaitayar.mfm.ui.budget

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.BudgetTypeDropdownData
import io.github.kedaitayar.mfm.databinding.FragmentAddBudgetBinding
import io.github.kedaitayar.mfm.viewmodels.BudgetViewModel
import io.github.kedaitayar.mfm.data.entity.Budget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "AddBudgetFragment"

@AndroidEntryPoint
class AddBudgetFragment : Fragment(R.layout.fragment_add_budget) {
    private val budgetViewModel: BudgetViewModel by viewModels()
    private var _binding: FragmentAddBudgetBinding? = null
    private val binding get() = _binding!!
    private var dropDownPos = -1 //pos of selected budget type dropdown

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBudgetBinding.inflate(inflater, container, false)
        context ?: return binding.root

        setupDropdown()

        binding.buttonAddBudget.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val budgetTypeDropdownData = binding.autoCompleteBudgetType.adapter.getItem(dropDownPos) as BudgetTypeDropdownData
                val budgetTypeId = budgetTypeDropdownData.budgetTypeId?.toInt()
                if (budgetTypeId != null){
                    if (budgetTypeId == 1 || budgetTypeId == 2) {
                        val budget = Budget(
                            budgetName = binding.textInputEditBudgetName.text.toString(),
                            budgetGoal = binding.textInputEditBudgetGoal.text.toString().toDouble(),
                            budgetType = budgetTypeId
                        )
                        budgetViewModel.insert(budget)
                    }
                }
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

    private fun setupDropdown() {
        budgetViewModel.allBudgetType.observe(viewLifecycleOwner, Observer { it ->
            it?.let { list ->
                val items: List<BudgetTypeDropdownData> = list.map {
                    BudgetTypeDropdownData(
                        budgetTypeId = it.budgetTypeId,
                        budgetTypeName = it.budgetTypeName
                    )
                }
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    items
                )
                binding.autoCompleteBudgetType.setAdapter(adapter)
            }
        })

        binding.autoCompleteBudgetType.setOnItemClickListener { parent, view, position, id ->
            dropDownPos = position
            Log.i(TAG, "dropdown selected: pos $dropDownPos")
            val selectedItem = parent.adapter.getItem(position) as BudgetTypeDropdownData
            Log.i(TAG, "dropdown selected: ${selectedItem.budgetTypeName}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}