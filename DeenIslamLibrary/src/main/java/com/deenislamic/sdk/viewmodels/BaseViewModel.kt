package com.deenislamic.sdk.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

internal abstract class BaseViewModel:ViewModel() {

    private val _isAPILoaded: MutableLiveData<Boolean> = MutableLiveData(false)
    val isAPILoaded: MutableLiveData<Boolean> get() = _isAPILoaded

    fun check_api_state()
    {
        if(isAPILoaded.value==false) _isAPILoaded.value = true
    }

}