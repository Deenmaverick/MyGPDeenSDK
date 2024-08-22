package com.deenislamic.sdk.views.subscription

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
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
import com.deenislamic.sdk.service.models.quran.learning.FaqList
import com.deenislamic.sdk.service.models.subscription.DonateAmount
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.service.network.response.payment.recurring.CheckRecurringResponse
import com.deenislamic.sdk.service.network.response.subscription.Faq
import com.deenislamic.sdk.service.network.response.subscription.PaymentType
import com.deenislamic.sdk.service.repository.PaymentRepository
import com.deenislamic.sdk.service.repository.SubscriptionRepository
import com.deenislamic.sdk.utils.BKASH
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.GP
import com.deenislamic.sdk.utils.LoadingButton
import com.deenislamic.sdk.utils.SpanningLinearLayoutManager
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.invisible
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.PaymentViewModel
import com.deenislamic.sdk.viewmodels.SubscriptionViewModel
import com.deenislamic.sdk.views.adapters.common.FaqListAdapter
import com.deenislamic.sdk.views.adapters.podcast.LivePodcastRecentAdapter
import com.deenislamic.sdk.views.adapters.subscription.DonationAmountAdapter
import com.deenislamic.sdk.views.adapters.subscription.PackListAdapter
import com.deenislamic.sdk.views.adapters.subscription.PremiumFeatureAdapter
import com.deenislamic.sdk.views.adapters.subscription.ScholarListAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


