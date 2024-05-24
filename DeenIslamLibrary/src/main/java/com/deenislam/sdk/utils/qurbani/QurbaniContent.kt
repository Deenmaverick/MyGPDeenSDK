package com.deenislam.sdk.utils.qurbani

fun Float.getBanglaSize(default:Float): Float {
    return  when (this) {
        20F -> default+2

        40F -> default+4

        60F -> default+6

        80F -> default+8

        100F -> default+10
        else -> default
    }
}
