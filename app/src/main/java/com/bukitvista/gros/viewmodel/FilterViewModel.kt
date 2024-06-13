package com.bukitvista.gros.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Date

data class FilterItem(
    var startDate: Date? = null,
    var endDate: Date? = null,
    var priority: Int? = null,
    var activeTag: Int? = 1,
    var assignTo: String? = null,
)

class FilterViewModel: ViewModel() {
    private val _filters = MutableLiveData<FilterItem>().apply {
        value = FilterItem()
    }
    val filters: LiveData<FilterItem>
        get() = _filters

    fun setItems(items: FilterItem) {
        _filters.value = items
    }

    fun update(startDate: Date? = this.filters.value?.startDate,
               endDate: Date? = this.filters.value?.endDate,
               priority: Int? = this.filters.value?.priority,
               activeTag: Int? = this.filters.value?.activeTag,
               assignTo: String? = this.filters.value?.assignTo) {
        _filters.value?.startDate = startDate
        _filters.value?.endDate = endDate
        _filters.value?.priority = priority
        _filters.value?.activeTag = activeTag
        _filters.value?.assignTo = assignTo
        _filters.value = _filters.value // Trigger observer update
    }

    fun updateRangeDate(startDate: Date, endDate: Date){
        _filters.value?.startDate = startDate
        _filters.value?.endDate = endDate
        _filters.value = _filters.value // Trigger observer update
    }

    fun updatePriority(priority: Int?) {
        _filters.value?.priority = priority
        _filters.value = _filters.value // Trigger observer update
    }

    fun updateActiveTag(activeTag: Int?) {
        _filters.value?.activeTag = activeTag
        _filters.value = _filters.value // Trigger observer update
    }

    fun updateAssignTo(assignTo: String?) {
        _filters.value?.assignTo = assignTo
        _filters.value = _filters.value // Trigger observer update
    }
}
