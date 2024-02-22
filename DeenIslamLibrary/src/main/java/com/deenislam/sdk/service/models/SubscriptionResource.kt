package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.network.response.subscription.Data

internal interface SubscriptionResource {

    data class CheckSubs(val value: Data) :SubscriptionResource
    data class AutoRenewCancel(val planStatus: String) :SubscriptionResource
}