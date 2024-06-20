package com.deenislamic.sdk.viewmodels.quran;

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.quran.AlQuranResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.network.response.quran.juz.JuzResponse
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.AyatResponse
import com.deenislamic.sdk.service.network.response.quran.surah_details.SurahDetails
import com.deenislamic.sdk.service.repository.quran.AlQuranRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

internal class AlQuranViewModel(
    private val repository: AlQuranRepository
) : ViewModel() {

    private val _surahDetails:MutableLiveData<AlQuranResource> = MutableLiveData()
    val surahDetails:MutableLiveData<AlQuranResource> get() = _surahDetails

    private val _juzLiveData:MutableLiveData<AlQuranResource> = MutableLiveData()
    val juzLiveData:MutableLiveData<AlQuranResource> get() = _juzLiveData

    private val _homePatchLiveData:MutableLiveData<AlQuranResource> = MutableLiveData()
    val homePatchLiveData:MutableLiveData<AlQuranResource> get() = _homePatchLiveData

    private val _surahListLiveData:MutableLiveData<AlQuranResource> = MutableLiveData()
    val surahListLiveData:MutableLiveData<AlQuranResource> get() = _surahListLiveData

    private val _paraListLiveData:MutableLiveData<AlQuranResource> = MutableLiveData()
    val paraListLiveData:MutableLiveData<AlQuranResource> get() = _paraListLiveData


    private val _ayatFavLiveData:MutableLiveData<AlQuranResource> = MutableLiveData()
    val ayatFavLiveData:MutableLiveData<AlQuranResource> get() = _ayatFavLiveData

    private val _tafsirLiveData:MutableLiveData<AlQuranResource> = MutableLiveData()
    val tafsirLiveData:MutableLiveData<AlQuranResource> get() = _tafsirLiveData



    var surahListState: Parcelable? = null

    fun getSurahDetails(surahID:Int,language:String,page:Int,contentCount:Int)
    {
        viewModelScope.launch {
            processSurahDetailsData(repository.fetchSurahdetails(surahID,language,page,contentCount))
        }
    }

    private fun processSurahDetailsData(response: ApiResource<SurahDetails?>)
    {
        Log.e("response", Gson().toJson(response))

        when(response)
        {
            is ApiResource.Failure -> _surahDetails.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value?.Success == true) {
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

    fun getVersesByChapter(
        language: String,
        page: Int,
        contentCount: Int,
        chapter_number: Int,
        isReadingMode: Boolean
    )
    {
        viewModelScope.launch {
            processVersesByChapter(repository.getVersesByChapter(
                language = language,
                page=page,
                contentCount=contentCount,
                chapter_number=chapter_number,
                isReadingMode = isReadingMode
            ))
        }
    }

    private fun processVersesByChapter(response: ApiResource<AyatResponse?>)
    {
        when(response)
        {
            is ApiResource.Failure -> _surahDetails.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value?.Data?.Ayaths?.isNotEmpty() == true)
                    _surahDetails.value = AlQuranResource.VersesByChapter(response.value)
                else
                    _surahDetails.value = CommonResource.EMPTY
            }
        }
    }

    // juz list

    fun juzList()
    {
        viewModelScope.launch {
            processJuzList(repository.fetchJuzList())
        }
    }

    private fun processJuzList(response: ApiResource<JuzResponse?>)
    {
        when(response)
        {
            is ApiResource.Failure -> _juzLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value?.juzs?.isNotEmpty() == true)
                    _juzLiveData.value = AlQuranResource.juzList(response.value.juzs)
                else
                    _juzLiveData.value = CommonResource.EMPTY

            }
        }
    }

    // get verses by juz

    fun getVersesByJuz(language: String, page: Int, contentCount: Int,juz_number:Int,isReadingMode: Boolean)
    {
        Log.e("qloadApiDataaaa",juz_number.toString())
        viewModelScope.launch {
            processVersesByChapter(repository.getVersesByJuz(language = language,page=page,contentCount=contentCount,juz_number=juz_number,isReadingMode))
        }
    }

    // Al-Quran GM

    fun getQuranHomePatch(language: String)
    {
        viewModelScope.launch {
            when(val response = repository.getQuranHomePatch(
                language = language
            ))
            {
                is ApiResource.Failure -> _homePatchLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.isNotEmpty() == true)
                        _homePatchLiveData.value = AlQuranResource.QuranHomePatch(response.value)
                    else
                        _homePatchLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun getSurahList(language: String, page: Int, contentCount: Int)
    {
        viewModelScope.launch {
            when(val response = repository.getSurahList(
                language = language,
                page = page,
                contentCount = contentCount
            ))
            {
                is ApiResource.Failure -> _surahListLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.isNotEmpty() == true)
                        _surahListLiveData.value = AlQuranResource.SurahList(response.value)
                    else
                        _surahListLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun getParaList(language: String, page: Int, contentCount: Int)
    {
        viewModelScope.launch {
            when(val response = repository.getParaList(
                language = language,
                page = page,
                contentCount = contentCount
            ))
            {
                is ApiResource.Failure -> _paraListLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.isNotEmpty() == true)
                        _paraListLiveData.value = AlQuranResource.ParaList(response.value)
                    else
                        _paraListLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun updateFavAyat(language: String, ContentId:Int,isFav:Boolean,position:Int)
    {
        viewModelScope.launch {
            when(val response = repository.updateFavAyat(
                language = language,
                ContentId = ContentId,
                isFav = isFav
            ))
            {
                is ApiResource.Failure -> _ayatFavLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _ayatFavLiveData.value = AlQuranResource.ayatFav(!isFav,position)
                    else
                        _ayatFavLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun getTafsir(surahID: Int, verseID: Int, ayatArabic: String, arabicFont: Int)
    {
        viewModelScope.launch {
            when(val response = repository.getTafsir(surahID,verseID,ayatArabic,arabicFont))
            {
                is ApiResource.Failure -> _tafsirLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.isNotEmpty() == true)
                        _tafsirLiveData.value = AlQuranResource.Tafsir(
                            response.value.Data,
                            ayatArabic,
                            arabicFont
                        )
                    else
                        _tafsirLiveData.value = CommonResource.EMPTY

                }
            }
        }

    }
}

