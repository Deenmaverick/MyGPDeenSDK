package com.deenislamic.sdk.utils.singleton

import androidx.lifecycle.LifecycleCoroutineScope
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.viewmodels.UserTrackViewModel
import kotlinx.coroutines.launch

internal object UserTracking {
    fun track(
        lifecycleScope: LifecycleCoroutineScope,
        userTrackViewModel: UserTrackViewModel,
        language: String,
        trackingID: Long,
        pagename: String
    ) {
        lifecycleScope.launch {
            userTrackViewModel.trackUser(
                language = language,
                msisdn = DeenSDKCore.GetDeenMsisdn(),
                pagename = pagename,
                trackingID = trackingID
            )
        }
    }
}