package com.deenislamic.sdk.views.gphome

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.RadioButton
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.database.AppPreference
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.GPHomeResource
import com.deenislamic.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislamic.sdk.service.models.ramadan.StateModel
import com.deenislamic.sdk.service.network.response.gphome.Data
import com.deenislamic.sdk.service.network.response.gphome.Menu
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.service.network.response.prayertimes.WaktTracker
import com.deenislamic.sdk.service.repository.GPHomeRespository
import com.deenislamic.sdk.utils.GridSpacingItemDecoration
import com.deenislamic.sdk.utils.LocaleUtil
import com.deenislamic.sdk.utils.MilliSecondToStringTime
import com.deenislamic.sdk.utils.StateArrayForGPHome
import com.deenislamic.sdk.utils.StringTimeToMillisecond
import com.deenislamic.sdk.utils.TimeDiffForPrayer
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.epochTimeToStringTime
import com.deenislamic.sdk.utils.formateDateTime
import com.deenislamic.sdk.utils.getDrawable
import com.deenislamic.sdk.utils.getPrayerTimeName
import com.deenislamic.sdk.utils.getPrayerTimeWaktWise
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.isTimeInRange
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.prayerMomentLocale
import com.deenislamic.sdk.utils.prayerMomentLocaleForToast
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.stringTimeToEpochTime
import com.deenislamic.sdk.utils.timeLocale
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.GPHomeViewModel
import com.deenislamic.sdk.views.adapters.common.gridmenu.MenuCallback
import com.deenislamic.sdk.views.adapters.gphome.GPHomeMenuAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GPHome @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),MenuCallback {

    private var viewmodel: GPHomeViewModel ? = null

    private var localContext:Context ? = null
    private var localInflater: LayoutInflater ? = null
    private var prayertimeData:com.deenislamic.sdk.service.network.response.prayertimes.Data ? = null

    private lateinit var gpHomeMenuAdapter: GPHomeMenuAdapter
    private lateinit var gpHomeDialogMenuAdapter:GPHomeMenuAdapter
    private var menu:RecyclerView ? = null
    private  var mainView: ConstraintLayout? = null
    private var dateArabic:AppCompatTextView ? = null
    private var prayerName:AppCompatTextView ? = null
    private var startTime:AppCompatTextView ? = null
    private var endTime:AppCompatTextView ? = null
    private var iftarSehri:AppCompatTextView ? = null
    private var nextPrayerName:AppCompatTextView ? = null
    private var nextPrayerTime:AppCompatTextView ? = null
    private var location:MaterialButton ? = null
    private var prayerTimeBtn:AppCompatTextView ? = null
    private var bgPrayer:AppCompatImageView ? = null
    private var icLogo:AppCompatImageView ? = null
    private var prayerTimeLy:ConstraintLayout ? = null
    private var nowTv:AppCompatTextView ? = null

    private var fajrCard:MaterialCardView ? = null
    private var fajrCheckbox: RadioButton? = null
    private var fajrWaktTxt:AppCompatTextView ? = null
    private var fajrWaktTime:AppCompatTextView ? = null

    private var dhuhrCard:MaterialCardView ? = null
    private var dhuhrCheckbox: RadioButton? = null
    private var dhuhrWaktTxt:AppCompatTextView ? = null
    private var dhuhrWaktTime:AppCompatTextView ? = null

    private var asrCard:MaterialCardView ? = null
    private var asrCheckbox: RadioButton? = null
    private var asrWaktTxt:AppCompatTextView ? = null
    private var asrWaktTime:AppCompatTextView ? = null

    private var maghribCard:MaterialCardView ? = null
    private var maghribCheckbox: RadioButton? = null
    private var maghribWaktTxt:AppCompatTextView ? = null
    private var maghribWaktTime:AppCompatTextView ? = null

    private var ishaCard:MaterialCardView ? = null
    private var ishaCheckbox: RadioButton? = null
    private var ishaWaktTxt:AppCompatTextView ? = null
    private var ishaWaktTime:AppCompatTextView ? = null

    private var countDownTimer: CountDownTimer?=null
    private var currentState = "dhaka"
    private var currentStateModel: StateModel? = null
    // Common view ( loading,no internet etc)
    private  var progressLayout: CircularProgressIndicator? = null
    private  var noInternetLayout: NestedScrollView? =null
    private  var noInternetRetry: MaterialButton? = null

    private var dialog:BottomSheetDialog ? = null


    init {


        // init viewmodel
        val repository = GPHomeRespository(
            authenticateService = NetworkProvider().getInstance().provideAuthService(),
            deenService = NetworkProvider().getInstance().provideDeenService()
        )

        viewmodel = GPHomeViewModel(repository = repository)

        localContext = LocaleUtil.createLocaleContext(context, Locale(DeenSDKCore.GetDeenLanguage()))

        val themedContext = ContextThemeWrapper(localContext, R.style.DeenSDKTheme) // Replace with your theme

        localInflater = LayoutInflater.from(themedContext)


        // Inflate the layout
        localInflater?.inflate(R.layout.deen_layout_gphome, this, true)


        // init view
        menu = findViewById(R.id.menu)
        mainView = findViewById(R.id.mainView)
        dateArabic = findViewById(R.id.dateArabic)
        prayerName = findViewById(R.id.prayerName)
        startTime = findViewById(R.id.startTime)
        endTime = findViewById(R.id.endTime)
        iftarSehri = findViewById(R.id.iftarSehri)
        nextPrayerName = findViewById(R.id.nextPrayerName)
        nextPrayerTime = findViewById(R.id.nextPrayerTime)
        prayerTimeBtn = findViewById(R.id.prayerTimeBtn)
        bgPrayer = findViewById(R.id.bgPrayer)
        icLogo = findViewById(R.id.icLogo)
        prayerTimeLy = findViewById(R.id.prayerTimeLy)
        nowTv = findViewById(R.id.nowTv)

        fajrCard = findViewById(R.id.fajrCard)
        fajrCheckbox = fajrCard?.findViewById(R.id.prayerCheck)
        fajrWaktTxt = fajrCard?.findViewById(R.id.waktTxt)
        fajrWaktTxt?.text = localContext?.getString(R.string.prayer_fajr)
        fajrWaktTime = fajrCard?.findViewById(R.id.waktTimeTxt)

        dhuhrCard = findViewById(R.id.dhuhrCard)
        dhuhrCheckbox = dhuhrCard?.findViewById(R.id.prayerCheck)
        dhuhrWaktTxt = dhuhrCard?.findViewById(R.id.waktTxt)
        dhuhrWaktTxt?.text = localContext?.getString(R.string.prayer_dhuhr)
        dhuhrWaktTime = dhuhrCard?.findViewById(R.id.waktTimeTxt)

        asrCard = findViewById(R.id.asrCard)

        asrCheckbox = asrCard?.findViewById(R.id.prayerCheck)
        asrWaktTxt = asrCard?.findViewById(R.id.waktTxt)
        asrWaktTxt?.text = localContext?.getString(R.string.prayer_asr)
        asrWaktTime = asrCard?.findViewById(R.id.waktTimeTxt)

        maghribCard = findViewById(R.id.maghribCard)
        maghribCheckbox = maghribCard?.findViewById(R.id.prayerCheck)
        maghribWaktTxt = maghribCard?.findViewById(R.id.waktTxt)
        maghribWaktTxt?.text = localContext?.getString(R.string.prayer_maghrib)
        maghribWaktTime = maghribCard?.findViewById(R.id.waktTimeTxt)

        ishaCard = findViewById(R.id.ishaCard)
        ishaCheckbox = ishaCard?.findViewById(R.id.prayerCheck)
        ishaWaktTxt = ishaCard?.findViewById(R.id.waktTxt)
        ishaWaktTxt?.text = localContext?.getString(R.string.prayer_isha)
        ishaWaktTime = ishaCard?.findViewById(R.id.waktTimeTxt)


        location = findViewById(R.id.location)

        noInternetLayout = findViewById(R.id.no_internet_layout)
        noInternetRetry = findViewById(R.id.no_internet_retry)
        progressLayout = findViewById(R.id.progressLayout)

        //inital part
        fajrCard?.setOnClickListener {
            trackPrayerWakt("Fajr")
        }

        dhuhrCard?.setOnClickListener {
            trackPrayerWakt("Zuhr")
        }

        asrCard?.setOnClickListener {
            trackPrayerWakt("Asar")
        }

        maghribCard?.setOnClickListener {
            trackPrayerWakt("Maghrib")
        }

        ishaCard?.setOnClickListener {
            trackPrayerWakt("Isha")
        }

        prayerTimeBtn?.setOnClickListener {
            DeenSDKCore.openFromRC("prayer_time")
        }

        location?.setOnClickListener {
            showLocationBottomSheetDialog()
        }


        setupCommonLayout()

        // Set up observer when the view is attached to the window
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                initObserver()
            }
        })

    }

    private fun getWaktStatus(wakt:String):Boolean {

       return prayertimeData?.WaktTracker?.firstOrNull { data-> data.Wakt == wakt }?.status?:false

    }

    private fun trackPrayerWakt(wakt:String){

       val date = prayertimeData?.Date?.formateDateTime("yyyy-MM-dd'T'HH:mm:ss","dd/MM/yyyy")

        val notifyTime = prayertimeData?.let {
            if (date != null) {
                getPrayerTimeWaktWise(wakt, date, it)
            }else 0L
        }

        notifyTime?.let {

            if(notifyTime >= 0L) {
                DeenSDKCore.baseContext?.apply {
                    localContext?.getString(R.string.prayer_time_not_start,wakt.prayerMomentLocaleForToast())
                        ?.let { it1 -> toast(it1) }
                }
                return
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            viewmodel?.setPrayerTrack(DeenSDKCore.GetDeenLanguage(),wakt,!getWaktStatus(wakt))
        }
    }

    private fun loadpage(data: Data) {

        mainView?.show()

        icLogo?.imageLoad(url = "gpSDKIcon.png".getDrawable(), placeholder_1_1 = true)

        // prayer time

        fajrWaktTime?.text = data.PrayerTime.Fajr.stringTimeToEpochTime().epochTimeToStringTime("h:mm a")
        dhuhrWaktTime?.text = data.PrayerTime.Juhr.stringTimeToEpochTime().epochTimeToStringTime("h:mm a")
        asrWaktTime?.text = data.PrayerTime.Asr.stringTimeToEpochTime().epochTimeToStringTime("h:mm a")
        maghribWaktTime?.text = data.PrayerTime.Magrib.stringTimeToEpochTime().epochTimeToStringTime("h:mm a")
        ishaWaktTime?.text = data.PrayerTime.Isha.stringTimeToEpochTime().epochTimeToStringTime("h:mm a")

        // date card

        dateArabic?.text ="${data.IslamicDate} "

        prayertimeData = data.PrayerTime
        prayerTime()
        prayertimeData?.WaktTracker?.let { prayerTracker(it) }

        if(data.Menu.isNotEmpty()) {
            menu?.let {
                it.apply {
                    if(!this@GPHome::gpHomeMenuAdapter.isInitialized)
                        gpHomeMenuAdapter = GPHomeMenuAdapter(data.Menu,this@GPHome)
                    layoutManager = GridLayoutManager(context,4)
                    addItemDecoration(GridSpacingItemDecoration(4, 1.dp, false))

                    adapter = gpHomeMenuAdapter
                }
            }
        }

        baseViewState()
    }

    private fun prayerTime() {

        prayertimeData?.let { data ->


            var currentTime = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Date())
            val prayerMomentRangeData: PrayerMomentRange =
                getPrayerTimeName(
                    PrayerTimesResponse(Data = data, "", true, 0),
                    currentTime.stringTimeToEpochTime()
                )

            prayerName?.text = prayerMomentRangeData.MomentName.prayerMomentLocale()
            startTime?.text = prayerMomentRangeData.StartTime.timeLocale()
            endTime?.text = prayerMomentRangeData.EndTime.timeLocale()

            val sehriTime = data.Sehri.stringTimeToEpochTime()
            val iftarTime = data.Magrib.stringTimeToEpochTime()

            iftarSehri?.text = if(isTimeInRange(currentTime.stringTimeToEpochTime(), sehriTime,iftarTime)) {
                localContext?.getString(
                    R.string.iftarat,
                    data.Magrib.StringTimeToMillisecond()
                        .MilliSecondToStringTime("hh:mm aa")
                        .numberLocale()
                )
            }else{
                localContext?.getString(
                    R.string.sehriat,
                    data.Sehri.StringTimeToMillisecond()
                        .MilliSecondToStringTime("hh:mm aa")
                        .numberLocale()
                )
            }

            prayerTimeLy?.show()
            nowTv?.show()
            prayerName?.show()

            when(prayerMomentRangeData.MomentName) {
                "Fajr" -> {
                    bgPrayer?.imageLoad(url = "fajr.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                    prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_prayer_fajr))
                }
                "Dhuhr" -> {
                    bgPrayer?.imageLoad(url = "dhuhr.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                    prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_prayer_dhuhr))
                }
                "Asr" -> {
                    bgPrayer?.imageLoad(url = "asr.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                    prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_prayer_asr))
                }
                "Maghrib" -> {
                    bgPrayer?.imageLoad(url = "maghrib.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                    prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_prayer_maghrib))
                }
                "Isha" -> {
                    bgPrayer?.imageLoad(url = "isha.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                    prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_prayer_isha))
                }
                "Ishraq" -> {
                    bgPrayer?.imageLoad(url = "ishrak.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                    prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_prayer_isharaq))
                }
                "Chasht" -> {
                    bgPrayer?.imageLoad(url = "chasht.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                    prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_primary))
                }
                localContext?.getString(R.string.forbidden_time) -> {
                    bgPrayer?.imageLoad(url = "forbidden.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                }
                else -> {

                    prayerTimeLy?.hide()
                    nowTv?.hide()
                    prayerName?.hide()
                    /*timefor.hide()
                    prayerMoment.setPadding(0,8.dp,0,0)
                    prayerMoment.hide()
                    prayerMomentRange.hide()*/

                    when(prayerMomentRangeData.NextPrayerName){
                        "Fajr" -> {
                            bgPrayer?.imageLoad(url = "fajr.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                            prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_prayer_fajr))
                        }
                        "Dhuhr" -> {
                            bgPrayer?.imageLoad(url = "dhuhr.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                            prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_prayer_dhuhr))
                        }
                        "Asr" -> {
                            bgPrayer?.imageLoad(url = "asr.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                            prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_prayer_asr))
                        }
                        "Maghrib" -> {
                            bgPrayer?.imageLoad(url = "maghrib.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                            prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_prayer_maghrib))
                        }
                        "Isha" -> {
                            bgPrayer?.imageLoad(url = "isha.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                            prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_prayer_isha))
                        }
                        "Ishraq" -> {
                            bgPrayer?.imageLoad(url = "ishrak.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                            prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_prayer_isharaq))
                        }
                        "Chasht" -> {
                            bgPrayer?.imageLoad(url = "chasht.webp".getDrawable(), custom_placeholder_1_1 = R.drawable.bg_deen_gp_prayer_placeholder)
                            prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_primary))
                        }
                        else -> {
                            bgPrayer?.setBackgroundResource(R.drawable.bg_deen_gp_prayer_placeholder)
                            prayerName?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_prayer_fajr))

                        }

                    }
                }
            }


            nextPrayerName?.text = "${prayerMomentRangeData.NextPrayerName.prayerMomentLocale()} • "


            nextPrayerTime?.text = "-00:00:00".timeLocale()
            prayerMomentRangeData.nextPrayerTimeCount.let {
                if (it > 0) {
                    nextPrayerTime?.text =
                        prayerMomentRangeData.nextPrayerTimeCount.TimeDiffForPrayer().numberLocale()
                    countDownTimer?.cancel()
                    countDownTimer = object : CountDownTimer(it, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            nextPrayerTime?.text =
                                millisUntilFinished.TimeDiffForPrayer().numberLocale()
                            currentTime =
                                SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Date())
                            val nowtime = currentTime.stringTimeToEpochTime()

                            if (prayerMomentRangeData.MomentName != context?.getString(R.string.forbidden_time) &&
                                prayerMomentRangeData.MomentName != "Chasht"
                            ) {
                                if (prayerMomentRangeData.forbidden1.invoke(nowtime)) {
                                    countDownTimer?.cancel()
                                    prayerTime()
                                } else if (prayerMomentRangeData.forbidden2.invoke(nowtime)) {
                                    countDownTimer?.cancel()
                                    prayerTime()
                                } else if (prayerMomentRangeData.forbidden3.invoke(nowtime)) {
                                    countDownTimer?.cancel()
                                    prayerTime()
                                } else if (prayerMomentRangeData.chasht.invoke(nowtime)) {
                                    countDownTimer?.cancel()
                                    prayerTime()
                                }
                            }
                        }

                        override fun onFinish() {
                            countDownTimer?.cancel()
                            prayerTime()
                        }
                    }
                    countDownTimer?.start()
                }

            }

        }
    }

    private fun prayerTracker(waktTracker: List<WaktTracker>) {

        /*val progress = waktTracker.filter { it.status }.size * 20

        if(progressBar?.progress != progress) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar?.setProgress(progress, true)
            }else
                progressBar?.progress = progress

            progressLy?.let {
                progresstxt?.text = "$progress%".numberLocale()
                val constraintSet = ConstraintSet()
                constraintSet.clone(it)
                constraintSet.setHorizontalBias(R.id.progresstxt,  ((progress / 100.0)-0.1).toFloat())
                constraintSet.applyTo(it)
            }

        }*/

        waktTracker.forEach {
            when(it.Wakt)
            {
                "Fajr" -> fajrCheckbox?.isChecked = it.status
                "Zuhr" -> dhuhrCheckbox?.isChecked = it.status
                "Asar" -> asrCheckbox?.isChecked = it.status
                "Maghrib" -> maghribCheckbox?.isChecked = it.status
                "Isha" -> ishaCheckbox?.isChecked = it.status
            }
        }

    }

    /*private fun updatePrayerTracker(status: Boolean, button: RadioButton?) {

        val primaryColor = ContextCompat.getColor(context,R.color.deen_gp_primary)
        val secondaryColor = ContextCompat.getColor(context,R.color.deen_gp_txt_gray_secondary)

        if(status){
            button?.setTextColor(primaryColor)
            button?.icon = ContextCompat.getDrawable(context,R.drawable.ic_deen_gp_check)
            button?.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_END
            button?.iconTint = ColorStateList.valueOf(primaryColor)
        }else{
            button?.setTextColor(ContextCompat.getColor(context,R.color.deen_gp_txt_gray_secondary))
            button?.icon = ContextCompat.getDrawable(context,R.drawable.ic_deen_gp_time_off)
            button?.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            button?.iconTint = ColorStateList.valueOf(secondaryColor)
        }
    }*/


    private fun initObserver(){
        findViewTreeLifecycleOwner()?.let { lifecyleowner ->

            viewmodel?.gphomeLiveData?.observe(lifecyleowner){
            when(it){
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is GPHomeResource.GPHome -> {
                    loadpage(it.data)
                }
                }
            }

            viewmodel?.gphomePrayerLiveData?.observe(lifecyleowner){
                when(it){
                    is CommonResource.API_CALL_FAILED -> Unit
                    is GPHomeResource.GPHomePrayerTrack -> {
                        prayertimeData?.WaktTracker?.firstOrNull { data-> data.Wakt == it.prayer_tag }?.status = it.status
                        prayertimeData?.WaktTracker?.let { it1 -> prayerTracker(it1) }
                    }
                }
            }

        }
    }

     fun loadapi(){

         currentState = AppPreference.getPrayerTimeLoc().toString()

         val filteredStates = StateArrayForGPHome.firstOrNull { state ->
             currentState.lowercase().contains(state.state.lowercase()) ||
                     currentState.lowercase().contains(state.statebn.lowercase())
         }

         filteredStates?.let {
             currentState = it.state
             currentStateModel = it
         }

         currentStateModel?.let {
             location?.text = if(DeenSDKCore.GetDeenLanguage() == "bn") it.statebn else it.state
         }

        baseLoadingState()
        CoroutineScope(Dispatchers.IO).launch {
            viewmodel?.getGPHome(currentState)
        }
    }



    private fun setupCommonLayout() {


        progressLayout?.let {
            ViewCompat.setTranslationZ(it, 10F)
        }

        noInternetLayout?.let {
            ViewCompat.setTranslationZ(it, 10F)
        }

        noInternetRetry?.setOnClickListener {
            loadapi()
        }

    }

    private fun baseLoadingState() {
        mainView?.hide()
        progressLayout?.visible(true)
        noInternetLayout?.visible(false)
    }


    private fun baseNoInternetState() {
        mainView?.hide()
        progressLayout?.hide()
        noInternetLayout?.show()
    }

    private fun baseViewState() {
        mainView?.show()
        progressLayout?.hide()
        noInternetLayout?.hide()
    }

     override fun showMenuBottomSheetDialog(menu: List<Menu>) {

         dialog?.dismiss()

        dialog = DeenSDKCore.baseContext?.let { BottomSheetDialog(it) }

        val view = localInflater?.inflate(R.layout.dialog_deen_gp_menu, null)

        val icClose:AppCompatImageView? = view?.findViewById(R.id.icClose)
        val menuList:RecyclerView? = view?.findViewById(R.id.menuList)

        icClose?.setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.setCancelable(true)

        if (view != null) {
            dialog?.setContentView(view)
        }

         menuList?.apply {
             if(!this@GPHome::gpHomeDialogMenuAdapter.isInitialized)
                 gpHomeDialogMenuAdapter = GPHomeMenuAdapter(menu,this@GPHome,true)
             adapter = gpHomeDialogMenuAdapter
             layoutManager = GridLayoutManager(context,4)
         }

        dialog?.setOnShowListener {
            val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                val displayMetrics = context.resources.displayMetrics
                val height = (displayMetrics.heightPixels * 0.5).toInt() // 50% of screen height

                sheet.layoutParams.height = height
                behavior.peekHeight = height
                sheet.requestLayout()
            }
        }

        dialog?.show()
    }


    private fun showLocationBottomSheetDialog() {

        dialog?.dismiss()

        dialog = DeenSDKCore.baseContext?.let { BottomSheetDialog(it) }

        val view = localInflater?.inflate(R.layout.dialog_deen_gp_location, null)

        val icClose:AppCompatImageView? = view?.findViewById(R.id.icClose)
        val menuList:RecyclerView? = view?.findViewById(R.id.menuList)

        icClose?.setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.setCancelable(true)

        if (view != null) {
            dialog?.setContentView(view)
        }

       /* menuList?.apply {
            if(!this@GPHome::gpHomeDialogMenuAdapter.isInitialized)
                gpHomeDialogMenuAdapter = GPHomeMenuAdapter(menu,this@GPHome,true)
            adapter = gpHomeDialogMenuAdapter
            layoutManager = GridLayoutManager(context,4)
        }*/

        dialog?.setOnShowListener {
            val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                val displayMetrics = context.resources.displayMetrics
                val height = (displayMetrics.heightPixels * 0.5).toInt() // 50% of screen height

                sheet.layoutParams.height = height
                behavior.peekHeight = height
                sheet.requestLayout()
            }
        }

        dialog?.show()
    }



    override fun menuClicked(pagetag: String) {
        DeenSDKCore.openFromRC(pagetag)
    }
}
