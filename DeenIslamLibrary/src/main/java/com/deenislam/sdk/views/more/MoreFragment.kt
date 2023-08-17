package com.deenislam.sdk.views.more

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.PRIVACY_URL
import com.deenislam.sdk.utils.TERMS_URL
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.transition.MaterialSharedAxis

internal class MoreFragment : BaseRegularFragment(),otherFagmentActionCallback {

    private lateinit var settingLayout:MaterialCardView
    private lateinit var username:AppCompatTextView
    private lateinit var editProfileBtn:MaterialButton
    private lateinit var termsLayout:MaterialCardView
    private lateinit var privacyLayout:MaterialCardView

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainview = localInflater.inflate(R.layout.fragment_more,container,false)

        //init view
        settingLayout = mainview.findViewById(R.id.settingLayout)
        username = mainview.findViewById(R.id.username)
        editProfileBtn = mainview.findViewById(R.id.editProfileBtn)
        termsLayout = mainview.findViewById(R.id.termsLayout)
        privacyLayout = mainview.findViewById(R.id.privacyLayout)

        setupActionForOtherFragment(
            action1 = R.drawable.ic_close,
            action2 = 0,
            callback = this@MoreFragment,
            actionnBartitle = localContext.getString(R.string.app_name),
            backEnable = false,
            view = mainview,
            isBackIcon = true
        )

        return mainview
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username.text =  if(!DeenSDKCore.msisdn.first().equals("+")) "+${DeenSDKCore.msisdn.numberLocale()}" else DeenSDKCore.msisdn.numberLocale()

        settingLayout.setOnClickListener {
            gotoFrag(R.id.action_moreFragment_to_settingFragment)
        }

        editProfileBtn.setOnClickListener {

        }

        termsLayout.setOnClickListener {

            val url = TERMS_URL
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            requireActivity().let {
                if (intent.resolveActivity(it.packageManager) != null) {
                    startActivity(intent)
                }
            }
        }

        privacyLayout.setOnClickListener {

            val url = PRIVACY_URL
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            requireActivity().let {
                if (intent.resolveActivity(it.packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
    }


    override fun action1() {
        onBackPress()
    }

    override fun action2() {
    }



}