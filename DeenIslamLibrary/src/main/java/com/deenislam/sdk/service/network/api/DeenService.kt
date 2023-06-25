package com.deenislam.sdk.service.network.api

import com.deenislam.sdk.service.network.response.prayer_calendar.PrayerCalendarResponse
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.service.network.response.quran.SurahList
import com.deenislam.sdk.service.network.response.quran.surah_details.SurahDetails
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

internal interface DeenService {

    @POST("PrayerTime/PrayerTimeDateWise")
    suspend fun prayerTime(@Body parm: RequestBody): PrayerTimesResponse

    @POST("PrayerTime/CurrentMonthPrayerTime")
    suspend fun currentMonthPrayerTime(@Body parm: RequestBody): PrayerCalendarResponse

    @POST("Quran/Surah")
    suspend fun surahList(@Body parm: RequestBody): SurahList

    @POST("Quran/SurahDetails")
    suspend fun surahDetails(@Body parm: RequestBody): SurahDetails


}