package com.deenislam.sdk.service.network.body.youtube

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.body.youtube.Context
import com.deenislam.sdk.service.network.body.youtube.PlaybackContext

@Keep
internal data class YoutubeBody(
    val context: Context = Context(),
    val playbackContext: PlaybackContext = PlaybackContext(),
    val videoId: String
)