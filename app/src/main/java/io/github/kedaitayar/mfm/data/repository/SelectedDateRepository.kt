package io.github.kedaitayar.mfm.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDateTime
import java.time.OffsetDateTime
import javax.inject.Singleton

@Singleton
class SelectedDateRepository {
    val selectedDate: LiveData<LocalDateTime> get() = _selectedDate
    private val _selectedDate = MutableLiveData<LocalDateTime>()
    private val _selectedOffsetDate =
        MutableStateFlow<OffsetDateTime>(OffsetDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0))
    val selectedOffsetDate: Flow<OffsetDateTime> = _selectedOffsetDate

    init {
        _selectedDate.postValue(LocalDateTime.now())
    }

    fun increaseMonth() {
        _selectedDate.postValue(_selectedDate.value?.plusMonths(1))
        _selectedOffsetDate.value = _selectedOffsetDate.value.plusMonths(1)
    }

    fun decreaseMonth() {
        _selectedDate.postValue(_selectedDate.value?.minusMonths(1))
        _selectedOffsetDate.value = _selectedOffsetDate.value.minusMonths(1)
    }
}