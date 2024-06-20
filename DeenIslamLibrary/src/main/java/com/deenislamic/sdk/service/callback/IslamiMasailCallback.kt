package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.network.response.islamimasail.catlist.Data

internal interface IslamiMasailCallback {

    fun masailCatClicked(getdata: Data){

    }
    fun masailQuestionClicked(getdata: com.deenislamic.sdk.service.network.response.islamimasail.questionbycat.Data) {

    }

    fun questionBookmarkClicked(
        getdata: com.deenislamic.sdk.service.network.response.islamimasail.questionbycat.Data
    ) {

    }
    fun questionShareClicked(getdata: com.deenislamic.sdk.service.network.response.islamimasail.questionbycat.Data) {

    }
}