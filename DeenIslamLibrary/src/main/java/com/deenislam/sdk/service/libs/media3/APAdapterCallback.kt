package com.deenislam.sdk.service.libs.media3

interface APAdapterCallback {

    fun isPlaying(position:Int=-1)
    fun isPause(position:Int=-1)
    fun isStop(position:Int=-1)
}