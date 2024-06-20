package com.deenislamic.sdk.service.network.response.zakat.nisab

import androidx.annotation.Keep

@Keep
internal data class Data(
    val ChargeAmount: Int,
    val Id: Int,
    val Product: String
)