package com.deenislam.sdk.service.models

internal sealed class OnBoardingResource
{
    data class isSetUserPref(val result:Boolean):OnBoardingResource()
}
