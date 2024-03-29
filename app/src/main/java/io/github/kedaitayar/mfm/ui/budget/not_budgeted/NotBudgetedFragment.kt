package io.github.kedaitayar.mfm.ui.budget.not_budgeted

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentNotBudgetedBinding
import io.github.kedaitayar.mfm.util.safeCollection
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@AndroidEntryPoint
class NotBudgetedFragment : Fragment(R.layout.fragment_not_budgeted) {
    private val notBudgetedViewModel: NotBudgetedViewModel by viewModels()
    private val binding: FragmentNotBudgetedBinding by viewBinding()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        notBudgetedViewModel.notBudgetedAmount.safeCollection(viewLifecycleOwner) {
            binding.textViewNotBudgetedAmount.text =
                resources.getString(R.string.currency_symbol, formatter.format(it))

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
}