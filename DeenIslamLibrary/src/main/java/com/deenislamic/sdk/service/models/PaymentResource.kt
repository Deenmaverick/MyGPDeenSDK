package com.deenislamic.sdk.service.models

internal interface PaymentResource {
    data class PaymentUrl(val message: String) :PaymentResource
    data class PaymentUrlNagad(val status: Boolean, val message: String?) :PaymentResource
    object PaymentIPNSuccess:PaymentResource
    object PaymentIPNFailed:PaymentResource
    object PaymentIPNCancle:PaymentResource
}