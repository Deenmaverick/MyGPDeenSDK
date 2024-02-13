package com.deenislam.sdk.service.network.api

import com.deenislam.sdk.service.network.response.BasicResponse
import com.deenislam.sdk.service.network.response.allah99name.Allah99NameResponse
import com.deenislam.sdk.service.network.response.common.subcatcardlist.SubCatResponse
import com.deenislam.sdk.service.network.response.dailydua.alldua.AllDuaResponse
import com.deenislam.sdk.service.network.response.dailydua.duabycategory.DuaByCategory
import com.deenislam.sdk.service.network.response.dailydua.favdua.FavDua
import com.deenislam.sdk.service.network.response.dailydua.todaydua.TodayDua
import com.deenislam.sdk.service.network.response.dashboard.DashboardResponse
import com.deenislam.sdk.service.network.response.islamicevent.IslamicEventListResponse
import com.deenislam.sdk.service.network.response.islamifazael.IslamiFazaelResponse
import com.deenislam.sdk.service.network.response.islamifazael.bycat.FazaelByCatResponse
import com.deenislam.sdk.service.network.response.islamimasail.answer.MasailAnswerResponse
import com.deenislam.sdk.service.network.response.islamimasail.catlist.IslamiMasailCatResponse
import com.deenislam.sdk.service.network.response.islamimasail.questionbycat.MasailQuestionByCatResponse
import com.deenislam.sdk.service.network.response.khatamquran.KhatamQuranVideosResponse
import com.deenislam.sdk.service.network.response.prayer_calendar.PrayerCalendarResponse
import com.deenislam.sdk.service.network.response.prayerlearning.PrayerLearningAllCategory
import com.deenislam.sdk.service.network.response.prayerlearning.visualization.VisualizationResponse
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.service.network.response.prayertimes.tracker.PrayerTrackResponse
import com.deenislam.sdk.service.network.response.quran.SurahList
import com.deenislam.sdk.service.network.response.quran.surah_details.SurahDetails
import com.deenislam.sdk.service.network.response.ramadan.RamadanResponse
import com.deenislam.sdk.service.network.response.ramadan.calendar.RamadanCalendarResponse
import com.deenislam.sdk.service.network.response.zakat.SavedZakatResponse
import com.deenislam.sdk.service.network.response.zakat.nisab.NisabResponse
import com.deenislamic.service.network.response.islamiceducationvideo.IslamiceducationVideoResponse
import com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.DigitalQuranClassResponse
import com.deenislamic.service.network.response.quran.learning.digital_quran_class.quiz.QuranCLassQuizQuestionResponse
import com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.quiz.result.AnswerSubmitResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

internal interface DeenService {

    @POST("PrayerTime/PrayerTimeDateWise")
    suspend fun prayerTime(@Body parm: RequestBody): PrayerTimesResponse

    @POST("PrayerTracker/AddTrackingInfo")
    suspend fun setPrayerTimeTrack(@Body parm: RequestBody): BasicResponse

    @POST("PrayerTracker/GetTrackingInfo")
    suspend fun getPrayerTimeTrack(): PrayerTrackResponse

    @POST("PrayerTime/CurrentMonthPrayerTime")
    suspend fun currentMonthPrayerTime(@Body parm: RequestBody): PrayerCalendarResponse

    @POST("Quran/Surah")
    suspend fun surahList(@Body parm: RequestBody): SurahList

    @POST("Quran/SurahDetails")
    suspend fun surahDetails(@Body parm: RequestBody): SurahDetails

    // Daily Dua
    @POST("dua/AllCategories")
    suspend fun duaAllCategory(@Body parm: RequestBody): AllDuaResponse

    @POST("dua/AllDuaByCategory")
    suspend fun duaByCategory(@Body parm: RequestBody): DuaByCategory

    @POST("dua/AllFavDua")
    suspend fun getFavDua(@Body parm: RequestBody): FavDua

    @POST("dua/FavoriteDua")
    suspend fun setFavDua(@Body parm: RequestBody): BasicResponse

    @POST("dua/TodaysDua")
    suspend fun getTodayDua(@Body parm: RequestBody): TodayDua

    // Zakat calculator


