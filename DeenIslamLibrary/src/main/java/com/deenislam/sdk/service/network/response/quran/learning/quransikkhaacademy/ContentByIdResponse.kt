package com.deenislam.sdk.service.network.response.quran.learning.quransikkhaacademy


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class ContentByIdResponse(
    @SerializedName("code")
    var code: Int? = null,
    @SerializedName("data")
    var `data`: Data? = null,
    @SerializedName("msg")
    var msg: String? = null,
    @SerializedName("status")
    var status: String? = null
) {
    @Keep
    internal data class Data(
        @SerializedName("card")
        var card: String? = null,
        @SerializedName("content_type")
        var contentType: String? = null,
        @SerializedName("createdAt")
        var createdAt: String? = null,
        @SerializedName("hls")
        var hls: String? = null,
        @SerializedName("_id")
        var id: String? = null,
        @SerializedName("is_free")
        var isFree: Boolean? = null,
        @SerializedName("is_published")
        var isPublished: Boolean? = null,
        @SerializedName("private_metadata")
        var privateMetadata: PrivateMetadata? = null,
        @SerializedName("public_metadata")
        var publicMetadata: PublicMetadata? = null,
        @SerializedName("sort_index")
        var sortIndex: Int? = null,
        @SerializedName("title")
        var title: String? = null,
        @SerializedName("updatedAt")
        var updatedAt: String? = null,
        @SerializedName("url")
        var url: String? = null,
        @SerializedName("__v")
        var v: Int? = null
    ) {
        @Keep
        internal data class PrivateMetadata(
            @SerializedName("vimeo_id")
            var vimeoId: Int? = null
        )

        @Keep
        internal data class PublicMetadata(
            @SerializedName("duration")
            var duration: Int? = null
        )
    }
}
