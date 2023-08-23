package com.deenislam.sdk.views.start

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deenislam.sdk.R
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.transition.MaterialSharedAxis

internal class BlankFragment : BaseRegularFragment() {


    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("BLANK_FRAG","OK")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }



    override fun onBackPress() {
        setupOtherFragment(false)
    }

}