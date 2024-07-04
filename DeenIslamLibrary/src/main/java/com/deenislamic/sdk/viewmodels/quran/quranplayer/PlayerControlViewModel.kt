package com.deenislamic.sdk.viewmodels.quran.quranplayer;

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.quran.AlQuranSettingResource
import com.deenislamic.sdk.service.repository.quran.quranplayer.PlayerControlRepository
import com.deenislamic.sdk.utils.AlQuranSetting_arabic_font
import com.deenislamic.sdk.utils.AlQuranSetting_bn_tafsir
import com.deenislamic.sdk.utils.AlQuranSetting_choose_qari
import kotlinx.coroutines.launch


internal class PlayerControlViewModel(
    private val repository: PlayerControlRepository
) : ViewModel() {

    private val _alQuranSettingsLiveData:MutableLiveData<AlQuranSettingResource> = MutableLiveData()
    val alQuranSettingsLiveData:MutableLiveData<AlQuranSettingResource> get() = _alQuranSettingsLiveData

    fun getSetting()
    {
        viewModelScope.launch {
            _alQuranSettingsLiveData.value = AlQuranSettingResource.AlQuranSettings(repository.getSetting())
        }
    }

    fun updateBanglaPronounce(isEnable: Boolean, type: String){
        viewModelScope.launch {
            _alQuranSettingsLiveData.value = AlQuranSettingResource.UpdateAlQuranSettings(repository.updateBanglaPronounce(isEnable), type
            )
        }
    }

    fun updateBanglaMeaning(isEnable: Boolean, type: String){
        viewModelScope.launch {
            _alQuranSettingsLiveData.value = AlQuranSettingResource.UpdateAlQuranSettings(repository.updateBanglaMeaning(isEnable),type)
        }
    }

    fun updateAutoScroll(isEnable: Boolean, type: String){
        viewModelScope.launch {
            _alQuranSettingsLiveData.value = AlQuranSettingResource.UpdateAlQuranSettings(repository.updateAutoScroll(isEnable),type)
        }
    }

    fun updateAutoPlayNext(isEnable: Boolean, type: String){
        viewModelScope.launch {
            _alQuranSettingsLiveData.value = AlQuranSettingResource.UpdateAlQuranSettings(repository.updateAutoPlayNext(isEnable),type)
        }
    }

    fun updateFontSize(size: Float,type:String){
        viewModelScope.launch {
            _alQuranSettingsLiveData.value = AlQuranSettingResource.UpdateAlQuranSettings(repository.updateFontSize(size,type),type)
        }
    }

    fun updateQari(qariID:Int){
        viewModelScope.launch {
            _alQuranSettingsLiveData.value = AlQuranSettingResource.UpdateAlQuranSettings(repository.updateQari(qariID),AlQuranSetting_choose_qari)
        }
    }

    fun updateTranslator(translator:Int,type: String){
        viewModelScope.launch {
            _alQuranSettingsLiveData.value = AlQuranSettingResource.UpdateAlQuranSettings(repository.updateTranslator(translator,type),type)
        }
    }

    fun updateArabicFont(font:Int){
        viewModelScope.launch {
            _alQuranSettingsLiveData.value = AlQuranSettingResource.UpdateAlQuranSettings(repository.updateArabicFont(font),AlQuranSetting_arabic_font)
        }
    }

    fun updateThemeSetting(theme_font_size: Float, arabic_font: Int, bangla_font_size: Float)
    {
        viewModelScope.launch {
            _alQuranSettingsLiveData.value = AlQuranSettingResource.UpdateAlQuranSettings(repository.updateThemeSetting(theme_font_size,arabic_font,bangla_font_size))
        }
    }

    fun updatePortableZoom(theme_font_size:Float)
    {
        viewModelScope.launch {
            _alQuranSettingsLiveData.value = AlQuranSettingResource.UpdateAlQuranSettings(repository.updatePortableZoom(theme_font_size))
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
            Log.e("updateTranslationSet","CALLED")
            _alQuranSettingsLiveData.value = AlQuranSettingResource.UpdateAlQuranSettings(repository.updateTranslationSetting(
                translation_font_size,
                transliteration,
                en_translator,
                bn_translator))
        }
    }

    fun updateAudioSetting(auto_scroll:Boolean,auto_play_next:Boolean,qari:Int)
    {
        viewModelScope.launch {
            _alQuranSettingsLiveData.value = AlQuranSettingResource.UpdateAlQuranSettings(repository.updateAudioSetting(
                auto_scroll = auto_scroll,
                auto_play_next = auto_play_next,
                qari = qari
            ))
        }
    }

    fun updateTafsir(tafsir:Int)
    {
        viewModelScope.launch {
            _alQuranSettingsLiveData.value = AlQuranSettingResource.UpdateAlQuranSettings(repository.updateTafsirMaker(
                tafsir = tafsir
            ),AlQuranSetting_bn_tafsir)
        }
    }

    fun clear()
    {
        _alQuranSettingsLiveData.value = CommonResource.CLEAR
    }
}