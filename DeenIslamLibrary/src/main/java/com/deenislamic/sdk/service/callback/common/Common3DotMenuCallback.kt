package com.deenislamic.sdk.service.callback.common

import com.deenislamic.sdk.service.models.common.OptionList


internal interface Common3DotMenuCallback {

    fun  <T> Common3DotMenuOptionClicked (getdata: OptionList, data: T) {

    }
}