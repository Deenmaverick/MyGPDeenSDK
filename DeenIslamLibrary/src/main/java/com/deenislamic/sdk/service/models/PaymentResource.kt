package com.deenislamic.sdk.service.models

internal interface PaymentResource {
    data class PaymentUrl(val message: String,val isDonation:Boolean=false) :PaymentResource
    data class PaymentUrlNagad(val status: Boolean, val message: String?,val isDonation:Boolean=false) :PaymentResource
    object PaymentIPNSuccess:PaymentResource
    object PaymentIPNFailed:PaymentResource
    object PaymentIPNCancle:PaymentResource
}