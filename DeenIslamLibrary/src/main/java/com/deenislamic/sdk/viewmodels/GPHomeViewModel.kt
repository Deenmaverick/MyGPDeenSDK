package com.deenislamic.sdk.viewmodels;

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.GPHomeResource
import com.deenislamic.sdk.service.models.prayer_time.PrayerNotificationResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.repository.GPHomeRespository
import kotlinx.coroutines.launch


internal class GPHomeViewModel(
    private val repository: GPHomeRespository,
) : ViewModel() {

    private val _gphomeLiveData: MutableLiveData<GPHomeResource> = MutableLiveData()
    val gphomeLiveData: MutableLiveData<GPHomeResource> get() = _gphomeLiveData

    private val _gphomePrayerLiveData: MutableLiveData<GPHomeResource> = MutableLiveData()
    val gphomePrayerLiveData: MutableLiveData<GPHomeResource> get() = _gphomePrayerLiveData


    suspend fun getGPHome(location:String){

        viewModelScope.launch {
            when(val response = repository.getGPHome(location)){
                is ApiResource.Failure -> _gphomeLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if(response.value?.Success == true){
                        _gphomeLiveData.value = GPHomeResource.GPHome(response.value.Data)
                    }else
                        _gphomeLiveData.value = CommonResource.API_CALL_FAILED
                }
            }
        }

    }

    fun setPrayerTrack(language:String,prayer_tag: String,bol:Boolean) {
        viewModelScope.launch {

            val response = repository.setPrayerTimeTrack(language = language,prayer_tag = prayer_tag, isPrayed = bol)

            when(response) {
                is ApiResource.Failure -> _gphomePrayerLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if(response.value?.Success == true)
                        _gphomePrayerLiveData.value = GPHomeResource.GPHomePrayerTrack(prayer_tag,bol)
                    else
                        _gphomePrayerLiveData.value = CommonResource.API_CALL_FAILED
                }
            }
        }
    }


}