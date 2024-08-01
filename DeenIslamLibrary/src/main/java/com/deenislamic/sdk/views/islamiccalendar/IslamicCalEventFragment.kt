package com.deenislamic.sdk.views.islamiccalendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.IslamicCalEventCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.hijricalendar.HijriCustomCalendar
import com.deenislamic.sdk.service.models.IslamicCalEventResource
import com.deenislamic.sdk.service.network.response.islamiccalendar.IslamicEvent
import com.deenislamic.sdk.service.repository.IslamicCalendarEventRepository
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CalendarIntentHelper
import com.deenislamic.sdk.utils.MENU_ISLAMIC_EVENT
import com.deenislamic.sdk.utils.get9DigitRandom
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.monthNameLocale
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.viewmodels.IslamicCalEventViewModel
import com.deenislamic.sdk.views.adapters.islamiccalendar.IslamicCalEventAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


internal class IslamicCalEventFragment : BaseRegularFragment(), IslamicCalEventCallback {

    private lateinit var customCalendar: HijriCustomCalendar
    private lateinit var calendarIntentHelper: CalendarIntentHelper
    private lateinit var viewmodel: IslamicCalEventViewModel
    private val calendarEventList = ArrayList<IslamicEvent>()
    private lateinit var monthYearText: TextView
    private lateinit var hijriMonthYearText: TextView
//    private lateinit var layoutShow: LinearLayoutCompat
//    private lateinit var layoutHide: LinearLayoutCompat
    private lateinit var leftNavigate: AppCompatImageView
    private lateinit var rightNavigate: AppCompatImageView
    private lateinit var recyclerView:RecyclerView
    private lateinit var imvUpcoming: AppCompatImageView
    private lateinit var tvUpcomingEventTitle: AppCompatTextView
    private lateinit var tvUpcomingEventLeft: AppCompatTextView
    private lateinit var adapter: IslamicCalEventAdapter
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var monthYearSelector: ConstraintLayout
    private lateinit var layoutCalendar: ConstraintLayout
    private lateinit var progressLayout: LinearLayout
    private lateinit var upcomingBanner: RelativeLayout

    private var currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)
        // init viewmodel
        val repository = IslamicCalendarEventRepository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = IslamicCalEventViewModel(repository)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_islamic_calendar_s,container,false)

        customCalendar = mainview.findViewById(R.id.customCalendar)
        monthYearText = mainview.findViewById(R.id.monthYearText)
        hijriMonthYearText = mainview.findViewById(R.id.hijriMonthYearText)
/*        layoutShow = mainview.findViewById(R.id.layoutShow)
        layoutHide = mainview.findViewById(R.id.layoutHide)*/
        leftNavigate = mainview.findViewById(R.id.imvCalendarLeftNavigate)
        rightNavigate = mainview.findViewById(R.id.imvCalendarRightNavigate)
        recyclerView = mainview.findViewById(R.id.recyclerIslamicCalendarEvents)
        imvUpcoming = mainview.findViewById(R.id.imvUpcomingEvent)
        tvUpcomingEventTitle = mainview.findViewById(R.id.tvUpComingEventTitle)
        tvUpcomingEventLeft = mainview.findViewById(R.id.tvUpcomingEventLeft)
        nestedScrollView = mainview.findViewById(R.id.nestedScrollView)
        monthYearSelector = mainview.findViewById(R.id.monthYearSelector)
        layoutCalendar = mainview.findViewById(R.id.customCalendarLayout)
        upcomingBanner = mainview.findViewById(R.id.upcomingBanner)
        progressLayout = mainview.findViewById(R.id.progressLayout)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.islamic_calendar),
            backEnable = true,
            view = mainview
        )

        adapter = IslamicCalEventAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        calendarIntentHelper = CalendarIntentHelper(requireActivity())

