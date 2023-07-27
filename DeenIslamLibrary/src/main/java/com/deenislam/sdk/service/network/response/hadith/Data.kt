package com.deenislam.sdk.service.network.response.hadith

import com.deenislam.sdk.service.network.response.hadith.Collection

internal data class Data(
    val collection: List<Collection>,
    val hasBooks: Boolean,
    val hasChapters: Boolean,
    val name: String,
    val totalAvailableHadith: Int,
    val totalHadith: Int
)