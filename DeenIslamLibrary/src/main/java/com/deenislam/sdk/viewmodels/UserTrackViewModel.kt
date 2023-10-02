package com.deenislam.sdk.viewmodels;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.repository.UserTrackRepository
import kotlinx.coroutines.launch

internal class UserTrackViewModel(
    private val repository: UserTrackRepository
) : ViewModel() {

    fun trackUser(language:String,msisdn:String,pagename:String,trackingID:Int)
    {
        viewModelScope.launch {
            repository.userTrack(
                language = language,
                msisdn = msisdn,
                pagename = pagename,
                trackingID = trackingID
            )
        }
    }

}