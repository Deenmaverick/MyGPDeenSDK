package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.network.response.hajjandumrah.makkahlive.Data

internal interface HajjAndUmrahResource {

    data class makkahLiveVideos(val data: List<Data>) :HajjAndUmrahResource
   }