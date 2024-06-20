package com.deenislamic.sdk.utils

import android.content.Context
import android.content.res.AssetManager
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.models.quran.quranplayer.FontList
import com.deenislamic.sdk.service.models.quran.quranplayer.PlayerCommonSelectionData
import com.deenislamic.sdk.service.models.ramadan.StateModel
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Qari
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.TafsirList
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Translator
import com.deenislamic.sdk.service.network.response.quran.qurangm.surahlist.Data
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

internal fun transformCommonCardListPatchModel(
    item: Item,
    itemTitle: String,
    itemSubTitle: String,
    itemMidContent:String="",
    itemBtnText: String
): Item {
    return Item(
        ArabicText = item.ArabicText,
        ContentType = item.ContentType,
        FeatureID = item.FeatureID,
        FeatureName = item.FeatureName,
        FeatureTitle = item.FeatureTitle,
        Id = item.Id,
        IsActive = item.IsActive,
        Language = item.Language,
        Reference = item.Reference,
        Sequence = item.Sequence,
        Text = item.Text,
        Title = item.Title,
        contentBaseUrl = item.contentBaseUrl,
        imageurl1 = item.imageurl1,
        imageurl2 = item.imageurl2,
        imageurl3 = item.imageurl3,
        imageurl4 = item.imageurl4,
        imageurl5 = item.imageurl5,
        SurahId = item.SurahId,
        JuzId = item.JuzId,
        VerseId = item.VerseId,
        HadithId = item.HadithId,
        CategoryId = item.CategoryId,
        SubCategoryId = item.SubCategoryId,
        DuaId = item.DuaId,
        MText = item.MText,
        Meaning = item.Meaning,
        ECount = item.ECount,
        itemTitle = itemTitle,
        itemSubTitle = itemSubTitle,
        itemBtnText = itemBtnText,
        isVideo = item.isVideo,
        isLive = item.isLive,
        FeatureSize = item.FeatureSize,
        itemMidContent = itemMidContent
    )
}

internal fun durationToSeconds(durationMillis: Long): Long {
    return TimeUnit.MILLISECONDS.toSeconds(durationMillis)
}

internal fun String.removeHtmlTags(): String {
    return this.replace(Regex("<.*?>"), "")
}

internal fun transformDashboardItemForKhatamQuran(
    item: Item
): CommonCardData {
    return CommonCardData(
        Id = item.Id,
        category = null,
        categoryID = item.CategoryId,
        duration = item.DuaId.toString(),
        imageurl = item.imageurl1,
        reference = item.Reference,
        referenceurl = null,
        title = item.ArabicText,
        videourl = item.imageurl2,
        buttonTxt = null,
        isPlaying = false,
        DurationInSec = item.DuaId,
        DurationWatched = 0,
        IsCompleted = false,
        customReference = null,
    )
}

fun String.monthShortNameLocale(): String =

    if(DeenSDKCore.GetDeenLanguage() == "bn") {

        val englishShortMonths = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        val bengaliMonths = listOf(
            "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
            "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
        )

        val regex1 = "\\b(${englishShortMonths.joinToString("|")})\\b".toRegex()

        this.replace(regex1) { result ->
            val matchedMonth = result.value
            val index = englishShortMonths.indexOfFirst { it.equals(matchedMonth, ignoreCase = true) }
            if (index != -1) bengaliMonths[index] else matchedMonth
        }

    }
    else this

internal fun transformPatchToSurahData(newDataModel: Item): Data {

    return Data(
        ContentUrl= "",
        Id= newDataModel.Id,
        ImageUrl= "",
        IsFavorite = false,
        Order= newDataModel.Sequence,
        SurahId= newDataModel.SurahId,
        SurahName= newDataModel.MText,
        SurahNameInArabic= "",
        SurahNameMeaning= newDataModel.Meaning,
        SurahOrigin= newDataModel.Text,
        TotalAyat= newDataModel.ECount.toString(),
        rKey= "",
        wKey= ""
    )
}

internal fun transformPlayerReciterData(newDataModel: Qari): PlayerCommonSelectionData {

    return PlayerCommonSelectionData(
        imageurl = newDataModel.imageurl?:"",
        Id = newDataModel.title,
        title = newDataModel.text
    )
}

internal fun transformPlayerTranslatorData(newDataModel: Translator): PlayerCommonSelectionData {

    return PlayerCommonSelectionData(
        imageurl = newDataModel.imageurl,
        Id = newDataModel.title,
        title = newDataModel.text,
        language = newDataModel.language
    )
}

