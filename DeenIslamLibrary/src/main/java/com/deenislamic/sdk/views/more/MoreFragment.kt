package com.deenislamic.sdk.views.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.utils.PRIVACY_URL
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.TERMS_URL
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.transition.MaterialSharedAxis
import java.io.File

internal class MoreFragment : BaseRegularFragment(),otherFagmentActionCallback {

    private lateinit var settingLayout:MaterialCardView
    private lateinit var username:AppCompatTextView
    private lateinit var editProfileBtn:MaterialButton
    private lateinit var termsLayout:MaterialCardView
    private lateinit var privacyLayout:MaterialCardView
    private lateinit var favLayout:MaterialCardView
    private lateinit var premiumLayout:MaterialCardView
    private lateinit var getPremiumTxt:AppCompatTextView
    private lateinit var premiumSub:AppCompatTextView
    private lateinit var downloadLayout:MaterialCardView
    private lateinit var downloadCount:AppCompatTextView

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
        premiumLayout = mainview.findViewById(R.id.premiumLayout)
        getPremiumTxt = mainview.findViewById(R.id.getPremiumTxt)
        premiumSub = mainview.findViewById(R.id.premiumSub)
        downloadLayout = mainview.findViewById(R.id.downloadLayout)
        downloadCount = mainview.findViewById(R.id.downloadCount)

        setupActionForOtherFragment(
            action1 = R.drawable.deen_ic_close,
            action2 = 0,
            callback = this@MoreFragment,
            actionnBartitle = localContext.getString(R.string.islamic),
            backEnable = false,
            view = mainview,
            isBackIcon = true
        )

        premiumLayout.setOnClickListener {
            gotoFrag(R.id.action_global_subscriptionFragment)
        }

        if(Subscription.isSubscribe){
            premiumSub.hide()
            getPremiumTxt.setPadding(0,12.dp,0,24.dp)
            getPremiumTxt.text  = localContext.getString(R.string.you_are_premium_user)
        }

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

        downloadCount.text = localContext.getString(R.string.more_download_count,
            (getQuranOfflineDownloadCount()).toString().numberLocale()
        )

        username.text = DeenSDKCore.GetDeenMsisdn().numberLocale()

        settingLayout.setOnClickListener {
            gotoFrag(R.id.action_moreFragment_to_settingFragment)
        }

        editProfileBtn.setOnClickListener {

        }

        downloadLayout.setOnClickListener {
            /*if(!Subscription.isSubscribe){
                gotoFrag(R.id.action_global_subscriptionFragment)
                return@setOnClickListener
            }*/
            gotoFrag(R.id.action_global_myDownloadFragment)
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
            /*if(!Subscription.isSubscribe){
                gotoFrag(R.id.action_global_subscriptionFragment)
                return@setOnClickListener
            }*/
            gotoFrag(R.id.action_global_myFavoritesFragment)
        }
    }


    override fun action1() {
        onBackPress()
    }

    override fun action2() {
    }

    private fun getQuranOfflineDownloadCount(): Int {

        val destinationFolder = File(requireContext().filesDir, "quran")
        destinationFolder.mkdirs()
        val subdirectories = destinationFolder.listFiles { file -> file.isDirectory }
        val fileNameToCheck = "surahinfo.json"
        var countDownload = 0

        subdirectories?.forEach { subdirectory ->
            val fileToCheck = File(subdirectory, fileNameToCheck)
            if (fileToCheck.exists()) {
                countDownload++
            }
        }

        return countDownload
    }


}