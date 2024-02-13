package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.network.response.islamifazael.bycat.FazaelDataItem

internal interface IslamiFazaelResource {

    data class AllFazaelList(val data: List<com.deenislam.sdk.service.network.response.islamifazael.Data>) :IslamiFazaelResource

    data class FazaelByCat(val data: List<FazaelDataItem>) :IslamiFazaelResource
}