package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.allah99name.Data

internal interface Allah99NameResource {

    data class getAllah99Name(val data: List<Data>) :Allah99NameResource
}