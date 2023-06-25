package com.deenislam.sdk.service.libs.media3

interface AudioPlayerCallback {
    fun playAudioFromUrl(url: String, position: Int=-1)
    fun play(position: Int=-1)
    fun pause(position: Int=-1)
}