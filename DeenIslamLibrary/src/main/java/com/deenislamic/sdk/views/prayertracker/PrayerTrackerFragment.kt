package com.deenislamic.sdk.views.prayertracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.PrayerTrackerCallback
import com.deenislamic.sdk.service.di.DatabaseProvider
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.prayer_time.PrayerNotificationResource
import com.deenislamic.sdk.service.models.prayer_time.PrayerTimeResource
import com.deenislamic.sdk.service.network.response.prayertimes.calendartracker.PrayerTrackerResponse
import com.deenislamic.sdk.service.repository.PrayerTimesRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.dayNameLocale
import com.deenislamic.sdk.utils.getWaktNameByTag
import com.deenislamic.sdk.utils.monthNameLocale
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.prayerMomentLocaleForToast
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.viewmodels.PrayerTimesViewModel
import com.deenislamic.sdk.views.adapters.prayertracker.PrayerTrackerAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.service.libs.trackercalendar.CalendarTrackerDay
import com.deenislamic.sdk.service.libs.trackercalendar.TrackerCalendar
import com.deenislamic.sdk.viewmodels.common.PrayerTimeVMFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


internal class PrayerTrackerFragment : BaseRegularFragment(), PrayerTrackerCallback {

    private lateinit var viewmodel: PrayerTimesViewModel
    private lateinit var viewmodelWithActivity: PrayerTimesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var prayerTimesAdapter: PrayerTrackerAdapter
    private lateinit var customCalendar: TrackerCalendar
    private lateinit var prayerDateTime: AppCompatTextView
    private lateinit var englishDate: AppCompatTextView
    private lateinit var imvPrevMonth: AppCompatImageView
    private lateinit var imvNextMonth: AppCompatImageView
    private lateinit var prayerTrackerResponse: PrayerTrackerResponse
    private lateinit var layoutHeader : ConstraintLayout
    private lateinit var targetDate: String
    private var prayerTrackLastWakt = ""
    private var firstload = false

    private var currentState = "dhaka"
    private var prayerdate: String = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())
    private var todayPrayerDate = SimpleDateFormat("yyyy-MM-dd'T'00:00:00", Locale.ENGLISH).format(Date())
    private var currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    override fun OnCreate() {
        super.OnCreate()

        val prayerTimesRepository = PrayerTimesRepository(
            deenService = NetworkProvider().getInstance().provideDeenService(),
            prayerNotificationDao = DatabaseProvider().getInstance().providePrayerNotificationDao(),
            prayerTimesDao = DatabaseProvider().getInstance().providePrayerTimesDao()
        )

        viewmodel = PrayerTimesViewModel(prayerTimesRepository)



        val factory = PrayerTimeVMFactory(prayerTimesRepository)
        viewmodelWithActivity = ViewModelProvider(
            requireActivity(),
            factory
        )[PrayerTimesViewModel::class.java]

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_prayer_tracker, container, false)

        recyclerView = mainView.findViewById(R.id.recyclerPrayerTime)
        prayerDateTime = mainView.findViewById(R.id.dateTimeArabic)
        englishDate = mainView.findViewById(R.id.dateTime)
        customCalendar = mainView.findViewById(R.id.calendar)
        layoutHeader = mainView.findViewById(R.id.monthYearSelector)
        imvPrevMonth = mainView.findViewById(R.id.leftBtn)
        imvNextMonth = mainView.findViewById(R.id.rightBtn)

        prayerTimesAdapter = PrayerTrackerAdapter()
        prayerTimesAdapter.setOnButtonClickListener(this)

        recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter = prayerTimesAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        targetDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
