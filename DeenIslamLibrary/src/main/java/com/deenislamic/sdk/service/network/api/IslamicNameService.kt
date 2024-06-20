package com.deenislamic.sdk.service.network.api

import com.deenislamic.sdk.service.network.response.BasicResponse
import com.deenislamic.sdk.service.network.response.eidjamat.EidJamatListResponse
import com.deenislamic.sdk.service.network.response.islamicname.IslamicNameResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

internal interface IslamicNameService {

    @POST("IslamicName/AllFavNames")
    suspend fun getFavNames(@Body parm: RequestBody): IslamicNameResponse

    @POST("IslamicName/AddFavorite")
    suspend fun modifyFavName(@Body parm: RequestBody): BasicResponse

    @POST("IslamicName/GetAllNames")
    suspend fun getIslamicNames(@Body parm: RequestBody): IslamicNameResponse

    @POST("Islamic/getEidJamatByDivision")
    suspend fun getEidJamatListByDivision(@Body parm: RequestBody): EidJamatListResponse


}