internal fun transformPlayerTafsirData(newDataModel: TafsirList): PlayerCommonSelectionData {

    return PlayerCommonSelectionData(
        imageurl = newDataModel.imageurl,
        Id = newDataModel.title,
        title = newDataModel.text,
        language = newDataModel.language
    )
}

internal fun Context.deleteSingleFile(fileAbsolutePath: String, fileName: String): Boolean {
    try {
        val file = File(filesDir, fileAbsolutePath)

        if (file.exists()) {
            val deleted = File(file, fileName).delete()
            // Optionally, delete the directory if it's empty after file deletion
            // file.delete()
            return deleted
        }
    } catch (e: Exception) {
        // Handle exceptions, e.g., log an error message
        e.printStackTrace()
    }
    return false
}

internal fun Context.retrieveSingleFile(fileAbsolutePath: String): String? {
    try {
        val file = File(filesDir, fileAbsolutePath)

        if (file.exists()) {
            return file.readText()
        }
    } catch (e: Exception) {
        // Handle exceptions, e.g., log an error message
        e.printStackTrace()
    }
    return null
}

fun Context.loadHtmlFromAssets(filename: String): String {
    val assetManager: AssetManager = assets
    return try {
        val inputStream: InputStream = assetManager.open(filename)
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, StandardCharsets.UTF_8)
    } catch (e: IOException) {
        e.printStackTrace()
        ""
    }
}

internal fun Context.getArabicFontList():ArrayList<FontList> {

    return arrayListOf(
        FontList(
            fontname = this.getString(R.string.indopak),
            fontid = "1"
        ),
        FontList(
            fontname = this.getString(R.string.uthmanic_script_hafs_regular),
            fontid = "2"
        ),
        FontList(
            fontname = this.getString(R.string.al_majeed),
            fontid = "3"
        )

    )

}

internal fun transformArabicFontData(font: FontList): PlayerCommonSelectionData {

    return PlayerCommonSelectionData(
        imageurl = null,
        Id = font.fontid.toInt(),
        title = font.fontname
    )
}


