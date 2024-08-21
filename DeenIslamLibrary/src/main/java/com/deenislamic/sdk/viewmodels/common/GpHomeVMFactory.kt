package com.deenislamic.sdk.viewmodels.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.deenislamic.sdk.service.repository.GPHomeRespository
import com.deenislamic.sdk.viewmodels.GPHomeViewModel

internal class GpHomeVMFactory(
    private val gpHomeRespository: GPHomeRespository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GPHomeViewModel(gpHomeRespository) as T
    }
}