package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.service.network.response.quran.learning.quransikkhaacademy.ContentListResponse
import com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class.CourseConten
import com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class.quiz.Option

internal interface QuranLearningCallback {
    fun homePatchItemClicked(getData: Item)
    {

    }

    fun courseCurriculumClicked(getData: ContentListResponse.Data.Result)
    {

    }

    fun courseCurriculumClickedV2(getData: CourseConten)
    {

    }

    fun courseQuizClicked(getData: CourseConten)
    {

    }

    fun courseQuizAnswerSelected(getData: Option)
    {

    }
}