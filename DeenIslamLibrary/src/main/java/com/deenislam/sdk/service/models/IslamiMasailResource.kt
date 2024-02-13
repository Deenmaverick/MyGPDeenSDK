package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.network.response.islamimasail.catlist.IslamiMasailCatResponse
import com.deenislam.sdk.service.network.response.islamimasail.questionbycat.Data
import com.deenislam.sdk.service.network.response.islamimasail.questionbycat.MasailQuestionByCatResponse

internal interface IslamiMasailResource {

    data class MasailCatList(val data: IslamiMasailCatResponse) :IslamiMasailResource
    data class MasailQuestionList(val value: MasailQuestionByCatResponse) :IslamiMasailResource
    data class QuestionBookmar(val copy: Data) :IslamiMasailResource
    object postQuestion:IslamiMasailResource
    data class HomePatch(val data: List<com.deenislam.sdk.service.network.response.dashboard.Data>) :IslamiMasailResource
    data class AnswerData(val data: com.deenislam.sdk.service.network.response.islamimasail.answer.Data) :IslamiMasailResource
}