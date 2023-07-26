package com.deenislam.sdk.service.callback

internal interface AlQuranAyatCallback
{
    fun playNextAyat(position: Int)
    fun isAyatPlaying(position: Int, duration: Int?)
    fun isAyatPause()

    fun isLoadingState(b: Boolean)
}