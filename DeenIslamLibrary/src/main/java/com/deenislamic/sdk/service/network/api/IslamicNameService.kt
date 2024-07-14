package com.deenislamic.sdk.service.network.api

import com.deenislamic.sdk.service.network.response.BasicResponse
import com.deenislamic.sdk.service.network.response.islamicname.IslamicNameCategoriesResponse
import com.deenislamic.sdk.service.network.response.islamicname.IslamicNameHomeResponse
import com.deenislamic.sdk.service.network.response.islamicname.IslamicNameResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

internal interface IslamicNameService {

    @POST("IslamicName/AllFavNames")
    suspend fun getFavNames(@Body parm: RequestBody): IslamicNameResponse

    @POST("IslamicName/AddFavorite")
    suspend fun modifyFavName(@Body parm: RequestBody): BasicResponse

    @POST("IslamicName/GetAllNamesAlphabetWise")
    suspend fun getIslamicNames(@Body parm: RequestBody): IslamicNameResponse

    @POST("IslamicName/GetAllCategoryBygender")
    suspend fun getIslamicNameCategories(@Body parm: RequestBody): IslamicNameCategoriesResponse

    @POST("IslamicName/GetAllNamesByCategory")
    suspend fun getIslamicNamesByCatId(@Body parm: RequestBody): IslamicNameResponse

    @POST("IslamicName/GetNamePatch")
    suspend fun getIslamicNamesPatch(@Body parm: RequestBody): IslamicNameHomeResponse


}