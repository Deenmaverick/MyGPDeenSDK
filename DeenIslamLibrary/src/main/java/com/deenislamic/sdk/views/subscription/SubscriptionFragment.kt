package com.deenislamic.sdk.views.subscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.SubscriptionCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.alertdialog.CustomAlertDialog
import com.deenislamic.sdk.service.libs.alertdialog.CustomDialogCallback
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.PaymentResource
import com.deenislamic.sdk.service.models.SubscriptionResource
import com.deenislamic.sdk.service.models.payment.PaymentModel
import com.deenislamic.sdk.service.network.response.payment.recurring.CheckRecurringResponse
import com.deenislamic.sdk.service.network.response.subscription.PaymentType
import com.deenislamic.sdk.service.repository.PaymentRepository
import com.deenislamic.sdk.service.repository.SubscriptionRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.LoadingButton
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.TERMS_URL
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.PaymentViewModel
import com.deenislamic.sdk.viewmodels.SubscriptionViewModel
import com.deenislamic.sdk.views.adapters.subscription.PackListAdapter
import com.deenislamic.sdk.views.adapters.subscription.PremiumFeatureAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


internal class SubscriptionFragment : BaseRegularFragment(),SubscriptionCallback,
    CustomDialogCallback {


    private lateinit var viewmodel: SubscriptionViewModel
    private lateinit var packList:RecyclerView
    private lateinit var activePlan:MaterialCardView
    private lateinit var featureList:RecyclerView
    private lateinit var nextBtn:MaterialButton
    private lateinit var cancelBtn:MaterialButton
    private lateinit var bottomCardview:LinearLayout
    private var selectedPlan = -1
    private var planStatus = "0BK"
    private var selectedPaymentType:PaymentType?= null
    private var customAlertDialog: CustomAlertDialog? =null
    private lateinit var paymentViewmodel: PaymentViewModel
    override fun OnCreate() {
        super.OnCreate()
        // init viewmodel

        val paymentRepository = PaymentRepository(
            paymentService = NetworkProvider().getInstance().providePaymentService(),
            nagadPaymentService = NetworkProvider().getInstance().provideNagadPaymentService(),
            authInterceptor = NetworkProvider().getInstance().provideAuthInterceptor())

        val subscriptionRepository = SubscriptionRepository(
            paymentService = NetworkProvider().getInstance().providePaymentService(),
            authInterceptor = NetworkProvider().getInstance().provideAuthInterceptor(),
            authenticateService = NetworkProvider().getInstance().provideAuthService()
        )

        viewmodel = SubscriptionViewModel(paymentRepository = paymentRepository, repository = subscriptionRepository)


        val factory = VMFactory(paymentRepository)
        paymentViewmodel = ViewModelProvider(
            requireActivity(),
            factory
        )[PaymentViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_subscription,container,false)

        activePlan = mainview.findViewById(R.id.activePlan)
        featureList = mainview.findViewById(R.id.featureList)
        nextBtn = mainview.findViewById(R.id.nextBtn)
        cancelBtn = mainview.findViewById(R.id.cancelBtn)
        bottomCardview = mainview.findViewById(R.id.bottomCardview)
        packList = mainview.findViewById(R.id.packList)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.subscription),
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)

        CallBackProvider.setFragment(this)
        customAlertDialog = CustomAlertDialog().getInstance()

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelBtn.setOnClickListener {

            customAlertDialog?.setupDialog(
                callback = this,
                context = requireContext(),
                btn1Text = localContext.getString(R.string.no),
                btn2Text = localContext.getString(R.string.yes),
                titileText = localContext.getString(R.string.want_to_cancel),
                subTitileText = localContext.getString(R.string.do_you_want_to_cancel_the_subscription)
            )


            customAlertDialog?.showDialog(false)

        }

        nextBtn.setOnClickListener {

            selectedPaymentType?.let {
                val bundle = Bundle()
                val paymentModel = PaymentModel(
                    title = it.packageHeader,
                    amount = it.packageAmount,
                    redirectPage = R.id.subscriptionFragment,
                    isBkashEnable = it.isBkashEnable,
                    isNagadEnable = it.isNagadEnable,
                    isSSLEnable = it.isSSLEnable,
                    isGpayEnable = false,
                    serviceIDBkash = it.serviceIDBkash,
                    serviceIDNagad = it.serviceIDNagad,
                    serviceIDSSL = it.serviceIDSSL,
                    paySuccessMessage = localContext.getString(R.string.your_payment_has_been_successful),
                    tcUrl = TERMS_URL,
                    isRecurring = true
                )
                bundle.putParcelable("payment", paymentModel)
                gotoFrag(R.id.action_global_paymentListFragment,bundle)
            }


           /* if(selectedPlan>0){



            }else{
                context?.toast(localContext.getString(R.string.select_a_plan))
            }*/
        }

        initObserver()
        baseLoadingState()
        loadapi()
    }

    override fun clickBtn1() {
        customAlertDialog?.dismissDialog()
    }

    override fun clickBtn2() {
        val btn2 = customAlertDialog?.getBtn2()
        btn2?.text = btn2?.let { LoadingButton().getInstance(requireContext()).loader(it) }

        nextBtn.isCheckable = false
        //cancelBtn.text = LoadingButton().getInstance(requireContext()).loader(cancelBtn,R.color.deen_primary)
        lifecycleScope.launch {
            viewmodel.cancelAutoRenewal(planStatus,selectedPlan)
        }
    }

    private fun setActivePlan(
        activeColor: Int = R.color.deen_primary,
        info:String = "",
    packData:PaymentType?){
        packList.hide()
        packData?.let {
            selectedPaymentType = it
            val title: AppCompatTextView = activePlan.findViewById(R.id.title)
            val subText: AppCompatTextView = activePlan.findViewById(R.id.subText)
            val infoText: AppCompatTextView = activePlan.findViewById(R.id.infoText)
            val icTick: AppCompatImageView = activePlan.findViewById(R.id.icTick)

            title.text = it.packageTitle
            subText.text = it.packageDescription

            if (info.isNotEmpty()) {
                infoText.text = info
                infoText.show()
            } else
                infoText.hide()

            var finalActiveColor = activeColor
            //bundle check
            if(it.isDataBundle) {
                finalActiveColor = R.color.deen_primary
                infoText.hide()
            }


            icTick.hide()

            context?.let {getcontext->
                activePlan.setCardBackgroundColor(ContextCompat.getColor(getcontext, finalActiveColor))
                title.setTextColor(ContextCompat.getColor(getcontext, R.color.deen_white))
                subText.setTextColor(ContextCompat.getColor(getcontext, R.color.deen_white))
            }

            if (finalActiveColor == R.color.deen_yellow) {
                icTick.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.deen_txt_black_deep
                    )
                )
                context?.let {
                    title.setTextColor(ContextCompat.getColor(it, R.color.deen_txt_black_deep))
                    subText.setTextColor(ContextCompat.getColor(it, R.color.deen_txt_black_deep))
                    infoText.setTextColor(ContextCompat.getColor(it, R.color.deen_txt_black))
                }
                //cancelRenewHint.hide()
            } else {
                icTick.setColorFilter(ContextCompat.getColor(requireContext(), R.color.deen_yellow))
                //cancelRenewHint.show()
            }


            activePlan.show()
        }

    }


    private fun loadapi(){
        lifecycleScope.launch {
            viewmodel.checkRecurringStatus(getLanguage())
        }
    }

    override fun noInternetRetryClicked() {
        baseLoadingState()
        loadapi()
    }

    private fun initObserver(){
        viewmodel.subscriptionLiveData.observe(viewLifecycleOwner){
            when(it){
                CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is SubscriptionResource.CheckSubs -> {
                    lifecycleScope.launch {
                        viewmodel.clearCheckSub()
                    }

                    featureList.apply {
                        adapter = PremiumFeatureAdapter(it.value.premiumFeatures)
                    }

                    packList.apply {
                        val filterPack = it.value.paymentTypes.filter { pdata-> !pdata.isDataBundle }
                        if(filterPack.isNotEmpty()){
                            selectedPaymentType = filterPack[0]
                            nextBtn.text = localContext.getString(R.string.subStartBtnText,selectedPaymentType?.packageTitle)
                        }
                        adapter = PackListAdapter(filterPack)
                    }

                    selectedPlan = it.value.pageResponse?.Data?.ServiceID?:-1
                    planStatus = it.value.pageResponse?.Message.toString()


                    it.value.pageResponse?.let { it1 -> checkPaymentStatus(it1,it.value.paymentTypes) }
                    baseViewState()
                }
            }
        }

        viewmodel.autorenewLiveData.observe(viewLifecycleOwner){
            when(it){
                CommonResource.API_CALL_FAILED -> {
                    customAlertDialog?.dismissDialog()
                    lifecycleScope.launch {
                        viewmodel.clearAutoRenew()
                    }

                    val btn2 = customAlertDialog?.getBtn2()
                    btn2?.isClickable = true
                    LoadingButton().getInstance(requireContext()).removeLoader()
                    customAlertDialog?.dismissDialog()

                    cancelBtn.text = localContext.getString(R.string.cancel_plan)
                    nextBtn.isClickable = true
                    context?.toast("Failed to cancel Auto-Renewal")
                }
                is SubscriptionResource.AutoRenewCancel -> {
                    val btn2 = customAlertDialog?.getBtn2()
                    btn2?.isClickable = true
                    LoadingButton().getInstance(requireContext()).removeLoader()
                    customAlertDialog?.dismissDialog()
                    cancelBtn.text = localContext.getString(R.string.cancel_plan)
                    nextBtn.isClickable = true
                    lifecycleScope.launch {
                        viewmodel.clearAutoRenew()
                    }
                    if(it.planStatus == "1BK"){

                        setActivePlan(
                            activeColor = R.color.deen_yellow,
                            info = localContext.getString(R.string.you_cancelled_the_subscription), packData = selectedPaymentType
                        )
                        nextBtn.text = localContext.getString(R.string.renew_plan)
                        bottomCardview.show()
                        cancelBtn.hide()

                    }
                    else if(it.planStatus == "2BK"){
                       resetSubscription()
                    }
                }
            }
        }

        paymentViewmodel.paymentIPNLiveData.observe(viewLifecycleOwner)
        {

            if(CommonResource.CLEAR != it) {
                paymentViewmodel.clearIPN()
            }

            when(it)
            {
                is PaymentResource.PaymentIPNSuccess -> {
                    onBackPress()
                }

                is PaymentResource.PaymentIPNFailed -> {
                    onBackPress()
                }

                is PaymentResource.PaymentIPNCancle -> {
                    onBackPress()
                }
            }
        }
    }

    private fun checkPaymentStatus(
        response: CheckRecurringResponse,
        paymentTypes: List<PaymentType>
    ) {

        val activePackDetails = paymentTypes.firstOrNull {
            val serviceid = response.Data.ServiceID
            (it.serviceIDBkash == response.Data.ServiceID && serviceid>0) || (it.serviceIDSSL == response.Data.ServiceID && serviceid>0)
        }


        activePackDetails?.let { packDetails ->

            if(response.Message == "1BK" && response.Data.isSubscribe){
                Subscription.isSubscribe = true
                setActivePlan(info = localContext.getString(R.string.subs_info_txt,calculateAutoRenewTime(response.Data.EndDate)), packData = packDetails)

                cancelBtn.visible(!packDetails.isDataBundle)
                bottomCardview.hide()
            }
            else if(response.Message == "1BK"){
                Subscription.isSubscribe = true
                setActivePlan(
                    info = localContext.getString(R.string.you_cancelled_the_subscription),
                    activeColor = R.color.deen_yellow,  packData = packDetails
                )

                nextBtn.text = localContext.getString(R.string.renew_plan)
                nextBtn.visible(!packDetails.isDataBundle)
                bottomCardview.show()
            }
            else if(response.Message == "2BK"){

                setActivePlan(
                    info = localContext.getString(R.string.subscription_expired),
                    activeColor = R.color.deen_brand_error, packData = packDetails
                )
                nextBtn.visible(!packDetails.isDataBundle)
                cancelBtn.visible(!packDetails.isDataBundle)
                nextBtn.hide()
            }
            else
                resetSubscription()
        }

    }

    private fun resetSubscription(){
        packList.show()
        activePlan.hide()
    }


    private fun calculateAutoRenewTime(endDate: String): String? {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val inputDateTime = inputFormat.parse(endDate)

        val outputFormat = if (getLanguage() == "en") {
            SimpleDateFormat("d MMMM 'at' h:mm a", Locale.ENGLISH)
        } else {
            SimpleDateFormat("d MMMM 'রাত' h:mm a", Locale("bn", "BD"))
        }

        return inputDateTime?.let { outputFormat.format(it) }
    }

    override fun selectedPack(getdata: PaymentType) {
        selectedPaymentType = getdata
    }

    inner class VMFactory(
        private val paymentRepository: PaymentRepository
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PaymentViewModel(paymentRepository) as T
        }
    }

}