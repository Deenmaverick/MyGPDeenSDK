package com.deenislamic.sdk.views.adapters.prayer_times;

import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.ViewInflationListener
import com.deenislamic.sdk.service.database.entity.PrayerNotification
import com.deenislamic.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislamic.sdk.service.models.ramadan.StateModel
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.service.network.response.prayertimes.tracker.Data
import com.deenislamic.sdk.utils.*
import com.deenislamic.sdk.views.adapters.common.CommonStateList
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.views.prayertimes.patch.ForbiddenTimes
import com.deenislamic.sdk.views.prayertimes.patch.OtherPrayerTimes
import com.deenislamic.sdk.views.prayertimes.patch.PrayerTimes
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*


const val TYPE_WIDGET1:Int = 1
const val TYPE_WIDGET2:Int = 2
const val TYPE_WIDGET3:Int = 3
const val TYPE_WIDGET4:Int = 4
const val TYPE_WIDGET5:Int = 5
const val TYPE_WIDGET6:Int = 6
internal class PrayerTimesAdapter(
    private val callback: prayerTimeAdapterCallback? = null,
    private var viewInflationListener: ViewInflationListener
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()
    private  var prayerList:ArrayList<Int> = arrayListOf(1,2,3,4,5)
    private lateinit var inc_prayer_times:LinearLayout
    private var updateDataState:Boolean = false

    private var prayerData:PrayerTimesResponse ? = null
    private var todayprayerData:PrayerTimesResponse ? = null
    private var dateWisePrayerNotificationData:ArrayList<PrayerNotification>?= null
    private var  prayerMomentRangeData: PrayerMomentRange? = null

    // init view
    private lateinit var timefor:AppCompatTextView
    private lateinit var  prayerMoment:AppCompatTextView
    private lateinit var  dateTimeArabic:AppCompatTextView
    private lateinit var dateTime:AppCompatTextView
    private lateinit var prayerMomentRange: AppCompatTextView
    private lateinit var nextPrayerName:AppCompatTextView
    private lateinit var nextPrayerTimeCount:AppCompatTextView
    private lateinit var prayerBG:AppCompatImageView
    private lateinit var leftBtn:AppCompatImageView
    private lateinit var rightBtn:AppCompatImageView
    private lateinit var allPrayer:LinearLayout
    private lateinit var stateBtn:LinearLayout
    private lateinit var stateTxt:AppCompatTextView
    private var commonStateList: CommonStateList? = null


    private var inflatedViewCount:Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    {
        val main_view = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_match, parent, false)

        prayerList.forEachIndexed()
        {
                index, item ->
            when(item)
            {
                //pryaer time card

                TYPE_WIDGET1 -> {

                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.item_prayer_time) {
                        timefor = this.findViewById(R.id.timefor)
                        prayerMoment= this.findViewById(R.id.prayerMoment)
                        prayerMomentRange = this.findViewById(R.id.appCompatTextView)
                        nextPrayerName = this.findViewById(R.id.nextPrayerName)
                        nextPrayerTimeCount = this.findViewById(R.id.nextPrayerTime)
                        prayerBG = this.findViewById(R.id.prayerBG)
                        allPrayer = this.findViewById(R.id.allPrayer)
                        stateBtn = this.findViewById(R.id.stateBtn)
                        stateTxt =  this.findViewById(R.id.stateTxt)
                        allPrayer.setOnClickListener { callback?.clickMonthlyCalendar() }

                        widget1_view()
                    }

                }

                TYPE_WIDGET2 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.item_prayer_part1) {
                        dateTimeArabic  = this.findViewById(R.id.dateTimeArabic)
                        dateTime =  this.findViewById(R.id.dateTime)
                        leftBtn = this.findViewById(R.id.leftBtn)
                        rightBtn = this.findViewById(R.id.rightBtn)

                        widget2_view()
                    }
                }

                TYPE_WIDGET3 -> {
                    //prayer times fazr to isha
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.layout_prayer_time) {
                        PrayerTimes().getInstance().load(this, callback = callback)
                        widget3_view()
                    }
                }

                TYPE_WIDGET4 -> {

                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.layout_prayer_time) {
                        OtherPrayerTimes().getInstance().load(this,callback)
                        widget4_view()
                    }

                }

                TYPE_WIDGET5 -> {
                    //monthly prayer tracker
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.item_monthly_prayer_tracker_btn) {
                        val trackerBtn: MaterialButton = this.findViewById(R.id.inf)
                        trackerBtn.setOnClickListener {
                            callback?.monthlyTrackerBtnClicked()
                        }
                    }

                }

                TYPE_WIDGET6 -> {
                    //forbidden times
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.layout_prayer_time) {
                        ForbiddenTimes().getInstance().load(this )
                        widget5_view()
                    }

                }
            }

            if (index == prayerList.size - 1) {

                com.deenislamic.sdk.utils.prepareStubView<View>(
                    main_view.findViewById(R.id.widget),
                    R.layout.layout_footer
                ) {

                }
            }
        }

        return   ViewHolder(
            main_view
        )
    }

    private inline fun <T : View> prepareStubView(
        stub: AsyncViewStub,
        layoutID:Int,
        crossinline prepareBlock: T.() -> Unit
    ) {
        stub.layoutRes = layoutID
        val inflatedView = stub.inflatedId as? T

        if (inflatedView != null) {
            inflatedView.prepareBlock()
        } else {
            stub.inflate(AsyncLayoutInflater.OnInflateFinishedListener { view, _, _ ->
                (view as? T)?.prepareBlock()

                inflatedViewCount++

                if (inflatedViewCount == prayerList.size) {
                    viewInflationListener.onAllViewsInflated()
                }
            })
        }
    }

    fun updateData(data: PrayerTimesResponse, Notificationdata: ArrayList<PrayerNotification>?)
    {
        //if(todayprayerData==null)
            todayprayerData = data

        prayerData = data
        dateWisePrayerNotificationData = Notificationdata
        widget1_view()
        widget2_view()

        /*if(dateWisePrayerNotificationData?.isNotEmpty() == true) {
            widget3_view()
            widget4_view()
        }*/

        widget3_view()
        widget4_view()

        if(inflatedViewCount>0)
            viewInflationListener.onAllViewsInflated()

        notifyDataSetChanged()
    }


    fun updateTrackingData(data: Data)
    {
        PrayerTimes().getInstance().updateTrackingData(data)
        notifyDataSetChanged()
    }

    fun updateState(stateModel: StateModel)
    {
        if(this::stateTxt.isInitialized)
        stateTxt.text = stateModel.stateValue
        commonStateList?.stateSelected(stateModel)
    }

    fun updateNotificationData(Notificationdata: ArrayList<PrayerNotification>?)
    {
        dateWisePrayerNotificationData = Notificationdata

        if(dateWisePrayerNotificationData?.isNotEmpty() == true) {
            widget3_view()
            widget4_view()
        }

     /*   widget3_view()
        widget4_view()*/
        if(inflatedViewCount>0)
            viewInflationListener.onAllViewsInflated()

        notifyDataSetChanged()
    }



    override fun getItemCount(): Int = if(prayerData !=null) 1 else 0

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    //private fun inflate_widget(layoutInflater: LayoutInflater,layoutInt: Int):View = layoutInflater.inflate(layoutInt,rootview)


    // All widget
    private fun widget1_view()
    {

        if(!this::prayerBG.isInitialized)
            return

        val getContext = prayerBG.context

        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())

        todayprayerData?.Data?.state?.let {
            stateTxt.text = it
        }

        prayerMomentRangeData =  todayprayerData?.let { getPrayerTimeName(it,currentTime.stringTimeToEpochTime() /*"3:42:06 AM".StringTimeToMillisecond("hh:mm:ss aa")*/ ) }

        prayerMomentRangeData?.MomentName?.let { Log.e("UTIL_PRAYER1", it) }

        timefor.show()
        prayerMoment.setPadding(0,0,0,0)
        prayerMomentRange.show()

        when (prayerMomentRangeData?.MomentName) {
            "Fajr" -> prayerBG.setBackgroundResource(R.drawable.fajr)
            "Dhuhr" -> prayerBG.setBackgroundResource(R.drawable.dhuhr)
            "Asr" -> prayerBG.setBackgroundResource(R.drawable.asr)
            "Maghrib" -> prayerBG.setBackgroundResource(R.drawable.maghrib)
            "Isha" -> prayerBG.setBackgroundResource(R.drawable.isha)
            "Ishraq" -> prayerBG.setBackgroundResource(R.drawable.fajr)
            "Chasht" -> prayerBG.setBackgroundResource(R.drawable.fajr)
            getContext?.getString(R.string.forbidden_time) -> prayerBG.setBackgroundResource(R.drawable.deen_bg_forbidden_prayer)
            else -> {
                timefor.hide()
                prayerMoment.setPadding(0,8.dp,0,0)
                prayerMoment.hide()
                prayerMomentRange.hide()
                when(prayerMomentRangeData?.NextPrayerName){
                    "Fajr" -> prayerBG.setBackgroundResource(R.drawable.fajr)
                    "Dhuhr" -> prayerBG.setBackgroundResource(R.drawable.dhuhr)
                    "Asr" -> prayerBG.setBackgroundResource(R.drawable.asr)
                    "Maghrib" -> prayerBG.setBackgroundResource(R.drawable.maghrib)
                    "Isha" -> prayerBG.setBackgroundResource(R.drawable.isha)
                    "Ishraq" -> prayerBG.setBackgroundResource(R.drawable.fajr)
                    "Chasht" -> prayerBG.setBackgroundResource(R.drawable.fajr)
                    else -> {

                        prayerBG.setBackgroundColor(ContextCompat.getColor(getContext,R.color.deen_black))

                    }

                }
            }
        }

        prayerMoment.text = prayerMomentRangeData?.MomentName?.prayerMomentLocale()
        prayerMomentRange.text = prayerMomentRangeData?.StartTime?.timeLocale() +" - " + prayerMomentRangeData?.EndTime?.timeLocale()
        nextPrayerName.text = getContext.resources.getString(R.string.billboard_next_prayer,prayerMomentRangeData?.NextPrayerName?.prayerMomentLocale()?:"--")
        nextPrayerTimeCount.text = "-00:00:00".timeLocale()

        prayerMomentRangeData?.nextPrayerTimeCount?.let {

            if(it>0) {
                nextPrayerTimeCount.text = "-"+prayerMomentRangeData?.nextPrayerTimeCount?.TimeDiffForPrayer()?.numberLocale()
                com.deenislamic.sdk.utils.singleton.CountDownTimer.prayerTimer?.cancel()
                com.deenislamic.sdk.utils.singleton.CountDownTimer.prayerTimer =object : CountDownTimer(it, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        nextPrayerTimeCount.text = "-"+millisUntilFinished.TimeDiffForPrayer().numberLocale()
                        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Date())
                        val nowtime = currentTime.stringTimeToEpochTime()

                        //Log.e("PrayerBannerPT",prayerMomentRangeData.forbidden3.invoke(nowtime).toString())

                        if(prayerMomentRangeData?.MomentName != getContext?.getString(R.string.forbidden_time) &&
                            prayerMomentRangeData?.MomentName != "Chasht") {
                            if (prayerMomentRangeData?.forbidden1?.invoke(nowtime) == true) {
                                com.deenislamic.sdk.utils.singleton.CountDownTimer.prayerTimer?.cancel()
                                callback?.nextPrayerCountownFinish()
                            } else if (prayerMomentRangeData?.forbidden2?.invoke(nowtime) == true) {
                                com.deenislamic.sdk.utils.singleton.CountDownTimer.prayerTimer?.cancel()
                                callback?.nextPrayerCountownFinish()
                            } else if (prayerMomentRangeData?.forbidden3?.invoke(nowtime) == true) {
                                com.deenislamic.sdk.utils.singleton.CountDownTimer.prayerTimer?.cancel()
                                callback?.nextPrayerCountownFinish()
                            }else if (prayerMomentRangeData?.chasht?.invoke(nowtime) == true) {
                                com.deenislamic.sdk.utils.singleton.CountDownTimer.prayerTimer?.cancel()
                                callback?.nextPrayerCountownFinish()
                            }
                        }
                    }

                    override fun onFinish() {
                        com.deenislamic.sdk.utils.singleton.CountDownTimer.prayerTimer?.cancel()
                        callback?.nextPrayerCountownFinish()
                    }
                }
                com.deenislamic.sdk.utils.singleton.CountDownTimer.prayerTimer?.start()
            }

        }

        if(commonStateList==null)
            commonStateList = CommonStateList(stateBtn)


    }

    private fun widget2_view()
    {

        if(!this::dateTime.isInitialized)
            return

        //val TodayDateTime = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US).format(Date())
        // val format = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH) // or you can add before dd/M/yyyy
        //val newDate = format.parse(prayerData?.Data?.Date?.StringTimeToMillisecond("yyyy-MM-dd'T'HH:mm:ss")?.MilliSecondToStringTime("EEEE, dd MMMM yyyy"))

        val newDate = prayerData?.Data?.Date?.formateDateTime("yyyy-MM-dd'T'HH:mm:ss","EEEE, dd MMMM yyyy")
        //val newDate = format.parse(prayerData?.Data?.Date?.StringTimeToMillisecond("dd/MM/yyyy")?.MilliSecondToStringTime("EEEE, dd MMMM yyyy"))

        dateTime.text = newDate?.monthNameLocale()?.numberLocale()?.dayNameLocale()
        prayerData?.Data?.IslamicDate?.let {
            dateTimeArabic.text = it + " • " + prayerData?.Data?.BanglaDate

        }

        leftBtn.setOnClickListener {
            callback?.leftBtnClick()
        }

        rightBtn.setOnClickListener {
            callback?.rightBtnClick()
        }
    }

    private fun widget3_view()
    {
        prayerData?.let { PrayerTimes().getInstance().update(it,dateWisePrayerNotificationData,prayerMomentRangeData) }
    }


    private fun widget4_view()
    {
        prayerData?.let { OtherPrayerTimes().getInstance().update(it,dateWisePrayerNotificationData,prayerMomentRangeData) }
    }

    private fun widget5_view()
    {
        prayerData?.let { ForbiddenTimes().getInstance().update(it) }
    }


    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        override fun onBind(position: Int) {
            super.onBind(position)


        }
    }

}

internal interface prayerTimeAdapterCallback
{
    fun leftBtnClick()
    fun rightBtnClick()
    fun nextPrayerCountownFinish()
    fun clickNotification(position: String)
    fun clickMonthlyCalendar()

    fun prayerCheck(prayer_tag: String, date: String, isPrayed: Boolean)

    fun monthlyTrackerBtnClicked()
}
