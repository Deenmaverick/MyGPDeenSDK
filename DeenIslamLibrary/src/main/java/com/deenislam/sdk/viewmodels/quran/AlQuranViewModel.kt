package com.deenislam.sdk.viewmodels.quran;

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.quran.AlQuranResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.response.quran.juz.JuzResponse
import com.deenislam.sdk.service.network.response.quran.surah_details.SurahDetails
import com.deenislam.sdk.service.repository.quran.AlQuranRepository
import com.deenislam.sdk.service.network.response.quran.verses.Verses
import com.google.gson.Gson
import kotlinx.coroutines.launch

internal class AlQuranViewModel(
    private val repository: AlQuranRepository
) : ViewModel() {

    private val _surahDetails:MutableLiveData<AlQuranResource> = MutableLiveData()
    val surahDetails:MutableLiveData<AlQuranResource> get() = _surahDetails

    private val _juzLiveData:MutableLiveData<AlQuranResource> = MutableLiveData()
    val juzLiveData:MutableLiveData<AlQuranResource> get() = _juzLiveData

    fun getSurahDetails(surahID:Int,language:String,page:Int,contentCount:Int)
    {
        viewModelScope.launch {
            processSurahDetailsData(repository.fetchSurahdetails(surahID,language,page,contentCount) as ApiResource<SurahDetails>)
        }
    }

    private fun processSurahDetailsData(response: ApiResource<SurahDetails>)
    {
        Log.e("response", Gson().toJson(response))

        when(response)
        {
            is ApiResource.Failure -> _surahDetails.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Success) {
                    if (response.value.Data.AyathList.isNotEmpty())
                        _surahDetails.value = AlQuranResource.surahDetails(response.value.Data)
                    else
                        _surahDetails.value = CommonResource.EMPTY
                }
                else
                    _surahDetails.value = CommonResource.API_CALL_FAILED
            }
        }
    }

    // quran api

    fun getVersesByChapter(language: String, page: Int, contentCount: Int,chapter_number:Int)
    {
        viewModelScope.launch {
            processVersesByChapter(repository.getVersesByChapter(language = language,page=page,contentCount=contentCount,chapter_number=chapter_number) as ApiResource<Verses>)
        }
    }

    private fun processVersesByChapter(response: ApiResource<Verses>)
    {
        when(response)
        {
            is ApiResource.Failure -> _surahDetails.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.verses.isNotEmpty())
                    _surahDetails.value = AlQuranResource.versesByChapter(response.value.verses,response.value.pagination.next_page)
                else
                    _surahDetails.value = CommonResource.EMPTY
            }
        }
    }

    // juz list

    fun juzList()
    {
        viewModelScope.launch {
            processJuzList(repository.fetchJuzList() as ApiResource<JuzResponse>)
        }
    }

    private fun processJuzList(response: ApiResource<JuzResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _juzLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.juzs.isNotEmpty())
                    _juzLiveData.value = AlQuranResource.juzList(response.value.juzs)
                else
                    _juzLiveData.value = CommonResource.EMPTY

            }
        }
    }

    // get verses by juz

    fun getVersesByJuz(language: String, page: Int, contentCount: Int,juz_number:Int)
    {
        viewModelScope.launch {
            processVersesByChapter(repository.getVersesByJuz(language = language,page=page,contentCount=contentCount,juz_number=juz_number) as ApiResource<Verses>)
        }
    }
}

