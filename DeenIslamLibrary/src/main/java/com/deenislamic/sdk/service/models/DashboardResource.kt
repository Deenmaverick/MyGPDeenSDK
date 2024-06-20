package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.dashboard.Data

internal interface DashboardResource {

    data class DashboardData(val data: List<Data>) :DashboardResource
 }