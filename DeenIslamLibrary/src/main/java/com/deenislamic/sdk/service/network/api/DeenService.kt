package com.deenislamic.sdk.service.network.api

import com.deenislamic.sdk.service.network.response.BasicResponse
import com.deenislamic.sdk.service.network.response.allah99name.Allah99NameResponse
import com.deenislamic.sdk.service.network.response.boyan.categoriespaging.BoyanCategoriesResponse
import com.deenislamic.sdk.service.network.response.boyan.scholarspaging.BoyanScholarResponse
import com.deenislamic.sdk.service.network.response.boyan.videopreview.BoyanVideoPreviewResponse
import com.deenislamic.sdk.service.network.response.common.subcatcardlist.SubCatResponse
import com.deenislamic.sdk.service.network.response.dailydua.alldua.AllDuaResponse
import com.deenislamic.sdk.service.network.response.dailydua.duabycategory.DuaByCategory
import com.deenislamic.sdk.service.network.response.dailydua.favdua.FavDua
import com.deenislamic.sdk.service.network.response.dailydua.todaydua.TodayDua
import com.deenislamic.sdk.service.network.response.dashboard.DashboardResponse
import com.deenislamic.sdk.service.network.response.hajjandumrah.makkahlive.MakkahLiveVideoResponse
import com.deenislamic.sdk.service.network.response.islamicevent.IslamicEventListResponse
import com.deenislamic.sdk.service.network.response.islamifazael.IslamiFazaelResponse
import com.deenislamic.sdk.service.network.response.islamifazael.bycat.FazaelByCatResponse
import com.deenislamic.sdk.service.network.response.islamimasail.answer.MasailAnswerResponse
import com.deenislamic.sdk.service.network.response.islamimasail.catlist.IslamiMasailCatResponse
import com.deenislamic.sdk.service.network.response.islamimasail.questionbycat.MasailQuestionByCatResponse
import com.deenislamic.sdk.service.network.response.khatamquran.KhatamQuranVideosResponse
import com.deenislamic.sdk.service.network.response.podcast.category.PodcastCatResponse
import com.deenislamic.sdk.service.network.response.podcast.comment.PodcastCommentResponse
import com.deenislamic.sdk.service.network.response.podcast.content.PodcastContentResponse
import com.deenislamic.sdk.service.network.response.prayer_calendar.PrayerCalendarResponse
import com.deenislamic.sdk.service.network.response.prayerlearning.PrayerLearningAllCategory
import com.deenislamic.sdk.service.network.response.prayerlearning.visualization.VisualizationResponse
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.service.network.response.prayertimes.tracker.PrayerTrackResponse
import com.deenislamic.sdk.service.network.response.quran.SurahList
import com.deenislamic.sdk.service.network.response.quran.surah_details.SurahDetails
import com.deenislamic.sdk.service.network.response.ramadan.RamadanResponse
import com.deenislamic.sdk.service.network.response.ramadan.calendar.RamadanCalendarResponse
import com.deenislamic.sdk.service.network.response.zakat.SavedZakatResponse
import com.deenislamic.sdk.service.network.response.zakat.nisab.NisabResponse
import com.deenislamic.sdk.service.network.response.islamiceducationvideo.IslamiceducationVideoResponse
import com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class.DigitalQuranClassResponse
import com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class.quiz.QuranCLassQuizQuestionResponse
import com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class.quiz.result.AnswerSubmitResponse
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.AyatResponse
import com.deenislamic.sdk.service.network.response.quran.qurangm.paralist.ParaListResponse
import com.deenislamic.sdk.service.network.response.quran.qurangm.surahlist.SurahListResponse
import com.deenislamic.sdk.service.network.response.quran.tafsir.TafsirResponse
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

    @POST("dua/DuaByCategory")
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

    @POST("Hajj/getHajjMenubyCategoryNew")
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

    @POST("Quran/NewKhotomeQuranRamadan")
    suspend fun getKhatamQuranRamadanVideos(@Body parm: RequestBody): KhatamQuranVideosResponse


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

    // Al-Quran
    @POST("Quran/GetQuranHomePatch")
    suspend fun getQuranHomePatch(@Body parm: RequestBody): DashboardResponse

    @POST("Quran/SurahV2")
    suspend fun getSurahList(@Body parm: RequestBody): SurahListResponse

    @POST("Quran/ParaQC")
    suspend fun getParaList(@Body parm: RequestBody): ParaListResponse

    @POST("Quran/AyathfromSurahQC")
    suspend fun getVersesByChapter(@Body parm: RequestBody): AyatResponse

    @POST("Quran/ReadAyathfromSurahQC")
    suspend fun getReadingVersesByChapter(@Body parm: RequestBody): AyatResponse

    @POST("Quran/AyathfromParaQC")
    suspend fun getVersesByPara(@Body parm: RequestBody): AyatResponse

    @POST("Quran/ReadAyathfromParaQC")
    suspend fun getReadingVersesByPara(@Body parm: RequestBody): AyatResponse

    @POST("Quran/addFavoriteAyathQC")
    suspend fun updateFavAyat(@Body parm: RequestBody):  BasicResponse

    @POST("Quran/getTafsir")
    suspend fun getTafsir(@Body parm: RequestBody): TafsirResponse


    // Podcast
    @POST("Podcasts/getPodcastPatch")
    suspend fun getPodcastHomePatch(
        @Body parm: RequestBody
    ): DashboardResponse

    @POST("Podcasts/podcastscontentbyid")
    suspend fun getPodcastContent(
        @Body parm: RequestBody
    ): PodcastContentResponse

    @POST("Podcasts/podcastscontentbycategoryid")
    suspend fun getPodcastCategory(
        @Body parm: RequestBody
    ): PodcastCatResponse

    @POST("Comment/GetComments")
    suspend fun getPodcastAllComment(
        @Body parm: RequestBody
    ): PodcastCommentResponse

    @POST("Comment/AddFavoriteComment")
    suspend fun likeLivePodcastComment(
        @Body parm: RequestBody
    ): PodcastCommentResponse

    @POST("Comment/AddComment")
    suspend fun livePodcastAddComment(
        @Body parm: RequestBody
    ): PodcastCommentResponse

    // Hajj And Umrah

    @POST("LiveVideo/GetLivevideos")
    suspend fun getMakkahLiveVideoes(@Body parm: RequestBody): MakkahLiveVideoResponse

    //Boyan
    @POST("Boyan/GetBoyanPatch")
    suspend fun getBoyanHome(
        @Body parm: RequestBody
    ): DashboardResponse

    @POST("Boyan/BoyanCategory")
    suspend fun getBoyanCategories(
        @Body parm: RequestBody
    ): BoyanCategoriesResponse

    @POST("Boyan/BoyanScholars")
    suspend fun getBoyanScholars(
        @Body parm: RequestBody
    ): BoyanScholarResponse

    @POST("Boyan/BoyanListByScholars")
    suspend fun getBoyanVideoPreview(
        @Body parm: RequestBody
    ): BoyanVideoPreviewResponse

    @POST("Boyan/BoyanListByCategory")
    suspend fun getBoyanCategoryVideoPreview(
        @Body parm: RequestBody
    ): BoyanVideoPreviewResponse

    // Dua Amal
    @POST("IslamicVideos/DuaAmol")
    suspend fun getDuaAmolHome(@Body parm: RequestBody): IslamiceducationVideoResponse

    @POST("Islamic/GetQurbaniPatch")
    suspend fun getQurbaniPatch(@Body parm: RequestBody):  DashboardResponse

    @POST("Islamic/getQurbaniTopicsbyCategory")
    suspend fun getQurbaniContentByCat(@Body parm: RequestBody):  SubCatResponse


    @POST("dua/GetduaPatch")
    suspend fun duaAllPatch(@Body parm: RequestBody): DashboardResponse


}