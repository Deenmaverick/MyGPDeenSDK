package com.deenislam.sdk.views.subscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.SubscriptionResource
import com.deenislam.sdk.service.models.payment.PaymentModel
import com.deenislam.sdk.service.models.subscription.PremiumFeature
import com.deenislam.sdk.service.network.response.payment.recurring.CheckRecurringResponse
import com.deenislam.sdk.service.repository.IslamiMasailRepository
import com.deenislam.sdk.service.repository.PaymentRepository
import com.deenislam.sdk.service.repository.SubscriptionRepository
import com.deenislam.sdk.utils.LoadingButton
import com.deenislam.sdk.utils.TERMS_URL
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.toast
import com.deenislam.sdk.viewmodels.SubscriptionViewModel
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


internal class SubscriptionFragment : BaseRegularFragment() {

    private lateinit var viewmodel: SubscriptionViewModel

    private lateinit var dailyPlan:MaterialCardView
    private lateinit var weeklyPlan:MaterialCardView
    private lateinit var monthlyPlan:MaterialCardView
    private lateinit var featureList:RecyclerView
    private lateinit var nextBtn:MaterialButton
    private lateinit var cancelBtn:MaterialButton
    private lateinit var bottomCardview:LinearLayout
    private lateinit var cancelRenewHint:AppCompatTextView
    private var selectedPlan = -1
    private var planStatus = "0BK"

    private val premiumFeatures = arrayListOf(
        PremiumFeature(
            featureName = "Ad Free Experience",
            featureSubText = "No ads while using the app"
        ),
        PremiumFeature(
            featureName = "Offline Quran",
            featureSubText = "Listen Quran offline"
        ),
        PremiumFeature(
            featureName = "My Favorite",
            featureSubText = "Access all your favorites in one place"
        ),
        PremiumFeature(
            featureName = "Podcast",
            featureSubText = "Comment and access all feature in podcast"
        ),
        PremiumFeature(
            featureName = "Tracker",
            featureSubText = "Track prayer and ramadan"
        ),
    )

