package com.deenislam.sdk.views.quran.learning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.QuranLearningCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.libs.media3.ExoVideoManager
import com.deenislam.sdk.service.libs.media3.VideoPlayerCallback
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.PaymentResource
import com.deenislam.sdk.service.models.payment.PaymentModel
import com.deenislam.sdk.service.models.quran.learning.FaqList
import com.deenislam.sdk.service.models.quran.learning.QuranLearningResource
import com.deenislam.sdk.service.network.response.common.CommonCardData
import com.deenislam.sdk.service.network.response.quran.learning.quransikkhaacademy.ContentListResponse
import com.deenislam.sdk.service.repository.PaymentRepository
import com.deenislam.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.QURAN_SHIKKHA_PAYMENT_TC
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.toast
import com.deenislam.sdk.viewmodels.PaymentViewModel
import com.deenislam.sdk.viewmodels.quran.learning.QuranLearningViewModel
import com.deenislam.sdk.views.adapters.quran.learning.QuranClassFaqList
import com.deenislam.sdk.views.adapters.quran.learning.QuranCourseCurriculumAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


internal class QuranLearningTpFragment : BaseRegularFragment(), QuranLearningCallback,
    VideoPlayerCallback {

    private lateinit var playerView: PlayerView
    private lateinit var exoVideoManager: ExoVideoManager
    private lateinit var vPlayerControlAction2: AppCompatImageView
    private lateinit var vPlayerControlBtnBack: AppCompatImageView
    private lateinit var vPlayerControlBtnPlay: AppCompatImageView
    private lateinit var vPlayerControlAction1:AppCompatImageView
    private lateinit var actionbar:ConstraintLayout

    private lateinit var courseCurriculumList: RecyclerView
    private lateinit var faqList: RecyclerView

    private lateinit var viewmodel: QuranLearningViewModel
    private var firstload = false
    private var selectedData: ContentListResponse.Data.Result ? = null
    private lateinit var quranCourseCurriculumAdapter: QuranCourseCurriculumAdapter
    private lateinit var paymentViewmodel:PaymentViewModel

    override fun OnCreate() {
        super.OnCreate()

        viewmodel =QuranLearningViewModel(
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
        val mainview = localInflater.inflate(R.layout.fragment_quran_learning_tp,container,false)

        playerView = mainview.findViewById(R.id.playerView)

        exoVideoManager = ExoVideoManager(
            context = requireContext(),
            isLiveVideo = false,
            mainview = mainview
        )
        exoVideoManager.setupActionbar(isBackBtn = true, title = localContext.getString(R.string.digital_quran_class))

        vPlayerControlBtnBack = mainview.findViewById(R.id.vPlayerControlBtnBack)
        vPlayerControlBtnPlay = mainview.findViewById(R.id.vPlayerControlBtnPlay)
        vPlayerControlAction2 = mainview.findViewById(R.id.vPlayerControlAction2)
        vPlayerControlAction1 = mainview.findViewById(R.id.vPlayerControlAction1)
        courseCurriculumList = mainview.findViewById(R.id.courseCurriculumList)
        actionbar = mainview.findViewById(R.id.actionbar)
        faqList = mainview.findViewById(R.id.faqList)

        vPlayerControlAction1.hide()
        setupCommonLayout(mainview)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.digital_quran_class),
            backEnable = true,
            view = mainview
        )

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
        //loadapi()

    }


    override fun onBackPress() {
        if (exoVideoManager.isVideoPlayerFullScreen()) {
            exoVideoManager.toggleFullScreen(requireActivity())
        } else {
            exoVideoManager.onDestory()
            super.onBackPress()

        }
    }


    override fun onDestroyView() {
        lifecycleScope.launch {
            viewmodel.clear()
            paymentViewmodel.clearIPN()
        }
        exoVideoManager.onDestory()
        super.onDestroyView()
    }


    private fun initObserver()
    {
        viewmodel.quranShikkhaAcademyLiveData.observe(viewLifecycleOwner)
        {

            if(CommonResource.CLEAR != it) {
                lifecycleScope.launch {
                    viewmodel.clear()
                }
            }

            when(it)
            {
                CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is QuranLearningResource.QSAContentList ->{

                    if(it.data.results.isNotEmpty()) {
                        getContentByID(it.data.results[0].id,it.data.results[0])
                    }

                    actionbar.hide()

                    courseCurriculumList.apply {
                        quranCourseCurriculumAdapter = QuranCourseCurriculumAdapter(it.data)
                        adapter = quranCourseCurriculumAdapter
                    }

                    faqList.apply {
                        val faqlist: ArrayList<FaqList> = arrayListOf(
                            FaqList(
                                question = "আমি কি এখানে কুরআন শিক্ষা করতে পারবো?",
                                content = "জ্বী আমরা সর্বাধুনিক পদ্ধতিতে সবচেয়ে কম সময়ে কুরআন শরিফ শুদ্ধভাবে পড়ার জন্য শিক্ষা দিয়ে থাকি।"
                            ),
                            FaqList(
                                question = " কোর্সে কি কি শিখাবেন?",
                                content = "কোর্সে কি কি শিখাবেন?  \n" +
                                        "১) বানান ছাড়া অত্যাধুনিক পদ্ধতিতে সরাসরি কুরআন পড়ায় অভ্যস্ত হওয়া। \n" +
                                        "২) মাখরাজ মুখস্থ না করে প্রায়োগিকভাবে অক্ষরের সঠিক উচ্চারণে অভ্যস্ত হওয়া। \n" +
                                        "৩) তাজবীদ ( কুরআন পড়ার সঠিক নিয়মাবলী )  * বাংলার সাথে মিল রেখে হারাকাত, তানবীন, মোটা উচ্চারণের ( ইস্তি'লা) অক্ষর, ছুকুন, ক্বলক্বলাহ্, যুক্ত অক্ষরের পরিচয় , তাশদীদ, ওয়াজিব গুনাহ, মাদ (টান), মীম ছুকুন, নূন ছুকুন ও তানবীন, আল্লাহ শব্দ, চিকন-মোটা, গোল তা, চন্দ্র অক্ষর- সূর্য অক্ষর, সিজদাহ্, সাকতাহ্, নূনে কুত্বনী, থামার চিহ্ন সমূহ সহজ সূত্রে শেখা।  \n" +
                                        "৪) মাত্র ১০/১২ মিনিটে একটি সহজ সূত্রে ৩০ পারা কোরআনের সকল যুক্ত অক্ষর চিনতে পারা। \n" +
                                        "৫) মাত্র ২ টি দাগ দিয়ে আরবী ২৯ টি অক্ষর সহ আরবী লিখুনি জগতের সমস্ত লেখার কৌশল শেখা।"
                            ),
                            FaqList(
                                question = "কোর্সটি কাদের জন্য?",
                                content = "যারা ছোটবেলায় কোরআন পড়েছেন কিন্তু একেবারেই ভুলে গেছেন  যারা কখনো কোরআন পড়েননি অর্থ্যাৎ অক্ষরজ্ঞান শূন্য যারা কিছুটা পড়তে পারেন কিন্তু ১০০% শুদ্ধ হচ্ছে কি না। "
                            ),
                            FaqList(
                                question = "কোর্স থেকে কিভাবে উপকৃত হবেন?",
                                content = "কোর্স শেষে কোরআন শরীফের যেকোন জায়গা থেকে শুদ্ধভাবে পড়তে পারবেন এবং নিজের ভুল নিজে ধরতে পারার যোগ্যতা তৈরি হবে ইনশাআল্লাহ।"
                            ),
                            FaqList(
                                question = "আমি আপনাদের থেকে কিভাবে শিখব?",
                                content = "আপনি সরাসরি প্রিরেকর্ডেড ভিডিও দেখে শিখে নিতে পারবেন শুদ্ধভাবে কুরআন শরিফ পড়া।"
                            ),
                            FaqList(
                                question = "ভিডিও এর এক্সেস কত দিনের জন্য থাকবে?",
                                content = "ভিডিও আপনি ১ বছরের জন্য পাবেন।"
                            ),
                            FaqList(
                                question = "কোর্সের বাইরে আর কি কি আপনারা দিবেন?",
                                content = "আপনি আমাদের অ্যাপ থেকে  আপনি নিয়মিত কোরআন, দোয়া, হাদিস, নামাজের টাইম, কিবলা কম্পাস ইত্যাদি ফ্রিতে পাবেন।"
                            ),
                            FaqList(
                                question = "পেমেন্ট কিভাবে করবো?",
                                content = "আমাদের গেইটওয়েতে আপনার পছন্দসই পেমেন্ট মেথড দিয়ে পেমেন্ট করতে পারেন।"
                            )
                        )

                        adapter = QuranClassFaqList(faqlist)

                    }

                    firstload = true
                    baseViewState()
                }
            }
        }

        viewmodel.quranShikkhaContentLiveData.observe(viewLifecycleOwner)
        {

            if(CommonResource.CLEAR != it) {
                lifecycleScope.launch {
                    viewmodel.clear()
                }
            }

            when(it)
            {
                is QuranLearningResource.QSAContentByID ->{

                    when(it.data?.contentType)
                    {
                        "file" ->{
                            val bundle = Bundle()
                            bundle.putString("pageTitle",it.contentData.title)
                            //bundle.putString("pdfUrl","https://dev.deenislamic.com/pdf?file="+it.data.url?.urlEncode())
                            bundle.putString("pdfUrl",it.data.url)
                            gotoFrag(R.id.action_global_pdfViewerFragment,bundle)
                        }

                        else ->{

                            exoVideoManager.playRegularVideoFromUrl(
                                singleVideoUrl = it.data?.url,
                                initialPlayerStart = true
                            )

                        }
                    }

                }

                is QuranLearningResource.QSAContentByIDFailed -> Unit
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
                    loadapi(true)
                    context?.toast(localContext.getString(R.string.quran_shikkha_pay_success))
                }

                is PaymentResource.PaymentIPNFailed -> {
                    loadapi()
                    context?.toast(localContext.getString(R.string.payfailed_msg))
                }

                is PaymentResource.PaymentIPNCancle -> {
                    loadapi()
                    context?.toast(localContext.getString(R.string.payment_has_been_cancelled))
                }
            }
        }
    }

    private fun loadapi(isPaid: Boolean = false)
    {
        actionbar.show()
        baseLoadingState()
        lifecycleScope.launch {
            if(isPaid){
                val orderConfirm = async {  viewmodel.qsaOrderConfirm() }
                orderConfirm.await()
            }
            viewmodel.qsaGetContentList()
        }
    }

    private fun getContentByID(id: String, getData: ContentListResponse.Data.Result)
    {
        selectedData = getData
        lifecycleScope.launch {
            viewmodel.qsaGetContentByID(id = id, contentData = getData)
        }
    }

    override fun noInternetRetryClicked() {
        loadapi(true)
    }

    override fun courseCurriculumClicked(getData: ContentListResponse.Data.Result)
    {
        if(!getData.isUnlocked) {
            val bundle = Bundle()

            val paymentModel = PaymentModel(
                title = localContext.getString(R.string.digital_quran_class),
                amount = "450.00",
                redirectPage = R.id.quranLearningTpFragment,
                isBkashEnable = true,
                isNagadEnable = false,
                isSSLEnable = false,
                isGpayEnable = false,
                serviceIDBkash = 2012,
                serviceIDSSL = 2012,
                serviceIDNagad = "ND-DN-QC-TK-450",
                serviceIDGpay = "quran_class_450_tk",
                paySuccessMessage = localContext.getString(R.string.quran_shikkha_pay_success),
                tcUrl = QURAN_SHIKKHA_PAYMENT_TC
            )
            bundle.putParcelable("payment", paymentModel)
            gotoFrag(R.id.action_global_paymentListFragment,bundle)
            return
        }

        getContentByID(getData.id,getData)

    }

    override fun videoPlayerReady(isPlaying: Boolean, mediaData: CommonCardData?) {

        if (isPlaying) {

            selectedData?.let {
                quranCourseCurriculumAdapter.updateData(
                    it.copy(isPlaying = true)
                )
            }

        } else {

            selectedData?.let {
                quranCourseCurriculumAdapter.updateData(
                    it.copy(isPlaying = false)
                )
            }

        }

    }

    override fun videoPlayerToggleFullScreen(isFullScreen:Boolean)
    {
       /* if(!isFullScreen)
            (requireActivity() as MainActivity).setupDefaultTheme()*/
    }

    inner class VMFactory(
        private val paymentRepository: PaymentRepository
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PaymentViewModel(paymentRepository) as T
        }
    }

}