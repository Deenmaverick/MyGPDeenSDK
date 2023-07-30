package com.deenislam.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.SettingResource
import com.deenislam.sdk.service.repository.SettingRepository
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
                _settingLiveData.value = SettingResource.languageData(response.language)
        }
    }

    fun getLanguage()
    {
        viewModelScope.launch {
            val response = repository.getLanguage()

            if(response == null)
                _settingLiveData.value = SettingResource.languageFailed
            else
                _settingLiveData.value = SettingResource.languageData(response.language)
        }

    }


}