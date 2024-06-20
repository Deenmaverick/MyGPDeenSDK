package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.hadith.Data
import com.deenislamic.sdk.service.network.response.hadith.preview.HadithPreviewResponse

internal interface HadithResource
{

    data class hadithCollection(val data: List<Data>) :HadithResource
    data class hadithChapterByCollection(val data: List<com.deenislamic.sdk.service.network.response.hadith.chapter.Data>) :HadithResource
    data class hadithPreview(val value: HadithPreviewResponse) :HadithResource

    data class setFavHadith(val position: Int, val fav: Boolean) :HadithResource

    data class hadithFavData(val value: HadithPreviewResponse) :HadithResource
    object updateFavFailed:HadithResource


}