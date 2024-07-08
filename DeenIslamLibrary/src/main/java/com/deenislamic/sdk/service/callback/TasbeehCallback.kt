package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.database.entity.Tasbeeh

interface TasbeehCallback {

    fun tasbeehLoadApi(selectedPos: Int, todayDate: String)
    fun tasbeehSetCount(selectedCount: Int, it1: Tasbeeh, todayDate: String)
    fun tasbeehShare(arabicDua: String, duaTxt: String)

}