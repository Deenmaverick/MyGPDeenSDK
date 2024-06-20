package com.deenislamic.sdk.service.weakref.ramadan

import com.deenislamic.sdk.views.ramadan.patch.RamadanTrackCard
import java.lang.ref.WeakReference

internal object RamadanInstance {

    private var ramadancardInstance: WeakReference<RamadanTrackCard>? = null

    // Getter method for the instance
    fun getRamadanCardInstance(): RamadanTrackCard? = ramadancardInstance?.get()

    fun updateRamadanCard(ramadanCardInstance: RamadanTrackCard) {
        ramadancardInstance = WeakReference(ramadanCardInstance)
    }

}