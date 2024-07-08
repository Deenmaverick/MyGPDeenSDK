package com.deenislamic.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.database.entity.Tasbeeh
import com.deenislamic.sdk.service.models.TasbeehResource
import com.deenislamic.sdk.service.repository.TasbeehRepository
import kotlinx.coroutines.launch


internal class TasbeehViewModel(
    private val repository: TasbeehRepository
) : ViewModel() {

    private val _tasbeehLiveData:MutableLiveData<TasbeehResource> = MutableLiveData()
    val tasbeehLiveData:MutableLiveData<TasbeehResource> get() = _tasbeehLiveData

    fun fetchDuaData(duaid:Int,date:String)
    {
        viewModelScope.launch {
            _tasbeehLiveData.value = repository.getDuaData(duaid,date)
                ?.let { TasbeehResource.DuaData(it) }
        }
    }

    fun fetchUserPref()
    {
        viewModelScope.launch {
            _tasbeehLiveData.value = TasbeehResource.userPref(repository.getUserPref())
        }
    }

    fun fetchRecentCount()
    {
        viewModelScope.launch {
            _tasbeehLiveData.value = repository.getRecentCount()?.let {
                TasbeehResource.RecentCount(
                    it
                )
            }
        }
    }

    fun fetchTotalCount()
    {
        viewModelScope.launch {
            _tasbeehLiveData.value = repository.getTotalCount()
                ?.let { TasbeehResource.TotalCount(it) }
        }
    }

    fun fetchTodayCount(date: String)
    {
        viewModelScope.launch {
            _tasbeehLiveData.value = repository.getTodayCount(date)
                ?.let { TasbeehResource.TodayCount(it) }
        }
    }

    fun fetchWeeklyCount(startdate:String,enddate:String)
    {
        viewModelScope.launch {
            _tasbeehLiveData.value = repository.getWeeklyCount(startdate,enddate)
                ?.let { TasbeehResource.WeeklyCount(it) }
        }
    }

    fun fetchTodayRecentCount(date:String)
    {
        viewModelScope.launch {
            _tasbeehLiveData.value = repository.getTodayRecentCount(date)?.let {
                TasbeehResource.RecentCount(
                    it
                )
            }
        }
    }

    fun fetchWeeklyRecentCount(startdate:String,enddate:String)
    {
        viewModelScope.launch {
            _tasbeehLiveData.value = repository.getWeeklyRecentCount(startdate,enddate)?.let {
                TasbeehResource.RecentCount(
                    data = it,
                    weeklyChartData = ArrayList(repository.getWeeklyChartData(startdate,enddate))
                )
            }
        }
    }



    fun updateTasbeehSound()
    {
        viewModelScope.launch {
            _tasbeehLiveData.value = TasbeehResource.userPref(repository.updateTasbeehSound())
        }
    }

    fun setTasbeehCount(target:Int, data: Tasbeeh, todayDate:String)
    {
        viewModelScope.launch {
            repository.setTasbeehCount(target,data,todayDate)?.get(0)?.let {
            _tasbeehLiveData.value = TasbeehResource.DuaData(it)
            }
            _tasbeehLiveData.value = repository.getRecentCount()?.let {it1->
                TasbeehResource.RecentCount(
                    it1
                )
            }
        }
    }

    fun resetTasbeeh(type:Int,duaid: Int,date: String)
    {
        viewModelScope.launch {
            repository.resetTasbeehCount(type,duaid,date)?.get(0)?.let {
                _tasbeehLiveData.value = TasbeehResource.resetDuaData(it)
            }
              _tasbeehLiveData.value = repository.getRecentCount()?.let {it1->
                    TasbeehResource.RecentCount(it1)
                }
        }
    }

}