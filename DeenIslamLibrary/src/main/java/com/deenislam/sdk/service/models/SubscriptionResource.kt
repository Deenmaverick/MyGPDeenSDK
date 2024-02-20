package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.network.response.payment.recurring.CheckRecurringResponse

internal interface SubscriptionResource {

    data class CheckSubs(val value: CheckRecurringResponse) :SubscriptionResource
    data class AutoRenewCancel(val planStatus: String) :SubscriptionResource
}