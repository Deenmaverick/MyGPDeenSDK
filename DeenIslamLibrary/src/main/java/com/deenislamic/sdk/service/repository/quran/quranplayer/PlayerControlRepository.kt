package com.deenislamic.sdk.service.repository.quran.quranplayer;

import com.deenislamic.sdk.service.database.dao.PlayerSettingDao
import com.deenislamic.sdk.service.database.entity.PlayerSettingPref
import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.utils.AlQuranSetting_bn_translator
import com.deenislamic.sdk.utils.AlQuranSetting_en_translator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class PlayerControlRepository (
private val playerSettingDao: PlayerSettingDao?
): ApiCall {

    suspend fun getSetting() =
        withContext(Dispatchers.IO)
        {
            val setting = playerSettingDao?.select()

            if(setting?.isEmpty() == true) {
                playerSettingDao?.insert(PlayerSettingPref())
                return@withContext PlayerSettingPref()
            } else
                return@withContext setting?.get(0)
        }

    suspend fun updateThemeSetting(
        theme_font_size: Float,
        arabic_font: Int,
        bangla_font_size: Float
    ) =
        withContext(Dispatchers.IO)
        {
            val setting = playerSettingDao?.select()

            setting?.get(0)?.theme_font_size = theme_font_size
            setting?.get(0)?.arabic_font = arabic_font
            setting?.get(0)?.translation_font_size = bangla_font_size

            val result = setting?.let { playerSettingDao?.update(it) } ?: 0
            if (result > 0)
                playerSettingDao?.select()?.get(0)
            else
                setting?.get(0)

        }

    suspend fun updateTranslationSetting(
        translation_font_size: Float,
        transliteration: Boolean,
        en_translator: Int,
        bn_translator: Int
    ) =
        withContext(Dispatchers.IO)
        {
            val setting = playerSettingDao?.select()

            setting?.get(0)?.translation_font_size = translation_font_size
            setting?.get(0)?.transliteration = transliteration
            setting?.get(0)?.en_translator = en_translator
            setting?.get(0)?.bn_translator = bn_translator

            if(setting!=null && playerSettingDao!=null){
                if(playerSettingDao.update(setting)>0)
                    playerSettingDao.select()[0]
                else
                    setting[0]
            }
            else
                null



        }

    suspend fun updateAudioSetting(auto_scroll:Boolean,auto_play_next:Boolean,qari:Int) =
        withContext(Dispatchers.IO)
        {
            val setting = playerSettingDao?.select()

            setting?.get(0)?.auto_scroll = auto_scroll
            setting?.get(0)?.auto_play_next = auto_play_next
            setting?.get(0)?.recitation = qari


            if(setting!=null && playerSettingDao!=null){

                if(playerSettingDao.update(setting)>0)
                    playerSettingDao.select()[0]
                else
                    setting[0]
            }
            else
                null



        }


    suspend fun updateTafsirMaker(tafsir:Int) =
        withContext(Dispatchers.IO)
        {

            val setting = playerSettingDao?.select()

            setting?.get(0)?.tafsir = tafsir

            if(setting!=null && playerSettingDao!=null){
                if(playerSettingDao.update(setting) >0)
            return@withContext playerSettingDao.select()[0]
                else
                    return@withContext setting[0]
            } else {
                null
            }

        }

    suspend fun updatePortableZoom(theme_font_size:Float) =
        withContext(Dispatchers.IO)
        {
            val setting = playerSettingDao?.select()

            setting?.get(0)?.theme_font_size = theme_font_size

            if(setting!=null && playerSettingDao!=null){
                if(playerSettingDao.update(setting)>0)
                    playerSettingDao.select()[0]
                else
                    setting[0]
            }
            else
                null


        }


    suspend fun updateBanglaPronounce(
        isEnable: Boolean
    ) =
        withContext(Dispatchers.IO)
        {
            val setting = playerSettingDao?.select()

            setting?.get(0)?.transliteration = isEnable

            val result = setting?.let { playerSettingDao?.update(it) } ?: 0
            if (result > 0)
                playerSettingDao?.select()?.get(0)
            else
                setting?.get(0)

        }

    suspend fun updateBanglaMeaning(
        isEnable: Boolean
    ) =
        withContext(Dispatchers.IO)
        {
            val setting = playerSettingDao?.select()

            setting?.get(0)?.bn_meaning = isEnable

            val result = setting?.let { playerSettingDao?.update(it) } ?: 0
            if (result > 0)
                playerSettingDao?.select()?.get(0)
            else
                setting?.get(0)

        }


    suspend fun updateAutoScroll(
        isEnable: Boolean
    ) =
        withContext(Dispatchers.IO)
        {
            val setting = playerSettingDao?.select()

            setting?.get(0)?.auto_scroll = isEnable

            val result = setting?.let { playerSettingDao?.update(it) } ?: 0
            if (result > 0)
                playerSettingDao?.select()?.get(0)
            else
                setting?.get(0)

        }

    suspend fun updateAutoPlayNext(
        isEnable: Boolean
    ) =
        withContext(Dispatchers.IO)
        {
            val setting = playerSettingDao?.select()

            setting?.get(0)?.auto_play_next = isEnable

            val result = setting?.let { playerSettingDao?.update(it) } ?: 0
            if (result > 0)
                playerSettingDao?.select()?.get(0)
            else
                setting?.get(0)

        }


    suspend fun updateFontSize(
        size: Float,
        type: String
    ) =
        withContext(Dispatchers.IO)
        {
            val setting = playerSettingDao?.select()

            when(type){
                "arabic","ptc_arabic" -> setting?.get(0)?.theme_font_size = size
                "bangla","ptc_bangla" -> setting?.get(0)?.translation_font_size = size
                "english","ptc_english" -> setting?.get(0)?.english_font_size = size
            }


            val result = setting?.let { playerSettingDao?.update(it) } ?: 0
            if (result > 0)
                playerSettingDao?.select()?.get(0)
            else
                setting?.get(0)

        }

    suspend fun updateQari(qari:Int) =
        withContext(Dispatchers.IO)
        {

            val setting = playerSettingDao?.select()

            setting?.get(0)?.recitation = qari

            val result = setting?.let { playerSettingDao?.update(it) } ?: 0
            if (result > 0)
                playerSettingDao?.select()?.get(0)
            else
                setting?.get(0)

        }

    suspend fun updateTranslator(translator: Int, type: String) =
        withContext(Dispatchers.IO)
        {

            val setting = playerSettingDao?.select()

            when(type){
                AlQuranSetting_en_translator -> setting?.get(0)?.en_translator = translator
                AlQuranSetting_bn_translator -> setting?.get(0)?.bn_translator = translator
            }


            val result = setting?.let { playerSettingDao?.update(it) } ?: 0
            if (result > 0)
                playerSettingDao?.select()?.get(0)
            else
                setting?.get(0)

        }

    suspend fun updateArabicFont(font:Int) =
        withContext(Dispatchers.IO)
        {

            val setting = playerSettingDao?.select()

            setting?.get(0)?.arabic_font = font

            val result = setting?.let { playerSettingDao?.update(it) } ?: 0
            if (result > 0)
                playerSettingDao?.select()?.get(0)
            else
                setting?.get(0)

        }

} 