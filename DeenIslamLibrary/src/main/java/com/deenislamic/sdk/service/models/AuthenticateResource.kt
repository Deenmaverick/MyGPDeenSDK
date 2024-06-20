package com.deenislamic.sdk.service.models

internal sealed interface AuthenticateResource
{
    data class loginFailed(val msg:String):AuthenticateResource
    object loginSuccess:AuthenticateResource

    data class signupFailed(val msg:String):AuthenticateResource
    object signupSuccess:AuthenticateResource

    object clearLiveData:AuthenticateResource
}