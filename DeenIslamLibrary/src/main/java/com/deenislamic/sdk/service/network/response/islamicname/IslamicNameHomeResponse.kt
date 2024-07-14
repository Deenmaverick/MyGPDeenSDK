package com.deenislamic.sdk.service.network.response.islamicname


import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
internal data class IslamicNameHomeResponse(
    var data: List<Data>,
    var message: String? = null,
    var success: Boolean? = null,
    var totalData: Int? = null
) {
    @Keep
    data class Data(
        var design: String? = null,
        var id: Int? = null,
        var items: List<Item>,
        var logo: Any? = null,
        var sequence: Int? = null,
        var title: String? = null
    ) {
        @Parcelize
        @Keep
        data class Item(
            var arabicText: String,
            var categoryId: Int? = null,
            var contentBaseUrl: String? = null,
            var contentType: String? = null,
            var duaId: Int? = null,
            var eCount: String? = null,
            var featureID: Int? = null,
            var featureName: String? = null,
            var featureTitle: String? = null,
            var hadithId: Int? = null,
            var id: Int? = null,
            var imageurl1: String? = null,
            var imageurl2: String? = null,
            var imageurl3: String? = null,
            var imageurl4: String? = null,
            var imageurl5: String? = null,
            var isActive: String? = null,
            var juzId: Int? = null,
            var language: String? = null,
            var logo: String? = null,
            var mText: String? = null,
            var meaning: String? = null,
            var reference: String? = null,
            var sequence: Int? = null,
            var subCategoryId: Int? = null,
            var surahId: Int? = null,
            var text: String? = null,
            var title: String? = null,
            var verseId: Int? = null
        ) : Parcelable
    }
}
