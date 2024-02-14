package com.deenislam.sdk.service.weakref.main

import com.deenislam.sdk.service.libs.media3.QuranPlayer
import com.deenislam.sdk.service.libs.media3.QuranPlayerOffline
import java.lang.ref.WeakReference

internal object MainActivityInstance {

    private var quranPlayerRef: WeakReference<QuranPlayer>? = null

    private var quranPlayerOfflineRef: WeakReference<QuranPlayerOffline>? = null


    // Getter method for the instance
    fun getQuranPlayerInstance(): QuranPlayer? = quranPlayerRef?.get()

    fun getQuranPlayerOfflineInstance(): QuranPlayerOffline? = quranPlayerOfflineRef?.get()


    // Update method for the instance
    fun updateQuranPlayer(quranPlayer: QuranPlayer) {
        quranPlayerRef = WeakReference(quranPlayer)
    }

    fun updateQuranPlayerOffline(quranPlayer: QuranPlayerOffline) {
        quranPlayerOfflineRef = WeakReference(quranPlayer)
    }

    // Clear reference method
    fun clearReferences() {
        quranPlayerRef?.clear()
    }
}
