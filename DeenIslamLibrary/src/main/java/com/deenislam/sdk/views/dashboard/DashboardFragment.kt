package com.deenislam.sdk.views.dashboard

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.databinding.FragmentDashboardBinding
import com.deenislam.sdk.service.callback.DashboardPatchCallback
import com.deenislam.sdk.service.callback.ViewInflationListener
import com.deenislam.sdk.service.di.DatabaseProvider
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.DashboardResource
import com.deenislam.sdk.service.models.prayer_time.PrayerNotificationResource
import com.deenislam.sdk.service.models.prayer_time.PrayerTimeResource
import com.deenislam.sdk.service.network.response.dashboard.Data
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.service.repository.DashboardRepository
import com.deenislam.sdk.service.repository.PrayerTimesRepository
import com.deenislam.sdk.utils.MENU_AL_QURAN
import com.deenislam.sdk.utils.MENU_DIGITAL_TASBEEH
import com.deenislam.sdk.utils.MENU_DUA
import com.deenislam.sdk.utils.MENU_HADITH
import com.deenislam.sdk.utils.MENU_ISLAMIC_NAME
import com.deenislam.sdk.utils.MENU_PRAYER_TIME
import com.deenislam.sdk.utils.MENU_QIBLA_COMPASS
import com.deenislam.sdk.utils.MENU_ZAKAT
import com.deenislam.sdk.utils.get9DigitRandom
import com.deenislam.sdk.utils.getWaktNameByTag
import com.deenislam.sdk.utils.prayerMomentLocaleForToast
import com.deenislam.sdk.utils.toast
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.DashboardViewModel
import com.deenislam.sdk.viewmodels.PrayerTimesViewModel
import com.deenislam.sdk.views.adapters.MenuCallback
import com.deenislam.sdk.views.adapters.dashboard.DashboardPatchAdapter
import com.deenislam.sdk.views.adapters.dashboard.prayerTimeCallback
import com.deenislam.sdk.views.base.BaseFragment
import com.deenislam.sdk.views.main.actionCallback
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


