package com.deenislam.sdk.service.models.common

internal interface ContentSettingResource {
    data class Update(val contentSetting: ContentSetting) :ContentSettingResource
}