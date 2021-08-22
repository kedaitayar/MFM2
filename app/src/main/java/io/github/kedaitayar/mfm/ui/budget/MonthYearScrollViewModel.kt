package io.github.kedaitayar.mfm.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.repository.SelectedDateRepository
import javax.inject.Inject

@HiltViewModel
class MonthYearScrollViewModel @Inject constructor(
    private val selectedDateRepository: SelectedDateRepository
) : ViewModel() {
    val selectedDate = selectedDateRepository.selectedDate.asFlow()

    fun increaseMonth() {
        selectedDateRepository.increaseMonth()
    }

    fun decreaseMonth() {
        selectedDateRepository.decreaseMonth()
    }
}