package com.deenislam.sdk.utils

import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.service.network.response.common.CommonCardData
import com.deenislam.sdk.service.network.response.dashboard.Item
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