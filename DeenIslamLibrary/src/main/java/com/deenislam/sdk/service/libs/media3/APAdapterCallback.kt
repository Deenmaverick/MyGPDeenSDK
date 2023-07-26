package com.deenislam.sdk.service.libs.media3

internal interface APAdapterCallback {

    fun isPlaying(position: Int = -1, duration: Int?)
    fun isPause(position:Int=-1)
    fun isStop(position:Int=-1)
    fun isComplete(position: Int=-1)
}