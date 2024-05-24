package com.deenislam.sdk.service.callback.common

import com.deenislam.sdk.service.models.common.OptionList


internal interface Common3DotMenuCallback {

    fun  <T> Common3DotMenuOptionClicked (getdata: OptionList, data: T) {

    }
}