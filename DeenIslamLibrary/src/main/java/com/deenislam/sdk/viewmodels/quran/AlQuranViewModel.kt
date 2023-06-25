package com.deenislam.sdk.viewmodels.quran;

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.quran.AlQuranResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.response.quran.surah_details.Ayath
import com.deenislam.sdk.service.network.response.quran.surah_details.SurahDetails
import com.deenislam.sdk.service.repository.quran.AlQuranRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlQuranViewModel @Inject constructor(
    private val repository: AlQuranRepository
) : ViewModel() {

    private val _surahDetails:MutableLiveData<AlQuranResource> = MutableLiveData()
    val surahDetails:MutableLiveData<AlQuranResource> get() = _surahDetails

    fun getSurahDetails(surahID:Int,language:String,page:Int,contentCount:Int)
    {
        viewModelScope.launch {
            processSurahDetailsData(repository.fetchSurahdetails(surahID,language,page,contentCount))
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
                    if (response.value.Data.AyathList.size > 0)
                        _surahDetails.value = AlQuranResource.surahDetails(response.value.Data)
                    else
                        _surahDetails.value = CommonResource.EMPTY
                }
                else
                    _surahDetails.value = CommonResource.API_CALL_FAILED
            }
        }
    }
}

