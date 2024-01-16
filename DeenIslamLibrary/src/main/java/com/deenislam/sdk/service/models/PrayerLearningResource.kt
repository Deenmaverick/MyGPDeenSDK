package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.network.response.prayerlearning.Data

internal interface PrayerLearningResource {

    data class AllCat(val data: List<Data>) :PrayerLearningResource
    data class Visualization(val data: com.deenislam.sdk.service.network.response.prayerlearning.visualization.Data) :PrayerLearningResource

    data class SubCatList(val data: List<com.deenislam.sdk.service.network.response.common.subcatcardlist.Data>) :PrayerLearningResource
}