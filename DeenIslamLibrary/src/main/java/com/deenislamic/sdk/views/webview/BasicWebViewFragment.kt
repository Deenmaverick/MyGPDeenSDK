package com.deenislamic.sdk.views.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.deenislamic.sdk.R
import com.deenislamic.sdk.views.base.BaseRegularFragment


internal class BasicWebViewFragment : BaseRegularFragment() {

    private lateinit var webview: WebView
    private val navargs:BasicWebViewFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_basic_web_view,container,false)

        //init view

        webview = mainView.findViewById(R.id.webview)

        setupActionForOtherFragment(0,0,null,navargs.title,true,mainView)

        setupCommonLayout(mainView)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadWebVieww()

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebVieww()
    {
        val webSettings = webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.displayZoomControls = false
        webSettings.builtInZoomControls = true
        //webSettings.allowFileAccess = true
        webSettings.setGeolocationEnabled(true)
        //webview.clearCache(true)


        webview.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {

                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.url.toString())
                    baseLoadingState()
                    true
                } else {
                    view.loadUrl(request.toString()) // Note: request.toString() might not provide the same level of detail as request.url.toString() in newer versions
                    baseLoadingState()
                    true
                }
            }


            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if(error?.errorCode == -2)
                        baseNoInternetState()
                    super.onReceivedError(view, request, error)
                } else {
                    if (request != null && error != null) {
                        var errorCode = -2

                        var errorDescription: String? = null

                        errorDescription = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            // Use the method on devices with API level 23 or higher
                            errorCode = error.errorCode
                            try {
                                error.description.toString()
                            } catch (e: NoSuchMethodError) {
                                // Handle the absence of the method on older devices
                                // Provide a default error description
                                "An error occurred"
                            }
                        } else {
                            // Handle the absence of the method on older devices
                            "An error occurred"
                        }

                        val url = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            request.url.toString()
                        } else {
                            request.toString()
                        }

                        handleWebViewError(view, errorCode, errorDescription, url)
                    }
                }

            }



            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                baseLoadingState()
                super.onPageStarted(view, url, favicon)
            }

            override fun onLoadResource(view: WebView, url: String) {
                super.onLoadResource(view, url)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                webview.visibility = View.VISIBLE
                baseViewState()
            }
        }

        baseLoadingState()

        if(navargs.weburl!=null)
            webview.loadUrl(navargs.weburl!!)
        else if(navargs.webpage!=null)
            webview.loadDataWithBaseURL(null, navargs.webpage!!, "text/html", "UTF-8", null)
        else
            baseEmptyState()

    }

    private fun handleWebViewError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        if (errorCode == -2) {
            baseNoInternetState()
        }
        // Add more error handling cases if needed

        // Common error handling logic here
    }

    override fun noInternetRetryClicked() {
        loadWebVieww()
    }

}