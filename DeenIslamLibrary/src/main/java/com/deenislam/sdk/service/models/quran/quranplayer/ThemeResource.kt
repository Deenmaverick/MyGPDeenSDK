package com.deenislam.sdk.service.models.quran.quranplayer

import com.deenislam.sdk.service.database.entity.PlayerSettingPref

sealed interface ThemeResource
{
    data class playerSettings(val setting: PlayerSettingPref?) : ThemeResource
}