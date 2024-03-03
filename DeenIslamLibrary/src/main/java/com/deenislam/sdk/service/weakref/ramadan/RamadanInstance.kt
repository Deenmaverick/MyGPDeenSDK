package com.deenislam.sdk.service.weakref.ramadan

import com.deenislam.sdk.service.libs.media3.QuranPlayer
import com.deenislam.sdk.service.libs.media3.QuranPlayerOffline
import com.deenislam.sdk.service.weakref.main.MainActivityInstance
import com.deenislam.sdk.views.ramadan.patch.RamadanTrackCard
import java.lang.ref.WeakReference

internal object RamadanInstance {

    private var ramadancardInstance: WeakReference<RamadanTrackCard>? = null

    // Getter method for the instance
    fun getRamadanCardInstance(): RamadanTrackCard? = ramadancardInstance?.get()

    fun updateRamadanCard(ramadanCardInstance: RamadanTrackCard) {
        ramadancardInstance = WeakReference(ramadanCardInstance)
    }

}