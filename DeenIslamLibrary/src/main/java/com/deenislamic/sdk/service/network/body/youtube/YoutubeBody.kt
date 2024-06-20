package com.deenislamic.sdk.service.network.body.youtube

import androidx.annotation.Keep

@Keep
internal data class YoutubeBody(
    val context: Context = Context(),
    val playbackContext: PlaybackContext = PlaybackContext(),
    val videoId: String
)