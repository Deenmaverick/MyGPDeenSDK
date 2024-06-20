package com.deenislamic.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.PrayerLearningResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.repository.PrayerLearningRepository
import kotlinx.coroutines.launch


internal class PrayerLearningViewModel(
    private val repository: PrayerLearningRepository
) : ViewModel() {

    private val _prayerLearningCatLiveData: MutableLiveData<PrayerLearningResource> = MutableLiveData()
    val prayerLearningCatLiveData: MutableLiveData<PrayerLearningResource> get() = _prayerLearningCatLiveData

    private val _prayerLearningVisualizationLiveData: MutableLiveData<PrayerLearningResource> = MutableLiveData()
    val prayerLearningVisualizationLiveData: MutableLiveData<PrayerLearningResource> get() = _prayerLearningVisualizationLiveData

    private val _prayerLearningSubCatLiveData: MutableLiveData<PrayerLearningResource> = MutableLiveData()
    val prayerLearningSubCatLiveData: MutableLiveData<PrayerLearningResource> get() = _prayerLearningSubCatLiveData

    fun getAllCat(language:String)
    {

        viewModelScope.launch {

            when(val response = repository.getPrayerLeareningAllCategory(language))
            {
                is ApiResource.Failure -> _prayerLearningCatLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.isNotEmpty() == true)
                        _prayerLearningCatLiveData.value = PrayerLearningResource.AllCat(response.value.Data)
                    else
                        _prayerLearningCatLiveData.value = CommonResource.EMPTY

                }
            }
        }

    }

    fun getVisualization(language: String,gender:String)
    {
        viewModelScope.launch {
            when(val response = repository.getPrayerLeareningVisualization(
                language = language,
                gender = gender
            ))
            {
                is ApiResource.Failure -> _prayerLearningVisualizationLiveData.value =  CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.Content?.isNotEmpty() == true && response.value.Data.Head.isNotEmpty())
                        _prayerLearningVisualizationLiveData.value = PrayerLearningResource.Visualization(response.value.Data)
                    else
                        _prayerLearningVisualizationLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun getSubCatList(language: String,cat:Int)
    {
        viewModelScope.launch {

            when(val response = repository.getPrayerLeareningSubCat(language = language, cat = cat))
            {
                is ApiResource.Failure -> _prayerLearningSubCatLiveData.value =  CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.isNotEmpty() == true)
                        _prayerLearningSubCatLiveData.value = PrayerLearningResource.SubCatList(response.value.Data)
                    else
                        _prayerLearningSubCatLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }
}