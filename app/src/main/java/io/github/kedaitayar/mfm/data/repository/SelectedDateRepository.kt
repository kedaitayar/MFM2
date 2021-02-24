package io.github.kedaitayar.mfm.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.kedaitayar.mfm.data.podata.SelectedDate
import java.time.LocalDateTime
import javax.inject.Singleton

@Singleton
class SelectedDateRepository {
    val selectedDate: LiveData<LocalDateTime> get() = _selectedDate
    private val _selectedDate = MutableLiveData<LocalDateTime>()

    init {
        _selectedDate.postValue(LocalDateTime.now())
    }

    fun increaseMonth(){
        _selectedDate.postValue(_selectedDate.value?.plusMonths(1))
    }

    fun decreaseMonth(){
        _selectedDate.postValue(_selectedDate.value?.minusMonths(1))
    }
}