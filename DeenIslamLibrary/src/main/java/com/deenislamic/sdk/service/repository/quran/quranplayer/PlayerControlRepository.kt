package com.deenislamic.sdk.service.repository.quran.quranplayer;

import com.deenislamic.sdk.service.database.dao.PlayerSettingDao
import com.deenislamic.sdk.service.database.entity.PlayerSettingPref
import com.deenislamic.sdk.service.network.ApiCall
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

    suspend fun updateThemeSetting(theme_font_size:Float, arabic_font:Int) =
        withContext(Dispatchers.IO)
        {
            val setting = playerSettingDao?.select()

            setting?.get(0)?.theme_font_size = theme_font_size
            setting?.get(0)?.arabic_font = arabic_font

            setting?.let {
                if(playerSettingDao?.update(it)!! >0)
                    playerSettingDao.select()[0]
                else
                    setting[0]
            }

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

} 