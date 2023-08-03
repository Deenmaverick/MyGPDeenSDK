package com.deenislam.sdk.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.DashboardResource
import com.deenislam.sdk.service.models.prayer_time.PrayerTimeResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.repository.DashboardRepository
import com.deenislam.sdk.service.repository.PrayerTimesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

internal class DashboardViewModel(
    private val repository: DashboardRepository,
    private val prayerTimesRepository: PrayerTimesRepository
):ViewModel() {

    private val _dashLiveData:MutableLiveData<DashboardResource> = MutableLiveData()
    val dashLiveData:MutableLiveData<DashboardResource> get() = _dashLiveData

    private val _prayerTimes:MutableLiveData<PrayerTimeResource> = MutableLiveData()
    val prayerTimes:MutableLiveData<PrayerTimeResource> get() = _prayerTimes


    fun getDashboard(localtion:String,language:String,requiredDate:String)
    {
        viewModelScope.launch {

            val getDashResponse = async {  repository.getDashboardData(language) }
            val getPrayerTimesResponse = async {  prayerTimesRepository.getPrayerTimes(localtion,language,requiredDate) }

            val dashResponse = getDashResponse.await()
            val prayerResponse = getPrayerTimesResponse.await()

            when(prayerResponse)
            {
                is ApiResource.Failure -> _prayerTimes.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                    if(prayerResponse.value?.Success == true)
                        _prayerTimes.value = PrayerTimeResource.postPrayerTime(prayerResponse.value)
                    else
                        _prayerTimes.value = PrayerTimeResource.prayerTimeEmpty

            }

            when(dashResponse)
            {
                is ApiResource.Failure -> _dashLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(dashResponse.value?.Success == true)
                        _dashLiveData.value = DashboardResource.DashboardData(dashResponse.value.Data)
                    else
                        _dashLiveData.value = CommonResource.API_CALL_FAILED
                }
            }
        }
    }


}