package com.deenislamic.sdk.service.network.response.gphome

import androidx.annotation.Keep
import com.deenislamic.sdk.service.network.response.prayertimes.Data

@Keep
internal data class Data(
    val EventImages: String,
    val IslamicDate: String,
    val IslamicEvent: String,
    val Menu: List<Menu>,
    val PrayerTime: Data,
    val isPremium:String?=""

)