package com.deenislamic.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamiFazaelResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.repository.IslamiFazaelRepository
import kotlinx.coroutines.launch


internal class IslamiFazaelViewModel (
    private val fazaelRepository: IslamiFazaelRepository
) : ViewModel() {

    private val _islamiFazaelLivedata:MutableLiveData<IslamiFazaelResource> = MutableLiveData()
    val islamiFazaelLivedata:MutableLiveData<IslamiFazaelResource> get() = _islamiFazaelLivedata

    suspend fun getAllIslamiFazael(language:String){

        viewModelScope.launch {

            when(val response = fazaelRepository.getAllFazael(language)){
                is ApiResource.Failure -> _islamiFazaelLivedata.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if(response.value?.Data?.isNotEmpty() == true)
                        _islamiFazaelLivedata.value = IslamiFazaelResource.AllFazaelList(response.value.Data)
                    else
                        _islamiFazaelLivedata.value = CommonResource.EMPTY
                }
            }
        }
    }

    suspend fun getFazaelByCat(language:String,catid:Int){

        viewModelScope.launch {

            when(val response = fazaelRepository.getFazaelByCat(language,catid)){
                is ApiResource.Failure -> _islamiFazaelLivedata.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if(response.value?.Data?.isNotEmpty() == true)
                        _islamiFazaelLivedata.value = IslamiFazaelResource.FazaelByCat(response.value.Data)
                    else
                        _islamiFazaelLivedata.value = CommonResource.EMPTY
                }
            }
        }
    }

}