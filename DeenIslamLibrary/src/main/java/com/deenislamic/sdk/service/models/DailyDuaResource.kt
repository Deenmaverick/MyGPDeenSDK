package com.deenislamic.sdk.service.models

internal interface DailyDuaResource
{
    data class duaALlCategory(val data: List<com.deenislamic.sdk.service.network.response.dashboard.Data>) :DailyDuaResource
    data class duaPreview(val data: List<com.deenislamic.sdk.service.network.response.dailydua.duabycategory.Data>) :DailyDuaResource
    data class favDuaList(val data: List<com.deenislamic.sdk.service.network.response.dailydua.favdua.Data>) :DailyDuaResource

    data class setFavDua(val position: Int, val fav: Boolean) :DailyDuaResource

    data class todayDuaList(val data: List<com.deenislamic.sdk.service.network.response.dailydua.todaydua.Data>) :DailyDuaResource
}