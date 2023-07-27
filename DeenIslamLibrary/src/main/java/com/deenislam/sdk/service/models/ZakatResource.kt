package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.network.response.zakat.Data

internal interface ZakatResource {

    object zakatHistoryAdded:ZakatResource

    data class savedZakatList(val data: List<Data>) :ZakatResource

    object historyDeleteFailed:ZakatResource
    data class historyDeleted(val adapterPosition: Int) :ZakatResource

    object historyUpdateFailed:ZakatResource
    object historyUpdateSuccess:ZakatResource
}