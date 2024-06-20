package com.deenislamic.sdk.viewmodels;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.repository.UserTrackRepository
import kotlinx.coroutines.launch

internal class UserTrackViewModel(
    private val repository: UserTrackRepository
) : ViewModel() {

    fun trackUser(language:String,msisdn:String,pagename:String,trackingID:Long)
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

    suspend fun saveAdvertisementrecord(adID:Int,response:String){
        viewModelScope.launch {
            repository.saveAdvertisementrecord(adID,response)
        }
    }

}