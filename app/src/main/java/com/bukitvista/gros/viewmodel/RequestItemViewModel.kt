package com.bukitvista.gros.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bukitvista.gros.response.RequestsResponseItem

class RequestItemViewModel: ViewModel() {
    val requestItem = MutableLiveData<RequestsResponseItem?>()
    fun setItem(item: RequestsResponseItem?) {
        requestItem.value = item
    }

    fun getItem(): MutableLiveData<RequestsResponseItem?> {
        return requestItem
    }

}