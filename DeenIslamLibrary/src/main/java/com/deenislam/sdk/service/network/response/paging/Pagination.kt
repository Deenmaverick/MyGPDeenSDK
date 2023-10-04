package com.deenislam.sdk.service.network.response.paging

data class Pagination(
    val TotalData: Int,
    val TotalPage: Int,
    val isNext: Boolean,
    val isPrevious: Boolean
)