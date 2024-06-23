package com.deenislamic.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.DailyDuaResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.network.response.dailydua.duabycategory.DuaByCategory
import com.deenislamic.sdk.service.network.response.dailydua.favdua.FavDua
import com.deenislamic.sdk.service.network.response.dailydua.todaydua.TodayDua
import com.deenislamic.sdk.service.network.response.dashboard.DashboardResponse
import com.deenislamic.sdk.service.repository.DailyDuaRepository
import kotlinx.coroutines.launch


internal class DailyDuaViewModel(
    private val repository: DailyDuaRepository
) : ViewModel() {

    private val _allDayLiveData:MutableLiveData<DailyDuaResource> = MutableLiveData()
    val allDayLiveData:MutableLiveData<DailyDuaResource> get() = _allDayLiveData

    private val _duaPreviewLiveData:MutableLiveData<DailyDuaResource> = MutableLiveData()
    val duaPreviewLiveData:MutableLiveData<DailyDuaResource> get() = _duaPreviewLiveData

    private val _favDuaLiveData:MutableLiveData<DailyDuaResource> = MutableLiveData()
    val favDuaLiveData:MutableLiveData<DailyDuaResource> get() = _favDuaLiveData

    private val _todayDuaLiveData:MutableLiveData<DailyDuaResource> = MutableLiveData()
    val todayDuaLiveData:MutableLiveData<DailyDuaResource> get() = _todayDuaLiveData


    // dua category

    fun getDuaAllCategory(language:String)
    {
        viewModelScope.launch {
            processDualAllCategory(repository.getAllDuaPatch(language) as ApiResource<DashboardResponse>)
        }
    }

    private fun processDualAllCategory(response: ApiResource<DashboardResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _allDayLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if (response.value.Success)
                {
                    if(response.value.Data.isNotEmpty())
                        _allDayLiveData.value = DailyDuaResource.duaALlCategory(response.value.Data)
                    else
                        _allDayLiveData.value = CommonResource.EMPTY
                }
                else
                    _allDayLiveData.value = CommonResource.API_CALL_FAILED
            }
        }

    }

    fun setFavDua(isFavorite: Boolean, duaId: Int, language: String, position: Int)
    {
        viewModelScope.launch {

            when(val response = repository.setFavDua(isFavorite,duaId,language))
            {
                is ApiResource.Failure -> _duaPreviewLiveData.value = CommonResource.ACTION_API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _duaPreviewLiveData.value = DailyDuaResource.setFavDua(position,!isFavorite)

                }
            }
        }
    }



    // dua preview by category

    fun getDuaByCategory(cat:Int,language: String)
    {
        viewModelScope.launch {
            processDuaByCategory(repository.getDuaByCategory(cat,language) as ApiResource<DuaByCategory>)
        }
    }

    private fun processDuaByCategory(response: ApiResource<DuaByCategory>)
    {
        when(response)
        {
            is ApiResource.Failure -> _duaPreviewLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Success)
                {
                    if(response.value.Data.isNotEmpty())
                        _duaPreviewLiveData.value = DailyDuaResource.duaPreview(response.value.Data)
                    else
                        _duaPreviewLiveData.value = CommonResource.EMPTY
                }
                else
                    _duaPreviewLiveData.value = CommonResource.API_CALL_FAILED
            }
        }
    }

    // favorite dua

    fun getFavDua(language: String)
    {
        viewModelScope.launch {
            processFavDua(repository.getfavDua(language) as ApiResource<FavDua>)
        }
    }

    private fun processFavDua(response: ApiResource<FavDua>)
    {
        when(response)
        {
            is ApiResource.Failure -> _favDuaLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data?.isNotEmpty() == true)
                {
                    _favDuaLiveData.value = DailyDuaResource.favDuaList(response.value.Data)
                }
                else
                    _favDuaLiveData.value = CommonResource.EMPTY
            }
        }
    }


    // Today's Dua

    fun getTodayDua(language: String)
    {
        viewModelScope.launch {
            processTodayDua(repository.getTodayDya(language) as ApiResource<TodayDua>)
        }
    }

    private fun processTodayDua(response: ApiResource<TodayDua>)
    {
        when(response)
        {
            is ApiResource.Failure -> _todayDuaLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data?.isNotEmpty() == true)
                    _todayDuaLiveData.value = DailyDuaResource.todayDuaList(response.value.Data)
                else
                    _todayDuaLiveData.value = CommonResource.EMPTY
            }
        }
    }

    fun clearFavDuaLiveData()
    {
        _duaPreviewLiveData.value = CommonResource.CLEAR
        _favDuaLiveData.value = CommonResource.CLEAR
    }


}