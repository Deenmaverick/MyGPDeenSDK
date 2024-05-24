package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.network.response.common.subcatcardlist.Data

internal interface SubCatCardListResource {

    data class SubcatList(val data: List<Data>) :SubCatCardListResource
}