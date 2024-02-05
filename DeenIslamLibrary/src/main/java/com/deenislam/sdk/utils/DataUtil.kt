package com.deenislam.sdk.utils

import com.deenislam.sdk.service.network.response.dashboard.Item

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