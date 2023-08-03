package com.deenislam.sdk.service.network.api

import com.deenislam.sdk.service.network.response.BasicResponse
import com.deenislam.sdk.service.network.response.dailydua.alldua.AllDuaResponse
import com.deenislam.sdk.service.network.response.dailydua.duabycategory.DuaByCategory
import com.deenislam.sdk.service.network.response.dailydua.favdua.FavDua
import com.deenislam.sdk.service.network.response.dailydua.todaydua.TodayDua
import com.deenislam.sdk.service.network.response.dashboard.DashboardResponse
import com.deenislam.sdk.service.network.response.prayer_calendar.PrayerCalendarResponse
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.service.network.response.quran.surah_details.SurahDetails
import com.deenislam.sdk.service.network.response.quran.SurahList
import com.deenislam.sdk.service.network.response.zakat.SavedZakatResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
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





}