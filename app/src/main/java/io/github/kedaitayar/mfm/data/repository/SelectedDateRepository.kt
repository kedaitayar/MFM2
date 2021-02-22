package io.github.kedaitayar.mfm.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.kedaitayar.mfm.data.podata.SelectedDate
import javax.inject.Singleton

@Singleton
class SelectedDateRepository {
    val selectedDate: LiveData<SelectedDate> get() = _selectedDate
    private val _selectedDate = MutableLiveData<SelectedDate>()

    init {
        _selectedDate.postValue(SelectedDate())
    }

    fun setSelectedDate(month: Int, year: Int){
        _selectedDate.postValue(SelectedDate(month = month, year = year))
    }
}