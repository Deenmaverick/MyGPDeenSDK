package com.deenislamic.sdk.views.payment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.alertdialog.CustomAlertDialog
import com.deenislamic.sdk.service.libs.alertdialog.CustomDialogCallback
import com.deenislamic.sdk.service.repository.PaymentRepository
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.load
import com.deenislamic.sdk.viewmodels.PaymentViewModel
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.net.URI
import java.net.URISyntaxException


internal class PaymentWebViewFragment : BaseRegularFragment(), CustomDialogCallback {

    private lateinit var webview: WebView
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView: View
    private var cancelAlertDialog: CustomAlertDialog? =null
    private var alertDialog: AlertDialog? = null
    private val navArgs:PaymentWebViewFragmentArgs by navArgs()
    private lateinit var viewmodel:PaymentViewModel
    private var paymentStatus = ""

    override fun OnCreate() {
        super.OnCreate()

        val paymentRepository = PaymentRepository(
            paymentService = NetworkProvider().getInstance().providePaymentService(),
            nagadPaymentService = NetworkProvider().getInstance().provideNagadPaymentService(),
            authInterceptor = NetworkProvider().getInstance().provideAuthInterceptor())

        val factory = VMFactory(paymentRepository)
        viewmodel = ViewModelProvider(
            requireActivity(),
            factory
        )[PaymentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_payment_web_view,container,false)

        webview = mainview.findViewById(R.id.webview)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = navArgs.title,
            backEnable = true,
            view = mainview
        )


        setupCommonLayout(mainview)

