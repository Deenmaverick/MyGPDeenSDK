package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.subscription.Data

internal interface SubscriptionResource {

    data class CheckSubs(val value: Data) :SubscriptionResource
    data class AutoRenewCancel(val planStatus: String) :SubscriptionResource
}