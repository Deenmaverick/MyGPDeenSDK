package com.deenislam.sdk.views.common.subcatcardlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.deenislam.sdk.R
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.card.MaterialCardView


internal class SubCatBasicCardDetailsFragment : BaseRegularFragment() , otherFagmentActionCallback {

    private val navArgs: SubCatBasicCardDetailsFragmentArgs by navArgs()
    private lateinit var content: WebView
    private lateinit var contentCard:MaterialCardView
    var fullContent = ""
    var title = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_sub_cat_basic_card_details,container,false)

        content = mainview.findViewById(R.id.content)
        contentCard = mainview.findViewById(R.id.contentCard)

         if (navArgs.data != null) {
              title = navArgs.data?.Title!!
         } else if (navArgs.dataName != null) {
             title = navArgs.dataName?.arabicText!!
         }

        setupActionForOtherFragment(
            action1 = /*if(navArgs.shareable) R.drawable.ic_share else 0*/0,
            action2 = 0,
            callback = this,
            actionnBartitle = title,
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupBackPressCallback(this)

        if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else
            loadpage()

    }

    private fun loadpage()
    {


            if(navArgs.data?.TextInArabic!=null && navArgs.data?.TextInArabic?.isNotEmpty() == true)
                fullContent = "$fullContent${navArgs.data?.TextInArabic}\n\n"
            if(navArgs.data?.Text!=null && navArgs.data?.Text?.isNotEmpty() == true)
                fullContent = "$fullContent${navArgs.data?.Text}\n\n"
            if(navArgs.data?.Pronunciation!=null && navArgs.data?.Pronunciation?.isNotEmpty() == true)
                fullContent = "$fullContent${navArgs.data?.Pronunciation}"



        if(navArgs.dataName?.logo!=null)
            fullContent = "$fullContent${navArgs.dataName?.logo}"


        // Use WebView to load HTML content
        content.loadDataWithBaseURL(null, fullContent.trim().replace("\n", "<br>"), "text/html", "UTF-8", null)

        val webSettings  = content.settings
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        content.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                baseViewState()
            }
        }
    }

    override fun action1() {
        //requireContext().shareText("$title:\n\n$fullContent\n\nExplore a world of Islamic content on your fingertips. https://shorturl.at/GPSY6")
    }

    override fun action2() {
    }


}