package com.bukitvista.gros.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bukitvista.gros.response.StaffResponse

class ProfileViewModel: ViewModel() {
    private val staffData = MutableLiveData<StaffResponse>()
    fun getStaffData(): MutableLiveData<StaffResponse> {
        return staffData
    }

    fun setStaffData(data: StaffResponse) {
        staffData.value = data
    }
}