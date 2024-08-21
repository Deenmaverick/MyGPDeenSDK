package com.deenislamic.sdk.service.network.response.subscription

import androidx.annotation.Keep
import com.deenislamic.sdk.service.network.response.payment.recurring.CheckRecurringResponse

@Keep
internal data class  Data(
    val paymentTypes: List<PaymentType>,
    val paymentMethod: paymentMethod,
    val premiumFeatures: List<PremiumFeature>,
    val Banners: List<Banner>,
    val faq: List<Faq>,
    val scholars: List<Scholar>,
    val pageResponse: CheckRecurringResponse?
)