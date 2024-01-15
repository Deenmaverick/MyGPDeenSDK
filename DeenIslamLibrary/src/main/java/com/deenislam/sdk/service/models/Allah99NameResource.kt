package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.network.response.allah99name.Data

interface Allah99NameResource {

    data class getAllah99Name(val data: List<Data>) :Allah99NameResource
}