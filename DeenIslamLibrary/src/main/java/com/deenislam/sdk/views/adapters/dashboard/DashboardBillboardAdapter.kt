package com.deenislam.sdk.views.adapters.dashboard

import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.utils.*
import com.deenislam.sdk.views.base.BaseViewHolder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

private const val PRAYER_VIEW = 1
private const val BILLBOARD_VIEW = 2
internal class DashboardBillboardAdapter(
    private val callback: prayerTimeCallback? = null
) : RecyclerView.Adapter<BaseViewHolder>(){

    private lateinit var  rootview:LinearLayout

    //init view

    private var initView:Boolean = false
    private lateinit var  prayerBG: AppCompatImageView
    private lateinit var prayerMoment:AppCompatTextView
    private lateinit var prayerMomentRange:AppCompatTextView
    private lateinit var nextPrayerName:AppCompatTextView
    private lateinit var nextPrayerTime:AppCompatTextView
    private lateinit var allPrayer:LinearLayout
    private lateinit var askingLy:AppCompatTextView
    private lateinit var progressTxt:AppCompatTextView
    private lateinit var namazTask:LinearProgressIndicator
    private lateinit var prayerCheck:RadioButton
    private lateinit var mainContainer:ConstraintLayout

    private var prayerData:PrayerTimesResponse ? = null
    private var countDownTimer:CountDownTimer?=null
    private var prayerNotificationData:ArrayList<PrayerNotification> ? =null

    fun update(data: PrayerTimesResponse)
    {
        prayerData = data
        notifyDataSetChanged()
    }

    fun updatePrayerTracker(data: ArrayList<PrayerNotification>)
    {
        prayerNotificationData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    {
        initView = false
        val main_view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_dashboard_main, parent, false)

        return  ViewHolder(main_view)
    }

    // init view function
    private fun initView(main_view:View,viewtype: Int)
    {
        if(initView) return

        when(viewtype)
        {
            PRAYER_VIEW ->
            {
                prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.item_dashboard_prayer_time) {

                    //shimmerContainer = this.findViewById(R.id.shimmerContainer)
                    mainContainer = this.findViewById(R.id.mainContainer)
                    prayerBG = this.findViewById(R.id.prayerBG)
                    askingLy = this.findViewById(R.id.askingLy)
                    progressTxt = this.findViewById(R.id.progressTxt)
                    namazTask = this.findViewById(R.id.namazTask)
                    prayerCheck = this.findViewById(R.id.prayerCheck)
                    prayerMoment = this.findViewById(R.id.prayerMoment)
                    prayerMomentRange = this.findViewById(R.id.appCompatTextView)
                    nextPrayerName = this.findViewById(R.id.nextPrayer)
                    nextPrayerTime = this.findViewById(R.id.nextPrayerTime)
                    allPrayer = this.findViewById(R.id.allPrayer)
                    prayerBG.setBackgroundColor(
                        ContextCompat.getColor(
                            prayerBG.context,
                            R.color.black
                        )
                    )
                    allPrayer.setOnClickListener {
                        callback?.allPrayerPage()
                    }

                }

            }
            BILLBOARD_VIEW ->
            {
                prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.item_dashboard_billboard) {
                    Log.e("BILLBOARD","INSIDE_CALL")
                }
                /*ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_dashboard_billboard, parent, false))*/
            }

            else -> throw java.lang.IllegalArgumentException("View cannot null")
        }

        initView = true

    }


    private fun widget1_view()
    {
        if(!this::allPrayer.isInitialized)
            return

        val currentTime = SimpleDateFormat("hh:mm:ss aa", Locale.getDefault()).format(Date())
        val prayerMomentRangeData: PrayerMomentRange? =  prayerData?.let { getPrayerTimeName(it,currentTime.StringTimeToMillisecond("hh:mm:ss aa") /*getPrayerTimeName(it,"08:01:45 PM".StringTimeToMillisecond("hh:mm:ss aa") */ ) }

        askingLy.text = "Have you prayed ${prayerMomentRangeData?.MomentName}?"
        prayerTracker(true)

        val get_prayer_tag = get_prayer_tag_by_name(prayerMomentRangeData?.MomentName.toString())

        val checkTrack = prayerNotificationData?.indexOfFirst {
                it.isPrayed
                && it.prayer_tag == get_prayer_tag
                        && it.prayer_tag.isNotEmpty()
                && get_prayer_tag.checkCompulsoryprayerByTag() }

        Log.e("prayerNotificationData", prayerMomentRangeData?.MomentName.toString()+" "+get_prayer_tag+" "+checkTrack+" "+Gson().toJson(prayerNotificationData))

        prayerCheck.isEnabled = !(checkTrack!=null && checkTrack >=0)
        prayerCheck.isChecked = (checkTrack!=null && checkTrack >=0)


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
        else {
            //prayerBG.setBackgroundResource(R.drawable.isha)
            prayerTracker(false)
            prayerBG.setBackgroundColor(
                ContextCompat.getColor(
                    prayerBG.context,
                    R.color.black
                )
            )

        }
        progressTxt.text = "${getCompletedPrayerCount()}/5"
        prayerMoment.text = prayerMomentRangeData?.MomentName
        namazTask.progress = getCompletedPrayerCount()*20
        prayerMomentRange.text = prayerMomentRangeData?.StartTime +" - " + prayerMomentRangeData?.EndTime
        nextPrayerName.text = "Next prayer: "+prayerMomentRangeData?.NextPrayerName?:"--"
        nextPrayerTime.text = "-00:00:00"




        prayerCheck.setOnClickListener {
            callback?.prayerTask(get_prayer_tag)
        }

        prayerMomentRangeData?.nextPrayerTimeCount?.let {
            if(it>0) {
                nextPrayerTime.text = "-"+prayerMomentRangeData.nextPrayerTimeCount?.TimeDiffForPrayer()
                countDownTimer?.cancel()
                countDownTimer = object : CountDownTimer(it, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        nextPrayerTime.text = "-" + millisUntilFinished.TimeDiffForPrayer()
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

    private fun prayerTracker(bol:Boolean)
    {
        askingLy.visible(bol)
        prayerCheck.visible(bol)
        namazTask.visible(bol)
        progressTxt.visible(bol)
    }

    private fun getCompletedPrayerCount():Int
    {
        var count = 0
        prayerNotificationData?.let {
            it.forEach {
                if(it.isPrayed && get_prayer_name_by_tag(it.prayer_tag).isNotEmpty() && it.prayer_tag.checkCompulsoryprayerByTag())
                    count++
            }
        }

        return  count
    }

    override fun getItemViewType(position: Int): Int {

        return if(position == ceil(itemCount.toDouble() / 2).toInt())
            PRAYER_VIEW
        else
            BILLBOARD_VIEW
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
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
            })
        }
    }

   inner class ViewHolder(itemView: View) : BaseViewHolder(itemView)
    {
        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)
            if (!initView) {
                initView(itemView, viewtype)
            }
            else {
                when (viewtype) {
                    PRAYER_VIEW -> {
                        widget1_view()
                    }
                    BILLBOARD_VIEW -> {

                    }

                }


            }

            if (position == 0) {
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 8.dp
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = 0
            }
            else if (position == 3) {
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = 8.dp
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 0
            }
            else
            {
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = 0
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 0
            }

        }
    }
}

internal interface prayerTimeCallback
{
    fun nextPrayerCountownFinish()
    fun allPrayerPage()

    fun prayerTask(momentName: String?)
}