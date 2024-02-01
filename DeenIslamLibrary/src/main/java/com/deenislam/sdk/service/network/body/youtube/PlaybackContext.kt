package com.deenislam.sdk.service.network.body.youtube

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.body.youtube.ContentPlaybackContext

@Keep
internal data class PlaybackContext(
    val contentPlaybackContext: ContentPlaybackContext = ContentPlaybackContext()
)