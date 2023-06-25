package com.deenislam.sdk.service.models

sealed class SplashResource
{
    object login_page:SplashResource()
    object onboarding_page:SplashResource()
    object dashboard_page:SplashResource()


}
