package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.database.entity.Tasbeeh
import com.deenislam.sdk.service.database.entity.UserPref

interface TasbeehResource {

    data class duaData(val data: Tasbeeh):TasbeehResource
    data class userPref(val userPref: UserPref?) :TasbeehResource
    data class recentCount(val data: List<Tasbeeh>):TasbeehResource
    data class resetDuaData(val data: Tasbeeh):TasbeehResource
}