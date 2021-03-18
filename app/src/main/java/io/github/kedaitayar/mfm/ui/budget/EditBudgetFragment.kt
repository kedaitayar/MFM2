package io.github.kedaitayar.mfm.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.podata.BudgetTypeDropdownData
import io.github.kedaitayar.mfm.databinding.FragmentEditBudgetBinding
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.viewmodels.BudgetViewModel
import io.github.kedaitayar.mfm.viewmodels.SharedViewModel
import kotlinx.coroutines.*

@AndroidEntryPoint
class EditBudgetFragment : Fragment(R.layout.fragment_edit_budget) {
    private val budgetViewModel: BudgetViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentEditBudgetBinding? = null
    private val binding get() = _binding!!
    private var dropDownPos = -1 //pos of selected budget type dropdown
    private val args: EditBudgetFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBudgetBinding.inflate(inflater, container, false)
        context ?: return binding.root

        setupDropdown()
        setupToolbar()
        binding.textInputEditBudgetName.setText(args.budgetListAdapterData.budgetName)
        binding.textInputEditBudgetGoal.setText(args.budgetListAdapterData.budgetGoal.toString())
        binding.autoCompleteBudgetType.setText(args.budgetListAdapterData.budgetTypeName)
        dropDownPos = args.budgetListAdapterData.budgetTypeId.toInt() - 1
        setupAddBudgetButton()

        return binding.root
    }

    private fun setupAddBudgetButton() {
        binding.buttonAddBudget.setOnClickListener {
            if (binding.textInputEditBudgetName.text.toString().isNullOrBlank()) {
                Snackbar.make(binding.root, "Budget name cannot be empty", Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.buttonAddBudget)
                    .show()
            } else if (binding.autoCompleteBudgetType.text.toString().isNullOrBlank()) {
                Snackbar.make(binding.root, "Budget type cannot be empty", Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.buttonAddBudget)
                    .show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    val budgetTypeDropdownData = binding.autoCompleteBudgetType.adapter.getItem(dropDownPos) as BudgetTypeDropdownData
                    val budgetTypeId = budgetTypeDropdownData.budgetTypeId?.toInt()
                    if (budgetTypeId != null) {
                        if (budgetTypeId == 1 || budgetTypeId == 2) {
                            val budget = Budget(
                                budgetId = args.budgetListAdapterData.budgetId,
                                budgetName = binding.textInputEditBudgetName.text.toString(),
                                budgetGoal = binding.textInputEditBudgetGoal.text.toString().toDoubleOrNull() ?: 0.0,
                                budgetType = budgetTypeId
                            )
                            val result = async { budgetViewModel.update(budget) }
                            withContext(Dispatchers.Main) {
                                if (result.await() > 0) {
                                    sharedViewModel.setSnackbarText("Budget updated")
                                    hideKeyboard()
                                    findNavController().navigateUp()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.topAppBar.inflateMenu(R.menu.menu_delete)
        binding.topAppBar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        binding.topAppBar.setNavigationOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Delete budget?")
                        .setNegativeButton("Cancel") { dialog, which -> }
                        .setPositiveButton("Delete") { dialog, which ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val budget = Budget(budgetId = args.budgetListAdapterData.budgetId)
                                val result = async { budgetViewModel.delete(budget) }
                                withContext(Dispatchers.Main) {
                                    if (result.await() > 0) {
                                        sharedViewModel.setSnackbarText("Budget deleted")
                                        findNavController().navigateUp()
                                    }
                                }
                            }
                        }.show()
                    true
                }
                else -> {
                    false
                }
            }
        }
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
            parent.adapter.getItem(position) as BudgetTypeDropdownData
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}