internal class SubscriptionNewFragment : BaseRegularFragment(), CustomDialogCallback,
    SubscriptionCallback {

    private lateinit var viewmodel: SubscriptionViewModel
    private lateinit var paymentViewmodel: PaymentViewModel

    private lateinit var bannerList:RecyclerView
    private lateinit var donationCard:MaterialCardView

    private lateinit var methodBkash:MaterialButton
    private lateinit var methodGP:MaterialButton

    private lateinit var activePlan: MaterialCardView
    private lateinit var packList:RecyclerView
    private lateinit var planListHint:AppCompatTextView
    private lateinit var cancelRenewHint:AppCompatTextView
    private lateinit var cancelBtn:MaterialButton
    private lateinit var nextBtn:MaterialButton
    private lateinit var bottomCardview: LinearLayout
    private lateinit var customDonationAmountList:RecyclerView
    private lateinit var customAmount:TextInputEditText
    private lateinit var donationBtn:MaterialButton
    private lateinit var featureList:RecyclerView
    private lateinit var faqList:RecyclerView
    private lateinit var packListAdapter: PackListAdapter
    private lateinit var donationAmountAdapter: DonationAmountAdapter
    private lateinit var scholarList:RecyclerView
    private lateinit var scholarCard:MaterialCardView

    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView: View
    private var alertDialog: AlertDialog? = null

    // other
    private var customAlertDialog: CustomAlertDialog? =null
    private var selectedPaymentMethod = ""
    private var isRecurringPayment = false
    private var selectedDonationAmountIndex= 0
    private var isGpaySubscribe = false
    private var isGpayCancelClicked = false
    private var selectedPaymentType: PaymentType?= null
    private var selectedPlan = -1
    private var planStatus = "0BK"
    private var subscriptionStatus = "0BK"

    private val donateAmountList = arrayListOf(
        DonateAmount(500,""),
        DonateAmount(1000,""),
        DonateAmount(2000,""),
        DonateAmount(5000,""),
        DonateAmount(10000,"")

    )

    override fun OnCreate() {
        super.OnCreate()
        // init viewmodel

        val paymentRepository = PaymentRepository(
            paymentService = NetworkProvider().getInstance().providePaymentService(),
            nagadPaymentService = NetworkProvider().getInstance().provideNagadPaymentService(),
            authInterceptor = NetworkProvider().getInstance().provideAuthInterceptor(),
            dcbPaymentService = NetworkProvider().getInstance().provideDCBPaymentService())

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
        val mainview = localInflater.inflate(R.layout.fragment_subscription_new,container,false)

        bannerList = mainview.findViewById(R.id.bannerList)
        donationCard = mainview.findViewById(R.id.customDonationLY)

        methodBkash = mainview.findViewById(R.id.methodBkash)
        methodGP = mainview.findViewById(R.id.methodGP)


        activePlan = mainview.findViewById(R.id.activePlan)
        packList = mainview.findViewById(R.id.packList)
        planListHint = mainview.findViewById(R.id.planListHint)
        cancelRenewHint = mainview.findViewById(R.id.cancelRenewHint)
        cancelBtn = mainview.findViewById(R.id.cancelBtn)
        nextBtn = mainview.findViewById(R.id.nextBtn)
        bottomCardview = mainview.findViewById(R.id.bottomCardview)
        customDonationAmountList = mainview.findViewById(R.id.customDonationAmountList)
        customAmount = mainview.findViewById(R.id.customAmount)
        donationBtn = mainview.findViewById(R.id.donationBtn)
        featureList = mainview.findViewById(R.id.featureList)
        faqList = mainview.findViewById(R.id.faqList)
        scholarList = mainview.findViewById(R.id.scholarList)
        scholarCard = mainview.findViewById(R.id.scholarCard)

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

        // setup payment method
        methodBkash.apply {
            this.strokeWidth = 0
            this.setIconResource(R.drawable.deen_ic_bkash_alt)
            context?.let { this.setTextColor(ContextCompat.getColor(it,R.color.deen_txt_black_deep)) }
            this.text = localContext.getString(R.string.bkash)
            setOnClickListener {
                if(selectedPaymentMethod!=BKASH)
                selectPaymentMethod(BKASH,this)
            }
        }

        methodGP.apply {
            this.strokeWidth = 0
            this.setIconResource(R.drawable.deen_ic_gp_logo)
            context?.let { this.setTextColor(ContextCompat.getColor(it,R.color.deen_txt_black_deep)) }
            this.text = localContext.getString(R.string.grameenphone)
            setOnClickListener {
                if(selectedPaymentMethod!= GP)
                selectPaymentMethod(GP,this)
            }
        }


        // set donation amount

        customDonationAmountList.apply {
            itemAnimator = null
            if(!this@SubscriptionNewFragment::donationAmountAdapter.isInitialized)
            donationAmountAdapter = DonationAmountAdapter(donateAmountList)
            adapter = donationAmountAdapter
            customAmount.setText("500")
            post {
                donationAmountAdapter.update(donateAmountList[0].copy(isActive = true))
            }
        }


        customAmount.addTextChangedListener {editTText ->
            val getdata = donationAmountAdapter.getdata().filter { it.isActive }.firstOrNull()

            getdata?.let {
                donationAmountAdapter.update(it.copy(isActive = false))
            }

            val getmatched = donateAmountList.firstOrNull { it.amount.toString() == editTText.toString() }

            getmatched?.let {
                donationAmountAdapter.update(it.copy(isActive = true))
            }
        }

        donationBtn.setOnClickListener {

            if(customAmount.text.toString().isEmpty()) {
                context?.let { it.toast("Please enter donation amount") }
                return@setOnClickListener
            }

            customAmount.text.toString().toDoubleOrNull()?.let {
               if(it<20){
                   context?.let { context -> context.toast("Minimum amount 20") }
                   return@setOnClickListener
               }
            }

            when(selectedPaymentMethod){
                BKASH -> {
                    processLoadingDialog()
                    bKashDonationPayment(customAmount.text.toString())
                }

            }

        }

        // main init

        initObserver()
        initPaymentObserver()
        baseLoadingState()
        loadapi()

    }


    override fun clickBtn1() {
        customAlertDialog?.dismissDialog()
    }

    override fun clickBtn2() {
        if(!isGpaySubscribe) {
            val btn2 = customAlertDialog?.getBtn2()
            btn2?.text = btn2?.let { LoadingButton().getInstance(requireContext()).loader(it) }

            nextBtn.isCheckable = false
            lifecycleScope.launch {
                viewmodel.cancelAutoRenewal(planStatus, selectedPlan,selectedPaymentType)
            }
        }
        else
            cacelGpaySub()
    }

    private fun setActivePlan(
        activeColor: Int = R.color.deen_primary,
        info:String = "",
        packData: PaymentType?){
        packList.hide()
        packData?.let {
            selectedPaymentType = it
            val title: AppCompatTextView = activePlan.findViewById(R.id.title)
            val subText: AppCompatTextView = activePlan.findViewById(R.id.subText)
            val infoText: AppCompatTextView = activePlan.findViewById(R.id.infoText)
            val amount: AppCompatTextView = activePlan.findViewById(R.id.amount)
            amount.invisible()
            val icTick: AppCompatImageView = activePlan.findViewById(R.id.icTick)

            title.text = it.packageTitle
            if(it.packageDescription.isNotEmpty()) {
                subText.text = it.packageDescription
                subText.show()
            }else{
                subText.hide()
            }

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


            icTick.show()

            context?.let {getcontext->
                activePlan.setCardBackgroundColor(ContextCompat.getColor(getcontext, finalActiveColor))
                title.setTextColor(ContextCompat.getColor(getcontext, R.color.deen_white))
                subText.setTextColor(ContextCompat.getColor(getcontext, R.color.deen_white))
                infoText.setTextColor(ContextCompat.getColor(getcontext, R.color.deen_white_70))
            }

            if (finalActiveColor == R.color.deen_yellow) {
                icTick.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.deen_txt_black_deep
                    )
                )
                context?.let {getcontext->
                    title.setTextColor(ContextCompat.getColor(getcontext, R.color.deen_txt_black_deep))
                    subText.setTextColor(ContextCompat.getColor(getcontext, R.color.deen_txt_black_deep))
                    infoText.setTextColor(ContextCompat.getColor(getcontext, R.color.deen_txt_black))
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

                    methodBkash.visible(it.value.paymentMethod.isBkash)
                    methodGP.visible(it.value.paymentMethod.isGP)

                    bannerList.apply {
                        setHasFixedSize(true)
                        layoutManager = SpanningLinearLayoutManager(this.context,RecyclerView.HORIZONTAL,false,200.dp)
                        adapter = LivePodcastRecentAdapter(
                            items = it.value.Banners.map { item-> transformCommonCardListPatchModel(
                                item,
                                itemTitle = "",
                                itemMidContent = "",
                                itemSubTitle = "",
                                itemBtnText = "") },
                        )
                    }

                    featureList.apply {
                        adapter = PremiumFeatureAdapter(it.value.premiumFeatures)

                    }

                    if(it.value.scholars.isNotEmpty()){
                    scholarList.apply {
                        adapter = ScholarListAdapter(it.value.scholars)
                    }}else{
                        scholarCard.hide()
                    }

                    selectedPlan = it.value.pageResponse?.Data?.ServiceID?:-1
                    planStatus = it.value.pageResponse?.Message.toString()

                    val filterPack = it.value.paymentTypes/*.filter { pdata-> !pdata.isDataBundle }*/
                    if(filterPack.isNotEmpty()){
                        selectedPaymentType = filterPack[0]
                        nextBtn.text = localContext.getString(R.string.subStartBtnText,selectedPaymentType?.packageTitle)
                    }

                    faqList.apply {
                        adapter = FaqListAdapter(it.value.faq.map { item-> transformFaq(item) })
                    }


                    packList.apply {
                        if(!this@SubscriptionNewFragment::packListAdapter.isInitialized)
                            packListAdapter = PackListAdapter(filterPack)
                        adapter = packListAdapter
                        post {

                            when(selectedPaymentMethod){
                                BKASH -> {
                                    selectPaymentMethod(selectedPaymentMethod, methodBkash)
                                }
                                GP -> {
                                    selectPaymentMethod(selectedPaymentMethod, methodGP)
                                }


                                else -> {
                                    packListAdapter.updatePaymentMethod(GP, subscriptionStatus)
                                    selectPaymentMethod(GP, methodGP)
                                }
                            }
                        }
                    }

                    //it.value.pageResponse?.let { it1 -> checkPaymentStatus(it1,it.value.paymentTypes) }
                    it.value.pageResponse?.let { it1 -> checkPaymentStatus(it1,filterPack) }


                    baseViewState()
                }
            }
        }

        viewmodel.autorenewLiveData.observe(viewLifecycleOwner){
            when(it){
                CommonResource.API_CALL_FAILED -> {
                    lifecycleScope.launch {
                        viewmodel.clearAutoRenew()
                    }

                    LoadingButton().getInstance(requireContext()).removeLoader()
                    val btn2 = customAlertDialog?.getBtn2()
                    btn2?.text = localContext.getString(R.string.yes)
                    cancelBtn.text = localContext.getString(R.string.cancel_plan)
                    //nextBtn.isClickable = true
                    context?.toast("Failed to cancel Auto-Renewal")
                }
                is SubscriptionResource.AutoRenewCancel -> {

                    customAlertDialog?.dismissDialog()

                    LoadingButton().getInstance(requireContext()).removeLoader()
                    val btn2 = customAlertDialog?.getBtn2()
                    btn2?.text = localContext.getString(R.string.yes)
                    cancelBtn.text = localContext.getString(R.string.cancel_plan)
                    //nextBtn.isClickable = true
                    lifecycleScope.launch {
                        viewmodel.clearAutoRenew()
                    }
                    if(it.planStatus == "1BK"){

                        sub1BKNonMode()

                        setActivePlan(
                            activeColor = R.color.deen_yellow,
                            info = localContext.getString(R.string.you_cancelled_the_subscription), packData = selectedPaymentType
                        )
                        //nextBtn.text = localContext.getString(R.string.renew_plan)
                        //bottomCardview.show()
                        cancelBtn.hide()
                        cancelRenewHint.hide()

                    }
                    else if(it.planStatus == "2BK"){
                        sub2BKMode()
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
                    //onBackPress()
                }

                is PaymentResource.PaymentIPNFailed -> {
                    //onBackPress()
                }

                is PaymentResource.PaymentIPNCancle -> {
                    //onBackPress()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        lifecycleScope.launch {
            paymentViewmodel.clearIPN()
            paymentViewmodel.clearPayment()
        }

    }

    override fun onResume() {
        super.onResume()

        if(isGpayCancelClicked){
            baseLoadingState()
            loadapi()
            isGpayCancelClicked = false
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
                sub1BKMode()
                Subscription.isSubscribe = true
                setActivePlan(info = localContext.getString(R.string.subs_info_txt,calculateAutoRenewTime(response.Data.EndDate)), packData = packDetails)
                cancelBtn.visible(!packDetails.isDataBundle)
                cancelRenewHint.visible(!packDetails.isDataBundle)
                //bottomCardview.hide()

            }
            else if(response.Message == "1BK"){
                sub1BKNonMode()
                Subscription.isSubscribe = true

                if(activePackDetails.isRecurring){
                setActivePlan(
                    info = localContext.getString(R.string.you_cancelled_the_subscription),
                    activeColor = R.color.deen_yellow,  packData = packDetails
                )
                }else if(activePackDetails.isDataBundle){
                    setActivePlan(packData = packDetails)
                    donationMode()
                    packList.show()
                }else{
                    setActivePlan(packData = packDetails)
                }

                nextBtn.text = localContext.getString(R.string.renew_plan)
                nextBtn.visible(!packDetails.isDataBundle)
                //bottomCardview.show()
                // filter nagad and ssl
                if(!activePackDetails.isDataBundle) {
                    for (paymentType in paymentTypes) {
                        paymentType.isNagadEnable = false
                        paymentType.isSSLEnable = false
                    }
                }

            }
            else if(response.Message == "2BK"){

                setActivePlan(
                    info = localContext.getString(R.string.subscription_expired),
                    activeColor = R.color.deen_brand_error, packData = packDetails
                )
                sub2BKMode()
                //nextBtn.visible(!packDetails.isDataBundle)
                cancelBtn.visible(!packDetails.isDataBundle)
                cancelRenewHint.visible(!packDetails.isDataBundle)
                //nextBtn.hide()
            }
            else {
                unSubscribeMode()
            }
        }

    }

    private fun sub1BKMode(){
        bannerList.hide()
        //methodList.show()
        planListHint.hide()
        packList.hide()
        //donationCard.show()
        activePlan.show()
        subscriptionStatus = "1bk"
    }

    private fun sub1BKNonMode(){
        bannerList.hide()
        //methodList.show()
        planListHint.show()
        packList.show()
        //donationCard.show()
        activePlan.show()
        subscriptionStatus = "1bknon"
    }

    private fun sub2BKMode(){
        bannerList.hide()
        //methodList.show()
        planListHint.hide()
        packList.hide()
        //donationCard.show()
        activePlan.show()
        subscriptionStatus = "2bk"
    }

    private fun unSubscribeMode(){
        bannerList.show()
        //methodList.show()
        planListHint.show()
        packList.show()
        //donationCard.show()
        activePlan.hide()
        subscriptionStatus = "unsub"
    }

    private fun donationMode(){
        bannerList.show()
        //methodList.show()
        planListHint.show()
        packList.show()
        //donationCard.show()
        activePlan.show()
        subscriptionStatus = "unsub"
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

        if(alertDialog?.isShowing == true)
            return

        selectedPaymentType = getdata

        when(selectedPaymentMethod){
            BKASH -> {
                isRecurringPayment = getdata.isRecurring
                processLoadingDialog()
                bKashPayment()
            }

            GP -> {
                isRecurringPayment = getdata.isRecurring
                processLoadingDialog()
                lifecycleScope.launch {
                    paymentViewmodel.dcbGPCharging(getdata.serviceIDGP)
                }
            }

            else ->{
                context?.let { it.toast(localContext.getString(R.string.hint_choose_payment_method)) }
                return
            }
        }

    }

    override fun selectedCustomDonation(getdata: DonateAmount) {

        donationAmountAdapter.apply {
            update(getdata.copy(isActive = true))
            customAmount.setText(getdata.amount.toString())
        }

    }

    private fun cacelGpaySub(){
        isGpayCancelClicked = true
        customAlertDialog?.dismissDialog()
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/account/subscriptions")
        }
        startActivity(intent)
    }

    private fun unselectPaymentMethod(card:MaterialButton){
        card.apply {
            strokeWidth = 0
            context?.let { setTextColor(ContextCompat.getColor(it,R.color.deen_txt_black_deep)) }
        }
    }

    private fun selectPaymentMethod(method: String, materialButton: MaterialButton) {

        Log.e("selectPaymentMethod",subscriptionStatus)
        selectedPaymentMethod = ""
        unselectPaymentMethod(methodBkash)
        unselectPaymentMethod(methodGP)

        when(method){
            BKASH, GP -> {
                if(subscriptionStatus == "1bk"){
                    planListHint.hide()
                    packList.hide()
                }else if(subscriptionStatus == "1bknon"){
                    planListHint.show()
                    packList.show()
                } else if(subscriptionStatus == "2bk"){
                    planListHint.hide()
                    packList.hide()
                }else{
                    planListHint.show()
                    packList.show()
                }

                //donationCard.show()


            }

            /*GP -> {
                if(Subscription.isSubscribe && subscriptionStatus != "unsub"){
                    planListHint.hide()
                    packList.hide()
                }else if(subscriptionStatus == "2bk"){
                    planListHint.hide()
                    packList.hide()
                }else{
                    planListHint.show()
                    packList.show()
                }
                //donationCard.show()
                *//*selectedPaymentMethod = method
                packListAdapter.updatePaymentMethod(method)*//*
            }*/

        }

        selectedPaymentMethod = method
        if(packListAdapter.updatePaymentMethod(method,subscriptionStatus).isEmpty()){
            planListHint.hide()
            packList.hide()
        }

        materialButton.strokeWidth = 1.dp
        context?.let { materialButton.setTextColor(ContextCompat.getColor(it,R.color.deen_primary)) }

    }

    // Payment Processing //

    private fun initPaymentObserver() {
        paymentViewmodel.paymentLiveData.observe(viewLifecycleOwner) {

            if (CommonResource.CLEAR != it) {
                lifecycleScope.launch {
                    paymentViewmodel.clearPayment()
                }
            }

            when (it) {
                is CommonResource.API_CALL_FAILED -> {
                    alertDialog?.dismiss()
                    requireContext().toast("Unable to proceed payment! Please try again")
                }

                is PaymentResource.PaymentUrl -> {
                    alertDialog?.dismiss()
                    val bundle = Bundle()
                    bundle.putString("title", "${if(it.isDonation) localContext.getString(R.string.donation) else selectedPaymentType?.packageHeader}")
                    bundle.putString("paymentUrl", it.message)
                    bundle.putInt("redirectPage", R.id.subscriptionNewFragment)
                    bundle.putString("paySuccessMessage", localContext.getString(R.string.your_payment_has_been_successful))
                    gotoFrag(R.id.action_global_paymentWebViewFragment, bundle)

                }

                is PaymentResource.PaymentUrlNagad -> {
                    alertDialog?.dismiss()
                    if (it.status) {
                        val bundle = Bundle()
                        bundle.putString("title", "${if(it.isDonation) localContext.getString(R.string.donation) else selectedPaymentType?.packageHeader}")
                        bundle.putString("paymentUrl", it.message)
                        bundle.putInt("redirectPage", R.id.subscriptionNewFragment)
                        bundle.putString("paySuccessMessage", localContext.getString(R.string.your_payment_has_been_successful))
                        gotoFrag(R.id.action_global_paymentWebViewFragment, bundle)
                    } else {
                        requireContext().toast(it.message!!)
                    }
                }
            }

        }
    }

    private fun bKashPayment() {
        lifecycleScope.launch {
            if(isRecurringPayment)
                selectedPaymentType?.serviceIDBkash?.let { paymentViewmodel.recurringPayment(serviceID = it.toString()) }
            else
                selectedPaymentType?.serviceIDBkash?.let { paymentViewmodel.bKashPayment(serviceID = it.toString()) }
        }
    }



    private fun bKashDonationPayment(amount:String) {
        lifecycleScope.launch {
            paymentViewmodel.bKashDonationPayment(amount = amount)
        }
    }


    private fun processLoadingDialog(){

        materialAlertDialogBuilder =
            MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_progress_loading, null, false)

        alertDialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(false)
            .show()

    }

    private fun transformCommonCardListPatchModel(
        item: com.deenislamic.sdk.service.network.response.subscription.Banner,
        itemTitle: String,
        itemSubTitle: String,
        itemMidContent:String="",
        itemBtnText: String
    ): Item {
        return Item(
            ArabicText = "",
            ContentType = "",
            FeatureID = 0,
            FeatureName = "",
            FeatureTitle = "",
            Id = 0,
            IsActive = false,
            Language = "",
            Reference = "",
            Sequence = 0,
            Text = "",
            Title = "",
            contentBaseUrl = "https://islamic-content.sgp1.digitaloceanspaces.com",
            imageurl1 = item.featureSubText,
            imageurl2 = "",
            imageurl3 = "",
            imageurl4 = "",
            imageurl5 = "",
            SurahId = 0,
            JuzId = 0,
            VerseId = 0,
            HadithId = 0,
            CategoryId = 0,
            SubCategoryId = 0,
            DuaId = 0,
            MText = "",
            Meaning = "",
            ECount = "",
            itemTitle = itemTitle,
            itemSubTitle = itemSubTitle,
            itemBtnText = itemBtnText,
            isVideo = false,
            isLive = false,
            FeatureSize = "",
            itemMidContent = itemMidContent
        )
    }

    private fun transformFaq(item: Faq): FaqList {
        return FaqList(
            question = item.featureName,
            content = item.featureSubText
        )
    }

    inner class VMFactory(
        private val paymentRepository: PaymentRepository
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PaymentViewModel(paymentRepository) as T
        }
    }


}