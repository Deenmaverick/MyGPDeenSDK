package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.islamifazael.bycat.FazaelDataItem

internal interface IslamiFazaelResource {

    data class AllFazaelList(val data: List<com.deenislamic.sdk.service.network.response.islamifazael.Data>) :IslamiFazaelResource

    data class FazaelByCat(val data: List<FazaelDataItem>) :IslamiFazaelResource
}