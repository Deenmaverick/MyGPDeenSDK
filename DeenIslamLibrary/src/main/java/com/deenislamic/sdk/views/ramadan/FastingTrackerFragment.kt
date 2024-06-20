package com.deenislamic.sdk.views.ramadan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.calendar.CustomCalendar
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.RamadanResource
import com.deenislamic.sdk.service.network.response.ramadan.FastTracker
import com.deenislamic.sdk.service.network.response.ramadan.calendar.Calander
import com.deenislamic.sdk.service.network.response.ramadan.calendar.Data
import com.deenislamic.sdk.service.repository.RamadanRepository
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.dayNameLocale
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.monthNameLocale
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.viewmodels.RamadanViewModel
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


internal class FastingTrackerFragment : BaseRegularFragment() {

    private lateinit var customCalendar: CustomCalendar
    private val navArgs:FastingTrackerFragmentArgs by navArgs()
    private lateinit var datetime:AppCompatTextView
    private lateinit var datetimeCard:AppCompatTextView
    private lateinit var arabicDatetime:AppCompatTextView
    private lateinit var arabicDatetimeCard:AppCompatTextView
    //private lateinit var fastingCheck:RadioButton
    private lateinit var fastingProgress:LinearProgressIndicator
    private lateinit var ramadan_complete_txt:AppCompatTextView
    private lateinit var leftBtn:AppCompatImageView
    private lateinit var rightBtn:AppCompatImageView
    private lateinit var yesBtn:MaterialButton
    private lateinit var noBtn:MaterialButton
    private lateinit var trackingCard:MaterialCardView

    private var isFasting = false
    private lateinit var viewmodel: RamadanViewModel
    private var fastTracker: FastTracker? = null

    private var monthlyCalanderData:ArrayList<Calander> ? = null

    private val calendarInstance: Calendar = Calendar.getInstance()

