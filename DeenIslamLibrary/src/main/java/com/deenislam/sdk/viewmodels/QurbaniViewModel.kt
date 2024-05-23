package com.deenislam.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.QurbaniResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.repository.QurbaniRepository
import kotlinx.coroutines.launch


internal class QurbaniViewModel (
    private val qurbaniRepository: QurbaniRepository
) : ViewModel() {

    private val _qurbaniLiveData: MutableLiveData<QurbaniResource> = MutableLiveData()
    val qurbaniLiveData: MutableLiveData<QurbaniResource> get() = _qurbaniLiveData

    suspend fun getQurbaniPatch(language:String){

        viewModelScope.launch {
            when(val response = qurbaniRepository.getQurabniPatch(language)){
                is ApiResource.Failure -> _qurbaniLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if(response.value.Data.isNotEmpty()){
                        _qurbaniLiveData.value = QurbaniResource.QurbaniPatch(response.value.Data)
                    }else
                        _qurbaniLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    suspend fun getContentByCat(catid:Int,language: String){

        viewModelScope.launch {
            when(val response = qurbaniRepository.getCoantentByCat(catid,language)){
                is ApiResource.Failure -> _qurbaniLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if(response.value.Data.isNotEmpty())
                        _qurbaniLiveData.value = QurbaniResource.QurbaniContentByCat(response.value.Data)
                    else
                        _qurbaniLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }


}