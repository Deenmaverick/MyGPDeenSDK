package com.deenislamic.sdk.service.network.response.khatamquran


import androidx.annotation.Keep
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.google.gson.annotations.SerializedName

@Keep
internal data class KhatamQuranVideosResponse(
    @SerializedName("Data")
    var `data`: List<CommonCardData>?,
    @SerializedName("Message")
    var message: String? = null,
    @SerializedName("Pagination")
    var pagination: Pagination? = null,
    @SerializedName("Success")
    var success: Boolean? = null
) {
    internal data class Data(
        @SerializedName("category")
        var category: String? = null,
        @SerializedName("categoryID")
        var categoryID: Int? = null,
        @SerializedName("duration")
        var duration: Any? = null,
        @SerializedName("Id")
        var id: Int? = null,
        @SerializedName("imageurl")
        var imageurl: String? = null,
        @SerializedName("IsFavorite")
        var isFavorite: Boolean? = null,
        @SerializedName("reference")
        var reference: Any? = null,
        @SerializedName("referenceurl")
        var referenceurl: Any? = null,
        @SerializedName("title")
        var title: String? = null,
        @SerializedName("videourl")
        var videourl: String? = null
    )

    internal data class Pagination(
        @SerializedName("isNext")
        var isNext: Boolean? = null,
        @SerializedName("isPrevious")
        var isPrevious: Boolean? = null,
        @SerializedName("TotalData")
        var totalData: Int? = null,
        @SerializedName("TotalPage")
        var totalPage: Int? = null
    )
}