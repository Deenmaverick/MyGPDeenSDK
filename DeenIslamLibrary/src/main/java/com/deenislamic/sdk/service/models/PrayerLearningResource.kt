package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.prayerlearning.Data

internal interface PrayerLearningResource {

    data class AllCat(val data: List<Data>) :PrayerLearningResource
    data class Visualization(val data: com.deenislamic.sdk.service.network.response.prayerlearning.visualization.Data) :PrayerLearningResource

    data class SubCatList(val data: List<com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data>) :PrayerLearningResource
}