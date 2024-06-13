package com.bukitvista.gros.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bukitvista.gros.response.RequestsResponseItem

class RequestListViewModel: ViewModel() {
    private val _requestList = MutableLiveData<List<RequestsResponseItem>>()
    val requestList: MutableLiveData<List<RequestsResponseItem>>
        get() = _requestList

    fun setItems(items: List<RequestsResponseItem>) {
        _requestList.value = items
    }
}