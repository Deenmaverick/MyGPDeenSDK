package com.deenislamic.sdk.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.libs.advertisement.Advertisement
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.DashboardResource
import com.deenislamic.sdk.service.models.prayer_time.PrayerTimeResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.repository.DashboardRepository
import com.deenislamic.sdk.service.repository.PrayerTimesRepository
import com.deenislamic.sdk.utils.Subscription
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

            val getAdvertisement = async {  repository.getAdData(language) }.await()
            val getDashResponse = async {  repository.getDashboardData(language) }
            val getPrayerTimesResponse = async {  prayerTimesRepository.getPrayerTimes(localtion,language,requiredDate) }

            val dashResponse = getDashResponse.await()
            val prayerResponse = getPrayerTimesResponse.await()

            when(getAdvertisement){

                is ApiResource.Failure -> Unit
                is ApiResource.Success -> {
                    if(getAdvertisement.value?.Data?.isNotEmpty() == true){
                        Advertisement.adData = getAdvertisement.value.Data.toMutableList()
                        Advertisement.imageAdData = getAdvertisement.value.Data.filter { it.categoryName == "image_ad" }.toMutableList()
                        Advertisement.videoAdData = getAdvertisement.value.Data.filter { it.categoryName == "video_ad" }.toMutableList()
                    }

                }
            }

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
                    if(dashResponse.value?.Success == true) {
                        Subscription.isSubscribe = dashResponse.value.isPremium == "1BK"
                        _dashLiveData.value =
                            DashboardResource.DashboardData(dashResponse.value.Data)
                    }
                    else
                        _dashLiveData.value = CommonResource.API_CALL_FAILED
                }
            }
        }
    }

    suspend fun getPrayerTime(localtion:String,language:String,requiredDate:String){
        viewModelScope.launch {
            when(val prayerResponse = prayerTimesRepository.getPrayerTimes(localtion,language,requiredDate))
            {
                is ApiResource.Failure -> _prayerTimes.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                    if(prayerResponse.value?.Success == true)
                        _prayerTimes.value = PrayerTimeResource.postPrayerTime(prayerResponse.value)
                    else
                        _prayerTimes.value = PrayerTimeResource.prayerTimeEmpty

            }
        }
    }

    suspend fun saveAdvertisementrecord(adID:Int,response:String){
        viewModelScope.launch {
            repository.saveAdvertisementrecord(adID,response)
        }
    }


}