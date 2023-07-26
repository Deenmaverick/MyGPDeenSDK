package com.deenislam.sdk.viewmodels.quran.quranplayer;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.quran.quranplayer.ThemeResource
import com.deenislam.sdk.service.repository.quran.quranplayer.PlayerControlRepository
import kotlinx.coroutines.launch


internal class PlayerControlViewModel (
    private val repository: PlayerControlRepository
) : ViewModel() {

    private val _themeLiveData:MutableLiveData<ThemeResource> = MutableLiveData()
    val themeLiveData:MutableLiveData<ThemeResource> get() = _themeLiveData

    fun getSetting()
    {
        viewModelScope.launch {
            _themeLiveData.value = ThemeResource.playerSettings(repository.getSetting())
        }
    }

    fun updateThemeSetting(theme_font_size:Float, arabic_font:Int)
    {
        viewModelScope.launch {
            _themeLiveData.value = ThemeResource.playerSettings(repository.updateThemeSetting(theme_font_size,arabic_font))
        }
    }

    fun updateTranslationSetting(translation_font_size:Float,transliteration:Boolean)
    {
        viewModelScope.launch {
            _themeLiveData.value = ThemeResource.playerSettings(repository.updateTranslationSetting(translation_font_size,transliteration))
        }
    }

    fun updateAudioSetting(auto_scroll:Boolean,auto_play_next:Boolean)
    {
        viewModelScope.launch {
            _themeLiveData.value = ThemeResource.playerSettings(repository.updateAudioSetting(
                auto_scroll = auto_scroll,
                auto_play_next = auto_play_next
            ))
        }
    }
}