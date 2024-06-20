package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.hajjandumrah.makkahlive.Data

internal interface HajjAndUmrahResource {

    data class makkahLiveVideos(val data: List<Data>) :HajjAndUmrahResource
    data class hajjAndUmrahPatch(val data: List<com.deenislamic.sdk.service.network.response.dashboard.Data>) :HajjAndUmrahResource
    data class hajjMapTracker(val mapTag: String, val isTrack: Boolean, val indexPos: Int) :HajjAndUmrahResource

}