package com.deenislam.sdk.service.network.response.islamicevent


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
internal data class IslamicEventSubCatListResponse(
    @SerializedName("Data")
    var `data`: @RawValue List<Data>,
    @SerializedName("Message")
    var message: String? = null,
    @SerializedName("Success")
    var success: Boolean? = null,
    @SerializedName("TotalData")
    var totalData: Int? = null
) : Parcelable {
    @Keep
    @Parcelize
    data class Data(
        @SerializedName("category")
        var category: String? = null,
        @SerializedName("CategoryID")
        var categoryID: Int? = null,
        @SerializedName("Id")
        var id: Int? = null,
        @SerializedName("imageurl")
        var imageurl: String? = null,
        @SerializedName("language")
        var language: String? = null,
        @SerializedName("pronunciation")
        var pronunciation: String? = null,
        @SerializedName("serial")
        var serial: Int? = null,
        @SerializedName("Text")
        var text: String? = null,
        @SerializedName("textinarabic")
        var textinarabic: String? = null,
        @SerializedName("Title")
        var title: String
    ): Parcelable
}