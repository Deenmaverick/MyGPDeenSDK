package com.deenislamic.sdk.service.network.body.youtube

import androidx.annotation.Keep

@Keep
internal data class Context(
    val client: Client = Client(),
    val user: User = User()
)