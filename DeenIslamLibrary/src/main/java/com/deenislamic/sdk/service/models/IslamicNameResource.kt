package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.islamicname.Data
import com.deenislamic.sdk.service.network.response.islamicname.IslamicNameCategoriesResponse
import com.deenislamic.sdk.service.network.response.islamicname.IslamicNameHomeResponse


internal interface IslamicNameResource {

    data class favNames(val data: List<Data>) :IslamicNameResource
    data class favremoved(val adapaterPosition: Int) :IslamicNameResource
    object favremovedFailed:IslamicNameResource
    data class islamicNames(val data: List<Data>) :IslamicNameResource
    data class favDone(val adapaterPosition: Int, val bol:Boolean):IslamicNameResource
    object favFailed:IslamicNameResource
    data class islamicNamesCategories(val data: List<IslamicNameCategoriesResponse.Data>) :IslamicNameResource
    data class islamicNamesPatch(val data: List<IslamicNameHomeResponse.Data>) :IslamicNameResource

}