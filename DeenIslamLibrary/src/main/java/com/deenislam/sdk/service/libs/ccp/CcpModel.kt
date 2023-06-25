package com.deenislam.sdk.service.libs.ccp

import com.deenislam.sdk.utils.getFlagByCC

data class CcpModel(
    var countryCode: String,
    var dialCode: String?,
    var countryName: String,
)
{
    val ic_flag: Int
        get() = getFlagByCC(countryCode)
}