/*
        layoutShow.setOnClickListener {
            customCalendar.toggleCalendarView(showFullMonth = true)
            showCalendar()
        }

        layoutHide.setOnClickListener {
            customCalendar.toggleCalendarView(showFullMonth = false)
            hideCalendar()
        }*/

        leftNavigate.setOnClickListener {
            navigateToPreviousMonth()
        }

        rightNavigate.setOnClickListener{
            navigateToNextMonth()
        }

        updateCalendar()
        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!firstload) {

            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "islamic_calendar",
                    trackingID = getTrackingID()
                )
            }
        }
        firstload = true

        loadPage()
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "islamic_calendar",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }

    private fun loadPage() {
        loadApi()
        initObserver()
    }

    private fun loadApi() {
        lifecycleScope.launch {
            viewmodel.getIslamicCalEvent(getLanguage())
        }
    }

    private fun initObserver()
    {
        viewmodel.islamicCalEventData.observe(viewLifecycleOwner, Observer{
            when(it){
                is IslamicCalEventResource.IslamicCalendarEvents -> {
                    progressLayout.hide()
                    adapter.update(it.data.get(0).Event)
                    tvUpcomingEventTitle.text = it.data.get(0).upcomingEvent.text
                    tvUpcomingEventLeft.text = it.data.get(0).upcomingEvent.Status
                    imvUpcoming.imageLoad(BASE_CONTENT_URL_SGP +"Content/Dashboard/logo/up_ev.png")
                }
            }
        })
    }

    private fun updateCalendar() {
        // Gregorian Date
        val generalDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
        val generalCalendar = Calendar.getInstance()

        // Set the current year and month for Gregorian calendar
        generalCalendar.set(Calendar.YEAR, currentYear)
        generalCalendar.set(Calendar.MONTH, currentMonth - 1)

        // Format the Gregorian date and set it to the TextView
        val englishDate = generalDateFormat.format(generalCalendar.time).uppercase(Locale.ENGLISH).numberLocale().monthNameLocale()
        monthYearText.text = englishDate

        // Define the valid date range for UmmalquraCalendar
        val validDateRangeStart = Calendar.getInstance().apply { set(1937, 2, 14) } // March 14, 1937
        val validDateRangeEnd = Calendar.getInstance().apply { set(2077, 10, 16) } // November 16, 2077

        // Check if the current date is within the valid Hijri date range
        if (generalCalendar.time.before(validDateRangeStart.time) || generalCalendar.time.after(validDateRangeEnd.time)) {
            hijriMonthYearText.text = "Invalid Date for Hijri Calendar"
        } else {
            // Hijri Date
            val hijriCalendar = UmmalquraCalendar()
            hijriCalendar.time = generalCalendar.time

            // Custom Formatter for Bengali
            val day = hijriCalendar.get(Calendar.DAY_OF_MONTH)
            val month = hijriCalendar.get(Calendar.MONTH)
            val year = hijriCalendar.get(Calendar.YEAR)

            val bengaliMonths = arrayOf(
                "মহররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি", "জমাদিউল আউয়াল", "জমাদিউস সানি",
                "রজব", "শাবান", "রমজান", "শাওয়াল", "জিলকদ", "জিলহজ"
            )

            val formattedHijriDate = String.format("%02d %s %04d", day, bengaliMonths[month], year)
            hijriMonthYearText.text = formattedHijriDate
        }

        // Set the month in your custom calendar
        customCalendar.setMonth("${currentYear}/${String.format("%02d", currentMonth)}/01")
    }

    private fun showCalendar() {

        val params = layoutCalendar.layoutParams
        params.height = (300 * resources.displayMetrics.density).toInt()
        layoutCalendar.layoutParams = params

        val slideDown: Animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_down)

        customCalendar.startAnimation(slideDown)

        customCalendar.invalidate()
        customCalendar.requestLayout()

/*        layoutShow.visibility = View.GONE
        layoutHide.visibility = View.VISIBLE*/
    }

    private fun hideCalendar() {

        val params = layoutCalendar.layoutParams
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutCalendar.layoutParams = params

        val slideUp: Animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_up)
        customCalendar.startAnimation(slideUp)

        customCalendar.invalidate()
        customCalendar.requestLayout()
/*
        layoutShow.visibility = View.VISIBLE
        layoutHide.visibility = View.GONE*/
    }

    private fun navigateToPreviousMonth() {
        if (currentMonth == 1) {
            currentMonth = 12
            currentYear--
        } else {
            currentMonth--
        }
        updateCalendar()
    }

    private fun navigateToNextMonth() {
        if (currentMonth == 12) {
            currentMonth = 1
            currentYear++
        } else {
            currentMonth++
        }
        updateCalendar()
    }

    override fun itemPosition(eventName: String, eventDate: String) {
        calendarIntentHelper.addEventToDefaultCalendar(eventName,eventDate)
    }

    override fun itemClick(event: IslamicEvent) {
        when (event.ContentType){
            "rot" -> gotoFrag(R.id.action_global_ramadanOtherDayFragment)
            "ie" -> {
                val bundle = Bundle()
                bundle.putInt("categoryID", event.CategoryId.toInt())
                bundle.putString("pageTitle",event.text)
                bundle.putString("pageTag", MENU_ISLAMIC_EVENT)
                gotoFrag(R.id.action_global_subContentFragment,bundle)
            }
            "qurb" -> gotoFrag(R.id.action_global_qurbaniFragment)
            else -> {}
        }
    }
}