package com.deenislam.sdk.service.network.response.islamimasail.answer

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.islamimasail.answer.Data
import com.deenislam.sdk.service.network.response.paging.Pagination

@Keep
internal data class MasailAnswerResponse(
    val Data: List<Data>?,
    val Message: String,
    val Pagination: Pagination,
    val Success: Boolean
)