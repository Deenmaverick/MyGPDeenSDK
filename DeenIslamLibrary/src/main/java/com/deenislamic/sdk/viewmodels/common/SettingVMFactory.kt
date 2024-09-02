package com.deenislamic.sdk.viewmodels.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.deenislamic.sdk.service.repository.SettingRepository
import com.deenislamic.sdk.viewmodels.SettingViewModel

internal class SettingVMFactory(
    private val settingRepository: SettingRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingViewModel(settingRepository) as T
    }
}