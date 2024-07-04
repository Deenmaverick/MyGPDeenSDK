package com.deenislamic.sdk.views.quran

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.deenislamic.sdk.databinding.LayoutQuranNavDrawerBinding
import com.deenislamic.sdk.service.callback.AlQuranAyatCallback
import com.deenislamic.sdk.service.database.entity.PlayerSettingPref
import com.deenislamic.sdk.utils.AlQuranSetting_all
import com.deenislamic.sdk.utils.AlQuranSetting_arabic_font
import com.deenislamic.sdk.utils.AlQuranSetting_auto_play_next
import com.deenislamic.sdk.utils.AlQuranSetting_autoscroll
import com.deenislamic.sdk.utils.AlQuranSetting_bn_meaning
import com.deenislamic.sdk.utils.AlQuranSetting_bn_pronounce
import com.deenislamic.sdk.utils.AlQuranSetting_bn_tafsir
import com.deenislamic.sdk.utils.AlQuranSetting_bn_translator
import com.deenislamic.sdk.utils.AlQuranSetting_choose_qari
import com.deenislamic.sdk.utils.AlQuranSetting_en_translator
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.DEFAULT_ARABIC_FONT_ID
import com.deenislamic.sdk.utils.DEFAULT_BN_TAFSIR_ID
import com.deenislamic.sdk.utils.DEFAULT_BN_TRANSLATOR_ID
import com.deenislamic.sdk.utils.DEFAULT_EN_TRANSLATOR_ID
import com.deenislamic.sdk.utils.DEFAULT_QARI_ID
import com.deenislamic.sdk.utils.drawer
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.player
import com.deenislamic.sdk.viewmodels.quran.quranplayer.PlayerControlViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal object AlQuranSettings {

    private var updateSettingCall = false
    private var isArabicFontSizeCall = false
    private var isBanglaFontSizeCall = false
    private var isEnglishFontSizeCall = false
    var isUpdatePending = false
    var isAutoPlayNext = false
    var isServiceCallByUser = false

    private var callback = CallBackProvider.get<AlQuranAyatCallback>()


    fun setupDrawer(
        navdrawer: LayoutQuranNavDrawerBinding,
        lifecycleScope: LifecycleCoroutineScope,
        viewmodel: PlayerControlViewModel
    ) {

        callback = CallBackProvider.get<AlQuranAyatCallback>()

        navdrawer.bnPronunceSwitch.setOnCheckedChangeListener { _, b ->
            lifecycleScope.launch {
                viewmodel.updateBanglaPronounce(b, drawer + AlQuranSetting_bn_pronounce)
            }
        }

        navdrawer.bnMeaningSwitch.setOnCheckedChangeListener { _, b ->
            lifecycleScope.launch {
                viewmodel.updateBanglaMeaning(b, player + AlQuranSetting_bn_meaning)
            }
        }

        navdrawer.arabicFontSize.text = "0%".numberLocale()
        navdrawer.banglaFontSize.text = "0%".numberLocale()
        navdrawer.englishFontSize.text = "0%".numberLocale()

        navdrawer.arabicFontSlider.addOnChangeListener { _, value, _ ->
            if(!isArabicFontSizeCall) {
                navdrawer.arabicFontSize.text = "${value.toInt()}%".numberLocale()

                lifecycleScope.launch(Dispatchers.IO) {
                    viewmodel.updateFontSize(value, "arabic")
                }
            }else
                isArabicFontSizeCall = false

        }

        navdrawer.banglaFontSlider.addOnChangeListener { _, value, _ ->

            if(!isBanglaFontSizeCall) {
                navdrawer.banglaFontSize.text = "${value.toInt()}%".numberLocale()
                Log.e("FontSlider","bangla")
                lifecycleScope.launch(Dispatchers.IO) {
                    viewmodel.updateFontSize(value, "bangla")
                }
            }else
                isBanglaFontSizeCall = false

        }

        navdrawer.englishFontSlider.addOnChangeListener { _, value, _ ->

            if(!isEnglishFontSizeCall) {
                navdrawer.englishFontSize.text = "${value.toInt()}%".numberLocale()
                Log.e("FontSlider","english")
                lifecycleScope.launch(Dispatchers.IO) {
                    viewmodel.updateFontSize(value, "english")
                }
            }else
                isEnglishFontSizeCall = false

        }

        navdrawer.autoScrollSwtich.setOnCheckedChangeListener { _, b ->
            lifecycleScope.launch {
                viewmodel.updateAutoScroll(b,AlQuranSetting_autoscroll)
            }
        }

        navdrawer.autoPlaySwitch.setOnCheckedChangeListener { _, b ->
            lifecycleScope.launch {
                viewmodel.updateAutoPlayNext(b, AlQuranSetting_auto_play_next)
            }
        }

        navdrawer.chooseReciter.setOnClickListener {
            callback?.dialog_select_reciter()
        }

        navdrawer.chooseBanglaTranslator.setOnClickListener {
            callback?.dialog_select_translator("bn")
        }

        navdrawer.chooseEnglishTranslator.setOnClickListener {
            callback?.dialog_select_translator("en")
        }

        navdrawer.chooseBanglaTafsir.setOnClickListener {
            callback?.dialog_select_tafsirMaker()
        }

        navdrawer.chooseArabicFont.setOnClickListener {
            callback?.dialog_select_arabic_font()
        }

    }

    fun updateDrawer(navdrawer: LayoutQuranNavDrawerBinding, setting: PlayerSettingPref?, type:String) {

        callback = CallBackProvider.get<AlQuranAyatCallback>()

        updateSettingCall = true

        if(type!=AlQuranSetting_all)
        isUpdatePending = true

        if(type == player + AlQuranSetting_bn_pronounce || type == AlQuranSetting_all){
            setting?.transliteration?.let {
                    it1 -> navdrawer.bnPronunceSwitch.isChecked = it1
            }
        }

        if(type == player+AlQuranSetting_bn_meaning || type == AlQuranSetting_all){
            setting?.bn_meaning?.let {
                    it1 -> navdrawer.bnMeaningSwitch.isChecked = it1
            }
        }

        if(type == "pac_$AlQuranSetting_autoscroll" || type == AlQuranSetting_all){
            setting?.auto_scroll?.let {
                    it1 -> navdrawer.autoScrollSwtich.isChecked = it1
            }
        }

        if(type == "pac_$AlQuranSetting_auto_play_next" || type == AlQuranSetting_all){
            setting?.auto_play_next?.let { it1 ->
                isAutoPlayNext = it1
                navdrawer.autoPlaySwitch.isChecked = it1
            }
        }

        if(type == AlQuranSetting_choose_qari || type == AlQuranSetting_all){
            setting?.recitation?.let {
                    it1->
                val defvalue = if(it1!=1) it1 else  DEFAULT_QARI_ID
               navdrawer.chooseReciter.text = callback?.getqariList()?.firstOrNull { qari-> qari.title == defvalue }?.text.toString()
            }
        }

        if(type == AlQuranSetting_en_translator || type == AlQuranSetting_all){
            setting?.en_translator?.let {
                    it1->
                val defvalue = if(it1!=0) it1 else  DEFAULT_EN_TRANSLATOR_ID
                navdrawer.chooseEnglishTranslator.text = callback?.getTranslatorData()?.filter { it.language == "en" }?.firstOrNull { trn-> trn.title == defvalue }?.text.toString()
            }
        }

        if(type == AlQuranSetting_bn_translator || type == AlQuranSetting_all){
            setting?.bn_translator?.let {
                    it1->
                val defvalue = if(it1!=0) it1 else  DEFAULT_BN_TRANSLATOR_ID
                navdrawer.chooseBanglaTranslator.text = callback?.getTranslatorData()?.filter { it.language == "bn" }?.firstOrNull { trn-> trn.title == defvalue }?.text.toString()
            }
        }

        if(type == AlQuranSetting_bn_tafsir || type == AlQuranSetting_all){
            setting?.tafsir?.let {
                    it1->
                val defvalue = if(it1!=0) it1 else  DEFAULT_BN_TAFSIR_ID
                navdrawer.chooseBanglaTafsir.text = callback?.getTafsirList()?.filter { it.language == "bn" }?.firstOrNull { trn-> trn.title == defvalue }?.text.toString()
            }
        }

        if(type == AlQuranSetting_arabic_font || type == AlQuranSetting_all){
            setting?.arabic_font?.let {
                    it1->
                val defvalue = if(it1!=0) it1 else  DEFAULT_ARABIC_FONT_ID
                navdrawer.chooseArabicFont.text = callback?.getArabicFontList()?.firstOrNull { trn-> trn.fontid == defvalue.toString() }?.fontname.toString()
            }
        }

        if(type == "ptc_arabic" || type == AlQuranSetting_all){
            setting?.theme_font_size?.let { it1 ->
                val fontsize = (((it1.coerceIn(0F, 100F) + 10) / 20).toInt() * 20).toFloat()
                navdrawer.arabicFontSize.text = "${fontsize.toInt()}%".numberLocale()
                if(navdrawer.arabicFontSlider.value !=it1)
                    isArabicFontSizeCall = true
                navdrawer.arabicFontSlider.value =  fontsize
            }
        }

        if(type == "ptc_bangla" || type == AlQuranSetting_all){
            setting?.translation_font_size?.let { it1 ->

                val fontsize = (((it1.coerceIn(0F, 100F) + 10) / 20).toInt() * 20).toFloat()
                navdrawer.banglaFontSize.text = "${fontsize.toInt()}%".numberLocale()
                if(navdrawer.banglaFontSlider.value !=it1)
                    isBanglaFontSizeCall = true

                navdrawer.banglaFontSlider.value =  fontsize
            }
        }

        if(type == "ptc_english" || type == AlQuranSetting_all){
            setting?.english_font_size?.let { it1 ->
                val fontsize = (((it1.coerceIn(0F, 100F) + 10) / 20).toInt() * 20).toFloat()
                navdrawer.englishFontSize.text = "${fontsize.toInt()}%".numberLocale()
                if(navdrawer.englishFontSlider.value !=it1)
                    isEnglishFontSizeCall = true
                navdrawer.englishFontSlider.value =  fontsize
            }
        }

    }

    fun onDestroy(){
        isArabicFontSizeCall = true
        isBanglaFontSizeCall = true
        isEnglishFontSizeCall = true
    }
}