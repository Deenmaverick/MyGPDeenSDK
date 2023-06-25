package com.deenislam.sdk.views.base

import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class  BaseActivity<VB: ViewBinding>(
    private val bindingInflater: (inflater:LayoutInflater)->VB
): AppCompatActivity() {

    private var _binding:VB ? = null
    val binding:VB get() = _binding as VB

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        _binding = bindingInflater(layoutInflater)

        if(_binding == null)
            throw java.lang.IllegalArgumentException("View cannot null")

        onViewCreated()

    }

    abstract fun  onViewCreated()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}