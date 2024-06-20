package com.deenislamic.sdk.viewmodels.common;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.common.ContentSetting
import com.deenislamic.sdk.service.models.common.ContentSettingResource
import kotlinx.coroutines.launch


internal class ContentSettingViewModel : ViewModel() {

    private val _contentSettingLiveData: MutableLiveData<ContentSettingResource> = MutableLiveData()
    val contentSettingLiveData: MutableLiveData<ContentSettingResource> get() = _contentSettingLiveData


    suspend fun update(contentSetting: ContentSetting) {
        viewModelScope.launch {
            _contentSettingLiveData.value = ContentSettingResource.Update(contentSetting)
        }
    }

}