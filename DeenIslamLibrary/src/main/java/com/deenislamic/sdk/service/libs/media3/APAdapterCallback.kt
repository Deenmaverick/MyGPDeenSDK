package com.deenislamic.sdk.service.libs.media3

internal interface APAdapterCallback {

    fun isPlaying(position: Int = -1, duration: Long?, surahID: Int)
    fun isPause(position:Int=-1,byService:Boolean = false)
    fun isStop(position:Int=-1)
    fun isComplete(position: Int = -1, surahID: Int)
}