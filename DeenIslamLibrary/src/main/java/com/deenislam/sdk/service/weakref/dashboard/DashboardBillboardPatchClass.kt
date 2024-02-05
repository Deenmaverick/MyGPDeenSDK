package com.deenislam.sdk.service.weakref.dashboard

import com.deenislam.sdk.views.dashboard.patch.PrayerTime
import java.lang.ref.WeakReference

internal object DashboardBillboardPatchClass {

    // patch helper class instances with WeakReference
    private var prayerTimeRef: WeakReference<PrayerTime>? = null

    // Getter methods for instances
    fun getPrayerTimeInstance(): PrayerTime? = prayerTimeRef?.get()

    // Update methods for instances
    fun updatePrayerTime(prayerTime: PrayerTime) {
        prayerTimeRef = WeakReference(prayerTime)
    }

    // Clear references method
    fun clearReferences() {
        prayerTimeRef?.clear()
    }
}
