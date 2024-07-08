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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


internal class TasbeehMonthlyCountFragment : BaseRegularFragment() {

    private lateinit var viewmodel: TasbeehViewModel
    private lateinit var monthlyCount: AppCompatTextView
    private lateinit var totalCount: AppCompatTextView
    private lateinit var recentCount: RecyclerView
    private lateinit var recentTxt: AppCompatTextView
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

        val mainView = localInflater.inflate(R.layout.fragment_tasbeeh_monthly_count,container,false)

        monthlyCount = mainView.findViewById(R.id.monthlyCount)
        totalCount = mainView.findViewById(R.id.totalCount)
        recentCount = mainView.findViewById(R.id.recentCount)
        recentTxt = mainView.findViewById(R.id.recentTxt)

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

        // Get the current month's start and end dates
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(todayDate) ?: Date()
        val startOfMonth = SimpleDateFormat("yyyy-MM-01", Locale.ENGLISH).format(calendar.time)

        calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1) // Move to the last day of the previous month
        val endOfMonth = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(calendar.time)


        lifecycleScope.launch {
            viewmodel.fetchWeeklyCount(startOfMonth,endOfMonth)
            viewmodel.fetchTotalCount()
            viewmodel.fetchWeeklyRecentCount(startOfMonth,endOfMonth)
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

                is TasbeehResource.WeeklyCount -> monthlyCount.text = (if(it.weeklyCount>0) it.weeklyCount.toString() else "0").numberLocale()
                is TasbeehResource.TotalCount -> totalCount.text = (if(it.totalCount>0) it.totalCount.toString() else "0").numberLocale()
            }
        }
    }

}