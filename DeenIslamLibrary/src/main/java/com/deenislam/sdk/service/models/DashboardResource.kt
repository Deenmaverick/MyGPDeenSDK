package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.network.response.dashboard.Data

internal interface DashboardResource {

    data class DashboardData(val data: Data) :DashboardResource
}