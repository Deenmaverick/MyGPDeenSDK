package com.deenislamic.sdk.views.tasbeeh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.DatabaseProvider
import com.deenislamic.sdk.service.models.TasbeehResource
import com.deenislamic.sdk.service.repository.TasbeehRepository
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.viewmodels.TasbeehViewModel
import com.deenislamic.sdk.views.adapters.tasbeeh.TasbeehRecentCountAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


internal class TasbeehWeeklyCountFragment : BaseRegularFragment() {

    private lateinit var viewmodel: TasbeehViewModel
    private lateinit var weeklyCount: AppCompatTextView
    private lateinit var totalCount: AppCompatTextView
    private lateinit var recentCount: RecyclerView
    private lateinit var recentTxt: AppCompatTextView
    private lateinit var chartCard:MaterialCardView
    private var todayDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()

        val repository = TasbeehRepository(
            tasbeehDao = DatabaseProvider().getInstance().provideTasbeehDao(),
            userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
        )

        viewmodel = TasbeehViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainView = localInflater.inflate(R.layout.fragment_tasbeeh_weekly_count,container,false)

        weeklyCount = mainView.findViewById(R.id.weeklyCount)
        totalCount = mainView.findViewById(R.id.totalCount)
        recentCount = mainView.findViewById(R.id.recentCount)
        recentTxt = mainView.findViewById(R.id.recentTxt)
        chartCard = mainView.findViewById(R.id.chartCard)

        setupCommonLayout(mainView)

        return mainView
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if(menuVisible){
            loadpage()
        }
    }

    private fun loadpage()
    {
        if(firstload)
            return
        firstload  = true

        initObserver()

        lopadapi()

    }

    fun lopadapi()
    {

        baseLoadingState()

        // Get the current week's start and end dates
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(todayDate) ?: Date()
        calendar.firstDayOfWeek = Calendar.MONDAY // Set the first day of the week as Monday
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfWeek = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(calendar.time)

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val endOfWeek = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(calendar.time)

        lifecycleScope.launch {
            viewmodel.fetchWeeklyCount(startOfWeek,endOfWeek)
            viewmodel.fetchTotalCount()
            viewmodel.fetchWeeklyRecentCount(startOfWeek,endOfWeek)
        }
    }

    private fun initObserver(){

        viewmodel.tasbeehLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is TasbeehResource.RecentCount ->{

                    if(it.data.isEmpty())
                        recentTxt.hide()
                    else{
                        recentCount.apply {
                            adapter = TasbeehRecentCountAdapter(it.data)
                        }
                    }

                    baseViewState()
                }

                is TasbeehResource.WeeklyCount -> weeklyCount.text = (if(it.weeklyCount>0) it.weeklyCount.toString() else "0").numberLocale()
                is TasbeehResource.TotalCount -> totalCount.text = (if(it.totalCount>0) it.totalCount.toString() else "0").numberLocale()
            }
        }
    }

}