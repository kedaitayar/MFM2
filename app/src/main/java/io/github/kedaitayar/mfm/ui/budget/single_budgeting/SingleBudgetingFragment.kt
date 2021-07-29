package io.github.kedaitayar.mfm.ui.budget.single_budgeting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentSingleBudgetingBinding

private const val TAG = "SingleBudgetingFragment"

//R.layout.fragment_single_budgeting
@AndroidEntryPoint
class SingleBudgetingFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentSingleBudgetingBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSingleBudgetingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.chipAdd.id -> {
                    Log.i(TAG, "onViewCreated: chipAdd")
                    binding.textInputLayoutBudgetFrom.visibility = View.GONE
                    binding.textInputLayoutBudgetTo.visibility = View.GONE
                }
                binding.chipMinus.id -> {
                    Log.i(TAG, "onViewCreated: chipMinus")
                    binding.textInputLayoutBudgetFrom.visibility = View.GONE
                    binding.textInputLayoutBudgetTo.visibility = View.GONE
                }
                binding.chipMove.id -> {
                    Log.i(TAG, "onViewCreated: chipMove")
                    binding.textInputLayoutBudgetFrom.visibility = View.VISIBLE
                    binding.textInputLayoutBudgetTo.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}