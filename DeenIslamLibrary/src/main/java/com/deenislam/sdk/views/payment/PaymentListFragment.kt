package com.deenislam.sdk.views.payment

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.PaymentResource
import com.deenislam.sdk.service.models.payment.PaymentModel
import com.deenislam.sdk.service.repository.PaymentRepository
import com.deenislam.sdk.utils.LoadingButton
import com.deenislam.sdk.utils.TERMS_URL
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.toast
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.PaymentViewModel
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch


internal class PaymentListFragment : BaseRegularFragment() {

    private val navArgs: PaymentListFragmentArgs by navArgs()
    private lateinit var heading: AppCompatTextView
    private lateinit var payableAmount: AppCompatTextView

    private lateinit var paymentTelco: MaterialCardView
    private lateinit var telcoIcMethod: AppCompatImageView
    private lateinit var telcoPayTitle: AppCompatTextView

    private lateinit var paymentBkash: MaterialCardView
    private lateinit var bkashIcMethod: AppCompatImageView
    private lateinit var bkashPayTitle: AppCompatTextView

    private lateinit var paymentNagad: MaterialCardView
    private lateinit var nagadIcMethod: AppCompatImageView
    private lateinit var nagadPayTitle: AppCompatTextView

    private lateinit var paymentGpay: MaterialCardView
    private lateinit var gPayIcMethod: AppCompatImageView
    private lateinit var gPayTitle: AppCompatTextView


    private lateinit var paymentCard: MaterialCardView
    private lateinit var cardPayIcMethod: AppCompatImageView
    private lateinit var cardPayTitle: AppCompatTextView
    private lateinit var clCheckbox: ConstraintLayout
    private lateinit var appCompatCheckBox: AppCompatCheckBox
    private lateinit var tvAgreeTxt: AppCompatTextView

    private lateinit var payBtn: MaterialButton
    private var paymentWebViewTitle = ""
    private var firstload = false

    private lateinit var viewmodel: PaymentViewModel

    private var selectedPaymentMethod = ""
    private lateinit var paymentData: PaymentModel

    override fun OnCreate() {
        super.OnCreate()
        val paymentRepository = PaymentRepository(
            paymentService = NetworkProvider().getInstance().providePaymentService(),
            nagadPaymentService = NetworkProvider().getInstance().provideNagadPaymentService(),
            authInterceptor = NetworkProvider().getInstance().provideAuthInterceptor())

        viewmodel = PaymentViewModel(paymentRepository)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_payment_list, container, false)

        heading = mainview.findViewById(R.id.heading)
        payableAmount = mainview.findViewById(R.id.payableAmount)

        paymentTelco = mainview.findViewById(R.id.paymentTelco)
        telcoIcMethod = paymentTelco.findViewById(R.id.icMethod)
        telcoPayTitle = paymentTelco.findViewById(R.id.title)


        paymentBkash = mainview.findViewById(R.id.paymentBkash)
        bkashIcMethod = paymentBkash.findViewById(R.id.icMethod)
        bkashPayTitle = paymentBkash.findViewById(R.id.title)

        paymentNagad = mainview.findViewById(R.id.paymentNagad)
        nagadIcMethod = paymentNagad.findViewById(R.id.icMethod)
        nagadPayTitle = paymentNagad.findViewById(R.id.title)

        paymentGpay = mainview.findViewById(R.id.paymentGpay)
        gPayIcMethod = paymentGpay.findViewById(R.id.icMethod)
        gPayTitle = paymentGpay.findViewById(R.id.title)

        paymentCard = mainview.findViewById(R.id.paymentCard)
        cardPayIcMethod = paymentCard.findViewById(R.id.icMethod)
        cardPayTitle = paymentCard.findViewById(R.id.title)

        payBtn = mainview.findViewById(R.id.payBtn)

        telcoIcMethod.load(R.drawable.deen_ic_telco_payment)
        telcoPayTitle.text = localContext.getString(R.string.telco_payment)

        bkashIcMethod.load(R.drawable.deen_ic_bkash_payment)
        bkashPayTitle.text = localContext.getString(R.string.bkash)

        cardPayIcMethod.load(R.drawable.deen_ic_card_payment)
        cardPayTitle.text = localContext.getString(R.string.debit_credit_card)

        nagadIcMethod.load(R.drawable.deen_ic_nagad_payment)
        nagadPayTitle.text = localContext.getString(R.string.nagad)



