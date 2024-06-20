package com.deenislamic.sdk.service.network.response.paging

import androidx.annotation.Keep

@Keep
internal data class Pagination(
    val TotalData: Int,
    val TotalPage: Int,
    val isNext: Boolean,
    val isPrevious: Boolean
)