    @FormUrlEncoded
    @POST("Zakat/AddZakatHistory")
    suspend fun addZakatHistory(
        @Field("Nisab") Nisab:Double,
        @Field("CashInHands") CashInHands:Double,
        @Field("CashInBankAccount") CashInBankAccount:Double,
        @Field("GoldEquivalentamount") GoldEquivalentamount:Double,
        @Field("SilverEquivalentamount") SilverEquivalentamount:Double,
        @Field("InvestmentStockMarket") InvestmentStockMarket:Double,
        @Field("OtherInvestments") OtherInvestments:Double,
        @Field("PropertyValue") PropertyValue:Double,
        @Field("HouseRent") HouseRent:Double,
        @Field("CashinBusiness") CashinBusiness:Double,
        @Field("ProductinBusiness") ProductinBusiness:Double,
        @Field("AgricultureAmount") AgricultureAmount:Double,
        @Field("PensionAmount") PensionAmount:Double,
        @Field("OthercapitalAmount") OthercapitalAmount:Double,
        @Field("DebtsToFamily") DebtsToFamily:Double,
        @Field("DebtsToOthers") DebtsToOthers:Double,
        @Field("CreditCardPayment") CreditCardPayment:Double,
        @Field("HomePayment") HomePayment:Double,
        @Field("CarPayment") CarPayment:Double,
        @Field("BusinessPayment") BusinessPayment:Double,
        @Field("ZakatPayable") ZakatPayable:Double,
        @Field("TotalAssets") TotalAssets:Double,
        @Field("DebtsAndLiabilities") DebtsAndLiabilities:Double,
        @Field("language") language:String = "bn",
        @Field("isactive") isactive:String = "true"
    ): BasicResponse


    @FormUrlEncoded
    @POST("Zakat/UpdateZakatHistory")
    suspend fun updateZakatHistory(
        @Field("Nisab") Nisab:Double,
        @Field("CashInHands") CashInHands:Double,
        @Field("CashInBankAccount") CashInBankAccount:Double,
        @Field("GoldEquivalentamount") GoldEquivalentamount:Double,
        @Field("SilverEquivalentamount") SilverEquivalentamount:Double,
        @Field("InvestmentStockMarket") InvestmentStockMarket:Double,
        @Field("OtherInvestments") OtherInvestments:Double,
        @Field("PropertyValue") PropertyValue:Double,
        @Field("HouseRent") HouseRent:Double,
        @Field("CashinBusiness") CashinBusiness:Double,
        @Field("ProductinBusiness") ProductinBusiness:Double,
        @Field("AgricultureAmount") AgricultureAmount:Double,
        @Field("PensionAmount") PensionAmount:Double,
        @Field("OthercapitalAmount") OthercapitalAmount:Double,
        @Field("DebtsToFamily") DebtsToFamily:Double,
        @Field("DebtsToOthers") DebtsToOthers:Double,
        @Field("CreditCardPayment") CreditCardPayment:Double,
        @Field("HomePayment") HomePayment:Double,
        @Field("CarPayment") CarPayment:Double,
        @Field("BusinessPayment") BusinessPayment:Double,
        @Field("ZakatPayable") ZakatPayable:Double,
        @Field("TotalAssets") TotalAssets:Double,
        @Field("DebtsAndLiabilities") DebtsAndLiabilities:Double,
        @Field("language") language:String = "bn",
        @Field("isactive") isactive:String = "true",
        @Field("Id") Id:Int
    ): BasicResponse

    @POST("Zakat/ZakatHistory")
    suspend fun getSavedZakatList(@Body parm: RequestBody): SavedZakatResponse

    @POST("Zakat/DeleteZakatHistory")
    suspend fun delZakatHistory(@Body parm: RequestBody): BasicResponse

    @POST("Zakat/ZakatProduct")
    suspend fun getZakatNisab(): NisabResponse

    @POST("PrayerTime/PrayerTimeDateWiseX")
    suspend fun prayerTimeSDK(@Body parm: RequestBody): PrayerTimesResponse

    @POST("NameOfAllah")
    suspend fun get99NameOfAllah(@Body parm: RequestBody): Allah99NameResponse

    // Islamic Education Video
    @POST("IslamicVideos/Islamiceducationstories")
    suspend fun getIslamicEducationVideos(@Body parm: RequestBody): IslamiceducationVideoResponse

    @POST("IslamicVideos/AddIslamicContentHistory")
    suspend fun addIslamicContentHistory(@Body parm: RequestBody): BasicResponse

    // Islamic Event
    @POST("Islamic/getIslamicEvents")
    suspend fun getIslamicEvents(@Body parm: RequestBody): IslamicEventListResponse

    @POST("Islamic/getIslamicEventsbyCategory")
    suspend fun getIslamicEventSubCat(@Body parm: RequestBody): SubCatResponse

    // Hajj And Umrah

    @POST("Hajj/GetHajjPatch")
    suspend fun getHajjAndUmrahPatch(@Body parm: RequestBody): DashboardResponse

    @POST("Hajj/getHajjMenubyCategory")
    suspend fun getHajjAndUmrahSubCat(@Body parm: RequestBody): SubCatResponse

