package com.deenislam.sdk.views.start

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deenislam.sdk.R
import com.deenislam.sdk.views.base.BaseRegularFragment

internal class BlankFragment : BaseRegularFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("BLANK_FRAG","OK")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setupOtherFragment(true)
    }


    override fun onBackPress() {
        setupOtherFragment(false)
    }

}