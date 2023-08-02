package com.deenislam.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.IslamicNameResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.repository.IslamicNameRepository
import kotlinx.coroutines.launch

internal class IslamicNameViewModel (
    private val repository: IslamicNameRepository
) : ViewModel() {

    private val _favNamesLiveData:MutableLiveData<IslamicNameResource>  = MutableLiveData()
    val favNamesLiveData:MutableLiveData<IslamicNameResource> get() = _favNamesLiveData

    private val _islamicNamesLiveData:MutableLiveData<IslamicNameResource>  = MutableLiveData()
    val islamicNamesLiveData:MutableLiveData<IslamicNameResource> get() = _islamicNamesLiveData

    fun getFavNames(gender:String,language:String)
    {
        viewModelScope.launch {

            when(val response = repository.getAllFavNames(gender, language))
            {
                is ApiResource.Failure -> _favNamesLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.isNotEmpty() == true)
                        _favNamesLiveData.value = IslamicNameResource.favNames(response.value.Data)
                    else
                        _favNamesLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun removeFavName(contentId:Int,language:String,adapaterPosition:Int)
    {
        viewModelScope.launch {

            when(val response = repository.removeFavName(contentId, language))
            {
                is ApiResource.Failure -> _favNamesLiveData.value = CommonResource.ACTION_API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _favNamesLiveData.value = IslamicNameResource.favremoved(adapaterPosition)
                    else
                        _favNamesLiveData.value = IslamicNameResource.favremovedFailed
                }
            }

        }
    }

    fun getNames(gender:String,language:String)
    {
        viewModelScope.launch {
            when(val response = repository.getIslamicNames(gender, language))
            {
                is ApiResource.Failure -> _islamicNamesLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.isNotEmpty() == true)
                        _islamicNamesLiveData.value = IslamicNameResource.islamicNames(response.value.Data)
                    else
                        _islamicNamesLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun modifyFavNames(contentId: Int, language: String, isFav: Boolean, position: Int)
    {
        viewModelScope.launch {

            when(val response = repository.modifyFavNames(
                contentId = contentId,
                language = language,
                isFav = isFav
            ))
            {
                is ApiResource.Failure -> _islamicNamesLiveData.value = IslamicNameResource.favFailed
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _islamicNamesLiveData.value = IslamicNameResource.favDone(position,isFav)
                    else
                        _islamicNamesLiveData.value = IslamicNameResource.favFailed
                }
            }
        }
    }

    fun clear()
    {
        _favNamesLiveData.value = CommonResource.CLEAR
    }
}