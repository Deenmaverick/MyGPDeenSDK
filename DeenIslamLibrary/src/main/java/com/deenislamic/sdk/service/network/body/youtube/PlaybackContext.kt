package com.deenislamic.sdk.service.network.body.youtube

import androidx.annotation.Keep

@Keep
internal data class PlaybackContext(
    val contentPlaybackContext: ContentPlaybackContext = ContentPlaybackContext()
)