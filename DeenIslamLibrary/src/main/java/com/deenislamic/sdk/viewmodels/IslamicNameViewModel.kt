package com.deenislamic.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicNameResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.repository.IslamicNameRepository
import kotlinx.coroutines.launch

internal class IslamicNameViewModel (
    private val repository: IslamicNameRepository
) : ViewModel() {

    private val _favNamesLiveData:MutableLiveData<IslamicNameResource>  = MutableLiveData()
    val favNamesLiveData:MutableLiveData<IslamicNameResource> get() = _favNamesLiveData

    private val _islamicNamesLiveData:MutableLiveData<IslamicNameResource>  = MutableLiveData()
    val islamicNamesLiveData:MutableLiveData<IslamicNameResource> get() = _islamicNamesLiveData

    private val _eidJamatLiveData:MutableLiveData<IslamicNameResource>  = MutableLiveData()
    val eidJamatLiveData:MutableLiveData<IslamicNameResource> get() = _eidJamatLiveData

    private val _islamicNamesCatsLiveData:MutableLiveData<IslamicNameResource>  = MutableLiveData()
    val islamicNamesCatsLiveData:MutableLiveData<IslamicNameResource> get() = _islamicNamesCatsLiveData

    private val _islamicNamesHomeLiveData:MutableLiveData<IslamicNameResource>  = MutableLiveData()
    val islamicNamesHomeLiveData:MutableLiveData<IslamicNameResource> get() = _islamicNamesHomeLiveData


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

    fun getNames(gender:String,language:String,alphabet:String)
    {
        viewModelScope.launch {
            when(val response = repository.getIslamicNames(gender, language, alphabet))
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

    fun getEidJamatList(location: String) {
        viewModelScope.launch {
            when (val response = repository.getEidJamatList(location)) {
                is ApiResource.Failure -> _eidJamatLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if (response.value?.success == true && response.value.data.isNotEmpty())
                        _eidJamatLiveData.value =
                            IslamicNameResource.eidJamatList(response.value.data)
                    else
                        _eidJamatLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun getNamesCats(gender: String,language:String)
    {
        viewModelScope.launch {
            when(val response = repository.getIslamicNameCats(gender,language))
            {
                is ApiResource.Failure -> _islamicNamesCatsLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.data?.isNotEmpty() == true)
                        _islamicNamesCatsLiveData.value = IslamicNameResource.islamicNamesCategories(response.value.data)
                    else
                        _islamicNamesCatsLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun getNamesByCatId(id:Int)
    {
        viewModelScope.launch {
            when(val response = repository.getIslamicNamesByCatId(id))
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

    fun getNamesHomePatch(language:String)
    {
        viewModelScope.launch {
            when(val response = repository.getIslamicNamesPatch(language))
            {
                is ApiResource.Failure -> _islamicNamesHomeLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.data?.isNotEmpty() == true)
                        _islamicNamesHomeLiveData.value = IslamicNameResource.islamicNamesPatch(response.value.data)
                    else
                        _islamicNamesHomeLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }
}