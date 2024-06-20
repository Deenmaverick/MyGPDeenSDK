package com.deenislamic.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.SettingResource
import com.deenislamic.sdk.service.repository.SettingRepository
import kotlinx.coroutines.launch

internal  class SettingViewModel(
    private val repository: SettingRepository
) : ViewModel() {

    private val _settingLiveData:MutableLiveData<SettingResource> = MutableLiveData()
    val settingLiveData:MutableLiveData<SettingResource> get() = _settingLiveData

    fun updateSetting(language:String)
    {
        viewModelScope.launch {
            val response = repository.updateSetting(language)

            if(response == null)
                _settingLiveData.value = SettingResource.languageFailed
            else
                _settingLiveData.value = SettingResource.languageDataUpdate(response.language)
        }
    }

    fun updateLocationSetting(loc:Boolean)
    {
        viewModelScope.launch {
            val response = repository.updateLocationSetting(loc)

            if(response == null)
                _settingLiveData.value = SettingResource.languageFailed
            else
                _settingLiveData.value = SettingResource.settingData(response)
        }
    }

    fun getSetting()
    {
        viewModelScope.launch {
            val response = repository.getSetting()

            if(response == null)
                _settingLiveData.value = SettingResource.languageFailed
            else
                _settingLiveData.value = SettingResource.settingData(response)
        }

    }

    fun clear()
    {
        _settingLiveData.value = CommonResource.CLEAR
    }

}