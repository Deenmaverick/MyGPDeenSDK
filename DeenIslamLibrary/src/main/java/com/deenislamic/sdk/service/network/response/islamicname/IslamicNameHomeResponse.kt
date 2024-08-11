package com.deenislamic.sdk.service.network.response.islamicname


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
internal data class IslamicNameHomeResponse(
    @SerializedName("Data")
    var data: List<Data>,
    var Message: String? = null,
    var Success: Boolean? = null,
    var TotalData: Int? = null
) {
    @Keep
    data class Data(
        var Id: Int? = null,
        var Title: String? = null,
        var Design: String? = null,
        var AppDesign: String? = null,
        var Logo: Any? = null,
        var ButtonText: String? = null,
        var Sequence: Int? = null,
        var FeatureSize: String? = null,
        var Items: List<Item>
    ) {
        @Parcelize
        @Keep
        data class Item(
            var Id: Int? = null,
            var FeatureID: Int? = null,
            var FeatureName: String? = null,
            var FeatureTitle: String? = null,
            var Title: String? = null,
            var Text: String? = null,
            var Logo: String? = null,
            var ArabicText: String? = null,
            var Reference: String? = null,
            var imageurl1: String? = null,
            var imageurl2: String? = null,
            var imageurl3: String? = null,
            var imageurl4: String? = null,
            var imageurl5: String? = null,
            var contentBaseUrl: String? = null,
            var IsActive: String? = null,
            var Language: String? = null,
            var Sequence: Int? = null,
            var ContentType: String? = null,
            var SurahId: Int? = null,
            var JuzId: Int? = null,
            var VerseId: Int? = null,
            var HadithId: Int? = null,
            var CategoryId: Int? = null,
            var SubCategoryId: Int? = null,
            var DuaId: Int? = null,
            var MText: String? = null,
            var Meaning: String? = null,
            var ECount: String? = null,
            var FeatureDesign: String? = null,
            var FeatureLogo: String? = null,
            var FeatureButton: String? = null,
            var Serial: Int? = null,
            var isVideo: Boolean? = null,
            var isLive: Boolean? = null,
            var isPremium: Boolean? = null,
            var FeatureSize: String? = null
        ) : Parcelable
    }
}

