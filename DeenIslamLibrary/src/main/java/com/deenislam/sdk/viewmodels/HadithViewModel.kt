package com.deenislam.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.HadithResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.response.hadith.HadithResponse
import com.deenislam.sdk.service.network.response.hadith.chapter.HadithChapterResponse
import com.deenislam.sdk.service.network.response.hadith.preview.HadithPreviewResponse
import com.deenislam.sdk.service.repository.HadithRepository
import kotlinx.coroutines.launch

internal class HadithViewModel(
    private val hadithRepository: HadithRepository
) : ViewModel() {

    private val _hadithLiveData:MutableLiveData<HadithResource> = MutableLiveData()
    val hadithLiveData:MutableLiveData<HadithResource> get() = _hadithLiveData

    private val _hadithChapterLiveData:MutableLiveData<HadithResource> = MutableLiveData()
    val hadithChapterLiveData:MutableLiveData<HadithResource> get() = _hadithChapterLiveData

    private val _hadithPreviewLiveData:MutableLiveData<HadithResource> = MutableLiveData()
    val hadithPreviewLiveData:MutableLiveData<HadithResource> get() = _hadithPreviewLiveData

    private val _hadithFavLiveData:MutableLiveData<HadithResource> = MutableLiveData()
    val hadithFavLiveData:MutableLiveData<HadithResource> get() = _hadithFavLiveData


    fun getHadithCollection(language:String){
        viewModelScope.launch {
            processHadithCollection(
                hadithRepository.getHadithCollection(
                    language = language
                ) as ApiResource<HadithResponse>
            )
        }
    }

    private fun processHadithCollection(response: ApiResource<HadithResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _hadithLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data?.isNotEmpty() == true)
                    _hadithLiveData.value = HadithResource.hadithCollection(response.value.Data)
                else
                    _hadithLiveData.value = CommonResource.EMPTY
            }
        }
    }

    fun getHadithChapterByCollection(language:String,bookId:Int,page:Int,limit:Int){
        viewModelScope.launch {
            processHadithChapterResponse(
                hadithRepository.getChapterByCollection(
                    language = language,
                    bookId = bookId,
                    page = page,
                    limit = limit
                ) as ApiResource<HadithChapterResponse>
            )
        }
    }

    private fun processHadithChapterResponse(response: ApiResource<HadithChapterResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _hadithChapterLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data?.isNotEmpty() == true)
                    _hadithChapterLiveData.value = HadithResource.hadithChapterByCollection(response.value.Data)
                else
                    _hadithChapterLiveData.value = CommonResource.EMPTY
            }
        }
    }

    // hadith preview

    fun getHadithPreview(language: String, bookId:Int, chapterId: Int,page:Int,limit:Int)
    {
        viewModelScope.launch {
            hadithPreviewProcess(
                hadithRepository.getHadithPreview(
                    language = language,
                    bookId = bookId,
                    chapterId = chapterId,
                    page = page,
                    limit = limit
                ) as ApiResource<HadithPreviewResponse>
            )
        }
    }

    private fun hadithPreviewProcess(response: ApiResource<HadithPreviewResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _hadithPreviewLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data?.isNotEmpty() == true)
                    _hadithPreviewLiveData.value = HadithResource.hadithPreview(response.value)
                else
                    _hadithPreviewLiveData.value = CommonResource.EMPTY
            }
        }

    }

    fun setFavHadith(isFavorite: Boolean, hadithID: Int, language: String, position: Int)
    {
        viewModelScope.launch {

            when(val response = hadithRepository.setHadithFav(isFavorite,hadithID,language))
            {
                is ApiResource.Failure -> _hadithPreviewLiveData.value = CommonResource.ACTION_API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _hadithPreviewLiveData.value = HadithResource.setFavHadith(position,!isFavorite)
                    else
                        _hadithPreviewLiveData.value = HadithResource.updateFavFailed

                }
            }
        }
    }

    fun getFavHadith(language: String,page:Int,limit:Int)
    {
        viewModelScope.launch {

            when(val response = hadithRepository.getFavHadith(language = language, page = page, limit = limit))
            {
                is ApiResource.Failure -> _hadithFavLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.isNotEmpty() == true)
                        _hadithFavLiveData.value = HadithResource.hadithFavData(response.value)
                    else
                        _hadithFavLiveData.value = CommonResource.EMPTY

                }
            }
        }
    }

    fun clear()
    {
        _hadithPreviewLiveData.value = CommonResource.CLEAR
    }

    fun clearFavLiveData()
    {
        _hadithFavLiveData.value = CommonResource.CLEAR
    }

}