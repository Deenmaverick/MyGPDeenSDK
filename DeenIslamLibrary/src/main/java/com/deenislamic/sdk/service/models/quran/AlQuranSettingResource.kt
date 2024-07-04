package com.deenislamic.sdk.service.models.quran

import com.deenislamic.sdk.service.database.entity.PlayerSettingPref

internal interface AlQuranSettingResource
{
    data class AlQuranSettings(val setting: PlayerSettingPref?) : AlQuranSettingResource
    data class UpdateAlQuranSettings(val setting: PlayerSettingPref?,val type:String="all") : AlQuranSettingResource
}