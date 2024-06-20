package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.network.response.subscription.PaymentType

internal interface SubscriptionCallback {

    fun selectedPack(getdata: PaymentType)
}