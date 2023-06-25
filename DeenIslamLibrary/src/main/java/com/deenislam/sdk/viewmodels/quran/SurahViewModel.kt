package com.deenislam.sdk.viewmodels.quran;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.quran.SurahResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.response.quran.SurahList
import com.deenislam.sdk.service.repository.quran.SurahRepository
import kotlinx.coroutines.launch

class SurahViewModel (
    private val repository: SurahRepository
) : ViewModel() {

    private val _surahlist:MutableLiveData<SurahResource> = MutableLiveData()
    val surahlist:MutableLiveData<SurahResource> get() = _surahlist

    fun getSurahList(language:String)
    {
        viewModelScope.launch {
            processSurahList(repository.fetchSurahList(language))
        }
    }

    private fun processSurahList(response: ApiResource<SurahList>)
    {
        when(response)
        {
            is ApiResource.Failure -> _surahlist.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Success)
                    _surahlist.value = SurahResource.getSurahList(response.value.Data)
                else
                    _surahlist.value = CommonResource.API_CALL_FAILED
            }
        }
    }
}