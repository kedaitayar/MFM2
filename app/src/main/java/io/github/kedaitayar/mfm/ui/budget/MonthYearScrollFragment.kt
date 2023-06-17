package io.github.kedaitayar.mfm.ui.budget

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMonthYearScrollBinding
import io.github.kedaitayar.mfm.util.safeCollection
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class MonthYearScrollFragment : Fragment(R.layout.fragment_month_year_scroll) {
    private val monthYearScrollViewModel: MonthYearScrollViewModel by viewModels()
    private val binding: FragmentMonthYearScrollBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        monthYearScrollViewModel.selectedDate.safeCollection(viewLifecycleOwner) {
            binding.buttonDate.text =
                it.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ", " + it.year
        }

        binding.buttonLeft.setOnClickListener {
            monthYearScrollViewModel.decreaseMonth()
        }
        binding.buttonRight.setOnClickListener {
            monthYearScrollViewModel.increaseMonth()
        }
    }
}