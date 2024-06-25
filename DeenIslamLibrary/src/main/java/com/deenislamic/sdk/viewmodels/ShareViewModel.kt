package com.deenislamic.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.ShareResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.repository.ShareRepository
import kotlinx.coroutines.launch


internal class ShareViewModel(
    private val repository: ShareRepository
) : ViewModel() {

    private val _shareLiveData: MutableLiveData<ShareResource> = MutableLiveData()
    val shareLiveData: MutableLiveData<ShareResource> get() = _shareLiveData


    suspend fun getWallpaperCat(language:String){

        viewModelScope.launch {

            when(val response = repository.getWallpaperCat(language)){
                is ApiResource.Failure -> _shareLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {

                    if(response.value?.Data?.isNotEmpty() == true)
                        _shareLiveData.value = ShareResource.WallpaperCat(response.value.Data)
                    else
                        _shareLiveData.value = CommonResource.API_CALL_FAILED
                }
            }
        }
    }

    suspend fun getWallpaper(language:String,catid:Int){

        viewModelScope.launch {

            when(val response = repository.getWallpaper(language,catid)){
                is ApiResource.Failure -> _shareLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {

                    if(response.value?.Data?.isNotEmpty() == true)
                        _shareLiveData.value = ShareResource.Wallpaper(response.value.Data)
                    else
                        _shareLiveData.value = CommonResource.API_CALL_FAILED
                }
            }
        }
    }

}