        clCheckbox = mainview.findViewById(R.id.clCheckbox)
        appCompatCheckBox = mainview.findViewById(R.id.appCompatCheckBox)
        paymentData = navArgs.payment
        tvAgreeTxt = mainview.findViewById(R.id.tvAgreeTxt)

        if (!paymentData.isTelcoEnable)
            paymentTelco.hide()

        if (!paymentData.isBkashEnable)
            paymentBkash.hide()

        if (!paymentData.isNagadEnable)
            paymentNagad.hide()

        if (!paymentData.isSSLEnable)
            paymentCard.hide()

        if (!paymentData.isGpayEnable)
            paymentGpay.hide()

        clCheckbox.visible(paymentData.tcUrl != null)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = paymentData.title,
            backEnable = true,
            view = mainview
        )


        setClickableLinks(tvAgreeTxt)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*if (firstload)
            loadPage()
        else if (!isDetached) {
            view.postDelayed({
                loadPage()
            }, 300)
        } else*/
            loadPage()

        firstload = true

    }

    private fun loadPage() {

        initObserver()

        heading.text = paymentData.title
        payableAmount.text = "৳${paymentData.amount}".numberLocale()
        payBtn.text =
            localContext.getString(R.string.payincAmount, paymentData.amount).numberLocale()

        // manual activate method

        activePaymentMethod(paymentBkash)
        paymentWebViewTitle = localContext.getString(R.string.bkash)
        selectedPaymentMethod = "bKash"

        paymentTelco.setOnClickListener {
            activePaymentMethod(paymentTelco)
            paymentWebViewTitle = localContext.getString(R.string.telco_payment)
            selectedPaymentMethod = "telco"
        }

        paymentBkash.setOnClickListener {
            activePaymentMethod(paymentBkash)
            paymentWebViewTitle = localContext.getString(R.string.bkash)
            selectedPaymentMethod = "bKash"
        }

        paymentNagad.setOnClickListener {
            activePaymentMethod(paymentNagad)
            paymentWebViewTitle = localContext.getString(R.string.nagad)
            selectedPaymentMethod = "nagad"
        }

        paymentCard.setOnClickListener {
            activePaymentMethod(paymentCard)
            paymentWebViewTitle = localContext.getString(R.string.debit_credit_card)
            selectedPaymentMethod = "ssl"
        }

        paymentGpay.setOnClickListener {

            activePaymentMethod(paymentGpay)
            selectedPaymentMethod = "gpay"

        }

        payBtn.setOnClickListener {

            if (selectedPaymentMethod.isEmpty()) {
                context?.toast(localContext.getString(R.string.choose_a_payment_method))
                return@setOnClickListener
            }

            if (!appCompatCheckBox.isChecked) {
                requireContext().toast(localContext.getString(R.string.please_accept_payment_terms_and_condition))
                return@setOnClickListener
            }

            payBtn.isClickable = false
            payBtn.text = LoadingButton().getInstance(requireContext()).loader(payBtn)

            when (selectedPaymentMethod) {
                "bKash" -> bKashPayment()
                "ssl" -> sslPayment()
                "nagad" -> nagadPayment()
            }

            /*val bundle = Bundle()
            bundle.putString("title",paymentWebViewTitle)
            bundle.putString("paymentUrl","https://www.google.com")
            bundle.putInt("redirectPage",navArgs.redirectPage)
            gotoFrag(R.id.action_global_paymentWebViewFragment,bundle)*/
        }


        /*clCheckbox.setOnClickListener {
            if (paymentData.tcUrl != null) {
                val bundle = Bundle()
                bundle.putString("title", localContext.getString(R.string.terms_and_condition))
                bundle.putString("weburl", paymentData.tcUrl)
                gotoFrag(R.id.action_global_basicWebViewFragment, bundle)
            }
        }*/

    }

    private fun activePaymentMethod(card: MaterialCardView) {
        unselectAllMethod()
        card.strokeWidth = 1.dp
        card.findViewById<AppCompatImageView>(R.id.icTick).show()
    }

    private fun unselectAllMethod() {
        paymentTelco.findViewById<AppCompatImageView>(R.id.icTick).hide()
        paymentBkash.findViewById<AppCompatImageView>(R.id.icTick).hide()
        paymentNagad.findViewById<AppCompatImageView>(R.id.icTick).hide()
        paymentCard.findViewById<AppCompatImageView>(R.id.icTick).hide()

        paymentTelco.strokeWidth = 0
        paymentBkash.strokeWidth = 0
        paymentNagad.strokeWidth = 0
        paymentCard.strokeWidth = 0

    }

    private fun setClickableLinks(textView: AppCompatTextView) {
        val spannableStringBuilder = SpannableStringBuilder()

        // Combine the entire text
        val fullText = localContext.getString(R.string.i_agree_to_the_payment) +
                localContext.getString(R.string.terms_and_condition) +
                localContext.getString(R.string.and) +
                localContext.getString(R.string.terms_of_use)

        // Set a ClickableSpan for "Terms and Conditions"
        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (navArgs.payment.tcUrl != null) {
                    val bundle = Bundle()
                    bundle.putString("title", localContext.getString(R.string.terms_and_condition))
                    bundle.putString("weburl", navArgs.payment.tcUrl)
                    gotoFrag(R.id.action_global_basicWebViewFragment, bundle)
                }
            }
        }
        // Set ForegroundColorSpan to color the hyperlink
        val colorSpan1 = ForegroundColorSpan(getColor(textView.context, R.color.deen_primary))
        val start1 = fullText.indexOf(localContext.getString(R.string.terms_and_condition))
        val end1 = start1 + localContext.getString(R.string.terms_and_condition).length
        spannableStringBuilder.append(fullText)
        spannableStringBuilder.setSpan(clickableSpan1, start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringBuilder.setSpan(colorSpan1, start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set a ClickableSpan for "Terms of Use"
        val clickableSpan2 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val bundle = Bundle()
                bundle.putString("title", localContext.getString(R.string.terms_of_use))
                bundle.putString("weburl", TERMS_URL)
                gotoFrag(R.id.action_global_basicWebViewFragment, bundle)
            }
        }
        // Set ForegroundColorSpan to color the hyperlink
        val colorSpan2 = ForegroundColorSpan(getColor(textView.context, R.color.deen_primary))
        val start2 = fullText.indexOf(localContext.getString(R.string.terms_of_use))
        val end2 = start2 + localContext.getString(R.string.terms_of_use).length
        spannableStringBuilder.setSpan(clickableSpan2, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringBuilder.setSpan(colorSpan2, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set the SpannableStringBuilder to the TextView
        textView.text = spannableStringBuilder

        // Make the links clickable
        textView.movementMethod = LinkMovementMethod.getInstance()
    }



    private fun initObserver() {
            viewmodel.paymentLiveData.observe(viewLifecycleOwner)
            {

                if (CommonResource.CLEAR != it) {
                    lifecycleScope.launch {
                        viewmodel.clearPayment()
                    }
                }

                when (it) {
                    is CommonResource.API_CALL_FAILED -> {
                        requireContext().toast("Unable to proceed payment! Please try again")

                    }

                    is PaymentResource.PaymentUrl -> {

                        val bundle = Bundle()
                        bundle.putString("title", paymentWebViewTitle)
                        bundle.putString("paymentUrl", it.message)
                        bundle.putInt("redirectPage", paymentData.redirectPage)
                        bundle.putString("paySuccessMessage", paymentData.paySuccessMessage)
                        gotoFrag(R.id.action_global_paymentWebViewFragment, bundle)

                    }

                    is PaymentResource.PaymentUrlNagad -> {
                        if (it.status) {
                            val bundle = Bundle()
                            bundle.putString("title", paymentWebViewTitle)
                            bundle.putString("paymentUrl", it.message)
                            bundle.putInt("redirectPage", paymentData.redirectPage)
                            bundle.putString("paySuccessMessage", paymentData.paySuccessMessage)
                            gotoFrag(R.id.action_global_paymentWebViewFragment, bundle)
                        } else {
                            requireContext().toast(it.message!!)
                        }
                    }
                }

                payBtn.isClickable = true
                LoadingButton().getInstance(requireContext()).removeLoader()

                payBtn.text =
                    localContext.getString(R.string.payincAmount, paymentData.amount).numberLocale()

            }
        }

        private fun bKashPayment() {
            lifecycleScope.launch {
                if(navArgs.payment.isRecurring)
                    viewmodel.recurringPayment(serviceID = paymentData.serviceIDBkash)
                else
                    viewmodel.bKashPayment(serviceID = paymentData.serviceIDBkash)
            }
        }

        private fun nagadPayment() {
            lifecycleScope.launch {
                viewmodel.nagadPayment(serviceID = paymentData.serviceIDNagad)
            }
        }

        private fun sslPayment() {
            lifecycleScope.launch {
                viewmodel.sslPayment(serviceID = paymentData.serviceIDSSL)
            }
        }


}