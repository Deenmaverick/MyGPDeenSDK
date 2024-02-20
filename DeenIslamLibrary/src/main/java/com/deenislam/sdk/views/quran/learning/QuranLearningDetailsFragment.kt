package com.deenislam.sdk.views.quran.learning

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.QuranLearningCallback
import com.deenislam.sdk.service.callback.common.CommonCardCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.libs.media3.CustomTimeBar
import com.deenislam.sdk.service.libs.media3.ExoVideoManager
import com.deenislam.sdk.service.libs.media3.VideoPlayerCallback
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.PaymentResource
import com.deenislam.sdk.service.models.payment.PaymentModel
import com.deenislam.sdk.service.models.quran.learning.FaqList
import com.deenislam.sdk.service.models.quran.learning.QuranLearningResource
import com.deenislam.sdk.service.network.response.common.CommonCardData
import com.deenislam.sdk.service.repository.PaymentRepository
import com.deenislam.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.DIGITAL_QURAN_CLASS_TC
import com.deenislam.sdk.utils.PRODUCT_ID_QURAN_CLASS_350_TK
import com.deenislam.sdk.utils.durationToSeconds
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.toast
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.PaymentViewModel
import com.deenislam.sdk.viewmodels.quran.learning.QuranLearningViewModel
import com.deenislam.sdk.views.adapters.quran.learning.QuranClassFaqList
import com.deenislam.sdk.views.adapters.quran.learning.QuranLearningCourseV2Adapter
import com.deenislam.sdk.views.adapters.quran.learning.QuranUstadList
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.common.patch.SingleCardItemPatch
import com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.CourseConten
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


