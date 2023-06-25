package com.deenislam.sdk.service.models

sealed class OnBoardingResource
{
    data class isSetUserPref(val result:Boolean):OnBoardingResource()
}
