package com.deenislam.sdk.views.zakat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deenislam.sdk.R
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton

internal class ZakatHomeFragment : BaseRegularFragment() {

    private lateinit var newCaculateBtn:MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = layoutInflater.inflate(R.layout.fragment_zakat_home,container,false)

        //init view
        newCaculateBtn = mainView.findViewById(R.id.newCaculateBtn)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newCaculateBtn.setOnClickListener {

            gotoFrag(R.id.action_zakatFragment_to_zakatCalculatorFragment)
        }
    }

}