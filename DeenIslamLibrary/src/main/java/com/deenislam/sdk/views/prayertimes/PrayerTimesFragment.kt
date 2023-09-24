package com.deenislam.sdk.views.prayertimes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.isEmpty
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.ViewInflationListener
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.di.DatabaseProvider
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.libs.notification.NotificationPermission
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.prayer_time.PrayerNotificationResource
import com.deenislam.sdk.service.models.prayer_time.PrayerTimeResource
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.service.network.response.prayertimes.tracker.Data
import com.deenislam.sdk.service.repository.PrayerTimesRepository
import com.deenislam.sdk.utils.*
import com.deenislam.sdk.viewmodels.PrayerTimesViewModel
import com.deenislam.sdk.views.adapters.prayer_times.MuezzinAdapter
import com.deenislam.sdk.views.adapters.prayer_times.PrayerTimesAdapter
import com.deenislam.sdk.views.adapters.prayer_times.prayerTimeAdapterCallback
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


internal class PrayerTimesFragment : BaseRegularFragment(),
    otherFagmentActionCallback,
    prayerTimeAdapterCallback,
    ViewInflationListener,
    PrayerTimeNotification
{
    private lateinit var prayerTimesAdapter:PrayerTimesAdapter
    private var linearLayoutManager: LinearLayoutManager? = null

    private lateinit var prayerMain:RecyclerView
    private lateinit var progressLayout:LinearLayout
    private lateinit var no_internet_layout:NestedScrollView
    private lateinit var no_internet_retryBtn:MaterialButton
    private lateinit var mainContainer:ConstraintLayout

    private lateinit var dialog_okBtn:MaterialButton
    private lateinit var customAlertDialogView : View
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var notificationDialog:AlertDialog

    private lateinit var viewmodel:PrayerTimesViewModel

    private var firstload:Boolean = false
    private var prayerdate: String = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date())
    private var todayDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date())
    private var prayerTimesResponse:PrayerTimesResponse?=null

    private var pryaerNotificationData:ArrayList<PrayerNotification> = arrayListOf()

    // notification dialog view reference
    private lateinit var notify_txtTitile:AppCompatTextView
    private lateinit var notify_progressLayout:LinearLayout
    private lateinit var notification_off_btn:AppCompatRadioButton
    private lateinit var notification_only_btn:AppCompatRadioButton
    private lateinit var notification_sound_btn: AppCompatRadioButton
    private lateinit var notification_off_layout:LinearLayout
    private lateinit var notification_only_layout:LinearLayout
    private lateinit var notification_sound_layout:LinearLayout
    private lateinit var txtMuezzin:AppCompatTextView
    private lateinit var notification_muezzin_list:RecyclerView
    private  var notification_state = 0
    private var notification_prayer_name = ""
    private var muezzinAdapter: MuezzinAdapter ? =null
    private var isNotificationClicked:Boolean = false
    private var prayerTrackLastWakt = ""

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)

        NotificationPermission().getInstance().setupLauncher(this,localContext,true, activityContext = requireContext())
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).apply {
            duration = 300L
        }
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true).apply {
            duration = 300L
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).apply {
            duration = 300L
        }

        val prayerTimesRepository = PrayerTimesRepository(
            deenService = NetworkProvider().getInstance().provideDeenService(),
            prayerNotificationDao = DatabaseProvider().getInstance().providePrayerNotificationDao(),
            prayerTimesDao = DatabaseProvider().getInstance().providePrayerTimesDao()
        )

        val factory = VMFactory(prayerTimesRepository)
        viewmodel = ViewModelProvider(
            requireActivity(),
            factory
        )[PrayerTimesViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainview = layoutInflater.inflate(R.layout.fragment_prayer_times,container,false)

        //init view
        prayerMain = mainview.findViewById(R.id.prayerMain)
        progressLayout = mainview.findViewById(R.id.progressLayout)
        no_internet_layout = mainview.findViewById(R.id.no_internet_layout)
        no_internet_retryBtn = no_internet_layout.findViewById(R.id.no_internet_retry)
        mainContainer =  mainview.findViewById(R.id.container)


        setupActionForOtherFragment(R.drawable.ic_calendar,0,this@PrayerTimesFragment,localContext.getString(R.string.prayer_times),true,mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewmodel.listState != null) {
            linearLayoutManager?.onRestoreInstanceState(viewmodel.listState)
        }

        if (isNotificationClicked) {
            NotificationPermission().getInstance().reCheckNotificationPermission(requireContext())

            Log.e("isNotificationPermitted",
                NotificationPermission().getInstance().isNotificationPermitted().toString()
            )
            if(NotificationPermission().getInstance().isNotificationPermitted() && pryaerNotificationData.size>0) {
                updatePrayerNotificationDataOnly(pryaerNotificationData)
                isNotificationClicked = false
            }
        }

        if(prayerMain.isEmpty())
        {
            if(firstload)
            loadPage()
            else
            view.postDelayed({
                // Code to execute after the animation
                loadPage()
            }, 300)
        }


        if(!firstload) {

            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "prayer_time",
                    trackingID = getTrackingID()
                )
            }

            loadDataAPI()
        }
        firstload = true
    }


    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        if(!isBackPressed())
        viewmodel.listState = linearLayoutManager?.onSaveInstanceState()
    }


    fun loadDataAPI()
    {
            loadingState()
            lifecycleScope.launch {
                viewmodel.getPrayerTimes("Dhaka", getLanguage(), prayerdate)
                viewmodel.getDateWisePrayerNotificationData(prayerdate)
            }
    }


    private fun initObserver()
    {
        viewmodel.prayerTimes.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is PrayerTimeResource.postPrayerTime ->
                {
                    Log.e("PrayerTimeResource","MAIN")
                    prayerTimesResponse = it.data
                    viewState(arrayListOf())
                    /* lifecycleScope.launch {
                         viewmodel.getDateWisePrayerNotificationData(prayerdate)
                     }*/
                }
                CommonResource.API_CALL_FAILED -> noInternetState()
                PrayerTimeResource.prayerTimeEmpty -> callPrayerAPIDateWise(prayerdate)

                CommonResource.EMPTY -> Unit
            }
        }

        viewmodel.prayerTimesNotification.observe(viewLifecycleOwner)
        {
            Log.e("ROTATE_DATA",Gson().toJson(it))
            when(it)
            {
                is PrayerNotificationResource.dateWiseNotificationData ->
                {
                    Log.e("PrayerTimeResource","OTHER")
                    viewState(it.data)
                }
                is PrayerNotificationResource.notificationData -> showNotificationDialog(it.data)
                is PrayerNotificationResource.setNotification ->
                {
                    lifecycleScope.launch {

                        viewmodel.clearPrayerNotificationLiveData()
                    }
                    LoadingButton().getInstance(requireContext()).removeLoader()
                    dialog_okBtn.text = "OK"

                    when(it.value)
                    {
                        1 ->
                        {
                            /*lifecycleScope.launch {
                                viewmodel.getDateWisePrayerNotificationData(prayerdate)
                            }*/
                            viewState(it.data)
                            requireContext().toast("Notification setting updated for $notification_prayer_name prayer")
                            notificationDialog.dismiss()
                        }
                        2-> requireContext().toast("$notification_prayer_name prayer alarm was expired for selected date time")
                        0 ->  requireContext().toast("Notification setting update failed!")

                    }
                }

                is PrayerNotificationResource.NotificationStateRequired ->
                {
                    forceUserToEnableNotification()
                }

                is PrayerNotificationResource.prayerTrackFailed ->  requireContext().toast("Failed to set prayer track")
                is PrayerNotificationResource.prayerTrackData ->
                {

                    Log.e("setPrayerTimeTrack",Gson().toJson(it.data))

                    if(prayerTrackLastWakt.isNotEmpty())
                    requireContext().toast("আলহামদুলিল্লাহ আপনি ${prayerTrackLastWakt.prayerMomentLocaleForToast()} নামাজ আদায় করেছেন।")

                    updatePrayerTrackingView(it.data)
                }

                CommonResource.EMPTY -> Unit
            }
        }
    }

    private fun forceUserToEnableNotification()
    {
            MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.MaterialAlertDialog_MaterialComponents)
                .setTitle(localContext.getString(R.string.alert))
                .setMessage(localContext.getString(R.string.ask_user_to_enable_ptn))
                .setPositiveButton(localContext.getString(R.string.okay)) { _, _ ->
                    DeenSDKCore.SetPrayerTimeCallback(this@PrayerTimesFragment)
                    DeenSDKCore.prayerNotification(isEnabled = true,context = DeenSDKCore.baseContext!!, callback = DeenSDKCore.GetDeenCallbackListner()!!)
                    requireContext().toast("Request processing...")

                }
                .setNegativeButton(localContext.getString(R.string.cancel), null)
                .show()
    }

    fun loadPage()
    {

        NotificationPermission().getInstance().askPermission()

        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(no_internet_layout, 10F)

        // notification dialog setup
        setupNotificationView()

        linearLayoutManager =
            LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)

        prayerTimesAdapter = PrayerTimesAdapter(this@PrayerTimesFragment,this@PrayerTimesFragment)
        prayerMain.apply {
            adapter = prayerTimesAdapter
            layoutManager = linearLayoutManager
            isNestedScrollingEnabled = false
            post {
                initObserver()
            }
        }

        no_internet_retryBtn.setOnClickListener {
            loadDataAPI()
        }

    }

    private fun setupNotificationView()
    {
        customAlertDialogView = localInflater.inflate(R.layout.dialog_prayer_notification, null, false)

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(),R.style.DeenMaterialAlertDialog_rounded)
            .setView(customAlertDialogView)
            .setCancelable(true)
        notificationDialog = materialAlertDialogBuilder.create()

        notify_txtTitile = customAlertDialogView.findViewById(R.id.txtTitile)
        notify_progressLayout = customAlertDialogView.findViewById(R.id.progressLayout)
        notification_off_btn = customAlertDialogView.findViewById(R.id.notification_off_btn)
        notification_only_btn = customAlertDialogView.findViewById(R.id.notification_only_btn)
        notification_sound_btn = customAlertDialogView.findViewById(R.id.notification_sound_btn)
        notification_off_layout = customAlertDialogView.findViewById(R.id.notification_off_layout)
        notification_only_layout = customAlertDialogView.findViewById(R.id.notification_layout)
        notification_sound_layout = customAlertDialogView.findViewById(R.id.notification_sound_layout)
        notification_muezzin_list = customAlertDialogView.findViewById(R.id.listMuezzin)
        txtMuezzin = customAlertDialogView.findViewById(R.id.txtMuezzin)

        notification_off_layout.setOnClickListener {
            clearNotificationRadioBtn()
            muezzinListLayout(false)
            notification_off_btn.isChecked = true
            notification_state = 1
        }

        notification_only_layout.setOnClickListener {
            clearNotificationRadioBtn()
            muezzinListLayout(false)
            notification_only_btn.isChecked = true
            notification_state = 2
        }

        notification_sound_layout.setOnClickListener {
            clearNotificationRadioBtn()
            muezzinListLayout(false)
            notification_sound_btn.isChecked = true
            notification_state = 3
        }
    }

    private fun callPrayerAPIDateWise(date:String)
    {
        prayerdate = date
        loadingState()
        lifecycleScope.launch {
            viewmodel.getPrayerTimes("Dhaka", getLanguage(), date)
            viewmodel.getDateWisePrayerNotificationData(date)
        }
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "prayer_time",
                    trackingID = getTrackingID()
                )
            }
            viewmodel.listState = null
        }

        tryCatch { super.onBackPress() }

    }



    private fun viewState(data: ArrayList<PrayerNotification>)
    {

        if (data.size>0) {
            pryaerNotificationData = data
            prayerTimesAdapter.updateNotificationData(pryaerNotificationData)
            Log.e("viewState",Gson().toJson(pryaerNotificationData))
        }

        prayerTimesResponse?.let {
            prayerTimesAdapter.updateData(it,pryaerNotificationData)
        }

    }

    private fun updatePrayerTrackingView(data: Data)
    {
        prayerTimesAdapter.updateTrackingData(data)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updatePrayerAdapterOnly(data: PrayerTimesResponse)
    {
        prayerTimesAdapter.updateData(data, null)
        prayerMain.post {
            prayerTimesAdapter.notifyDataSetChanged()
        }
    }

    private fun updatePrayerNotificationDataOnly(data: ArrayList<PrayerNotification>)
    {
        prayerTimesAdapter.updateNotificationData(data)
    }

    private fun loadingState()
    {
        no_internet_layout.visible(false)
        progressLayout.visible(true)

    }

    private fun noInternetState()
    {
        progressLayout.visible(false)
        no_internet_layout.visible(true)
    }

    override fun action1() {
        gotoFrag(R.id.prayerCalendarFragment)
    }

    override fun action2() {
    }

    override fun leftBtnClick() {
        Log.e("PREV_DATE",prayerdate)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH) // or you can add before dd/M/yyyy
        val newDate = format.parse(prayerdate.StringTimeToMillisecond("dd/MM/yyyy").MilliSecondToStringTime("dd/MM/yyyy",-1))
        newDate?.let {callPrayerAPIDateWise(format.format(it))}
    }

    override fun rightBtnClick() {

        val format = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH) // or you can add before dd/M/yyyy
        val newDate = format.parse(prayerdate.StringTimeToMillisecond("dd/MM/yyyy").MilliSecondToStringTime("dd/MM/yyyy",1))
        newDate?.let {
            callPrayerAPIDateWise(format.format(it))
        }
    }

    override fun nextPrayerCountownFinish() {
        prayerTimesResponse?.let { updatePrayerAdapterOnly(it) }
    }

    private fun clearNotificationRadioBtn()
    {
        notification_off_btn.isChecked = false
        notification_only_btn.isChecked = false
        notification_sound_btn.isChecked = false

    }

    private fun initNotificationDialog(titile: String, prayer_tag: String)
    {
        prayerTimesResponse?.let {
            if(getPrayerTimeTagWise(prayer_tag=prayer_tag,date=prayerdate, data = it)<=0) {
                requireContext().toast("$titile prayer alarm expired for selected date time")
                return
            }
        }

        notification_prayer_name = titile
        muezzinListLayout(false)
       /* muezzinAdapter = MuezzinAdapter()
        notification_muezzin_list.apply {
            adapter = muezzinAdapter
        }*/
        dialog_okBtn = customAlertDialogView.findViewById(R.id.okBtn)
        notify_txtTitile.text = localContext.getString(R.string.prayer_notification_title,titile)
        ViewCompat.setTranslationZ(notify_progressLayout, 10F)
        notify_progressLayout.visible(true)

        dialog_okBtn.let {btn->
            btn.setOnClickListener {
                btn.text = LoadingButton().getInstance(requireContext()).loader(btn)
                lifecycleScope.launch {
                    viewmodel.setNotificationData(prayerdate,prayer_tag,notification_state,"",prayerTimesResponse)
                }
            }
        }

        notificationDialog.setOnCancelListener {
            notification_state = 0
        }

        lifecycleScope.launch {
            viewmodel.getNotificationData(prayerdate,prayer_tag)
        }

    }

    private fun showNotificationDialog(data: PrayerNotification?)
    {
        Log.e("PNT_DATA",Gson().toJson(data))

        clearNotificationRadioBtn()

        if(data!=null && NotificationPermission().getInstance().hasAlarm(requireContext(),data.id))
        {
            notification_state = data.state
            when(data.state)
            {
                1->
                {
                    notification_off_btn.isChecked = true
                }
                2->
                {
                    notification_only_btn.isChecked = true
                }
                3->
                {
                    muezzinListLayout(false)
                    notification_sound_btn.isChecked = true
                }
            }
        }
        else
            notification_off_btn.isChecked = true

        notify_progressLayout.visible(false)
        notificationDialog.show()
    }

    private fun muezzinListLayout(bol:Boolean)
    {
        txtMuezzin.visible(bol)
        notification_muezzin_list.visible(bol)
    }

    override fun clickNotification(position: String) {

        if(NotificationPermission().isNotificationPermitted()) {
            Log.e("Notification", position)
            when (position) {
                "pt1" -> {
                    initNotificationDialog(localContext.getString(R.string.fajr), position)
                }

                "pt2" -> {
                    initNotificationDialog(localContext.getString(R.string.sunrise), position)
                }

                "pt3" -> {
                    initNotificationDialog(localContext.getString(R.string.dhuhr), position)
                }

                "pt4" -> {
                    initNotificationDialog(localContext.getString(R.string.asr), position)
                }

                "pt5" -> {
                    initNotificationDialog(localContext.getString(R.string.maghrib), position)
                }

                "pt6" -> {
                    initNotificationDialog(localContext.getString(R.string.isha), position)
                }

               /* "opt1" -> {
                    initNotificationDialog(localContext.getString(R.string.tahajjud), position)
                }*/

                "opt1" -> {
                    initNotificationDialog(localContext.getString(R.string.suhoor), position)
                }

             /*   "opt3" -> {
                    initNotificationDialog(localContext.getString(R.string.ishraq), position)
                }*/

                "opt2" -> {
                    initNotificationDialog(localContext.getString(R.string.iftaar), position)
                }

            }
        }
        else {
            NotificationPermission().getInstance().showSettingDialog(localContext,requireContext())
            isNotificationClicked = true
        }
    }

    override fun clickMonthlyCalendar() {
        gotoFrag(R.id.prayerCalendarFragment)
    }

    override fun prayerCheck(prayer_tag: String, date: String, isPrayed: Boolean) {
        lifecycleScope.launch {


            prayerTrackLastWakt = if(isPrayed)
                prayer_tag.getWaktNameByTag()
            else
                ""


            viewmodel.setPrayerTrack(language = getLanguage(),prayer_tag=prayer_tag.getWaktNameByTag(),isPrayed)
        }
    }

    override fun onAllViewsInflated() {
        progressLayout.visible(false)
        no_internet_layout.visible(false)
    }

    inner class VMFactory(
        private val prayerTimesRepository : PrayerTimesRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PrayerTimesViewModel(prayerTimesRepository) as T
        }
    }

    override fun isSDKCoreNotificationEnable() {

        lifecycleScope.launch {
            viewmodel.getDateWisePrayerNotificationData(prayerdate)
        }
    }
}

interface PrayerTimeNotification
{
    fun isSDKCoreNotificationEnable()
}