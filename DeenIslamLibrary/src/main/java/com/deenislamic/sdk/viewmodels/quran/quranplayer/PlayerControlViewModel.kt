package com.deenislamic.sdk.viewmodels.quran.quranplayer;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.quran.quranplayer.ThemeResource
import com.deenislamic.sdk.service.repository.quran.quranplayer.PlayerControlRepository
import kotlinx.coroutines.launch


internal class PlayerControlViewModel(
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
            _themeLiveData.value = ThemeResource.updatePlayerSettings(repository.updateThemeSetting(theme_font_size,arabic_font))
        }
    }

    fun updatePortableZoom(theme_font_size:Float)
    {
        viewModelScope.launch {
            _themeLiveData.value = ThemeResource.updatePlayerSettings(repository.updatePortableZoom(theme_font_size))
        }
    }

    fun updateTranslationSetting(
        translation_font_size: Float,
        transliteration: Boolean,
        en_translator: Int,
        bn_translator: Int
    )
    {
        viewModelScope.launch {
            _themeLiveData.value = ThemeResource.updatePlayerSettings(repository.updateTranslationSetting(
                translation_font_size,
                transliteration,
                en_translator,
                bn_translator))
        }
    }

    fun updateAudioSetting(auto_scroll:Boolean,auto_play_next:Boolean,qari:Int)
    {
        viewModelScope.launch {
            _themeLiveData.value = ThemeResource.updatePlayerSettings(repository.updateAudioSetting(
                auto_scroll = auto_scroll,
                auto_play_next = auto_play_next,
                qari = qari
            ))
        }
    }

    fun updateTafsir(tafsir:Int)
    {
        viewModelScope.launch {
            _themeLiveData.value = ThemeResource.updatePlayerSettings(repository.updateTafsirMaker(
                tafsir = tafsir
            ))
        }
    }

    fun clear()
    {
        _themeLiveData.value = CommonResource.CLEAR
    }
}