    @POST("Hajj/getHajjGuide")
    suspend fun getHajjGuide(@Body parm: RequestBody): SubCatResponse

    @POST("Hajj/HajjGuideTracking")
    suspend fun updateHajjMapTracker(@Body parm: RequestBody): BasicResponse


    // Prayer Learning
    @POST("PrayerLearning/AllCategories")
    suspend fun getPrayerLearningAllCategory(@Body parm: RequestBody): PrayerLearningAllCategory

    @POST("PrayerLearning/Prayervisualization")
    suspend fun getPrayerLearningVisual(@Body parm: RequestBody): VisualizationResponse

    @POST("PrayerLearning/AllprayerlearningByCategory")
    suspend fun getPrayerLearningSubCat(@Body parm: RequestBody): SubCatResponse


    // Khatam e Quran Video
    @POST("Quran/KhotomeQuran")
    suspend fun getKhatamQuranVideos(@Body parm: RequestBody): KhatamQuranVideosResponse

    @POST("Quran/RecentKhotomeQuran")
    suspend fun getRecentKhatamQuranVideos(@Body parm: RequestBody): KhatamQuranVideosResponse
    @POST("Quran/AddKhotomeQuranHistory")
    suspend fun addKhatamQuranContentHistory(@Body parm: RequestBody): BasicResponse


    // Quran Learning

    @POST("Courses/GetQuranClassPatch")
    suspend fun getQuranLearnHomePatch(@Body parm: RequestBody): DashboardResponse

    @POST("Courses/CoursesContentById")
    suspend fun getDigitalQuranClass(@Body parm: RequestBody): DigitalQuranClassResponse

    // Quran Class Quiz
    @POST("Courses/coursequiz")
    suspend fun getQuranClassQuizQuestions(
        @Body parm: RequestBody
    ): QuranCLassQuizQuestionResponse

    @POST("Courses/coursehistorycreateupdate")
    suspend fun updateQuranClassVideoWatch(
        @Body parm: RequestBody
    ): BasicResponse


    @POST("Courses/coursequizanswersubmit")
    suspend fun submitQuizAnswer(
        @Body parm: RequestBody
    ): AnswerSubmitResponse


    // Ramadan
    @POST("SeheriIftarTime/SeheriIftarTime")
    suspend fun getOtherRamadanTime(@Body parm: RequestBody): RamadanResponse

    @POST("SeheriIftarTime/RamadanSeheriIftarTime")
    suspend fun getRamadanTime(@Body parm: RequestBody): RamadanResponse

    @POST("SeheriIftarTime/GetRamadanPatch")
    suspend fun getRamadanPatch(@Body parm: RequestBody): DashboardResponse


    @POST("FastTracker/AddFastTrackingInfo")
    suspend fun setRamadanTrack(@Body parm: RequestBody): BasicResponse

    @POST("SeheriIftarTime/ramadanCalander")
    suspend fun getRamadanCalendar(@Body parm: RequestBody): RamadanCalendarResponse

    // Islami  Fazael

    @POST("FazaelAndMasael/AllFazaelAndMasaelCategory")
    suspend fun getAllIslamiFazael(
        @Body parm: RequestBody
    ): IslamiFazaelResponse

    @POST("FazaelAndMasael/AllFazaelAndMasael")
    suspend fun getFazaelByCat(
        @Body parm: RequestBody
    ): FazaelByCatResponse

    // Islami Masail

    @POST("Masail/MasailCategory")
    suspend fun getMasailCat(
        @Body parm: RequestBody
    ): IslamiMasailCatResponse

    @POST("Masail/MasailQuestionsByCategory")
    suspend fun getMasailQuestionByCat(
        @Body parm: RequestBody
    ): MasailQuestionByCatResponse

    @POST("Masail/AddFavoriteMasailQuestions")
    suspend fun masailQuestionBookmar(
        @Body parm: RequestBody
    ): BasicResponse

    @POST("Masail/MasailFavQuestionsByUser")
    suspend fun getMasailFavList(
        @Body parm: RequestBody
    ): MasailQuestionByCatResponse

    @POST("Masail/AddNewMasailQuestions")
    suspend fun postMasailQuestion(
        @Body parm: RequestBody
    ): BasicResponse

    @POST("Masail/MasailQuestionsByUser")
    suspend fun getMyMasailQuestionList(
        @Body parm: RequestBody
    ): MasailQuestionByCatResponse

    @POST("Masail/GetMasailPatch")
    suspend fun GetMasailHomePatch(
        @Body parm: RequestBody
    ): DashboardResponse

    @POST("Masail/MasailAnswerByQuestion")
    suspend fun getMasailAnswer(
        @Body parm: RequestBody
    ): MasailAnswerResponse


}