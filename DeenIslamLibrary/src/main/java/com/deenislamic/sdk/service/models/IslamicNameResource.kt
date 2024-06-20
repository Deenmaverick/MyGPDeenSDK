package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.eidjamat.EidJamatListResponse
import com.deenislamic.sdk.service.network.response.islamicname.Data


internal interface IslamicNameResource {

    data class favNames(val data: List<Data>) :IslamicNameResource
    data class favremoved(val adapaterPosition: Int) :IslamicNameResource
    object favremovedFailed:IslamicNameResource
    data class islamicNames(val data: List<Data>) :IslamicNameResource
    data class favDone(val adapaterPosition: Int, val bol:Boolean):IslamicNameResource
    object favFailed:IslamicNameResource
    data class eidJamatList(val data: List<EidJamatListResponse.Data>) :IslamicNameResource
}