internal val bangladeshStateArray: ArrayList<StateModel> = arrayListOf(
    StateModel("dhaka", "Dhaka (ঢাকা)", "ঢাকা"),
    StateModel("barisal", "Barisal (বরিশাল)", "বরিশাল"),
    StateModel("khulna", "Khulna (খুলনা)", "খুলনা"),
    StateModel("chittagong", "Chittagong (চট্টগ্রাম)", "চট্টগ্রাম"),
    StateModel("mymensingh", "Mymensingh (ময়মনসিংহ)", "ময়মনসিংহ"),
    StateModel("rangpur", "Rangpur (রংপুর)", "রংপুর"),
    StateModel("rajshahi", "Rajshahi (রাজশাহী)", "রাজশাহী"),
    StateModel("sylhet", "Sylhet (সিলেট)", "সিলেট"),
    StateModel("bagerhat", "Bagerhat (বাগেরহাট)", "বাগেরহাট"),
    StateModel("chuadanga", "Chuadanga (চুয়াডাঙ্গা)", "চুয়াডাঙ্গা"),
    StateModel("jessore", "Jessore (যশোর)", "যশোর"),
    StateModel("jhenaidah", "Jhenaidah (ঝিনাইদহ)", "ঝিনাইদহ"),
    StateModel("kushtia", "Kushtia (কুষ্টিয়া)", "কুষ্টিয়া"),
    StateModel("magura", "Magura (মাগুরা)", "মাগুরা"),
    StateModel("meherpur", "Meherpur (মেহেরপুর)", "মেহেরপুর"),
    StateModel("narail", "Narail (নড়াইল)", "নড়াইল"),
    StateModel("satkhira", "Satkhira (সাতক্ষীরা)", "সাতক্ষীরা"),
    StateModel("bandarban", "Bandarban (বান্দরবান)", "বান্দরবান"),
    StateModel("brahmanbaria", "Brahmanbaria (ব্রাহ্মণবাড়িয়া)", "ব্রাহ্মণবাড়িয়া"),
    StateModel("chandpur", "Chandpur (চাঁদপুর)", "চাঁদপুর"),
    StateModel("comilla", "Comilla (কুমিল্লা)", "কুমিল্লা"),
    StateModel("coxsBazar", "CoxsBazar (কক্সবাজার)", "কক্সবাজার"),
    StateModel("feni", "Feni (ফেনী)", "ফেনী"),
    StateModel("khagrachhari", "Khagrachhari (খাগড়াছড়ি)", "খাগড়াছড়ি"),
    StateModel("lakshmipur", "Lakshmipur (লক্ষ্মীপুর)", "লক্ষ্মীপুর"),
    StateModel("noakhali", "Noakhali (নোয়াখালী)", "নোয়াখালী"),
    StateModel("rangamati", "Rangamati (রাঙ্গামাটি)", "রাঙ্গামাটি"),
    StateModel("faridpur", "Faridpur (ফরিদপুর)", "ফরিদপুর"),
    StateModel("tangail", "Tangail (টাঙ্গাইল)", "টাঙ্গাইল"),
    StateModel("gazipur", "Gazipur (গাজীপুর)", "গাজীপুর"),
    StateModel("gopalganj", "Gopalganj (গোপালগঞ্জ)", "গোপালগঞ্জ"),
    StateModel("kishoreganj", "Kishoreganj (কিশোরগঞ্জ)", "কিশোরগঞ্জ"),
    StateModel("madaripur", "Madaripur (মাদারীপুর)", "মাদারীপুর"),
    StateModel("manikganj", "Manikganj (মানিকগঞ্জ)", "মানিকগঞ্জ"),
    StateModel("munshiganj", "Munshiganj (মুন্সীগঞ্জ)", "মুন্সীগঞ্জ"),
    StateModel("narayanganj", "Narayanganj (নারায়ণগঞ্জ)", "নারায়ণগঞ্জ"),
    StateModel("narsingdi", "Narsingdi (নরসিংদী)", "নরসিংদী"),
    StateModel("rajbari", "Rajbari (রাজবাড়ী)", "রাজবাড়ী"),
    StateModel("shariatpur", "Shariatpur (শরীয়তপুর)", "শরীয়তপুর"),
    StateModel("barguna", "Barguna (বরগুনা)", "বরগুনা"),
    StateModel("bhola", "Bhola (ভোলা)", "ভোলা"),
    StateModel("jhalokati", "Jhalokati (ঝালকাঠি)", "ঝালকাঠি"),
    StateModel("patuakhali", "Patuakhali (পটুয়াখালী)", "পটুয়াখালী"),
    StateModel("pirojpur", "Pirojpur (পিরোজপুর)", "পিরোজপুর"),
    StateModel("dinajpur", "Dinajpur (দিনাজপুর)", "দিনাজপুর"),
    StateModel("gaibandha", "Gaibandha (গাইবান্ধা)", "গাইবান্ধা"),
    StateModel("kurigram", "Kurigram (কুড়িগ্রাম)", "কুড়িগ্রাম"),
    StateModel("lalmonirhat", "Lalmonirhat (লালমনিরহাট)", "লালমনিরহাট"),
    StateModel("nilphamari", "Nilphamari (নীলফামারী)", "নীলফামারী"),
    StateModel("panchagarh", "Panchagarh (পঞ্চগড়)", "পঞ্চগড়"),
    StateModel("thakurgaon", "Thakurgaon (ঠাকুরগাঁও)", "ঠাকুরগাঁও"),
    StateModel("bogra", "Bogra (বগুড়া)", "বগুড়া"),
    StateModel("pabna", "Pabna (পাবনা)", "পাবনা"),
    StateModel("joypurhat", "Joypurhat (জয়পুরহাট)", "জয়পুরহাট"),
    StateModel("chapainawabganj", "Chapainawabganj (চাঁপাইনবাবগঞ্জ)", "চাঁপাইনবাবগঞ্জ"),
    StateModel("naogaon", "Naogaon (নওগাঁ)", "নওগাঁ"),
    StateModel("natore", "Natore (নাটোর)", "নাটোর"),
    StateModel("sirajganj", "Sirajganj (সিরাজগঞ্জ)", "সিরাজগঞ্জ"),
    StateModel("habiganj", "Habiganj (হবিগঞ্জ)", "হবিগঞ্জ"),
    StateModel("moulvibazar", "Moulvibazar (মৌলভীবাজার)", "মৌলভীবাজার"),
    StateModel("sunamganj", "Sunamganj (সুনামগঞ্জ)", "সুনামগঞ্জ"),
    StateModel("sherpur", "Sherpur (শেরপুর)", "শেরপুর"),
    StateModel("jamalpur", "Jamalpur (জামালপুর)", "জামালপুর"),
    StateModel("netrokona", "Netrokona (নেত্রকোনা)", "নেত্রকোনা")
)