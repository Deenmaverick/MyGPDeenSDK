package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.database.entity.UserPref

internal interface SettingResource {

    data class settingData(val data: UserPref?) :SettingResource

    data class languageDataUpdate(val language: String) :SettingResource
    object languageFailed:SettingResource
}