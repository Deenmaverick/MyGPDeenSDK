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

    fun getHadithChapterByCollection(language:String,collectionName:String,page:Int,limit:Int){
        viewModelScope.launch {
            processHadithChapterResponse(
                hadithRepository.getChapterByCollection(
                    language = language,
                    collectionName = collectionName,
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
                if(response.value.data.isNotEmpty())
                    _hadithChapterLiveData.value = HadithResource.hadithChapterByCollection(response.value.data)
                else
                    _hadithChapterLiveData.value = CommonResource.EMPTY
            }
        }
    }

    // hadith preview

    fun getHadithPreview(language: String,bookname:String,chapter:Int)
    {
        viewModelScope.launch {
            hadithPreviewProcess(
                hadithRepository.getHadithPreview(
                    language = language,
                    bookname = bookname,
                    chapter = chapter
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
                if(response.value.isNotEmpty())
                    _hadithPreviewLiveData.value = HadithResource.hadithPreview(response.value)
                else
                    _hadithPreviewLiveData.value = CommonResource.EMPTY
            }
        }

    }

}