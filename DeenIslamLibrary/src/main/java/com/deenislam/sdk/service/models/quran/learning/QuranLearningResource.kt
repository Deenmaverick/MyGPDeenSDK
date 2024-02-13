package com.deenislam.sdk.service.models.quran.learning

import com.deenislam.sdk.service.network.response.dashboard.Data
import com.deenislam.sdk.service.network.response.quran.learning.quransikkhaacademy.ContentByIdResponse
import com.deenislam.sdk.service.network.response.quran.learning.quransikkhaacademy.ContentListResponse
import kotlinx.parcelize.RawValue

internal interface QuranLearningResource {

    data class QSAContentList(val data: @RawValue ContentListResponse.Data) : QuranLearningResource
    data class QSAContentByID(
        val data: ContentByIdResponse.Data?,
        val contentData: ContentListResponse.Data.Result
    ) : QuranLearningResource
    object QSAContentByIDFailed: QuranLearningResource

    object QSAOrderConfirmationFailed: QuranLearningResource

    object QSAOrderSuccess: QuranLearningResource
    data class HomePatch(val data: List<Data>) : QuranLearningResource
    data class DigitalQuranClass(val data: com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.Data) :
        QuranLearningResource
    data class QuranClassSecureUrl(val value: String) : QuranLearningResource

    data class QuranClassQuizQuestion(val data: List<com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.quiz.Data>) :
        QuranLearningResource
    object QuranClassVideoWatched: QuranLearningResource
    data class QuranQuizResult(val data: com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.quiz.result.Data) :
        QuranLearningResource
}