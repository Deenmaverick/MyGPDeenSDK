package com.deenislam.sdk.service.network.response.prayerlearning.visualization

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Content: List<Content>,
    val Head: List<Head>
)