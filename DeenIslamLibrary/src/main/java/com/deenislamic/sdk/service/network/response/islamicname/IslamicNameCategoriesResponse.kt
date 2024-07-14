package com.deenislamic.sdk.service.network.response.islamicname


import androidx.annotation.Keep

@Keep
internal data class IslamicNameCategoriesResponse(
    var data: List<Data>,
    var message: String? = null,
    var success: Boolean? = null,
    var totalData: Int? = null
) {
    @Keep
    data class Data(
        var gender: String? = null,
        var id: Int,
        var title: String? = null
    )
}
