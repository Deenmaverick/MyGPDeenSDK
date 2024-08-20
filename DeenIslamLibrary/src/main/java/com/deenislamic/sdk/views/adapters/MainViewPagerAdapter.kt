package com.deenislamic.sdk.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


internal class MainViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val getFragments: ArrayList<Fragment>
) : FragmentStateAdapter(fragmentManager,lifecycle) {

    //private val fragmentIds = getFragments.map { it.hashCode().toLong() }

    override fun getItemCount(): Int {
        return getFragments.size
    }


    fun getFragmentAt(position: Int): Fragment {
        return getFragments[position]
    }


    override fun createFragment(position: Int): Fragment {
        return getFragments[position]
    }

}