internal class QuranLearningDetailsFragment : BaseRegularFragment(),
    QuranLearningCallback,
    CommonCardCallback,
    VideoPlayerCallback {

    private lateinit var headbanner:ConstraintLayout
    private lateinit var oldPrice:AppCompatTextView
    private lateinit var courseCurriculumList:RecyclerView
    private lateinit var ustadList:RecyclerView
    private lateinit var faqList:RecyclerView
    private lateinit var buyBtn:MaterialButton
    private lateinit var title:AppCompatTextView
    private lateinit var description:AppCompatTextView
    private lateinit var bestSellerBtn:MaterialButton
    /*private lateinit var coursePeriod:AppCompatTextView
    private lateinit var isCertTxt:AppCompatTextView*/
    private lateinit var currentPrice:AppCompatTextView
    private lateinit var actionbar:ConstraintLayout

    private lateinit var playerView: PlayerView
    private lateinit var exoVideoManager: ExoVideoManager
    private lateinit var vPlayerControlAction2: AppCompatImageView
    private lateinit var vPlayerControlBtnBack: AppCompatImageView
    private lateinit var vPlayerControlBtnPlay: AppCompatImageView
    private lateinit var vPlayerControlAction1: AppCompatImageView
    private lateinit var custom_exo_progress: CustomTimeBar
    private var quranLearningCourseV2Adapter: QuranLearningCourseV2Adapter? = null

    private var selectedData: CourseConten? = null
    private var firstload = false
    private lateinit var viewmodel:QuranLearningViewModel
    private val navArgs:QuranLearningDetailsFragmentArgs by navArgs()
    private lateinit var paymentViewmodel:PaymentViewModel
    private var isSubscribe = false

    override fun OnCreate() {
        super.OnCreate()
        viewmodel = QuranLearningViewModel(
            QuranLearningRepository(
                quranShikkhaService = NetworkProvider().getInstance().provideQuranShikkhaService(),
                deenService = NetworkProvider().getInstance().provideDeenService(),
                dashboardService = NetworkProvider().getInstance().provideDashboardService()
            )
        )

        val paymentRepository = PaymentRepository(
            paymentService = NetworkProvider().getInstance().providePaymentService(),
            nagadPaymentService = NetworkProvider().getInstance().provideNagadPaymentService(),
            authInterceptor = NetworkProvider().getInstance().provideAuthInterceptor())

        val factory = VMFactory(paymentRepository)
        paymentViewmodel = ViewModelProvider(
            requireActivity(),
            factory
        )[PaymentViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        CallBackProvider.setFragment(this)

        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_quran_learning_details,container,false)

        headbanner = mainview.findViewById(R.id.headbanner)
        oldPrice = mainview.findViewById(R.id.oldPrice)
        courseCurriculumList = mainview.findViewById(R.id.courseCurriculumList)
        ustadList = mainview.findViewById(R.id.ustadList)
        faqList = mainview.findViewById(R.id.faqList)
        buyBtn = mainview.findViewById(R.id.buyBtn)
        title = mainview.findViewById(R.id.title)
        description = mainview.findViewById(R.id.description)
        bestSellerBtn = mainview.findViewById(R.id.bestSellerBtn)
        /*coursePeriod = mainview.findViewById(R.id.coursePeriod)
        isCertTxt = mainview.findViewById(R.id.isCertTxt)*/
        currentPrice = mainview.findViewById(R.id.currentPrice)
        playerView = mainview.findViewById(R.id.playerView)
        vPlayerControlBtnBack = mainview.findViewById(R.id.vPlayerControlBtnBack)
        vPlayerControlBtnPlay = mainview.findViewById(R.id.vPlayerControlBtnPlay)
        vPlayerControlAction2 = mainview.findViewById(R.id.vPlayerControlAction2)
        vPlayerControlAction1 = mainview.findViewById(R.id.vPlayerControlAction1)
        actionbar = mainview.findViewById(R.id.actionbar)
        custom_exo_progress = mainview.findViewById(R.id.custom_exo_progress)
        vPlayerControlAction1.hide()

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = navArgs.title,
            backEnable = true,
            view = mainview
        )

        exoVideoManager = ExoVideoManager(
            context = requireContext(),
            isLiveVideo = false,
            mainview = mainview
        )
        exoVideoManager.setupActionbar(isBackBtn = true, title = navArgs.title)


        setupCommonLayout(mainview)

        actionbar.show()

        return mainview
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackPressCallback(this)

        if (!isDetached) {
            view.postDelayed({
                loadPage()
            }, 300)
        }
        else
            loadPage()



    }

    private fun loadPage()
    {

        vPlayerControlAction2.setOnClickListener {
            exoVideoManager.toggleFullScreen(requireActivity())

        }

        vPlayerControlBtnBack.setOnClickListener {
            onBackPress()
        }

        initObserver()


        if(!paymentViewmodel.isIPNCalled)
            loadapi()
    }

    private fun loadapi()
    {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getDigitalQuranClass(getLanguage(),navArgs.courseId)
        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    private fun initObserver()
    {
        viewmodel.quranLearningLiveData.observe(viewLifecycleOwner)
        {

            lifecycleScope.launch {
                viewmodel.clear()
            }

            when(it)
            {
                CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is QuranLearningResource.QuranClassSecureUrl -> {

                    /*Log.e("QuranClassSecureUrl",it.value)*/
                    actionbar.hide()
                    headbanner.hide()
                    playerView.show()
                    exoVideoManager.playRegularVideoFromUrl(
                        singleVideoUrl = it.value,
                        initialPlayerStart = true,
                        startFromDuration = selectedData?.WatchDuration?:0
                    )
                }
                is QuranLearningResource.DigitalQuranClass -> {

                    title.text = it.data.course.CourseName

                    description.text = it.data.course.Description

                    bestSellerBtn.visible(it.data.course.isBestSeller)

                  /*  coursePeriod.text = localContext.getString(R.string.days_to_complete,it.data.course.TotalTrack.numberLocale())

                    isCertTxt.visible(it.data.course.isCertificate)
*/
                    currentPrice.text = localContext.getString(R.string.bdtandamount,it.data.course.DiscountPrice.numberLocale())

                    SingleCardItemPatch(
                        view = headbanner,
                        isPlayBtn = true,
                        viewMarginTop = 0,
                        cardMarginTop = 0,
                        bannerImg = BASE_CONTENT_URL_SGP+it.data.course.CourseImageUrl
                    ).load()

                    oldPrice.paintFlags = oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    if(it.data.course.ActualPrice == it.data.course.DiscountPrice)
                        oldPrice.hide()
                    else
                        oldPrice.text = localContext.getString(R.string.bdtandamount,it.data.course.ActualPrice.numberLocale())


                     courseCurriculumList.apply {
                         quranLearningCourseV2Adapter = QuranLearningCourseV2Adapter(it.data)
                         adapter = quranLearningCourseV2Adapter
                     }

                    ustadList.apply {
                        adapter = QuranUstadList(it.data.contenTeacher)
                    }

                    faqList.apply {
                        val faqlist: ArrayList<FaqList> = arrayListOf(
                            FaqList(
                                question = "ডিজিটাল কোরআন ক্লাস কি?",
                                content = "এই কোর্সটির মাধ্যমে আপনি কোরআন তিলাওয়াত সম্পর্কিত থাকা সকল ভুল-ভ্রান্তি দূর করে নিতে পারবেন। কোর্সটি সাজানো হয়েছে সব বয়সের মানুষের কথা চিন্তা করে এবং সবচেয়ে সহজ পদ্ধতিতে কোরআন তিলাওয়াত শিখানোর উদ্দেশ্যে। কোরআন পড়ার দক্ষতা বৃদ্ধি করতে, কোরআন শুদ্ধ করে পড়ার নিয়ম জানতে এবং সঠিক ও নির্ভুলভাবে অনলাইনে কোরআন শিক্ষা পেতে এখনই কোর্সটিতে  অংশগ্রহন করুন মাত্র ৩৫০ টাকা ফি দিয়ে।"
                            ),
                            FaqList(
                                question = "ডডিজিটাল কোরআন ক্লাস কি শিখতে পারবেন?",
                                content = "ডডিজিটাল কোরআন শিক্ষা কোর্সে ব্যবহারকারীরা আরবি বর্ণমালা, মৌলিক আরবি শব্দভান্ডার এবং ব্যাকরণের নিয়ম, কোরআনের লিপি কীভাবে পড়তে হয় এবং পবিত্র কোরআন তেলাওয়াতের সাথে সম্পর্কিত নিষেধাজ্ঞাগুলি শিখবে। অ্যাপের ব্যবহারকারীরা ভিডিও এবং কুইজ এর সমন্বিত একটি অনলাইন কোর্সে যা খুব সহজে অ্যাক্সেস করতে পারবেন যে কোন সময় যে কোন অবস্থান থেকে। এই কোর্সটি নিবেন বিশিষ্ট হাফেজ ক্বারী মোঃ রকিবুল ইসলাম।"
                            ),
                            FaqList(
                                question = "কোর্সটি তে কতোগুলা ভিডিও আছে?",
                                content = "এই কোর্সে সর্বমোট ২৮ টি ভিডিও ও ২৮ টি কুইজ আছে যার মাধ্যমে খুব সহজেই কোরআন শিখতে পারবেন।"
                            ),

                            FaqList(
                                question = "এই কোর্সে কিভাবে অংশগ্রহণ করবো?",
                                content = "কোরআন শিক্ষা করার এই কোর্সে আমরা কোনো ফি রাখছি না। শুধুমাত্র সার্ভার খরচ, হুজুরের হাদিয়া এবং আপনাদের কাছে কুরআনের আলোকে পৌছে দেয়ার খরচ বাবদ সামান্য কিছু হাদিয়া নিচ্ছি। আমাদের কোর্স ফি ৩৫০টাকা।"
                            ),
                            FaqList(
                                question = "কোর্সটির ভিডিও আমি কিভাবে দেখতে পাবো?",
                                content = "আপনি কোর্সে জয়েন করে ১টি ভিডিও সম্পূর্ণ দেখা শেষ হলে পরবর্তীতে ১টি কুইজে অংশগ্রহণ করতে হবে। কুইজের উত্তর দিয়ে আপনি পরবর্তী ভিডিও দেখতে পারবেন। এইভাবে আপনি ২৮টি ভিডিও ও ২৮টি কুইজে অংশগ্রহন করে কোর্সটি সমাপ্ত করতে পারবেন।"
                            ),
                            FaqList(
                                question = "যে কেউ কি ক্লাসে যোগ দিতে পারবেন?",
                                content = "হ্যাঁ. যে কেউ কোরআন শিখতে চাইলে ক্লাসে যোগ দিতে পারেন।"
                            ),
                            FaqList(
                                question = "কোর্সের মেয়াদকাল কত?",
                                content = "আপনি কোর্সে  জয়েন করার পর থেকে ৬ মাস পর্যন্ত যে কোন সময় ঘরে বসে ভিডিও দেখে কোরআন শিখতে পারবেন।"
                            ),

                        )

                        adapter = QuranClassFaqList(faqlist)

                    }

                    buyBtn.visible(!it.data.course.isSubscribed)

                    isSubscribe = it.data.course.isSubscribed
                  //  it.data.course.copy(isSubscribed = true)

                    buyBtn.setOnClickListener {
                        goPaymentPage()
                    }

                    baseViewState()

                    firstload = true
                }

            }
        }

        paymentViewmodel.paymentIPNLiveData.observe(viewLifecycleOwner)
        {

            if(CommonResource.CLEAR != it) {
                paymentViewmodel.clearIPN()
            }

            loadapi()

            when(it)
            {
                is PaymentResource.PaymentIPNSuccess -> {
                    context?.toast(localContext.getString(R.string.paysuccess_msg))
                }

                is PaymentResource.PaymentIPNFailed -> {
                    requireContext().toast(localContext.getString(R.string.payfailed_msg))
                }

                is PaymentResource.PaymentIPNCancle -> {
                    requireContext().toast(localContext.getString(R.string.payment_has_been_cancelled))
                }
            }
        }
    }


    override  fun courseCurriculumClickedV2(getData: CourseConten)
    {

        if(!getData.Status){
            requireContext().toast("Please complete previous class")
            return
        }

        if(!isSubscribe)
        {
            goPaymentPage()
            return
        }

        selectedData = getData

        custom_exo_progress.isEnabled = getData.IsComplete

        lifecycleScope.launch {
            viewmodel.getDigitalQuranSecureUrl(getData.FilePath)
        }
    }

    override fun courseQuizClicked(getData: CourseConten)
    {
        val bundle = Bundle()
        bundle.putInt("courseID",getData.CourseId)
        bundle.putInt("contentID",getData.ContentId)
        gotoFrag(R.id.action_global_quranLearningQuizFragment,bundle)
    }


    override fun videoPlayerReady(isPlaying: Boolean, mediaData: CommonCardData?) {

       if (isPlaying) {
            selectedData?.let {
                quranLearningCourseV2Adapter?.updateData(
                    it.copy(isPlaying = true)
                )
            }

        } else {

            selectedData?.let {
                quranLearningCourseV2Adapter?.updateData(
                    it.copy(isPlaying = false)
                )
            }

            sendWatchedDuration()

        }

    }


    override  fun videoPlayerEnded()
    {
        selectedData?.let {
            quranLearningCourseV2Adapter?.updateData(
                it.copy(isPlaying = false)
            )

            sendWatchedDuration(it.DurationInSeconds)
        }

        loadapi()
    }


    private fun sendWatchedDuration(durationInSeconds: Int=0)
    {
        selectedData?.let {

            if(!it.IsComplete) {
                lifecycleScope.launch {

                    async {

                        viewmodel.updateQuranClassVideoWatch(
                            language = getLanguage(),
                            courseId = it.CourseId,
                            contentID = it.ContentId,
                            duration = it.DurationInSeconds,
                            watchDuration =
                            if (durationInSeconds > 0)
                                durationInSeconds.toLong()
                            else
                                durationToSeconds(exoVideoManager.getExoPlayer().currentPosition)
                        )
                    }.await()

                }
            }

        }
    }


    private fun goPaymentPage()
    {
        val bundle = Bundle()
        val paymentModel = PaymentModel(
            title = localContext.getString(R.string.quran_learning),
            amount = "350.00",
            redirectPage = R.id.quranLearningDetailsFragment,
            isBkashEnable = true,
            isNagadEnable = false,
            isSSLEnable = false,
            isGpayEnable = false,
            serviceIDBkash = 2011,
            serviceIDSSL = 2011,
            serviceIDNagad = "ND-DN-QC-TK-350",
            serviceIDGpay = PRODUCT_ID_QURAN_CLASS_350_TK,
            paySuccessMessage = localContext.getString(R.string.quran_shikkha_pay_success),
            tcUrl = DIGITAL_QURAN_CLASS_TC
        )
        bundle.putParcelable("payment", paymentModel)
        gotoFrag(R.id.action_global_paymentListFragment,bundle)

    }


    override fun singleCardItemClicked()
    {
        if(!isSubscribe)
        {
            goPaymentPage()
            return
        }
        quranLearningCourseV2Adapter?.let {
            it.getFirstVideo()?.let {
                course ->
                selectedData = course
                lifecycleScope.launch {
                    viewmodel.getDigitalQuranSecureUrl(course.FilePath)
                }
            }

        }

    }

    override fun videoPlayerToggleFullScreen(isFullScreen:Boolean)
    {
        /*if(!isFullScreen)
            (requireActivity() as MainActivity).setupDefaultTheme()*/
    }


    override fun onBackPress() {
        sendWatchedDuration()
        if (exoVideoManager.isVideoPlayerFullScreen()) {
            exoVideoManager.toggleFullScreen(requireActivity())
        } else {
            exoVideoManager.onDestory()
            super.onBackPress()

        }

    }


    override fun onDestroyView() {
        sendWatchedDuration()
        lifecycleScope.launch {
            viewmodel.clear()
        }
        exoVideoManager.onDestory()
        super.onDestroyView()
    }




    inner class VMFactory(
        private val paymentRepository: PaymentRepository
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PaymentViewModel(paymentRepository) as T
        }
    }
}