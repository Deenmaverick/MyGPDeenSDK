package com.deenislamic.sdk.views.start

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import com.deenislamic.sdk.R
import com.deenislamic.sdk.views.base.BaseRegularFragment

@Keep
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