//        targetDate = SimpleDateFormat("dd", Locale.ENGLISH).format(Date())

        imvNextMonth.setOnClickListener {
            updatePrayerDateToNextMonth()
            prayerTimesAdapter.cleanData()
            loadDataAPI()
        }

        imvPrevMonth.setOnClickListener {
            updatePrayerDateToPreviousMonth()
            prayerTimesAdapter.cleanData()
            loadDataAPI()
        }

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            action3 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.prayer_tracker),
            backEnable = true,
            view = mainView,
            actionIconColor = R.color.deen_txt_black_deep
        )
        CallBackProvider.setFragment(this)
        setupCommonLayout(mainView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateCalendar()
        observePrayerTimes()

        if (!firstload) {
            loadDataAPI()
        }
        firstload = true

    }


    override fun noInternetRetryClicked() {
        loadDataAPI()
    }

    fun loadDataAPI() {

        baseLoadingState()

        lifecycleScope.launch {
            viewmodel.getPrayerTimeTracker(currentState, getLanguage(), prayerdate)
        }
    }

    private fun observePrayerTimes() {

        viewmodel.prayerTimes.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is PrayerTimeResource.prayerTracker -> {
                    baseViewState()


                    val trackerData = resource.data.Data.tracker.firstOrNull { it.TrackingDate == todayPrayerDate }
                    if (trackerData != null) {
                        DeenSDKCore.gpHomeCallback?.deenGPHomePrayerTrackListner(trackerData)

                        lifecycleScope.launch {
                            viewmodelWithActivity.setPrayerTrackFromMonthlyTracker(trackerData)
                        }

                    }

                    val prayerTimesResponse = resource.data
                    prayerTrackerResponse = prayerTimesResponse
                    updateHeader()
                    // Update the adapter with new data
                    prayerTimesAdapter.updateData(
                        prayerTimesResponse,
                        null, // Replace with actual notification data if available
                        null,
                        targetDate// Replace with actual prayer moment range data if available
                    )
                    customCalendar.updatePrayerData(resource.data.Data.tracker,targetDate.split("-").last().toInt().toString())
                }

                is CommonResource.API_CALL_FAILED -> baseNoInternetState()

                is PrayerTimeResource.prayerTimeEmpty -> {
                    // Handle empty prayer times
                }
                else -> {
                    // Handle API call failure
                }
            }
        })

        viewmodel.prayerTimesNotification.observe(viewLifecycleOwner)
        {
            when (it) {

                is PrayerNotificationResource.prayerTrackSuccess -> {


                    if (prayerTrackLastWakt.isNotEmpty())
                    {

                        requireContext().toast(
                            localContext.getString(
                                R.string.prayerTrackToast,
                                prayerTrackLastWakt.prayerMomentLocaleForToast()
                            )
                        )

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

                        val date = dateFormat.parse(targetDate)

                        val calendar = Calendar.getInstance()
                        calendar.time = date

                        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                        customCalendar.incrementProgressForToday(dayOfMonth.toString())
                    }

                    else
                    {

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

                        val date = dateFormat.parse(targetDate)

                        val calendar = Calendar.getInstance()
                        calendar.time = date

                        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                        customCalendar.decrementProgressForToday(dayOfMonth.toString())
                    }
                }

                CommonResource.EMPTY -> Unit
            }
        }
    }

    fun updatePrayerDateToPreviousMonth() {

        val isPreviousMonthCurrent = Calendar.getInstance().apply {
            time = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(prayerdate)
            add(Calendar.MONTH, -1)
        }.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)


        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val calendar = Calendar.getInstance().apply {
            time = dateFormat.parse(prayerdate) ?: Date()
            add(Calendar.MONTH, -1)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        if(isPreviousMonthCurrent)
        {
            prayerdate = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())
            targetDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(prayerdate))
        }

        else
        {
            // Update prayerdate to the first date of the previous month
            prayerdate = dateFormat.format(calendar.time)
            targetDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(prayerdate))

        }



        // Update calendar view
        customCalendar.showPreviousMonth()

        // Call the API with the new prayerdate
    }

    private fun updatePrayerDateToNextMonth() {

        // Check Current Month
        val isCurrentMonth = Calendar.getInstance().apply {
            time = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(prayerdate)
            add(Calendar.MONTH, 1)
        }.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)

        // Fix Next Month's First Date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val calendar = Calendar.getInstance().apply {
            time = dateFormat.parse(prayerdate) ?: Date()
            add(Calendar.MONTH, +1)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        if(isCurrentMonth)
        {
            prayerdate = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())
            targetDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(prayerdate))
        }

        else
        {
            // Update prayerdate to the first date of the previous month
            prayerdate = dateFormat.format(calendar.time)
            targetDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(prayerdate))
        }

        // Update calendar view
        customCalendar.showNextMonth()
    }

    private fun updateHeader() {

        if(!targetDate.isEmpty())
        {
            for (i in 0 until prayerTrackerResponse.Data.tracker.size) {
                if (prayerTrackerResponse.Data.tracker[i].TrackingDate.substringBefore("T") == targetDate) {
                    val formattedDate = formatDate(prayerTrackerResponse.Data.tracker[i].TrackingDate)
                    prayerDateTime.text = prayerTrackerResponse.Data.tracker[i].ArabicDate
                    englishDate.text = formattedDate.numberLocale().monthNameLocale().dayNameLocale()
                }
            }
        }
        else
        {
            val formattedDate = formatDate(prayerTrackerResponse.Data.prayerTime.Date)
            prayerDateTime.text = prayerTrackerResponse.Data.prayerTime.IslamicDate
            englishDate.text = formattedDate
        }
//        prayerDateTime.text = prayerTimesResponse.Data.prayerTime.IslamicDate + " • " + prayerTimesResponse.Data.prayerTime.IslamicDate

    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH)

        val date = inputFormat.parse(inputDate) ?: return ""

        return outputFormat.format(date)
    }

    fun getFormattedDate(day: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance()

        // Set the calendar with the given day, month, and year
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)

        // Define the date format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        // Format the date
        return dateFormat.format(calendar.time)
    }

    private fun updateCalendar()
    {
        customCalendar.setMonth("${currentYear}/${String.format("%02d", currentMonth)}/01")
    }

    override fun prayerTrack(prayer_tag: String, date: String, isPrayed: Boolean) {
        lifecycleScope.launch {
            val newDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date)
                ?.let { SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(it) }

            prayerTrackLastWakt = if (isPrayed)
                prayer_tag.getWaktNameByTag()
            else
                ""
            if (newDateFormat != null) {
                viewmodel.setPrayerTrackDateWise(
                    language = getLanguage(),
                    prayer_tag = prayer_tag.getWaktNameByTag(),
                    bol = isPrayed,
                    prayerdate = newDateFormat,
                    prayerTrackerResponse
                )
            }
        }
    }

    override fun prayerDate(day: CalendarTrackerDay) {
        updateHeader()
        val dateParts = targetDate.split("-")
        val year = dateParts[0].toInt()
        val month = dateParts[1].toInt() - 1

        targetDate = getFormattedDate(day.day.toInt(),month,year)
        prayerTimesAdapter.cleanData()
        lifecycleScope.launch {
            viewmodel.reloadPrayerTracker(prayerTrackerResponse)
        }
    }
}