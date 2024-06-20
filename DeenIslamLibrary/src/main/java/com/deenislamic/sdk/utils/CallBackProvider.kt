package com.deenislamic.sdk.utils

import androidx.fragment.app.Fragment

object CallBackProvider {

    private var fragment:Fragment ? = null

    fun setFragment(fragment: Fragment)
    {
        this.fragment = fragment
    }

    fun getFragment() = fragment

    inline fun <reified T> get(): T? {

        return if (getFragment() != null && getFragment() is T) {
            getFragment() as T
        } else {
            null
        }

       /* return if(getFragment()==null)
            null
        else
            when (T::class) {
                T::class -> getFragment() as T
                else -> null
            }*/
        }


}