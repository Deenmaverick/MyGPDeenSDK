package com.deenislam.sdk.service.network.response.quran.verses

data class Pagination(
    val current_page: Int,
    val next_page: Int?,
    val per_page: Int,
    val total_pages: Int,
    val total_records: Int
)