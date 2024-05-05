package com.deenislam.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.HajjAndUmrahResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.repository.HajjAndUmrahRepository
import kotlinx.coroutines.launch


internal class HajjAndUmrahViewModel(
    private val repository: HajjAndUmrahRepository
) : ViewModel() {

    private val _liveVideosLiveData:MutableLiveData<HajjAndUmrahResource> = MutableLiveData()
    val  liveVideosLiveData:MutableLiveData<HajjAndUmrahResource> get() = _liveVideosLiveData

    private val _hajjAndUmrahPatchLiveData:MutableLiveData<HajjAndUmrahResource> = MutableLiveData()
    val  hajjAndUmrahPatchLiveData:MutableLiveData<HajjAndUmrahResource> get() = _hajjAndUmrahPatchLiveData

    private val _hajjMapTrackerLiveData:MutableLiveData<HajjAndUmrahResource> = MutableLiveData()
    val  hajjMapTrackerLiveData:MutableLiveData<HajjAndUmrahResource> get() = _hajjMapTrackerLiveData


    fun getLiveVideos(language:String)
    {
        viewModelScope.launch {

            when(val response = repository.getMakkahLiveVides(language))
            {
                is ApiResource.Failure -> _liveVideosLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.isNotEmpty() == true)
                        _liveVideosLiveData.value = HajjAndUmrahResource.makkahLiveVideos(response.value.Data)
                    else
                        _liveVideosLiveData.value = CommonResource.EMPTY

                }
            }
        }
    }

    fun getHajjAndUmrahPatch(language:String)
    {
        viewModelScope.launch {

            when(val response = repository.getHajjAndUmrahPatch(language))
            {
                is ApiResource.Failure -> _hajjAndUmrahPatchLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.isNotEmpty() == true)
                        _hajjAndUmrahPatchLiveData.value = HajjAndUmrahResource.hajjAndUmrahPatch(response.value.Data)
                    else
                        _hajjAndUmrahPatchLiveData.value = CommonResource.EMPTY

                }
            }
        }
    }

    fun updateHajjMaptracking(mapTag:String,isTrack:Boolean,indexPos:Int,language:String) {
        viewModelScope.launch {

            when(val response = repository.updateHajjMapTracker(
                mapTag = mapTag,
                isTrack = isTrack,
                language = language
            ))
            {
                is ApiResource.Failure -> _hajjMapTrackerLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _hajjMapTrackerLiveData.value = HajjAndUmrahResource.hajjMapTracker(mapTag,isTrack,indexPos)
                    else
                        _hajjMapTrackerLiveData.value = CommonResource.API_CALL_FAILED

                }
            }
        }
    }


}