        materialAlertDialogBuilder =
            MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_login_success, null, false)

        cancelAlertDialog = CustomAlertDialog().getInstance()
        cancelAlertDialog?.setupDialog(
            callback = this@PaymentWebViewFragment,
            context = requireContext(),
            btn1Text = localContext.getString(R.string.continueTxt),
            btn2Text = localContext.getString(R.string.cancel),
            titileText = localContext.getString(R.string.want_to_cancel),
            subTitileText = localContext.getString(R.string.do_you_want_to_cancel_the_payment)
        )

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackPressCallback(this)

         /*if (!isDetached) {
            view.postDelayed({
                loadWebVieww()
            }, 300)
        }
        else*/
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
        webSettings.allowFileAccess = true
        //webSettings.loadWithOverviewMode = true
        //webview.clearCache(true)


        webview.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {

                view.loadUrl(getUrlFromRequest(request))
                baseLoadingState()
                return true
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

                        handleWebViewError(view, errorCode, errorDescription, getUrlFromRequest(request))
                    }
                }

            }



            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                baseLoadingState()

                Log.e("PaymentWebview",url)

                if(getDomain(url)== "payment.islamicservice.net" || getDomain(url)== "api.payment-app.info") {

                    try {
                        val uri = URI(url)
                        // Get the last path segment
                        //Log.e("onPageStarted",getLastPathSegment(uri))

                        when (getLastPathSegment(uri)) {

                            "SuccessCallbackView","success" -> {
                                paySuccess()
                                webview.hide()
                                return
                            }

                            "CancelCallbackView","deny" -> {
                                payCancel()
                                webview.hide()
                                return
                            }

                            "FailedCallbackView","failed","error" -> {
                                payFailed()
                                webview.hide()
                                return
                            }
                        }


                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                    }

                    val uri = Uri.parse(url)
                    val status = uri.getQueryParameter("status")

                    if (status != null) {

                        when (status) {
                            "Successful","SUCCEEDED" -> paySuccess()
                            "cancel","CANCELLED" -> payCancel()
                            "failure","FAILED" -> payFailed()
                            else -> payFailed()
                        }
                        webview.hide()
                        return

                    }
                } else if(getDomain(url)== "nagadpayment.islamicservice.net") {

                    try {
                        val uri = URI(url)


                        when (getLastPathSegment(uri)) {

                            "SuccessCallbackView" -> {
                                paySuccess()
                                webview.hide()
                                return
                            }

                            "CancelCallbackView" -> {
                                payCancel()
                                webview.hide()
                                return
                            }

                            "FailedCallbackView" -> {
                                payFailed()
                                webview.hide()
                                return
                            }
                        }


                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                    }

                    val uri = Uri.parse(url)
                    val status = uri.getQueryParameter("status")

                    if (status != null) {

                        when (status) {
                            "Success" -> paySuccess()
                            "Failed" -> payFailed()
                            "Aborted" -> payCancel()
                            else -> payFailed()
                        }
                        webview.hide()
                        return

                    }
                }

                super.onPageStarted(view, url, favicon)
            }

            override fun onLoadResource(view: WebView, url: String) {

                super.onLoadResource(view, url)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                baseViewState()

                //Log.e("onPageFinished","called")
            }
        }

        baseLoadingState()
        webview.loadUrl(navArgs.paymentUrl)

    }

    private fun getLastPathSegment(uri: URI): String {
        val path = uri.path
        if (path == null || path.isEmpty()) {
            return ""
        }

        // Split the path into segments using "/"
        val segments = path.split("/").toTypedArray()

        // Get the last segment
        return segments[segments.size - 1]
    }

    fun getDomain(urlString: String): String {
        try {
            val uri = URI(urlString)
            return uri.host ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun paySuccess()
    {
        paymentStatus = "success"
        viewmodel.ipnCallback(200)

        val okBtn = customAlertDialogView.findViewById<MaterialButton>(R.id.okBtn)
        val greetings = customAlertDialogView.findViewById<AppCompatTextView>(R.id.greeting)
        val hint:AppCompatTextView = customAlertDialogView.findViewById(R.id.hint)

        greetings.text = localContext.getString(R.string.thank_you_muhtaram)
        hint.text = navArgs.paySuccessMessage

        if (alertDialog?.isShowing == true)
            alertDialog?.dismiss()

        if (customAlertDialogView.parent != null)
            (customAlertDialogView.parent as ViewGroup).removeView(customAlertDialogView)

        alertDialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(false)
            .show()


        okBtn.let {
            it.setOnClickListener {
                alertDialog?.dismiss()
                alertDialog = null
                findNavController().popBackStack(navArgs.redirectPage, false)
            }
        }
    }

    private fun payFailed()
    {
        paymentStatus = "failed"

        viewmodel.ipnCallback(400)

        val okBtn = customAlertDialogView.findViewById<MaterialButton>(R.id.okBtn)
        val greetings = customAlertDialogView.findViewById<AppCompatTextView>(R.id.greeting)
        val hint:AppCompatTextView = customAlertDialogView.findViewById(R.id.hint)
        val logo:AppCompatImageView = customAlertDialogView.findViewById(R.id.logo)

        greetings.text = localContext.getString(R.string.sorry_muhtaram)
        hint.text = localContext.getString(R.string.payfailed_msg)
        logo.load(R.drawable.deen_ic_pay_failed)
        okBtn.text = localContext.getString(R.string.try_again)

        if (alertDialog?.isShowing == true)
            alertDialog?.dismiss()

        if (customAlertDialogView.parent != null)
            (customAlertDialogView.parent as ViewGroup).removeView(customAlertDialogView)

        alertDialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(false)
            .show()


        okBtn.let {
            it.setOnClickListener {
                alertDialog?.dismiss()
                alertDialog = null
                findNavController().popBackStack(navArgs.redirectPage, false)
            }
        }
    }

    private fun payCancel()
    {
        viewmodel.ipnCallback(100)
        findNavController().popBackStack(navArgs.redirectPage, false)
    }

    override fun onBackPress() {
        if(paymentStatus != "success" && paymentStatus != "failed")
        cancelAlertDialog?.showDialog()
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

    override fun clickBtn1() {
        cancelAlertDialog?.dismissDialog()
    }

    override fun clickBtn2() {
        payCancel()
        cancelAlertDialog?.dismissDialog()
    }

    fun getUrlFromRequest(request: WebResourceRequest): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            request.url.toString()
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // For older devices, try to extract the URL from the WebView
            try {
                val field = request.javaClass.getDeclaredField("mUrl")
                field.isAccessible = true
                field.get(request) as String
            } catch (e: Exception) {
                // Handle exceptions or return a default value
                ""
            }
        } else {
            ""
        }
    }


    inner class VMFactory(
        private val paymentRepository: PaymentRepository
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PaymentViewModel(paymentRepository) as T
        }
    }
}