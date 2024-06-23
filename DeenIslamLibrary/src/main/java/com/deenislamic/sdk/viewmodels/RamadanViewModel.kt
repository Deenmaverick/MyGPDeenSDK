package com.deenislamic.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.RamadanResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.network.response.ramadan.Data
import com.deenislamic.sdk.service.repository.RamadanRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


internal class RamadanViewModel (
    private val repository: RamadanRepository
) : ViewModel() {

    private val _ramadanLiveData:MutableLiveData<RamadanResource> = MutableLiveData()
    val ramadanLiveData:MutableLiveData<RamadanResource> get() = _ramadanLiveData

    private val _ramadanTrackLiveData:MutableLiveData<RamadanResource> = MutableLiveData()
    val ramadanTrackLiveData:MutableLiveData<RamadanResource> get() = _ramadanTrackLiveData

    private val _ramadanCalendarLiveData:MutableLiveData<RamadanResource> = MutableLiveData()
    val ramadanCalendarLiveData:MutableLiveData<RamadanResource> get() = _ramadanCalendarLiveData


    /*fun getOtherRamadanTime(location:String, language:String)
    {
        viewModelScope.launch {

            when(val response = repository.getRamadanPatch(location = location, language = language))
            {
                is ApiResource.Failure -> _ramadanLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.FastTime?.isNotEmpty() == true)
                        _ramadanLiveData.value = RamadanResource.ramadanTime(response.value.Data)
                    else
                        _ramadanLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }*/


    fun getOtherRamadanTime(
        location: String,
        language: String
    ) {
        viewModelScope.launch {

            var ramadanData: Data ? = null

            val getPatch = async { repository.getRamadanPatch(language = language)}.await()

            when(val response = repository.getOtherRamadanTime(location = location, language = language))
            {
                is ApiResource.Failure -> _ramadanLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    /*if(response.value.Data.FastTime.isNotEmpty())
                        _ramadanLiveData.value = RamadanResource.ramadanTime(response.value.Data)
                    else
                        _ramadanLiveData.value = CommonResource.EMPTY*/

                    if(response.value?.Data?.FastTime?.isNotEmpty() == true)
                        ramadanData = response.value.Data
                    else
                        _ramadanLiveData.value = CommonResource.EMPTY
                }
            }

            when(getPatch){
                is ApiResource.Failure ->  _ramadanLiveData.value = RamadanResource.RamadanPatch(ramadanData,null)
                is ApiResource.Success -> {
                    _ramadanLiveData.value = RamadanResource.RamadanPatch(ramadanData,getPatch.value?.Data)

                }
            }
        }
    }

    fun getRamadanTime(location: String, language: String, date: String?)
    {
        viewModelScope.launch {

            var ramadanData: Data? = null

            val getPatch = async { repository.getRamadanPatch(language = language)}.await()
            val response = async { repository.getRamadanTime(location = location, language = language,date)}.await()

            when(response)
            {
                is ApiResource.Failure -> _ramadanLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.FastTime?.isNotEmpty() == true)
                        ramadanData = response.value.Data
                    else
                        _ramadanLiveData.value = CommonResource.EMPTY
                }
            }

            when(getPatch){
                is ApiResource.Failure ->  _ramadanLiveData.value = RamadanResource.RamadanPatch(ramadanData,null)
                is ApiResource.Success -> {
                    _ramadanLiveData.value = RamadanResource.RamadanPatch(ramadanData,getPatch.value?.Data)

                }
            }


        }
    }


    fun setRamadanTrack(isFasting:Boolean,language:String)
    {
        viewModelScope.launch {

            when(val response = repository.setRamadanTrack(isFasting = isFasting, language = language))
            {
                is ApiResource.Failure -> _ramadanTrackLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _ramadanTrackLiveData.value = RamadanResource.ramadanTracking(isFasting)
                    else
                        _ramadanTrackLiveData.value = RamadanResource.ramadanTracking(!isFasting)
                }
            }
        }
    }

    fun getRamadanCalendar(date:String,language:String)
    {
        viewModelScope.launch {

            when(val response = repository.getRamadanCalendar(date = date, language = language))
            {
                is ApiResource.Failure -> _ramadanCalendarLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _ramadanCalendarLiveData.value = RamadanResource.ramadanCalendar(response.value.Data)
                    else
                        _ramadanCalendarLiveData.value =  CommonResource.API_CALL_FAILED
                }
            }
        }
    }

    fun clear(){

        _ramadanLiveData.value = CommonResource.CLEAR
    }

}