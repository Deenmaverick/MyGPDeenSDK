package com.deenislamic.sdk.viewmodels.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.deenislamic.sdk.service.repository.quran.quranplayer.PlayerControlRepository
import com.deenislamic.sdk.viewmodels.quran.quranplayer.PlayerControlViewModel

internal class PlayerControlVMFactory(
    private val repository: PlayerControlRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerControlViewModel(repository) as T
    }
}