package com.deenislam.sdk.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(
    fragmentManager: FragmentManager,
    lifecycle:Lifecycle,
    private val pageDestination: Array<Fragment>
) : FragmentStateAdapter(fragmentManager,lifecycle) {

    override fun getItemCount(): Int {
        return pageDestination.size
    }

    override fun createFragment(position: Int): Fragment {
        return pageDestination[position];
    }


}