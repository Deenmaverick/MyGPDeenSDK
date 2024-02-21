package com.deenislam.sdk.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.BoyanResource
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.response.boyan.categoriespaging.BoyanCategoriesResponse
import com.deenislam.sdk.service.network.response.boyan.scholarspaging.BoyanScholarResponse
import com.deenislam.sdk.service.network.response.boyan.videopreview.BoyanVideoPreviewResponse
import com.deenislam.sdk.service.repository.BoyanRepository
import kotlinx.coroutines.launch


internal class BoyanViewModel(
    private val repository: BoyanRepository
) : ViewModel()  {

    private val _boyanLiveData: MutableLiveData<BoyanResource> = MutableLiveData()
    val boyanLiveData: MutableLiveData<BoyanResource> get() = _boyanLiveData

    private val _boyanCategoryLiveData:MutableLiveData<BoyanResource> = MutableLiveData()
    val boyanCategoryLiveData:MutableLiveData<BoyanResource> get() = _boyanCategoryLiveData

    private val _boyanScholarsLiveData:MutableLiveData<BoyanResource> = MutableLiveData()
    val boyanScholarsLiveData:MutableLiveData<BoyanResource> get() = _boyanScholarsLiveData

    private val _boyanVideoPreviewLiveData:MutableLiveData<BoyanResource> = MutableLiveData()
    val boyanVideoPreviewLiveData:MutableLiveData<BoyanResource> get() = _boyanVideoPreviewLiveData

    private val _boyanCategoryVideoPreviewLiveData:MutableLiveData<BoyanResource> = MutableLiveData()
    val boyanCategoryVideoPreviewLiveData:MutableLiveData<BoyanResource> get() = _boyanCategoryVideoPreviewLiveData

    fun getBoyanHome(language: String) {
        viewModelScope.launch {

            when (val response = repository.getBoyanHome(language = language)) {
                is ApiResource.Failure -> _boyanLiveData.value =
                    CommonResource.API_CALL_FAILED

                is ApiResource.Success -> {
                    if (response.value?.Success == true)
                        _boyanLiveData.value = BoyanResource.BoyanHomeData(response.value.Data)
                    else
                        _boyanLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun getBoyanCategories(language:String, page:Int, limit:Int){
        viewModelScope.launch {
            processBoyanCategoriesResponse(
                repository.getBoyanCategories(
                    language = language,
                    page = page,
                    limit = limit
                ) as ApiResource<BoyanCategoriesResponse>
            )
        }
    }

    private fun processBoyanCategoriesResponse(response: ApiResource<BoyanCategoriesResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _boyanCategoryLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data.isNotEmpty())
                    _boyanCategoryLiveData.value = BoyanResource.BoyanCategoryData(response.value.Data)
                else
                    _boyanCategoryLiveData.value = CommonResource.EMPTY
            }
        }
    }

    fun getBoyanScholars(language:String, page:Int, limit:Int){
        viewModelScope.launch {
            processBoyanScholarsResponse(
                repository.getBoyanScholars(
                    language = language,
                    page = page,
                    limit = limit
                ) as ApiResource<BoyanScholarResponse>
            )
        }
    }

    private fun processBoyanScholarsResponse(response: ApiResource<BoyanScholarResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _boyanScholarsLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data.isNotEmpty())
                    _boyanScholarsLiveData.value = BoyanResource.BoyanScholarData(response.value.Data)
                else
                    _boyanScholarsLiveData.value = CommonResource.EMPTY
            }
        }
    }

    fun getBoyanVideoPreview(Id: Int, page: Int, limit: Int)
    {
        viewModelScope.launch {
            boyanVideoPreviewProcess(
                repository.getBoyanVideoPreview(
                    Id = Id,
                    page = page,
                    limit = limit
                ) as ApiResource<BoyanVideoPreviewResponse>
            )
        }
    }

    private fun boyanVideoPreviewProcess(response: ApiResource<BoyanVideoPreviewResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _boyanVideoPreviewLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data.isNotEmpty())
                    _boyanVideoPreviewLiveData.value = BoyanResource.BoyanVideoData(response.value)
                else
                    _boyanVideoPreviewLiveData.value = CommonResource.EMPTY
            }
        }
    }

    fun getBoyanCategoryVideoPreview(Id: Int, page: Int, limit: Int)
    {
        viewModelScope.launch {
            boyanCategoryVideoPreviewProcess(
                repository.getBoyanCategoryVideoPreview(
                    Id = Id,
                    page = page,
                    limit = limit
                ) as ApiResource<BoyanVideoPreviewResponse>
            )
        }
    }

    private fun boyanCategoryVideoPreviewProcess(response: ApiResource<BoyanVideoPreviewResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _boyanCategoryVideoPreviewLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data.isNotEmpty())
                    _boyanCategoryVideoPreviewLiveData.value = BoyanResource.BoyanVideoData(response.value)
                else
                    _boyanCategoryVideoPreviewLiveData.value = CommonResource.EMPTY
            }
        }
    }

}