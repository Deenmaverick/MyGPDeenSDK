package com.deenislamic.sdk.service.network.response.islamicevent


import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
internal data class IslamicEventSubCatListResponse(
    var data: @RawValue List<Data>,
    var message: String? = null,
    var success: Boolean? = null,
    var totalData: Int? = null
) : Parcelable {
    @Keep
    @Parcelize
    data class Data(
        var category: String? = null,
        var categoryID: Int? = null,
        var id: Int? = null,
        var imageurl: String? = null,
        var language: String? = null,
        var pronunciation: String? = null,
        var serial: Int? = null,
        var text: String? = null,
        var textinarabic: String? = null,
        var title: String
    ): Parcelable
}
