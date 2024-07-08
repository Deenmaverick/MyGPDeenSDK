package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.database.entity.Tasbeeh
import com.deenislamic.sdk.service.database.entity.UserPref
import com.deenislamic.sdk.service.models.tasbeeh.WeeklyChartData

internal interface TasbeehResource {

    data class DuaData(val data: Tasbeeh):TasbeehResource
    data class userPref(val userPref: UserPref?) :TasbeehResource
    data class RecentCount(val data: List<Tasbeeh>, val weeklyChartData: ArrayList<WeeklyChartData> = arrayListOf()):TasbeehResource
    data class resetDuaData(val data: Tasbeeh):TasbeehResource

    data class TodayCount(val todayCount: Long):TasbeehResource
    data class WeeklyCount(val weeklyCount: Long) :TasbeehResource
    data class TotalCount(val totalCount: Long) :TasbeehResource
}