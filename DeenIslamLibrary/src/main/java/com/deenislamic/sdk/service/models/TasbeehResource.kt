package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.database.entity.Tasbeeh
import com.deenislamic.sdk.service.database.entity.UserPref

internal interface TasbeehResource {

    data class duaData(val data: Tasbeeh):TasbeehResource
    data class userPref(val userPref: UserPref?) :TasbeehResource
    data class recentCount(val data: List<Tasbeeh>):TasbeehResource
    data class resetDuaData(val data: Tasbeeh):TasbeehResource
}