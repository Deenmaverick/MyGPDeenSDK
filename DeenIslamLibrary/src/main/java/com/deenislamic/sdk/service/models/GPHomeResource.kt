package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.gphome.Data

internal interface GPHomeResource {

    data class GPHome(val data: Data) :GPHomeResource
    data class GPHomePrayerTrack(val prayer_tag: String,val status:Boolean) :GPHomeResource
}