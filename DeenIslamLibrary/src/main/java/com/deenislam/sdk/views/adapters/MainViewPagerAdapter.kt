package com.deenislam.sdk.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


internal class MainViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    getFragments: ArrayList<Fragment>
) : FragmentStateAdapter(fragmentManager,lifecycle) {

    private var pageDestination: ArrayList<Fragment> = getFragments

    override fun getItemCount(): Int {
        return pageDestination.size
    }


   /* override fun getItemId(position: Int): Long {
            return pageDestination[position].hashCode().toLong()
    }*/



    override fun createFragment(position: Int): Fragment {
        return pageDestination[position]
    }

}