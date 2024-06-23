package com.deenislamic.sdk.viewmodels.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.deenislamic.sdk.service.repository.PrayerTimesRepository
import com.deenislamic.sdk.viewmodels.PrayerTimesViewModel

internal class PrayerTimeVMFactory(
    private val prayerTimesRepository : PrayerTimesRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PrayerTimesViewModel(prayerTimesRepository) as T
    }
}