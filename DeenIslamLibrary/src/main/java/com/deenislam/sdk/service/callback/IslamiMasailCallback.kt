package com.deenislam.sdk.service.callback

import com.deenislam.sdk.service.network.response.islamimasail.catlist.Data

internal interface IslamiMasailCallback {

    fun masailCatClicked(getdata: Data){

    }
    fun masailQuestionClicked(getdata: com.deenislam.sdk.service.network.response.islamimasail.questionbycat.Data) {

    }

    fun questionBookmarkClicked(
        getdata: com.deenislam.sdk.service.network.response.islamimasail.questionbycat.Data
    ) {

    }
    fun questionShareClicked(getdata: com.deenislam.sdk.service.network.response.islamimasail.questionbycat.Data) {

    }
}