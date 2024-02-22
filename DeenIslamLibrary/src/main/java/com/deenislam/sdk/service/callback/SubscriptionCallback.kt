package com.deenislam.sdk.service.callback

import com.deenislam.sdk.service.network.response.subscription.PaymentType

internal interface SubscriptionCallback {

    fun selectedPack(getdata: PaymentType)
}