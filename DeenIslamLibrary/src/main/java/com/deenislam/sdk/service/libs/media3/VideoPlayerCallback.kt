package com.deenislam.sdk.service.libs.media3

import com.deenislam.sdk.service.network.response.common.CommonCardData

internal interface VideoPlayerCallback {
    fun playVideo(position: Int=-1)
    {

    }
    fun pauseVideo(position: Int=-1)
    {

    }

    fun videoPlayerToggleFullScreen(isFullScreen:Boolean)
    {

    }

    fun videoPlayerEnded()
    {

    }

    fun videoPlayerReady(isPlaying: Boolean, mediaData: CommonCardData?)
    {

    }
}