package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.zakat.Data

internal interface ZakatResource {

    object zakatHistoryAdded:ZakatResource

    data class savedZakatList(val data: List<Data>) :ZakatResource

    object historyDeleteFailed:ZakatResource
    data class historyDeleted(val adapterPosition: Int) :ZakatResource

    object historyUpdateFailed:ZakatResource
    object historyUpdateSuccess:ZakatResource

    data class zakatNisab(val data: List<com.deenislamic.sdk.service.network.response.zakat.nisab.Data>) :ZakatResource

}