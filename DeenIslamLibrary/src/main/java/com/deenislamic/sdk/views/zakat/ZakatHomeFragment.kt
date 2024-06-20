package com.deenislamic.sdk.views.zakat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

internal class ZakatHomeFragment : BaseRegularFragment() {

    private lateinit var newCaculateBtn:MaterialButton

    private lateinit var faq1:MaterialCardView
    private lateinit var faq1Count:AppCompatTextView
    private lateinit var faq1Titile:AppCompatTextView
    private lateinit var faq1Content:AppCompatTextView

    private lateinit var faq2:MaterialCardView
    private lateinit var faq2Count:AppCompatTextView
    private lateinit var faq2Titile:AppCompatTextView
    private lateinit var faq2Content:AppCompatTextView

    private lateinit var faq3:MaterialCardView
    private lateinit var faq3Count:AppCompatTextView
    private lateinit var faq3Titile:AppCompatTextView
    private lateinit var faq3Content:AppCompatTextView

    private lateinit var faq4:MaterialCardView
    private lateinit var faq4Count:AppCompatTextView
    private lateinit var faq4Titile:AppCompatTextView
    private lateinit var faq4Content:AppCompatTextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_zakat_home,container,false)

        //init view
        newCaculateBtn = mainView.findViewById(R.id.newCaculateBtn)

        faq1 = mainView.findViewById(R.id.faq1)
        faq1Count = faq1.findViewById(R.id.countTxt)
        faq1Titile = faq1.findViewById(R.id.title)
        faq1Content = faq1.findViewById(R.id.content)

        faq2 = mainView.findViewById(R.id.faq2)
        faq2Count = faq2.findViewById(R.id.countTxt)
        faq2Titile = faq2.findViewById(R.id.title)
        faq2Content = faq2.findViewById(R.id.content)

        faq3 = mainView.findViewById(R.id.faq3)
        faq3Count = faq3.findViewById(R.id.countTxt)
        faq3Titile = faq3.findViewById(R.id.title)
        faq3Content = faq3.findViewById(R.id.content)

        faq4 = mainView.findViewById(R.id.faq4)
        faq4Count = faq4.findViewById(R.id.countTxt)
        faq4Titile = faq4.findViewById(R.id.title)
        faq4Content = faq4.findViewById(R.id.content)


        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newCaculateBtn.setOnClickListener {

            gotoFrag(R.id.action_zakatFragment_to_zakatCalculatorFragment)
        }

        // setup faq 1
        faq1Count.text = localContext.resources.getString(R.string.count1)
        faq1Titile.text = localContext.resources.getString(R.string.zakah_home_faq1_title)
        faq1Content.text = localContext.resources.getString(R.string.zakah_home_faq1_content)
        faq1.setOnClickListener {
            faq1Content.visible(!faq1Content.isVisible)
        }

        // setup faq 2
        faq2Count.text = localContext.resources.getString(R.string.count2)
        faq2Titile.text = localContext.resources.getString(R.string.zakah_home_faq2_title)
        faq2Content.text = localContext.resources.getString(R.string.zakah_home_faq2_content)
        faq2.setOnClickListener {
            faq2Content.visible(!faq2Content.isVisible)
        }

        // setup faq 3
        faq3Count.text = localContext.resources.getString(R.string.count3)
        faq3Titile.text = localContext.resources.getString(R.string.zakah_home_faq3_title)
        faq3Content.text = localContext.resources.getString(R.string.zakah_home_faq3_content)
        faq3.setOnClickListener {
            faq3Content.visible(!faq3Content.isVisible)
        }

        // setup faq 4
        faq4Count.text = localContext.resources.getString(R.string.count4)
        faq4Titile.text = localContext.resources.getString(R.string.zakah_home_faq4_title)
        faq4Content.text = localContext.resources.getString(R.string.zakah_home_faq4_content)
        faq4.setOnClickListener {
            faq4Content.visible(!faq4Content.isVisible)
        }

    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "zakat",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }

    }

}