package com.deenislam.sdk.service.repository.quran.learning;

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.DashboardService
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.service.network.api.QuranShikkhaService
import com.deenislam.sdk.utils.COURSE_SLUG_QURAN_ACADEMY
import com.deenislam.sdk.utils.QURAN_ACADEMY_GAKK_API_KEY
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.toRequestBody
import org.json.JSONObject


internal class QuranLearningRepository (
    private val quranShikkhaService: QuranShikkhaService,
    private val deenService: DeenService,
    private val dashboardService: DashboardService
) : ApiCall {

    suspend fun getContentList(
        token: String
    ) = makeApicall {

        quranShikkhaService.getContentList(
            accesstoken = token,
            card_slug = COURSE_SLUG_QURAN_ACADEMY,
            count = 200
        )

    }

    suspend fun getUserInfo(msisdn:String) = makeApicall {
        quranShikkhaService.getUser(apikey = QURAN_ACADEMY_GAKK_API_KEY, userPhone = msisdn)
    }

    suspend fun botMyOrder(msisdn: String) = makeApicall {

        val body = JSONObject()
        body.put("user_phone",msisdn)
        body.put("user_name",msisdn)
        body.put("course_slug",COURSE_SLUG_QURAN_ACADEMY)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        quranShikkhaService.createUser(apikey = QURAN_ACADEMY_GAKK_API_KEY, requestBody)
    }

    suspend fun getContentByID(
        token:String?=null,
        id:String
    ) = makeApicall {

        quranShikkhaService.getContentByID(
            accesstoken = token,
            id = id,
            hls = "true"
        )

    }

    suspend fun getHomePatch(language:String) = makeApicall {

        val body = JSONObject()
        body.put("language",language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService.getQuranLearnHomePatch(requestBody)

    }

    suspend fun getDigitalQuranClass(language: String, courseId: Int) = makeApicall {

        val body = JSONObject()
        body.put("language",language)
        body.put("courseId",courseId)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService.getDigitalQuranClass(requestBody)

    }

    suspend fun getDigitalQuranSecureUrl(url:String) = makeApicall {

        dashboardService.getQuranClassSecureUrl(url)

    }

    suspend fun getQuranClassQuizQuestion(language: String, courseId: Int,contentID:Int) = makeApicall {

        val body = JSONObject()
        body.put("language",language)
        body.put("courseId",courseId)
        body.put("contentID",contentID)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService.getQuranClassQuizQuestions(requestBody)

    }

    suspend fun updateQuranClassVideoWatch(
        msisdn: String,
        language: String,
        courseId: Int,
        contentID:Int,
        duration:Int,
        watchDuration: Long
    ) = makeApicall {

        val body = JSONObject()
        body.put("msisdn",msisdn)
        body.put("language",language)
        body.put("courseId",courseId)
        body.put("contenId",contentID)
        body.put("duration",duration)
        body.put("watchDuration",watchDuration)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService.updateQuranClassVideoWatch(requestBody)

    }

    suspend fun submitQuizAnswer(answerSheet: String) = makeApicall {
        // val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService.submitQuizAnswer(answerSheet.toRequestBody(RequestBodyMediaType))

    }
} 