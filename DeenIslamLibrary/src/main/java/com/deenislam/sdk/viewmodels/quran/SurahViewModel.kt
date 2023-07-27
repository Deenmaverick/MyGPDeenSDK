package com.deenislam.sdk.viewmodels.quran;

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.quran.SurahResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.repository.quran.SurahRepository
import com.deenislam.sdk.service.network.response.quran.SurahList
import kotlinx.coroutines.launch

internal class SurahViewModel (
    private val repository: SurahRepository
) : ViewModel() {

    private val _surahlist:MutableLiveData<SurahResource> = MutableLiveData()
    val surahlist:MutableLiveData<SurahResource> get() = _surahlist

    var listState: Parcelable? = null
    fun getSurahList(language:String)
    {
        viewModelScope.launch {
            processSurahList(repository.fetchSurahList(language) as ApiResource<SurahList>)
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

    // quran api

    fun getSurahList_Quran_Com(language:String)
    {
        viewModelScope.launch {
            processSurahList_quran_com(repository.fetchSurahList_quran_com(language))
        }
    }

    private fun processSurahList_quran_com(response: ApiResource<com.deenislam.sdk.service.network.response.quran.qurannew.surah.SurahList?>)
    {
        when(response)
        {
            is ApiResource.Failure -> _surahlist.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value?.chapters?.isNotEmpty() == true)
                    _surahlist.value = SurahResource.getSurahList_quran_com(response.value.chapters)
                else
                    _surahlist.value = CommonResource.EMPTY
            }
        }

    }
}