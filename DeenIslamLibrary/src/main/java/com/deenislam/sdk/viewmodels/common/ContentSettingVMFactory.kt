package com.deenislam.sdk.viewmodels.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ContentSettingVMFactory : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContentSettingViewModel() as T
    }
}