package com.deenislam.sdk.views.adapters.prayer_times;

import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.ViewInflationListener
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.utils.*
import com.deenislam.sdk.views.base.BaseViewHolder
import com.deenislam.sdk.views.prayertimes.patch.ForbiddenTimes
import com.deenislam.sdk.views.prayertimes.patch.OtherPrayerTimes
import com.deenislam.sdk.views.prayertimes.patch.PrayerTimes
import java.text.SimpleDateFormat
import java.util.*

const val TYPE_WIDGET1:Int = 1
const val TYPE_WIDGET2:Int = 2
const val TYPE_WIDGET3:Int = 3
const val TYPE_WIDGET4:Int = 4
const val TYPE_WIDGET5:Int = 5
internal class PrayerTimesAdapter(
    private val callback: prayerTimeAdapterCallback? = null,
    private var viewInflationListener: ViewInflationListener
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()
    private  var prayerList:ArrayList<Int> = arrayListOf(1,2,3,4,5)
    private lateinit var inc_prayer_times:LinearLayout
    private var updateDataState:Boolean = false
    private var countDownTimer:CountDownTimer?=null

    private var prayerData:PrayerTimesResponse ? = null
    private var todayprayerData:PrayerTimesResponse ? = null
    private var dateWisePrayerNotificationData:ArrayList<PrayerNotification>?= null
    private var  prayerMomentRangeData: PrayerMomentRange? = null

    // init view
    private var initView:Boolean = false
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
    private var inflatedViewCount:Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    {
        initView = false
        val main_view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_async_match, parent, false)

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
        if(todayprayerData==null)
            todayprayerData = data

        prayerData = data
        dateWisePrayerNotificationData = Notificationdata
        notifyDataSetChanged()
    }

    fun updateNotificationData(Notificationdata: ArrayList<PrayerNotification>?)
    {
        dateWisePrayerNotificationData = Notificationdata
    }



    override fun getItemCount(): Int =1

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    //private fun inflate_widget(layoutInflater: LayoutInflater,layoutInt: Int):View = layoutInflater.inflate(layoutInt,rootview)


    // All widget
    private fun widget1_view()
    {

        if(!this::prayerBG.isInitialized)
            return

        val currentTime = SimpleDateFormat("hh:mm:ss aa", Locale.getDefault()).format(Date())


        prayerMomentRangeData =  todayprayerData?.let { getPrayerTimeName(it,currentTime.StringTimeToMillisecond("hh:mm:ss aa") /*"3:42:06 AM".StringTimeToMillisecond("hh:mm:ss aa")*/ ) }

        prayerMomentRangeData?.MomentName?.let { Log.e("UTIL_PRAYER1", it) }


        if(prayerMomentRangeData?.MomentName == "Fajr")
        prayerBG.setBackgroundResource(R.drawable.fajr)
        else if(prayerMomentRangeData?.MomentName == "Dhuhr")
            prayerBG.setBackgroundResource(R.drawable.dhuhr)
        else if(prayerMomentRangeData?.MomentName == "Asr")
            prayerBG.setBackgroundResource(R.drawable.asr)
        else if(prayerMomentRangeData?.MomentName == "Maghrib")
            prayerBG.setBackgroundResource(R.drawable.maghrib)
        else if(prayerMomentRangeData?.MomentName == "Isha")
            prayerBG.setBackgroundResource(R.drawable.isha)
        else
        prayerBG.setBackgroundColor(prayerBG.context.resources.getColor(R.color.black,prayerBG.context.theme))

        prayerMoment.text = prayerMomentRangeData?.MomentName
        prayerMomentRange.text = prayerMomentRangeData?.StartTime +" - " + prayerMomentRangeData?.EndTime
        nextPrayerName.text = "Next prayer: "+prayerMomentRangeData?.NextPrayerName?:"--"
        nextPrayerTimeCount.text = "-00:00:00"

        prayerMomentRangeData?.nextPrayerTimeCount?.let {

            if(it>0) {
                nextPrayerTimeCount.text = "-"+prayerMomentRangeData?.nextPrayerTimeCount?.TimeDiffForPrayer()
                countDownTimer?.cancel()
                countDownTimer =object : CountDownTimer(it, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        nextPrayerTimeCount.text = "-"+millisUntilFinished.TimeDiffForPrayer()
                    }

                    override fun onFinish() {
                        countDownTimer?.cancel()
                        callback?.nextPrayerCountownFinish()
                    }
                }
                countDownTimer?.start()
            }


        }


    }

    private fun widget2_view()
    {

        if(!this::dateTime.isInitialized)
            return

        //val TodayDateTime = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
       // val format = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH) // or you can add before dd/M/yyyy
        //val newDate = format.parse(prayerData?.Data?.Date?.StringTimeToMillisecond("yyyy-MM-dd'T'HH:mm:ss")?.MilliSecondToStringTime("EEEE, dd MMMM yyyy"))

        val newDate = prayerData?.Data?.Date?.formateDateTime("yyyy-MM-dd'T'HH:mm:ss","EEEE, dd MMMM yyyy")
        //val newDate = format.parse(prayerData?.Data?.Date?.StringTimeToMillisecond("dd/MM/yyyy")?.MilliSecondToStringTime("EEEE, dd MMMM yyyy"))

        dateTime.text = newDate
        prayerData?.Data?.IslamicDate?.let {
            dateTimeArabic.text = it

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

     private fun initView(main_view:View)
    {
        if(initView)
            return

        prayerList.forEach()
        {
            when(it)
            {
                //pryaer time card

                TYPE_WIDGET1 -> {

                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.item_prayer_time) {
                        prayerMoment= this.findViewById(R.id.prayerMoment)
                        prayerMomentRange = this.findViewById(R.id.appCompatTextView)
                        nextPrayerName = this.findViewById(R.id.nextPrayerName)
                        nextPrayerTimeCount = this.findViewById(R.id.nextPrayerTime)
                        prayerBG = this.findViewById(R.id.prayerBG)
                        allPrayer = this.findViewById(R.id.allPrayer)
                        allPrayer.setOnClickListener { callback?.clickMonthlyCalendar() }
                    }

                }

                TYPE_WIDGET2 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.item_prayer_part1) {
                        dateTimeArabic  = this.findViewById(R.id.dateTimeArabic)
                        dateTime =  this.findViewById(R.id.dateTime)
                        leftBtn = this.findViewById(R.id.leftBtn)
                        rightBtn = this.findViewById(R.id.rightBtn)
                    }
                }

                TYPE_WIDGET3 -> {
                    //prayer times fazr to isha
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.layout_prayer_time) {
                        PrayerTimes().getInstance().load(this, callback = callback)
                    }
                }

                TYPE_WIDGET4 -> {

                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.layout_prayer_time) {
                        OtherPrayerTimes().getInstance().load(this,callback)
                    }

                }

                TYPE_WIDGET5 -> {
                    //forbidden times
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.layout_prayer_time) {
                        ForbiddenTimes().getInstance().load(this )
                    }

                }
            }
        }

        initView = true

    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        override fun onBind(position: Int) {
            super.onBind(position)

            if(!initView)
            initView(itemView)
            else {
                prayerList.forEach()
                { widget_position ->

                    when (widget_position) {

                        TYPE_WIDGET1 -> widget1_view()

                        TYPE_WIDGET2 -> widget2_view()

                        TYPE_WIDGET3-> widget3_view()

                        TYPE_WIDGET4-> widget4_view()

                        TYPE_WIDGET5 -> widget5_view()

                    }
                }
            }
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


}
