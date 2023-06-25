package com.deenislam.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.database.entity.Tasbeeh
import com.deenislam.sdk.service.models.TasbeehResource
import com.deenislam.sdk.service.repository.TasbeehRepository
import kotlinx.coroutines.launch

internal class TasbeehViewModel(
    private val repository: TasbeehRepository
) : ViewModel() {

    private val _duaLiveData:MutableLiveData<TasbeehResource> = MutableLiveData()
    val duaLiveData:MutableLiveData<TasbeehResource> get() = _duaLiveData

    fun fetchDuaData(duaid:Int)
    {
        viewModelScope.launch {
            _duaLiveData.value = TasbeehResource.duaData(repository.getDuaData(duaid)!!)
        }
    }

    fun fetchUserPref()
    {
        viewModelScope.launch {
            _duaLiveData.value = TasbeehResource.userPref(repository.getUserPref())
        }
    }

    fun fetchRecentCount()
    {
        viewModelScope.launch {
            _duaLiveData.value = TasbeehResource.recentCount(repository.getRecentCount()!!)
        }
    }

    fun updateTasbeehSound()
    {
        viewModelScope.launch {
            _duaLiveData.value = TasbeehResource.userPref(repository.updateTasbeehSound())
        }
    }

    fun setTasbeehCount(target:Int, data: Tasbeeh, todayDate:String)
    {
        viewModelScope.launch {
            _duaLiveData.value = TasbeehResource.duaData(
                repository.setTasbeehCount(target,data,todayDate)
                    ?.get(0)!!
            ).apply {
                _duaLiveData.value = TasbeehResource.recentCount(repository.getRecentCount()!!)
            }
        }
    }

    fun resetTasbeeh(type:Int,duaid: Int)
    {
        viewModelScope.launch {
            _duaLiveData.value = TasbeehResource.resetDuaData(repository.resetTasbeehCount(type,duaid)!![0]).apply {
                _duaLiveData.value = TasbeehResource.recentCount(repository.getRecentCount()!!)
            }
        }
    }

}