package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.network.response.dashboard.Data

internal interface QurbaniResource {
    data class QurbaniPatch(val data: List<Data>) : QurbaniResource
    data class QurbaniContentByCat(val data: List<com.deenislam.sdk.service.network.response.common.subcatcardlist.Data>) :
        QurbaniResource
}