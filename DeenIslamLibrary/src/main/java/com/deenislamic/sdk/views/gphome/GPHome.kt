package com.deenislamic.sdk.views.gphome

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import android.widget.ProgressBar
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.GPHomeResource
import com.deenislamic.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislamic.sdk.service.network.response.gphome.Data
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.service.network.response.prayertimes.WaktTracker
import com.deenislamic.sdk.service.repository.GPHomeRespository
import com.deenislamic.sdk.utils.GridSpacingItemDecoration
import com.deenislamic.sdk.utils.LocaleUtil
import com.deenislamic.sdk.utils.MilliSecondToStringTime
import com.deenislamic.sdk.utils.StringTimeToMillisecond
import com.deenislamic.sdk.utils.TimeDiffForPrayer
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.getPrayerTimeName
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.isTimeInRange
import com.deenislamic.sdk.utils.monthShortNameLocale
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.prayerMomentLocale
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.stringTimeToEpochTime
import com.deenislamic.sdk.utils.timeLocale
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.GPHomeViewModel
import com.deenislamic.sdk.views.adapters.common.gridmenu.MenuCallback
import com.deenislamic.sdk.views.adapters.gphome.GPHomeMenuAdapter
import com.google.android.material.button.MaterialButton
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
    private var menu:RecyclerView ? = null
    private  var mainView: ConstraintLayout? = null
    private var monthShort:AppCompatTextView ? = null
    private var day:AppCompatTextView ? = null
    private var dateArabic:AppCompatTextView ? = null
    private var islamicEvent:AppCompatTextView ? = null
    private var prayerName:AppCompatTextView ? = null
    private var startTime:AppCompatTextView ? = null
    private var endTime:AppCompatTextView ? = null
    private var iftarSehri:AppCompatTextView ? = null
    private var nextPrayerName:AppCompatTextView ? = null
    private var nextPrayerTime:AppCompatTextView ? = null
    private var progressLy:ConstraintLayout ? = null

    private var fajrBtn:MaterialButton ? = null
    private var duhurBtn:MaterialButton ? = null
    private var asrBtn:MaterialButton ? = null
    private var maghribBtn:MaterialButton ? = null
    private var ishaBtn:MaterialButton ? = null
    private var progressBar: ProgressBar? = null
    private var progresstxt:AppCompatTextView ? = null
    private var countDownTimer: CountDownTimer?=null

    // Common view ( loading,no internet etc)
    private  var progressLayout: CircularProgressIndicator? = null
    private  var noInternetLayout: NestedScrollView? =null
    private  var noInternetRetry: MaterialButton? = null



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
        monthShort = findViewById(R.id.monthShort)
        day = findViewById(R.id.day)
        dateArabic = findViewById(R.id.dateArabic)
        islamicEvent = findViewById(R.id.islamicEvent)
        prayerName = findViewById(R.id.prayerName)
        startTime = findViewById(R.id.startTime)
        endTime = findViewById(R.id.endTime)
        iftarSehri = findViewById(R.id.iftarSehri)
        nextPrayerName = findViewById(R.id.nextPrayerName)
        nextPrayerTime = findViewById(R.id.nextPrayerTime)
        fajrBtn = findViewById(R.id.fajrBtn)
        duhurBtn = findViewById(R.id.duhurBtn)
        asrBtn = findViewById(R.id.asrBtn)
        maghribBtn = findViewById(R.id.maghribBtn)
        ishaBtn = findViewById(R.id.ishaBtn)
        progressBar = findViewById(R.id.progressBar)
        progresstxt = findViewById(R.id.progresstxt)
        progressLy = findViewById(R.id.progressLy)

        noInternetLayout = findViewById(R.id.no_internet_layout)
        noInternetRetry = findViewById(R.id.no_internet_retry)
        progressLayout = findViewById(R.id.progressLayout)

        //inital part
        /*fajrBtn?.setOnClickListener {
            updatePrayerTracker(prayertimeData?.WaktTracker)
        }*/

        setupCommonLayout()

        // Set up observer when the view is attached to the window
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                initObserver()
            }
        })

    }

    private fun loadpage(data: Data) {



        mainView?.show()

        // date card
        val calendar = Calendar.getInstance()
        val dateFormatMonth = SimpleDateFormat("MMM", Locale.ENGLISH)
        val dateFormatDay = SimpleDateFormat("d", Locale.ENGLISH)

        val monthName = dateFormatMonth.format(calendar.time)
        val dayOfMonth = dateFormatDay.format(calendar.time)

        day?.text = dayOfMonth.numberLocale()
        monthShort?.text = monthName.monthShortNameLocale()

        dateArabic?.text = data.IslamicDate
        islamicEvent?.text = data.IslamicEvent

        if(data.IslamicEvent.isEmpty())
            islamicEvent?.hide()

        prayertimeData = data.PrayerTime
        prayerTime()

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


           prayerTracker(data.WaktTracker)

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

        val progress = waktTracker.filter { it.status }.size * 20

        if(progressBar?.progress != progress) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar?.setProgress(progress, true)
            }else
                progressBar?.progress = progress

            progressLy?.let {
                progresstxt?.text = "$progress%".numberLocale()
                val constraintSet = ConstraintSet()
                constraintSet.clone(it)
                constraintSet.setHorizontalBias(R.id.progresstxt, (0.3).toFloat())
                constraintSet.applyTo(it)
            }

        }

        waktTracker.forEach {
            when(it.Wakt)
            {
                "Fajr" -> updatePrayerTracker(it.status,fajrBtn)
                "Zuhr" -> updatePrayerTracker(it.status,duhurBtn)
                "Asar" -> updatePrayerTracker(it.status,asrBtn)
                "Maghrib" -> updatePrayerTracker(it.status,maghribBtn)
                "Isha" -> updatePrayerTracker(it.status,ishaBtn)
            }
        }
    }

    private fun updatePrayerTracker(status: Boolean, button: MaterialButton?) {

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
    }


    private fun initObserver(){
        findViewTreeLifecycleOwner()?.let {

            viewmodel?.gphomeLiveData?.observe(it){ gpresource ->
            when(gpresource){
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is GPHomeResource.GPHome -> {
                    loadpage(gpresource.data)
                }
                }
            }

            viewmodel?.gphomePrayerLiveData?.observe(it){ gpresource ->
                when(gpresource){
                    is CommonResource.API_CALL_FAILED -> Unit
                    is GPHomeResource.GPHomePrayerTrack -> {
                        prayertimeData?.WaktTracker?.first { data-> data.Wakt == gpresource.prayer_tag }?.status = gpresource.status
                        prayertimeData?.WaktTracker?.let { it1 -> prayerTracker(it1) }
                    }
                }
            }

        }
    }

     fun loadapi(){
        baseLoadingState()
        CoroutineScope(Dispatchers.IO).launch {
            viewmodel?.getGPHome("dhaka")
        }
    }

    override fun menuClicked(pagetag: String) {

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
}
