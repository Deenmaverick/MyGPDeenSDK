package com.deenislamic.sdk.views.nearestmosque

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.database.AppPreference
import com.deenislamic.sdk.utils.LocationHelper
import com.deenislamic.sdk.utils.get9DigitRandom
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class NearestMosqueWebviewFragment : BaseRegularFragment() {

    private lateinit var webview: WebView

    private lateinit var locationHelper: LocationHelper
    private val PERMISSION_REQUEST_CODE = 143
    private val navArgs:NearestMosqueWebviewFragmentArgs by navArgs()

    private var firstload = false
    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_nearest_mosque_webview,container,false)

        //init view

        webview = mainView.findViewById(R.id.webview)

        setupActionForOtherFragment(0,0,null,navArgs.pageTitle,true,mainView)

        setupCommonLayout(mainView)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationHelper = LocationHelper(requireContext())

        if(!firstload)
        {
            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "nearest_mosque",
                    trackingID = getTrackingID()
                )
            }
        }

        firstload = true

        /*if (!isDetached) {
            view.postDelayed({
                initLocation()
            }, 300)
        }
        else*/
            initLocation()


    }

    private fun initLocation(){
        if (isLocationPermissionGiven(requireContext())) {
            locationHelper.requestLocation()
            loadWebVieww()

        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted, proceed with location-related operations
                locationHelper.requestLocation()
            }
            loadWebVieww()
        }
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "nearest_mosque",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }

    private fun isLocationPermissionGiven(context: Context): Boolean {
        return !(ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
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
                val url = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.url.toString()
                } else {
                    request.toString()
                }

                // Check if the URL is a specific intent link
                return if (isIntentLink(url)) {
                    // Handle the intent link
                    handleIntentLink()
                    true // Return true to indicate that the WebView should not handle the URL
                } else {
                    view.loadUrl(url)
                    baseLoadingState()
                    false
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

        webview.webChromeClient = object : WebChromeClient() {

            override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
                callback.invoke(origin, true, false)
            }
        }

        baseLoadingState()

        webview.loadUrl("https://google.com/maps/search/${navArgs.query}/@${AppPreference.getUserCurrentLocation().lat},${AppPreference.getUserCurrentLocation().lng}")
    }

    private fun handleWebViewError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        baseNoInternetState()
    }

    override fun noInternetRetryClicked() {
        loadWebVieww()
    }

    private fun isIntentLink(url: String): Boolean {
        // Customize this method based on the specific criteria for detecting intent links
        return url.startsWith("intent:")
    }

    private fun handleIntentLink() {

        val mapsUrl = "https://www.google.com/maps?q=${navArgs.query}&hl=en"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle the case where no app can handle the intent
            e.printStackTrace()
        }
    }
}