    private var ramadandate: String
        get() = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(calendarInstance.time)
        set(value) {
            calendarInstance.time = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).parse(value) ?: Date()
        }
    override fun OnCreate() {
        super.OnCreate()

        // init viewmodel
        val repository = RamadanRepository(
            deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = RamadanViewModel(repository)

        ramadandate = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_fasting_tracker,container,false)

        customCalendar = mainview.findViewById(R.id.calendar)
        datetime = mainview.findViewById(R.id.dateTime)
        datetimeCard = mainview.findViewById(R.id.datetimeCard)
        arabicDatetime = mainview.findViewById(R.id.dateTimeArabic)
        arabicDatetimeCard = mainview.findViewById(R.id.arabicDatetimeCard)
        // = mainview.findViewById(R.id.fastingCheck)
        fastingProgress = mainview.findViewById(R.id.fastingTask)
        ramadan_complete_txt = mainview.findViewById(R.id.ramadan_complete_txt)
        leftBtn = mainview.findViewById(R.id.leftBtn)
        rightBtn = mainview.findViewById(R.id.rightBtn)
        yesBtn = mainview.findViewById(R.id.yesBtn)
        noBtn = mainview.findViewById(R.id.noBtn)
        trackingCard = mainview.findViewById(R.id.trackingCard)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.fasting_tracker),
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackPressCallback(this)

     /*   if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else*/
            loadpage()


    }

    private fun loadpage()
    {
        fastTracker = navArgs.fastingCardData

        fastTracker?.let {
              datetimeCard.text = it.Date.numberLocale().monthNameLocale().dayNameLocale()
              arabicDatetimeCard.text = it.islamicDate
              //fastingCheck.isChecked = it.isFasting
              fastingProgress.max = it.totalDays
              fastingProgress.progress = it.totalTracked
              ramadan_complete_txt.text = "${it.totalTracked}/${it.totalDays}".numberLocale()

            updateFastingTrack(it.isFasting,true)
        }


        //isFasting = fastingCheck.isChecked


        /* fastingCheck.setOnClickListener {
             lifecycleScope.launch {
                 viewmodel.setRamadanTrack(!isFasting,getLanguage())
             }
         }*/

        leftBtn.setOnClickListener {
            calendarInstance.add(Calendar.MONTH, -1)
            loadApi()
        }

        rightBtn.setOnClickListener {
            calendarInstance.add(Calendar.MONTH, 1)
            loadApi()
        }

        yesBtn.setOnClickListener {
            if(!Subscription.isSubscribe){
                gotoFrag(R.id.action_global_subscriptionFragment)
                return@setOnClickListener
            }
            lifecycleScope.launch {
                viewmodel.setRamadanTrack(true,getLanguage())
            }
        }

        noBtn.setOnClickListener {
            if(!Subscription.isSubscribe){
                gotoFrag(R.id.action_global_subscriptionFragment)
                return@setOnClickListener
            }
            lifecycleScope.launch {
                viewmodel.setRamadanTrack(false,getLanguage())
            }
        }


        initObserver()
        loadApi()
    }

    private fun initObserver()
    {
        viewmodel.ramadanTrackLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is RamadanResource.ramadanTracking -> updateFastingTrack(it.isFasting)
            }
        }

        viewmodel.ramadanCalendarLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is RamadanResource.ramadanCalendar -> viewState(it.data)
            }
        }
    }

    private fun loadApi()
    {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getRamadanCalendar(ramadandate,getLanguage())
        }
    }

    override fun noInternetRetryClicked() {
        loadApi()
    }

    private fun viewState(data: Data)
    {
        monthlyCalanderData = ArrayList(data.calander)

        datetime.text = data.Month.numberLocale().monthNameLocale().dayNameLocale()
        arabicDatetime.text = "${data.islamicMonthStart} ${localContext.getString(R.string.from)} ${data.islamicMonthEnd}"

        val activeDaysArray = getActiveDays(data.calander)
        val inactiveDaysArray = getInactiveDays(data.calander)

        customCalendar.setActiveDays(activeDaysArray)
        customCalendar.setInactiveDays(inactiveDaysArray)
        customCalendar.setMonth(ramadandate)

        val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date())
        if(currentDate != ramadandate)
        trackingCard.hide()
        else
            trackingCard.show()

        baseViewState()

    }

    private fun updateFastingTrack(fasting: Boolean,firstload:Boolean = false)
    {
        fastTracker?.let {
            if(fasting) {
                if(!firstload)
                it.totalTracked++
                yesBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                yesBtn.icon = ContextCompat.getDrawable(requireContext(),R.drawable.deen_ic_checkbox_oval)
                noBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))
                noBtn.icon = ContextCompat.getDrawable(requireContext(),R.drawable.radio_btn_unselected)
            }
            else {
                if(!firstload)
                it.totalTracked--
                yesBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))
                yesBtn.icon = ContextCompat.getDrawable(requireContext(),R.drawable.radio_btn_unselected)
                noBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_brand_error))
                noBtn.icon = ContextCompat.getDrawable(requireContext(),R.drawable.deen_ic_checkbox_oval_red)
            }


            fastingProgress.progress = it.totalTracked
            ramadan_complete_txt.text = "${it.totalTracked}/${it.totalDays}".numberLocale()
            //fastingCheck.isChecked = fasting
            isFasting = fasting

        }

        monthlyCalanderData?.let { it ->
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())

            val updateIndex = it.indexOfFirst { it.TrackingDate.contains(currentDate) }

            it[updateIndex].isTracked = if(fasting) 1 else 0
            val activeDaysArray = getActiveDays(it)
            val inactiveDaysArray = getInactiveDays(it)

            monthlyCalanderData = it

            Log.e("monthlyCalanderData",Gson().toJson(activeDaysArray)+Gson().toJson(inactiveDaysArray))

            customCalendar.setActiveDays(activeDaysArray)
            customCalendar.setInactiveDays(inactiveDaysArray)
            customCalendar.updateCalendarData()
        }

    }

    private fun getActiveDays(trackingList: List<Calander>): Array<Int> {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)

        return trackingList.filter { it.isTracked == 1 }.map {
            calendar.time = format.parse(it.TrackingDate) as Date
            calendar.get(Calendar.DAY_OF_MONTH)
        }.toTypedArray()
    }

    private fun getInactiveDays(trackingList: List<Calander>): Array<Int> {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)

        return trackingList.filter { it.isTracked == 0 }.map {
            calendar.time = format.parse(it.TrackingDate) as Date
            calendar.get(Calendar.DAY_OF_MONTH)
        }.toTypedArray()
    }

    fun getMonthFromRamadanDate(): Int {
        return calendarInstance.get(Calendar.MONTH) + 1  // Adding 1 to get a 1-indexed month value
    }


}