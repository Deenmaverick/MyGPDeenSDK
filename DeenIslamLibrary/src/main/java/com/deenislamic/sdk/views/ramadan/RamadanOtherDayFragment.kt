package com.deenislamic.sdk.views.ramadan

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.RamadanCallback
import com.deenislamic.sdk.service.callback.ViewInflationListener
import com.deenislamic.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislamic.sdk.service.database.AppPreference
import com.deenislamic.sdk.service.di.DatabaseProvider
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicBookResource
import com.deenislamic.sdk.service.models.RamadanResource
import com.deenislamic.sdk.service.models.ramadan.StateModel
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.service.network.response.ramadan.Data
import com.deenislamic.sdk.service.network.response.ramadan.FastTracker
import com.deenislamic.sdk.service.repository.IslamicBookRepository
import com.deenislamic.sdk.service.repository.PrayerTimesRepository
import com.deenislamic.sdk.service.repository.RamadanRepository
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.MENU_ISLAMIC_EVENT
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.bangladeshStateArray
import com.deenislamic.sdk.utils.get9DigitRandom
import com.deenislamic.sdk.utils.transformDashboardItemForKhatamQuran
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.utils.urlEncode
import com.deenislamic.sdk.viewmodels.IslamicBookViewModel
import com.deenislamic.sdk.viewmodels.PrayerTimesViewModel
import com.deenislamic.sdk.viewmodels.RamadanViewModel
import com.deenislamic.sdk.viewmodels.common.PrayerTimeVMFactory
import com.deenislamic.sdk.views.adapters.ramadan.OtherRamadanPatchAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal class RamadanOtherDayFragment : BaseRegularFragment(), RamadanCallback,
    ViewInflationListener, HorizontalCardListCallback {

    private lateinit var listview:RecyclerView

    private lateinit var viewmodel: RamadanViewModel
    private lateinit var prayerViewModel:PrayerTimesViewModel
    private lateinit var islamicBookViewmodel: IslamicBookViewModel

    private lateinit var otherRamadanPatchAdapter: OtherRamadanPatchAdapter

    private var stateArray:ArrayList<StateModel> = arrayListOf()
    private var selectedState:StateModel ? = null
    private var fastingCardData: FastTracker? = null
    private var currentState = "dhaka"

    private var firstload = false
    private var patchDataList: List<com.deenislamic.sdk.service.network.response.dashboard.Data> ? = null

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)
        // init viewmodel
        val repository = RamadanRepository(
            deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = RamadanViewModel(repository)

        val prayerTimesRepository = PrayerTimesRepository(
            deenService = NetworkProvider().getInstance().provideDeenService(),
            prayerNotificationDao = DatabaseProvider().getInstance().providePrayerNotificationDao(),
            prayerTimesDao = DatabaseProvider().getInstance().providePrayerTimesDao()
        )

        val factory = PrayerTimeVMFactory(prayerTimesRepository)
        prayerViewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[PrayerTimesViewModel::class.java]


        val ibrepository = IslamicBookRepository(
            deenService = NetworkProvider().getInstance().provideDeenService())

        val quranLearningRepository = QuranLearningRepository(
            quranShikkhaService = NetworkProvider().getInstance().provideQuranShikkhaService(),
            deenService = NetworkProvider().getInstance().provideDeenService(),
            dashboardService = NetworkProvider().getInstance().provideDashboardService()
        )

        islamicBookViewmodel = IslamicBookViewModel(repository = ibrepository, quranLearningRepository = quranLearningRepository)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_ramadan_other_day,container,false)

        listview = mainview.findViewById(R.id.listview)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.ramadan_titlle),
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)
        CallBackProvider.setFragment(this)
        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentState = AppPreference.getPrayerTimeLoc().toString()

        val filteredStates = bangladeshStateArray.firstOrNull { state ->
            currentState.lowercase().contains(state.state.lowercase()) ||
                    currentState.lowercase().contains(state.statebn.lowercase())
        }

        filteredStates?.let {
            currentState = it.state
            selectedState = it
        }

        if(!firstload)
        {
            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "ramadan_other_day",
                    trackingID = getTrackingID()
                )
            }
        }

        firstload = true

        /*if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }else*/
            loadpage()

    }

    private fun loadpage()
    {
        stateArray = arrayListOf(
            StateModel("dhaka", "Dhaka (ঢাকা)"),
            StateModel("barisal", "Barisal (বরিশাল)"),
            StateModel("khulna", "Khulna (খুলনা)"),
            StateModel("chittagong", "Chittagong (চট্টগ্রাম)"),
            StateModel("mymensingh", "Mymensingh (ময়মনসিংহ)"),
            StateModel("rangpur", "Rangpur (রংপুর)"),
            StateModel("rajshahi", "Rajshahi (রাজশাহী)"),
            StateModel("sylhet", "Sylhet (সিলেট)"),
            StateModel("bagerhat", "Bagerhat (বাগেরহাট)"),
            StateModel("chuadanga", "Chuadanga (চুয়াডাঙ্গা)"),
            StateModel("jessore", "Jessore (যশোর)"),
            StateModel("jhenaidah", "Jhenaidah (ঝিনাইদহ)"),
            StateModel("kushtia", "Kushtia (কুষ্টিয়া)"),
            StateModel("magura", "Magura (মাগুরা)"),
            StateModel("meherpur", "Meherpur (মেহেরপুর)"),
            StateModel("narail", "Narail (নড়াইল)"),
            StateModel("satkhira", "Satkhira (সাতক্ষীরা)"),
            StateModel("bandarban", "Bandarban (বান্দরবান)"),
            StateModel("brahmanbaria", "Brahmanbaria (ব্রাহ্মণবাড়িয়া)"),
            StateModel("chandpur", "Chandpur (চাঁদপুর)"),
            StateModel("comilla", "Comilla (কুমিল্লা)"),
            StateModel("coxsBazar", "CoxsBazar (কক্সবাজার)"),
            StateModel("feni", "Feni (ফেনী)"),
            StateModel("khagrachhari", "Khagrachhari (খাগড়াছড়ি)"),
            StateModel("lakshmipur", "Lakshmipur (লক্ষ্মীপুর)"),
            StateModel("noakhali", "Noakhali (নোয়াখালী)"),
            StateModel("rangamati", "Rangamati (রাঙ্গামাটি)"),
            StateModel("faridpur", "Faridpur (ফরিদপুর)"),
            StateModel("tangail", "Tangail (টাঙ্গাইল)"),
            StateModel("gazipur", "Gazipur (গাজীপুর)"),
            StateModel("gopalganj", "Gopalganj (গোপালগঞ্জ)"),
            StateModel("kishoreganj", "Kishoreganj (কিশোরগঞ্জ)"),
            StateModel("madaripur", "Madaripur (মাদারীপুর)"),
            StateModel("manikganj", "Manikganj (মানিকগঞ্জ)"),
            StateModel("munshiganj", "Munshiganj (মুন্সীগঞ্জ)"),
            StateModel("narayanganj", "Narayanganj (নারায়ণগঞ্জ)"),
            StateModel("narsingdi", "Narsingdi (নরসিংদী)"),
            StateModel("rajbari", "Rajbari (রাজবাড়ী)"),
            StateModel("shariatpur", "Shariatpur (শরীয়তপুর)"),
            StateModel("barguna", "Barguna (বরগুনা)"),
            StateModel("bhola", "Bhola (ভোলা)"),
            StateModel("jhalokati", "Jhalokati (ঝালকাঠি)"),
            StateModel("patuakhali", "Patuakhali (পটুয়াখালী)"),
            StateModel("pirojpur", "Pirojpur (পিরোজপুর)"),
            StateModel("dinajpur", "Dinajpur (দিনাজপুর)"),
            StateModel("gaibandha", "Gaibandha (গাইবান্ধা)"),
            StateModel("kurigram", "Kurigram (কুড়িগ্রাম)"),
            StateModel("lalmonirhat", "Lalmonirhat (লালমনিরহাট)"),
            StateModel("nilphamari", "Nilphamari (নীলফামারী)"),
            StateModel("panchagarh", "Panchagarh (পঞ্চগড়)"),
            StateModel("thakurgaon", "Thakurgaon (ঠাকুরগাঁও)"),
            StateModel("bogra", "Bogra (বগুড়া)"),
            StateModel("pabna", "Pabna (পাবনা)"),
            StateModel("joypurhat", "Joypurhat (জয়পুরহাট)"),
            StateModel("chapainawabganj", "Chapainawabganj (চাঁপাইনবাবগঞ্জ)"),
            StateModel("naogaon", "Naogaon (নওগাঁ)"),
            StateModel("natore", "Natore (নাটোর)"),
            StateModel("sirajganj", "Sirajganj (সিরাজগঞ্জ)"),
            StateModel("habiganj", "Habiganj (হবিগঞ্জ)"),
            StateModel("moulvibazar", "Moulvibazar (মৌলভীবাজার)"),
            StateModel("sunamganj", "Sunamganj (সুনামগঞ্জ)"),
            StateModel("sherpur", "Sherpur (শেরপুর)"),
            StateModel("jamalpur", "Jamalpur (জামালপুর)"),
            StateModel("netrokona", "Netrokona (নেত্রকোনা)")
        )

        initObserver()

        loadApi()
    }

    private fun initObserver()
    {
        viewmodel.ramadanLiveData.observe(viewLifecycleOwner)
        {

            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                /*is RamadanResource.ramadanTime -> {
                    lifecycleScope.launch {
                        viewmodel.clear()
                    }
                    viewState(it.data)
                }*/
                is RamadanResource.RamadanPatch -> it.data?.let { it1 -> viewState(it1,it.patch) }?:baseNoInternetState()

            }

        }

        viewmodel.ramadanTrackLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is RamadanResource.ramadanTracking -> updateFastingTrack(it.isFasting)
            }
        }

        islamicBookViewmodel.secureUrlLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is IslamicBookResource.PdfSecureUrl -> {

                    val bundle = Bundle()
                    bundle.putString("title",it.bookTitle)
                    bundle.putString("weburl","https://deenislamic.com/pdf?file="+ it.url.urlEncode())
                    gotoFrag(R.id.action_global_basicWebViewFragment,bundle)

                }
            }
        }
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "ramadan_other_day",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }

    private fun loadApi()
    {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getOtherRamadanTime(currentState,getLanguage())
        }
    }

    override fun noInternetRetryClicked() {
        loadApi()
    }

    private fun viewState(
        data: Data,
        patch: List<com.deenislamic.sdk.service.network.response.dashboard.Data>?
    )
    {
        listview.scrollToPosition(0)
        patchDataList = patch
        fastingCardData = data.FastTracker
        listview.apply {
            otherRamadanPatchAdapter = OtherRamadanPatchAdapter(data,stateArray,selectedState,patch)
            adapter = otherRamadanPatchAdapter
        }
        baseViewState()
    }

    private fun updateFastingTrack(fasting: Boolean)
    {
        otherRamadanPatchAdapter.updateFastingTrack(fasting)
    }

    override fun openMonthlyTracker() {
        fastingCardData = otherRamadanPatchAdapter.getFastingTrackData()
        fastingCardData?.let {
            val bundle = Bundle()
            bundle.putParcelable("fastingCardData",it)
            gotoFrag(R.id.action_global_fastingTrackerFragment,bundle)
        }

    }

    override fun setFastingTrack(isFast: Boolean) {
        if(!Subscription.isSubscribe){
            gotoFrag(R.id.action_global_subscriptionFragment)
            return
        }
        lifecycleScope.launch {
            viewmodel.setRamadanTrack(isFast,getLanguage())
        }
    }

    override fun stateSelected(stateModel: StateModel) {
        otherRamadanPatchAdapter.updateDropdownSelectedState(stateModel)
        currentState = stateModel.state
        selectedState = stateModel
        AppPreference.savePrayerTimeLoc(stateModel.state)
        lifecycleScope.launch {
            prayerViewModel.updateSelectedState(stateModel)
        }
        loadApi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.launch {

            viewmodel.clear()
        }
    }

    override fun onAllViewsInflated() {
        baseViewState()
    }

    override fun sehriCardClicked(sehriTime: String) {

        initSetAlarm(sehriTime,1,"Sehri")

    }

    override fun iftarCardClicked(iftaar: String) {
        initSetAlarm(iftaar,0,"Iftar")
    }

    private fun initSetAlarm(time:String,alarmType:Int,title:String){
        val sdf =
            SimpleDateFormat(
                "HH:mm",
                Locale.ENGLISH
            ) // or "hh:mm" for 12 hour format

        val date = sdf.parse(time)

        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val min = calendar.get(Calendar.MINUTE)

            val i = Intent(AlarmClock.ACTION_SET_ALARM)

            i.putExtra(AlarmClock.EXTRA_MESSAGE, title)

            if (alarmType == 0) {
                i.putExtra(AlarmClock.EXTRA_HOUR, 12 + hour)
            } else {
                i.putExtra(AlarmClock.EXTRA_HOUR, hour)
            }

            i.putExtra(AlarmClock.EXTRA_MINUTES, min)
            requireContext().startActivity(i)
        }
    }


    override fun patchClicked(data: Item) {

        when(data.ContentType){

            "du" ->{
                val bundle = Bundle().apply {
                    putInt("category", data.CategoryId)
                    putString("catName", data.ArabicText)
                }
                gotoFrag(R.id.action_global_allDuaPreviewFragment,data = bundle)
            }

            "ib" ->{

                val bundle = Bundle()
                bundle.putInt("id", data.CategoryId)
                bundle.putString("videoType", "category")
                bundle.putString("title", data.ArabicText)
                gotoFrag(R.id.action_global_boyanVideoPreviewFragment, bundle)
            }

            "ibook1" ->{

                val bundle = Bundle()
                bundle.putInt("id", data.CategoryId)
                bundle.putString("bookType", "category")
                bundle.putString("title", data.ArabicText)
                gotoFrag(R.id.action_global_islamicBookPreviewFragment, bundle)

            }

            "khq" ->{
                gotoFrag(R.id.action_global_khatamEquranHomeFragment)
            }

            "hd" ->{
                val bundle = Bundle().apply {
                    putInt("chapterId", data.SubCategoryId)
                    putInt("bookId", data.CategoryId)
                    putString("title", data.ArabicText)
                }
                gotoFrag(R.id.action_global_hadithPreviewFragment,data = bundle)
            }

            "fnm" -> gotoFrag(R.id.action_global_islamiFazaelFragment)
            "imas" -> gotoFrag(R.id.action_global_islamiMasailFragment)

            "ri" -> {
                val bundle = Bundle()
                bundle.putInt("categoryID", if(getLanguage() == "bn") 12 else  11)
                bundle.putString("pageTitle",data.ArabicText)
                bundle.putString("pageTag", MENU_ISLAMIC_EVENT)
                bundle.putBoolean("shareable",true)

                gotoFrag(R.id.action_global_subCatCardListFragment,bundle)
            }
        }
    }

    override fun patchItemClicked(getData: Item) {

        when(getData.ContentType){

            "ib" -> {
                val bundle = Bundle()
                bundle.putInt("id", getData.CategoryId)
                bundle.putString("videoType", "category")
                bundle.putString("pageTitle",getData.ArabicText)
                gotoFrag(R.id.action_global_boyanVideoPreviewFragment, bundle)
            }

            "ibook1" -> {

                lifecycleScope.launch {
                    islamicBookViewmodel.getDigitalQuranSecureUrl(getData.imageurl2, false, getData.CategoryId, getData.ArabicText)
                }
            }

            "khq" -> {


                patchDataList?.let {

                    var dataIndex = -1
                    var itemIndex = 0

                    patchDataList?.forEachIndexed { index, data ->
                        data.Items.forEachIndexed { innerIndex, item ->
                            // Replace with the condition to check if the items match
                            if (item.Id == getData.Id) {
                                dataIndex = index
                                itemIndex = innerIndex
                                return@forEachIndexed
                            }
                        }
                    }

                    if(dataIndex !=-1) {
                        val bundle = Bundle()
                        bundle.putInt("khatamQuranvideoPosition", itemIndex)
                        bundle.putParcelableArray("khatamQuranvideoList", it[dataIndex].Items.map { it1-> transformDashboardItemForKhatamQuran(it1) }.toTypedArray())
                        gotoFrag(R.id.action_global_khatamEQuranVideoFragment, bundle)
                    }

                }

            }

        }

    }

}