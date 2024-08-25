package com.deenislamic.sdk.views.prayertimes

import android.annotation.SuppressLint
import android.os.Bundle
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
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.RamadanCallback
import com.deenislamic.sdk.service.callback.ViewInflationListener
import com.deenislamic.sdk.service.database.AppPreference
import com.deenislamic.sdk.service.database.entity.PrayerNotification
import com.deenislamic.sdk.service.di.DatabaseProvider
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.notification.NotificationPermission
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.prayer_time.PrayerNotificationResource
import com.deenislamic.sdk.service.models.prayer_time.PrayerTimeResource
import com.deenislamic.sdk.service.models.ramadan.StateModel
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.service.network.response.prayertimes.tracker.Data
import com.deenislamic.sdk.service.repository.PrayerTimesRepository
import com.deenislamic.sdk.utils.*
import com.deenislamic.sdk.viewmodels.PrayerTimesViewModel
import com.deenislamic.sdk.views.adapters.prayer_times.MuezzinAdapter
import com.deenislamic.sdk.views.adapters.prayer_times.PrayerTimesAdapter
import com.deenislamic.sdk.views.adapters.prayer_times.prayerTimeAdapterCallback
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


internal class PrayerTimesFragment : BaseRegularFragment(),
    otherFagmentActionCallback,
    prayerTimeAdapterCallback,
    ViewInflationListener,
    PrayerTimeNotification,
    RamadanCallback
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

    private var currentState = "dhaka"
    private var currentStateModel: StateModel? = null

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)

        NotificationPermission().getInstance().setupLauncher(this,localContext,true, activityContext = requireContext())

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
        val mainview = localInflater.inflate(R.layout.fragment_prayer_times,container,false)

        //init view
        prayerMain = mainview.findViewById(R.id.prayerMain)
        progressLayout = mainview.findViewById(R.id.progressLayout)
        no_internet_layout = mainview.findViewById(R.id.no_internet_layout)
        no_internet_retryBtn = no_internet_layout.findViewById(R.id.no_internet_retry)
        mainContainer =  mainview.findViewById(R.id.container)

        CallBackProvider.setFragment(this)
        setupActionForOtherFragment(R.drawable.ic_calendar,0,this@PrayerTimesFragment,localContext.getString(R.string.prayer_times),true,mainview)

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
            currentStateModel = it
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
        }

        no_internet_retryBtn.setOnClickListener {
            loadDataAPI()
        }

        initStateObserver()
        initObserver()


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

        if (isNotificationClicked) {
            NotificationPermission().getInstance().reCheckNotificationPermission(requireContext())

            Log.e("isNotificationPermitted",
                NotificationPermission().getInstance().isNotificationPermitted().toString()
            )
            if(NotificationPermission().getInstance().isNotificationPermitted()  && pryaerNotificationData.size>0) {
                updatePrayerNotificationDataOnly(pryaerNotificationData)
                isNotificationClicked = false
            }
        }

    }


    fun loadDataAPI()
    {
            loadingState()
            lifecycleScope.launch {
                viewmodel.getPrayerTimes(currentState, getLanguage(), prayerdate)
                viewmodel.getDateWisePrayerNotificationData(prayerdate)
            }
    }


    private fun initStateObserver() {
        viewmodel.selecteStateLiveData.observe(viewLifecycleOwner)
        {
            when (it) {
                is PrayerTimeResource.selectedState -> {
                    currentState = it.state.state
                    currentStateModel = it.state
                    prayerTimesAdapter.updateState(it.state)
                }
            }
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
                    prayerTimesResponse?.Data?.state = currentStateModel?.stateValue
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
                            requireContext().toast(localContext.getString(R.string.pt_notification_update_txt,notification_prayer_name))
                            notificationDialog.dismiss()
                        }
                        2-> requireContext().toast(localContext.getString(R.string.pt_notification_expire_txt,notification_prayer_name))
                        0 ->  requireContext().toast(localContext.getString(R.string.notification_setting_update_failed))

                    }
                }

                is PrayerNotificationResource.NotificationStateRequired ->
                {
                    forceUserToEnableNotification()
                }

                is PrayerNotificationResource.prayerTrackFailed ->  requireContext().toast(localContext.getString(R.string.failed_to_set_prayer_track))
                is PrayerNotificationResource.prayerTrackData ->
                {

                    Log.e("setPrayerTimeTrack",Gson().toJson(it.data))
                    DeenSDKCore.gpHomeCallback?.deenGPHomePrayerTrackListner(it.data)

                    if(prayerTrackLastWakt.isNotEmpty()) {


                        requireContext().toast(localContext.getString(
                            R.string.prayer_track_success_txt,
                            prayerTrackLastWakt.prayerMomentLocaleForToast()))
                        //requireContext().toast("আলহামদুলিল্লাহ আপনি ${prayerTrackLastWakt.prayerMomentLocaleForToast()} নামাজ আদায় করেছেন।")
                    }
                    updatePrayerTrackingView(it.data)
                }

                CommonResource.EMPTY -> Unit
            }
        }

    }

    private fun monthlyTrackerObserver(){

        viewmodel.monthlyPrayerTrackLiveData.observe(viewLifecycleOwner){
            when(it){
                is PrayerNotificationResource.prayerTrackData ->
                {
                    if(prayerdate == todayDate)
                    updatePrayerTrackingView(it.data)
                }
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
                    DeenSDKCore.prayerNotification(
                        isEnabled = true,
                        context = DeenSDKCore.baseContext!!,
                        callback = DeenSDKCore.GetDeenCallbackListner()!!)
                    requireContext().toast(localContext.getString(R.string.request_processing))

                }
                .setNegativeButton(localContext.getString(R.string.cancel), null)
                .show()
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
        clickMonthlyCalendar()
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
                requireContext().toast(localContext.getString(R.string.pt_notification_expire_txt,titile))
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

        if(NotificationPermission().isNotificationPermitted(requireContext())) {
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

                NotificationPermission().getInstance().showSettingDialog(localContext,
                    requireContext()
                )
            isNotificationClicked = true
        }
    }

    override fun clickMonthlyCalendar() {
        val bundle = Bundle()
        bundle.putString("location",currentState)
        gotoFrag(R.id.action_global_prayerCalendarFragment,bundle)
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

    override fun monthlyTrackerBtnClicked() {
        gotoFrag(R.id.action_global_prayerTrackerFragment)
    }

    override fun onAllViewsInflated() {
        progressLayout.visible(false)
        no_internet_layout.visible(false)
        prayerMain.post {
            currentStateModel?.let {
                prayerTimesAdapter.updateState(it)
            }

        }

        monthlyTrackerObserver()
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

    override fun stateSelected(stateModel: StateModel) {
        AppPreference.savePrayerTimeLoc(stateModel.state)
        lifecycleScope.launch {
            viewmodel.updateSelectedState(stateModel)
        }
        loadDataAPI()
    }

}

interface PrayerTimeNotification
{
    fun isSDKCoreNotificationEnable()
}