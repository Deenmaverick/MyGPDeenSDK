package com.deenislam.sdk.service.repository.quran.quranplayer;

import com.deenislam.sdk.service.database.dao.PlayerSettingDao
import com.deenislam.sdk.service.database.entity.PlayerSettingPref
import com.deenislam.sdk.service.network.ApiCall
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

    suspend fun updateTranslationSetting(translation_font_size:Float,transliteration:Boolean) =
        withContext(Dispatchers.IO)
        {
            val setting = playerSettingDao?.select()

            setting?.get(0)?.translation_font_size = translation_font_size
            setting?.get(0)?.transliteration = transliteration

            setting?.let {
                if(playerSettingDao?.update(it)!! >0)
                    playerSettingDao.select()[0]
                else
                    setting[0]
            }


        }

    suspend fun updateAudioSetting(auto_scroll:Boolean,auto_play_next:Boolean) =
        withContext(Dispatchers.IO)
        {
            val setting = playerSettingDao?.select()

            setting?.get(0)?.auto_scroll = auto_scroll
            setting?.get(0)?.auto_play_next = auto_play_next

            setting?.let {
                if(playerSettingDao?.update(it)!! >0)
                    playerSettingDao.select()[0]
                else
                    setting[0]
            }

        }
} 