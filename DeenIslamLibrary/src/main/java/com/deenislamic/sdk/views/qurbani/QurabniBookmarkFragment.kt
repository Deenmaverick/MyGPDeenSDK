package com.deenislamic.sdk.views.qurbani

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.views.base.BaseRegularFragment


internal class QurabniBookmarkFragment : BaseRegularFragment() {

    private lateinit var listView:RecyclerView
    //private val viewmode by viewModels<QurbaniViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_qurabni_bookmark,container,false)

        listView = mainview.findViewById(R.id.listView)
        return mainview
    }

}