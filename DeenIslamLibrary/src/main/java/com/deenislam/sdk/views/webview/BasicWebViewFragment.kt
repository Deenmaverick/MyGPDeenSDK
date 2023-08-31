package com.deenislam.sdk.views.webview

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.navArgs
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialSharedAxis


internal class BasicWebViewFragment : BaseRegularFragment(), otherFagmentActionCallback {

    private lateinit var webview: WebView
    private lateinit var progressLayout: LinearLayout
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var noInternetRetry: MaterialButton
    private val args:BasicWebViewFragmentArgs by navArgs()


    override fun OnCreate() {
        super.OnCreate()
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_basic_web_view,container,false)

        //init view

        webview = mainView.findViewById(R.id.webview)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        noInternetLayout = mainView.findViewById(R.id.no_internet_layout)
        noInternetRetry = noInternetLayout.findViewById(R.id.no_internet_retry)

        setupActionForOtherFragment(0,0,null,args.title,true,mainView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.postDelayed({
            // Code to execute after the animation
            ViewCompat.setTranslationZ(progressLayout, 10F)
            ViewCompat.setTranslationZ(noInternetLayout, 10F)


            //click retry button for get api data again
            noInternetRetry.setOnClickListener {
                webview.reload()
            }

            loadWebVieww()
        }, 300)
    }

    private fun loadWebVieww()
    {
        val webSettings = webview.settings
        //webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true


        webview.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.url.toString())
                    loadingState()
                    true
                } else {
                    view.loadUrl(request.toString()) // Note: request.toString() might not provide the same level of detail as request.url.toString() in newer versions
                    loadingState()
                    true
                }
            }


                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                        if(error?.errorCode == -2)
                            noInternetState()
                        super.onReceivedError(view, request, error)
                    } else {
                        if (request != null && error != null) {
                            handleWebViewError(view, error.errorCode, error.description?.toString() ?: "", request.url.toString())
                        }
                    }
                }



            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            loadingState()
            super.onPageStarted(view, url, favicon)
        }

        override fun onLoadResource(view: WebView, url: String) {

            super.onLoadResource(view, url)
        }

        override fun onPageFinished(view: WebView, url: String) {

            super.onPageFinished(view, url)
            webview.visibility = View.VISIBLE
            viewState()
        }
    }


        loadingState()
        webview.loadUrl(args.weburl)

    }

    private fun handleWebViewError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        if (errorCode == -2) {
            noInternetState()
        }
        // Add more error handling cases if needed

        // Common error handling logic here
    }

    private fun loadingState()
    {
        progressLayout.visible(true)
        noInternetLayout.visible(false)
    }

    private fun noInternetState()
    {
        progressLayout.hide()
        noInternetLayout.show()
    }

    private fun viewState()
    {
        progressLayout.hide()
        noInternetLayout.hide()
    }

    override fun action1() {
    }

    override fun action2() {
    }

}