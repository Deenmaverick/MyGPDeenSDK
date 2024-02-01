package com.deenislam.sdk.service.network.body.youtube

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.body.youtube.Client
import com.deenislam.sdk.service.network.body.youtube.User

@Keep
internal data class Context(
    val client: Client = Client(),
    val user: User = User()
)