internal class DashboardFragment : BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate),
    actionCallback, MenuCallback, prayerTimeCallback , ViewInflationListener,
    DashboardPatchCallback {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var prayerViewModel:  PrayerTimesViewModel

    private val dashboardPatchMain:DashboardPatchAdapter by lazy { DashboardPatchAdapter(
        callback = this@DashboardFragment,
        menuCallback = this@DashboardFragment,
        viewInflationListener = this@DashboardFragment,
        dashboardPatchCallback = this@DashboardFragment
    ) }
    private var prayerdate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    private var prayerTrackLastWakt = ""

    override fun OnCreate() {
        super.OnCreate()

       /* isHomePage(true)
        setupBackPressCallback(this)
*/
        val dashboardRepository = DashboardRepository(authenticateService = NetworkProvider().getInstance().provideAuthService())

        val prayerTimesRepository = PrayerTimesRepository(
            deenService = NetworkProvider().getInstance().provideDeenService(),
            prayerNotificationDao = DatabaseProvider().getInstance().providePrayerNotificationDao(),
            prayerTimesDao = DatabaseProvider().getInstance().providePrayerTimesDao()
        )

        val factory = VMFactory(dashboardRepository,prayerTimesRepository)
        dashboardViewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[DashboardViewModel::class.java]

        val factoryPrayer = VMFactoryPrayer(prayerTimesRepository)
        prayerViewModel = ViewModelProvider(
            requireActivity(),
            factoryPrayer
        )[PrayerTimesViewModel::class.java]


    }

    override fun onPause() {
        super.onPause()
        Log.e("DASHBOARD_NEW","onPause")
    }



    private var prayerTimesResponse:PrayerTimesResponse?=null

    private var firstload:Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingState()
        ViewCompat.setTranslationZ(binding.progressLayout.root, 10F)
        ViewCompat.setTranslationZ(binding.noInternetLayout.root, 10F)
        binding.noInternetLayout.root.isClickable = true
        binding.progressLayout.root.isClickable = true
        initObserver()
        loadPage()
        binding.noInternetLayout.noInternetRetry.setOnClickListener {
            loadDataAPI()
        }

    }



   /* override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        prayerTimesResponse?.let { updatePrayerAdapterOnly(it) }
    }*/

    override fun onResume() {
        super.onResume()

            loadPage()
      /*  if(isDashboardVisible())
        setupBackPressCallback(this)*/
        Log.e("DASHBOARD_NEW",isDashboardVisible().toString())
    }

    /*override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if(menuVisible)
        {
            Log.e("setMenuVisibility","DAASHBOARD")
            setupAction(R.drawable.ic_menu,0,this@DashboardFragment,localContext.resources.getString(R.string.app_name))
        }
    }*/

    fun loadDataAPI()
    {
          loadingState()
            lifecycleScope.launch {
                dashboardViewModel.getDashboard("Dhaka", getLanguage(), prayerdate)
                DeenSDKCore.prayerNotification(DeenSDKCore.isPrayerNotificationEnabled(requireContext()))
            }
    }

   /* override fun BASE_API_CALL_STATE() {
        super.BASE_API_CALL_STATE()
        loadDataAPI()
    }
*/



    private fun initObserver()
    {

        dashboardViewModel.prayerTimes.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is PrayerTimeResource.postPrayerTime -> viewStatePrayerTime(it.data)

            }
        }

        dashboardViewModel.dashLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> nointernetState()
                is DashboardResource.DashboardData -> viewState(it.data)

            }
        }

        prayerViewModel.prayerTimesNotification.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is PrayerNotificationResource.prayerTrackFailed ->  requireContext().toast("Failed to set prayer track")
                is PrayerNotificationResource.prayerTrackData ->
                {

                    if(prayerTrackLastWakt.isNotEmpty())
                        requireContext().toast("আলহামদুলিল্লাহ আপনি ${prayerTrackLastWakt.prayerMomentLocaleForToast()} নামাজ আদায় করেছেন।")

                    updatePrayerTrackingView(it.data)
                }
            }
        }

        /*dashboardViewModel.prayerTimesNotification.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is PrayerNotificationResource.dateWiseNotificationData -> updateprayerTracker(it.data)
                else -> Unit
            }
        }*/

    }

    private fun updatePrayerTrackingView(data: com.deenislam.sdk.service.network.response.prayertimes.tracker.Data)
    {
        dashboardPatchMain.updatePrayerTracker(data)
    }

    private fun viewStatePrayerTime(data: PrayerTimesResponse)
    {
        if(prayerTimesResponse==null) {
            prayerTimesResponse = data
        }

    }

    private fun viewState(data: Data)
    {
        lifecycleScope.launch {

            val dashbTask = async { dashboardPatchMain.updateDashData(data) }

            dashbTask.await()

            prayerTimesResponse?.let {
                dashboardPatchMain.updatePrayerTime(it)
                //binding.progressLayout.root.visible(false)
                binding.noInternetLayout.root.visible(false)
            }?:nointernetState()
        }
    }


    private fun updatePrayerAdapterOnly(data: PrayerTimesResponse)
    {
        Log.e("updatePrayerAdapterOnly","Called")
        binding.dashboardMain.post {
            dashboardPatchMain.updatePrayerTime(data)
            //dashboardPatchMain.notifyDataSetChanged()
        }
    }

    private fun dataState()
    {
        binding.dashboardMain.visible(true)
        binding.progressLayout.root.visible(false)/*
        binding.progressLayout.root.visible(false)
        binding.noInternetLayout.root.visible(false)*/
    }

    private fun nointernetState()
    {
        binding.progressLayout.root.visible(false)
        binding.noInternetLayout.root.visible(true)
    }

    private fun loadingState()
    {
        binding.progressLayout.root.visible(true)
        binding.noInternetLayout.root.visible(false)
    }

    fun loadPage()
    {
        if(firstload != 0)
            return
        firstload = 1

        ViewCompat.setTranslationZ(binding.progressLayout.root, 10F)

        //dashboardPatchMain.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        val linearLayoutManager =
            LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)


        binding.dashboardMain.apply {
                adapter = dashboardPatchMain
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                isNestedScrollingEnabled = false
            }
                layoutManager = linearLayoutManager
                //overScrollMode = View.OVER_SCROLL_NEVER
                post {

                    loadDataAPI()
                }
            }

    /*    lifecycleScope.launch {

            userTrackViewModel.trackUser(
                language = getLanguage(),
                msisdn = DeenSDKCore.msisdn,
                pagename = "home",
                trackingID = get9DigitRandom()
            )
        }*/

    }


    override fun action1() {

        gotoFrag(R.id.moreFragment)
    }

    override fun action2() {

    }

    override fun nextPrayerCountownFinish() {

        prayerTimesResponse?.let { updatePrayerAdapterOnly(it) }
    }

    override fun allPrayerPage() {
        gotoFrag(R.id.prayerTimesFragment)
    }

    override fun prayerTask(momentName: String?, isPrayed: Boolean) {

        lifecycleScope.launch {

            prayerTrackLastWakt = if(isPrayed)
                momentName?.getWaktNameByTag().toString()
            else
                ""

            if (momentName?.isNotEmpty() == true) {
               prayerViewModel.setPrayerTrack(language = getLanguage(),prayer_tag=momentName.getWaktNameByTag(),isPrayed)
            }
            /*else
                prayerTimeViewModel.getDateWisePrayerNotificationData(prayerdate)*/
        }
    }

    override fun billboard_prayer_load_complete() {
        prayerTimesResponse?.let { dashboardPatchMain.updatePrayerTime(it) }
    }

    inner class VMFactory(
        private val dashboardRepository: DashboardRepository,
        private val prayerTimesRepository : PrayerTimesRepository
        ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(dashboardRepository,prayerTimesRepository) as T
        }
    }

    inner class VMFactoryPrayer(
        private val prayerTimesRepository : PrayerTimesRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PrayerTimesViewModel(prayerTimesRepository) as T
        }
    }

    override fun menuClicked(tag: String) {

        when(tag)
        {
            MENU_PRAYER_TIME ->  gotoFrag(R.id.prayerTimesFragment)
            MENU_AL_QURAN -> gotoFrag(R.id.quranFragment)
            MENU_HADITH -> gotoFrag(R.id.hadithFragment)
            MENU_DUA -> gotoFrag(R.id.dailyDuaFragment)
            MENU_ZAKAT -> gotoFrag(R.id.zakatFragment)
            MENU_DIGITAL_TASBEEH -> gotoFrag(R.id.tasbeehFragment)
            MENU_QIBLA_COMPASS -> gotoFrag(R.id.compassFragment)
            MENU_ISLAMIC_NAME -> gotoFrag(R.id.islamicNameFragment)
        }
    }

    override fun onAllViewsInflated() {
        dataState()
    }

    override fun dashboardPatchClickd(patch:String) {

        when(patch)
        {
            "Verse" -> gotoFrag(R.id.quranFragment)
            "Hadith" -> gotoFrag(R.id.hadithFragment)
            "Zakat" -> gotoFrag(R.id.zakatFragment)
            "Tasbeeh" -> gotoFrag(R.id.tasbeehFragment)
            "IslamicName" -> gotoFrag(R.id.islamicNameFragment)
            "Qibla" -> gotoFrag(R.id.compassFragment)
            "DailyDua" -> gotoFrag(R.id.dailyDuaFragment)
            "Compass" -> gotoFrag(R.id.compassFragment)
            "Dua" -> gotoFrag(R.id.dailyDuaFragment)
            "Quran" -> gotoFrag(R.id.quranFragment)
            "Tasbeeh" -> gotoFrag(R.id.tasbeehFragment)


        }
    }

}