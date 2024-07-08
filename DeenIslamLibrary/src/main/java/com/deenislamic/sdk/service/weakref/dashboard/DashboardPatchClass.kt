package com.deenislamic.sdk.service.weakref.dashboard

import android.util.Log
import com.deenislamic.sdk.views.dashboard.patch.Allah99Names
import com.deenislamic.sdk.views.dashboard.patch.Billboard
import com.deenislamic.sdk.views.dashboard.patch.Compass
import com.deenislamic.sdk.views.dashboard.patch.Tasbeeh
import java.lang.ref.WeakReference

internal object DashboardPatchClass {

    // patch helper class instances with WeakReference
    private var billboardRef: Billboard? = null
    private var compassRef: WeakReference<Compass>? = null
    private var allah99NamesRef: WeakReference<Allah99Names>? = null
    private var tasbeehRef: WeakReference<Tasbeeh>? = null

    // Getter methods for instances
    fun getBillboardInstance(): Billboard? = billboardRef
    fun getCompassInstance(): Compass? = compassRef?.get()
    fun getAllah99NamesInstance(): Allah99Names? = allah99NamesRef?.get()
    fun getTasbeehInstance(): Tasbeeh? = tasbeehRef?.get()

    // Update methods for instances
    fun updateBillboard(billboard: Billboard) {
        Log.e("updateBillboard",billboard.toString())
        billboardRef = billboard
    }

    fun updateCompass(compass: Compass) {
        compassRef = WeakReference(compass)
    }

    fun updateAllah99Names(allah99Names: Allah99Names) {
        allah99NamesRef = WeakReference(allah99Names)
    }

    fun updateTasbeeh(tasbeeh: Tasbeeh) {
        tasbeehRef = WeakReference(tasbeeh)
    }


    // Clear references method
    fun clearReferences() {
        //billboardRef?.clear()
        compassRef?.clear()
        allah99NamesRef?.clear()
    }
}

