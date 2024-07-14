package com.deenislamic.sdk.service.network.response.quran.learning.quransikkhaacademy


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class ContentByIdResponse(
    var code: Int? = null,
    var data: Data? = null,
    var msg: String? = null,
    var status: String? = null
) {
    @Keep
    internal data class Data(
        var card: String? = null,
        var contentType: String? = null,
        var createdAt: String? = null,
        var hls: String? = null,
        var id: String? = null,
        var isFree: Boolean? = null,
        var isPublished: Boolean? = null,
        var privateMetadata: PrivateMetadata? = null,
        var publicMetadata: PublicMetadata? = null,
        var sortIndex: Int? = null,
        var title: String? = null,
        var updatedAt: String? = null,
        var url: String? = null,
        var v: Int? = null
    ) {
        @Keep
        internal data class PrivateMetadata(
            var vimeoId: Int? = null
        )

        @Keep
        internal data class PublicMetadata(
            var duration: Int? = null
        )
    }
}
