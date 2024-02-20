package com.deenislam.sdk.views.more

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
    private lateinit var favLayout:MaterialCardView

    override fun OnCreate() {
        super.OnCreate()
       // setupBackPressCallback(this,true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).apply {
            duration = 300L
        }
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true).apply {
            duration = 300L
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).apply {
            duration = 300L
        }

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
        favLayout = mainview.findViewById(R.id.favLayout)


        setupActionForOtherFragment(
            action1 = R.drawable.deen_ic_close,
            action2 = 0,
            callback = this@MoreFragment,
            actionnBartitle = localContext.getString(R.string.islamic),
            backEnable = false,
            view = mainview,
            isBackIcon = true
        )

        return mainview
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPage()
        /*view.postDelayed({
            // Code to execute after the animation
            loadPage()
        }, 300)*/

    }

    private fun loadPage()
    {
        username.text = DeenSDKCore.GetDeenMsisdn().numberLocale()

        settingLayout.setOnClickListener {
            gotoFrag(R.id.action_moreFragment_to_settingFragment)
        }

        editProfileBtn.setOnClickListener {

        }

        termsLayout.setOnClickListener {

            val bundle = Bundle().apply {
                putString("title", localContext.getString(R.string.terms_of_use))
                putString("weburl", TERMS_URL)
            }
            gotoFrag(R.id.basicWebViewFragment,data = bundle)
        }

        privacyLayout.setOnClickListener {

            /*   val url = PRIVACY_URL
               val intent = Intent(Intent.ACTION_VIEW)
               intent.data = Uri.parse(url)
               requireActivity().let {
                   if (intent.resolveActivity(it.packageManager) != null) {
                       startActivity(intent)
                   }
               }*/


            val bundle = Bundle().apply {
                putString("title", localContext.getString(R.string.privacy_policy))
                putString("weburl", PRIVACY_URL)
            }
            gotoFrag(R.id.basicWebViewFragment,data = bundle)
        }

        favLayout.setOnClickListener {
            gotoFrag(R.id.action_global_myFavoritesFragment)
        }
    }


    override fun action1() {
        onBackPress()
    }

    override fun action2() {
    }



}