package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.islamimasail.catlist.IslamiMasailCatResponse
import com.deenislamic.sdk.service.network.response.islamimasail.questionbycat.Data
import com.deenislamic.sdk.service.network.response.islamimasail.questionbycat.MasailQuestionByCatResponse

internal interface IslamiMasailResource {

    data class MasailCatList(val data: IslamiMasailCatResponse) :IslamiMasailResource
    data class MasailQuestionList(val value: MasailQuestionByCatResponse) :IslamiMasailResource
    data class QuestionBookmar(val copy: Data) :IslamiMasailResource
    object postQuestion:IslamiMasailResource
    data class HomePatch(val data: List<com.deenislamic.sdk.service.network.response.dashboard.Data>) :IslamiMasailResource
    data class AnswerData(val data: com.deenislamic.sdk.service.network.response.islamimasail.answer.Data) :IslamiMasailResource
}