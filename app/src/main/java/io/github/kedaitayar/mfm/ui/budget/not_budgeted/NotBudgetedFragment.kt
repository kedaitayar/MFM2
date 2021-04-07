package io.github.kedaitayar.mfm.ui.budget.not_budgeted

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentNotBudgetedBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@AndroidEntryPoint
class NotBudgetedFragment : Fragment(R.layout.fragment_not_budgeted) {
    private val notBudgetedViewModel: NotBudgetedViewModel by viewModels()
    private var _binding: FragmentNotBudgetedBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotBudgetedBinding.bind(view)
        setupNotBudgetedAmount()
    }

    private fun setupNotBudgetedAmount() {
        val resources = requireContext().resources
        val format = DecimalFormatSymbols().apply {
            groupingSeparator = ' '
        }
        val formatter = DecimalFormat(resources.getString(R.string.currency_format)).apply {
            decimalFormatSymbols = format
        }

        notBudgetedViewModel.notBudgetedAmount.observe(viewLifecycleOwner) {
            binding.textViewNotBudgetedAmount.text = resources.getString(R.string.currency_symbol, formatter.format(it))

            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.gGreen, typedValue, true)
            val green = ContextCompat.getColor(requireContext(), typedValue.resourceId)
            requireContext().theme.resolveAttribute(R.attr.gRed, typedValue, true)
            val red = ContextCompat.getColor(requireContext(), typedValue.resourceId)
            if (it < 0) {
                binding.root.setBackgroundColor(red)
            } else {
                binding.root.setBackgroundColor(green)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}