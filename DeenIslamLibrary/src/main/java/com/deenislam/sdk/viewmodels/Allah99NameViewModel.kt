package com.deenislam.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.Allah99NameResource
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.repository.DeenServiceRepository
import kotlinx.coroutines.launch


internal class Allah99NameViewModel(
    private val deenServiceRepository: DeenServiceRepository
) : ViewModel() {

    private val _allah99nameLiveData: MutableLiveData<Allah99NameResource> = MutableLiveData()
    val allah99nameLiveData: MutableLiveData<Allah99NameResource> get() = _allah99nameLiveData

    fun get99NameOfAllah(language:String)
    {
        viewModelScope.launch {
            when(val response = deenServiceRepository.get99NameOfAllah(language))
            {
                is ApiResource.Failure -> _allah99nameLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                    {
                        _allah99nameLiveData.value = Allah99NameResource.getAllah99Name(response.value.Data)
                    }
                    else
                        _allah99nameLiveData.value = CommonResource.API_CALL_FAILED
                }
            }
        }
    }

}