package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data

internal interface SubCatCardListResource {

    data class SubcatList(val data: List<Data>) :SubCatCardListResource
}