package com.deenislam.sdk.service.models

internal interface SettingResource {

    data class languageData(val language: String) :SettingResource
    object languageFailed:SettingResource
}