    override fun OnCreate() {
        super.OnCreate()
        // init viewmodel

        val paymentRepository = PaymentRepository(
            paymentService = NetworkProvider().getInstance().providePaymentService(),
            nagadPaymentService = NetworkProvider().getInstance().provideNagadPaymentService(),
            authInterceptor = NetworkProvider().getInstance().provideAuthInterceptor())

        val subscriptionRepository = SubscriptionRepository(
            paymentService = NetworkProvider().getInstance().providePaymentService(),
            authInterceptor = NetworkProvider().getInstance().provideAuthInterceptor()
        )

        viewmodel = SubscriptionViewModel(paymentRepository = paymentRepository, repository = subscriptionRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_subscription,container,false)

        dailyPlan = mainview.findViewById(R.id.dailyPlan)
        weeklyPlan = mainview.findViewById(R.id.weeklyPlan)
        monthlyPlan = mainview.findViewById(R.id.monthlyPlan)
        featureList = mainview.findViewById(R.id.featureList)
        nextBtn = mainview.findViewById(R.id.nextBtn)
        cancelBtn = mainview.findViewById(R.id.cancelBtn)
        bottomCardview = mainview.findViewById(R.id.bottomCardview)
        cancelRenewHint = mainview.findViewById(R.id.cancelRenewHint)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.subscription),
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)

        featureList.hide()

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPlan()

        dailyPlan.setOnClickListener {
            nextBtn.text = localContext.getString(R.string.subStartBtnText,localContext.getString(R.string.daily))
            setActivePlan(dailyPlan)
        }

        weeklyPlan.setOnClickListener {
            nextBtn.text = localContext.getString(R.string.subStartBtnText,localContext.getString(R.string.weekly))
            setActivePlan(weeklyPlan)
        }

        monthlyPlan.setOnClickListener {
            selectedPlan = 2001
            nextBtn.text = localContext.getString(R.string.subStartBtnText,localContext.getString(R.string.monthly))
            setActivePlan(monthlyPlan)
        }

        cancelBtn.setOnClickListener {
            nextBtn.isCheckable = false
            cancelBtn.text = LoadingButton().getInstance(requireContext()).loader(cancelBtn,R.color.deen_primary)
            lifecycleScope.launch {
                viewmodel.cancelAutoRenewal(planStatus,selectedPlan)
            }
        }

        nextBtn.setOnClickListener {
            if(selectedPlan>0){

                val bundle = Bundle()
                val paymentModel = PaymentModel(
                    title = localContext.getString(R.string.monthly_subscription),
                    amount = "20.00",
                    redirectPage = R.id.subscriptionFragment,
                    isBkashEnable = true,
                    isNagadEnable = false,
                    isSSLEnable = false,
                    isGpayEnable = false,
                    serviceIDBkash = selectedPlan,
                    paySuccessMessage = localContext.getString(R.string.your_payment_has_been_successful),
                    tcUrl = TERMS_URL,
                    isRecurring = true
                )
                bundle.putParcelable("payment", paymentModel)
                gotoFrag(R.id.action_global_paymentListFragment,bundle)

            }else{
                context?.toast(localContext.getString(R.string.select_a_plan))
            }
        }

        initObserver()
        baseLoadingState()
        loadapi()
    }


    private fun setActivePlan(
        planCard: MaterialCardView?,
        activeColor: Int = R.color.deen_primary,
        info:String = ""){

        setInactivePlan(dailyPlan)
        setInactivePlan(weeklyPlan)
        setInactivePlan(monthlyPlan)

        planCard?.let {

            val title:AppCompatTextView = planCard.findViewById(R.id.title)
            val subText:AppCompatTextView = planCard.findViewById(R.id.subText)
            val infoText:AppCompatTextView = planCard.findViewById(R.id.infoText)
            val icTick:AppCompatImageView = planCard.findViewById(R.id.icTick)

            if(info.isNotEmpty()){
                infoText.text = info
                infoText.show()
            }
            else
                infoText.hide()

            icTick.show()

            context?.let {
                planCard.setCardBackgroundColor( ContextCompat.getColor(it,activeColor))
                title.setTextColor(ContextCompat.getColor(it,R.color.deen_white))
                subText.setTextColor(ContextCompat.getColor(it,R.color.deen_white))
            }

            if(activeColor == R.color.deen_yellow){
                icTick.setColorFilter(ContextCompat.getColor(requireContext(),R.color.deen_txt_black_deep))
                context?.let {
                    title.setTextColor(ContextCompat.getColor(it, R.color.deen_txt_black_deep))
                    subText.setTextColor(ContextCompat.getColor(it, R.color.deen_txt_black_deep))
                    infoText.setTextColor(ContextCompat.getColor(it, R.color.deen_txt_black))
                }
                cancelRenewHint.hide()
            }
            else{
                icTick.setColorFilter(ContextCompat.getColor(requireContext(),R.color.deen_yellow))
                cancelRenewHint.show()
            }
        }

    }

    private fun setInactivePlan(planCard: MaterialCardView) {

        val title:AppCompatTextView = planCard.findViewById(R.id.title)
        val subText:AppCompatTextView = planCard.findViewById(R.id.subText)
        val icTick:AppCompatImageView = planCard.findViewById(R.id.icTick)

        icTick.hide()

        context?.let {
            planCard.setCardBackgroundColor( ContextCompat.getColor(it,R.color.deen_white))
            title.setTextColor(ContextCompat.getColor(it,R.color.deen_txt_black_deep))
            subText.setTextColor(ContextCompat.getColor(it,R.color.deen_txt_ash))
        }
    }

    private fun setPlan(planCard: MaterialCardView,planName:String,planSubText:String){
        val title:AppCompatTextView = planCard.findViewById(R.id.title)
        val subText:AppCompatTextView = planCard.findViewById(R.id.subText)
        title.text = planName
        subText.text = planSubText
    }

    private fun loadapi(){
        lifecycleScope.launch {
            viewmodel.checkRecurringStatus()
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
                    planStatus = it.value.Message
                    selectedPlan = it.value.Data.ServiceID
                    checkPaymentStatus(it.value)
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
                    cancelBtn.text = localContext.getString(R.string.cancel_plan)
                    nextBtn.isClickable = true
                    context?.toast("Failed to cancel Auto-Renewal")
                }
                is SubscriptionResource.AutoRenewCancel -> {

                    LoadingButton().getInstance(requireContext()).removeLoader()
                    cancelBtn.text = localContext.getString(R.string.cancel_plan)
                    nextBtn.isClickable = true
                    lifecycleScope.launch {
                        viewmodel.clearAutoRenew()
                    }
                    if(it.planStatus == "1BK"){

                        updateActivePlan(
                            serviceID = selectedPlan,
                            info = localContext.getString(R.string.you_cancelled_the_subscription),
                            activeColor = R.color.deen_yellow
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
    }

    private fun checkPaymentStatus(response: CheckRecurringResponse) {
        if(response.Message == "1BK" && response.Data.isSubscribe){
            updateActivePlan(
                serviceID = response.Data.ServiceID,
                info = localContext.getString(R.string.subs_info_txt,calculateAutoRenewTime(response.Data.EndDate))
            )
            cancelBtn.show()
            bottomCardview.hide()
        }
        else if(response.Message == "1BK"){
            updateActivePlan(
                serviceID = response.Data.ServiceID,
                info = localContext.getString(R.string.you_cancelled_the_subscription),
                activeColor = R.color.deen_yellow
            )
            nextBtn.text = localContext.getString(R.string.renew_plan)
            bottomCardview.show()
        }
        else if(response.Message == "2BK"){
            updateActivePlan(
                serviceID = response.Data.ServiceID,
                activeColor = R.color.deen_brand_error,
                info = localContext.getString(R.string.subscription_expired)
            )
            cancelBtn.show()
            nextBtn.hide()
        }
    }


    private fun updateActivePlan(
        serviceID: Int,
        activeColor:Int = R.color.deen_primary,
        info:String = ""
    ) {
        when(serviceID){
            2001-> {
                monthlyPlan.isClickable = false
                setActivePlan(monthlyPlan,activeColor,info)
            }
        }
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

    private fun resetSubscription(){
        setupPlan()
        setActivePlan(null)
        nextBtn.text = localContext.getString(R.string.select_a_plan)
        selectedPlan = -1
    }

    private fun setupPlan(){

        setPlan(
            planCard = dailyPlan,
            planName = localContext.getString(R.string.daily),
            planSubText = localContext.getString(R.string.dailyPlanSubText)
        )

        setPlan(
            planCard = weeklyPlan,
            planName = localContext.getString(R.string.weekly),
            planSubText = localContext.getString(R.string.weeklyPlanSubText)
        )

        setPlan(
            planCard = monthlyPlan,
            planName = localContext.getString(R.string.monthly),
            planSubText = localContext.getString(R.string.monthlyPlanSubText)
        )
    }
}