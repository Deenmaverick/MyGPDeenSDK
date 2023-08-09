package com.deenislam.sdk.views.adapters.dashboard

import android.os.CountDownTimer
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
import com.deenislam.sdk.service.callback.DashboardPatchCallback
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislam.sdk.service.network.response.dashboard.Banner
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.service.network.response.prayertimes.tracker.Data
import com.deenislam.sdk.utils.AsyncViewStub
import com.deenislam.sdk.utils.StringTimeToMillisecond
import com.deenislam.sdk.utils.TimeDiffForPrayer
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.getPrayerTimeName
import com.deenislam.sdk.utils.getWaktNameByTag
import com.deenislam.sdk.utils.get_prayer_tag_by_name
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.utils.prayerMomentLocale
import com.deenislam.sdk.utils.timeLocale
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.views.base.BaseViewHolder
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val PRAYER_VIEW = 1
private const val BILLBOARD_VIEW = 2
internal class DashboardBillboardAdapter(
    private val callback: prayerTimeCallback? = null,
    private val dashboardPatchCallback: DashboardPatchCallback
) : RecyclerView.Adapter<BaseViewHolder>(){

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


    //banner
    private var billboardBanner:ArrayList<AppCompatImageView> = arrayListOf()

    private var prayerData:PrayerTimesResponse ? = null
    private var countDownTimer:CountDownTimer?=null
    private var prayerNotificationData:ArrayList<PrayerNotification> ? =null
    private var billboardData:ArrayList<Banner> = arrayListOf()

    fun update(data: PrayerTimesResponse)
    {
        prayerData = data
        notifyDataSetChanged()
    }

    fun updatePrayerTracker(data: Data)
    {
        prayerData?.Data?.WaktTracker?.forEach {

            when(it.Wakt)
            {
                "Fajr" -> it.status = data.Fajr
                "Zuhr" -> it.status = data.Zuhr
                "Asar" -> it.status = data.Asar
                "Maghrib" -> it.status = data.Maghrib
                "Isha" -> it.status = data.Isha
            }

        }

        notifyDataSetChanged()
    }

    fun updateBillboard(data: List<Banner>)
    {
        billboardData.clear()
        billboardData.addAll(data)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    {

        val main_view = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_dashboard_main, parent, false)

        when(viewType)
        {
            PRAYER_VIEW ->
            {
                prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.item_dashboard_prayer_time) {

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
                            R.color.black,
                        )
                    )
                    allPrayer.setOnClickListener {
                        callback?.allPrayerPage()
                    }

                    billboardBanner.add(AppCompatImageView(prayerBG.context))

                    callback?.billboard_prayer_load_complete()

                }



            }
            BILLBOARD_VIEW ->
            {
                prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.item_dashboard_billboard) {
                    // banner
                    billboardBanner.add(this.findViewById(R.id.billboardBanner))
                }
                /*ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_dashboard_billboard, parent, false))*/
            }

            else -> throw java.lang.IllegalArgumentException("View cannot null")
        }


        return  ViewHolder(main_view)
    }

    // init view function



    private fun widget1_view()
    {
        if(!this::allPrayer.isInitialized)
            return

        val getContext = prayerBG.context

        val currentTime = SimpleDateFormat("hh:mm:ss aa", Locale.getDefault()).format(Date())
        val prayerMomentRangeData: PrayerMomentRange? =  prayerData?.let { getPrayerTimeName(it,currentTime.StringTimeToMillisecond("hh:mm:ss aa") /*getPrayerTimeName(it,"08:01:45 PM".StringTimeToMillisecond("hh:mm:ss aa") */ ) }

        askingLy.text = getContext.resources.getString(R.string.billboard_have_you_prayed,prayerMomentRangeData?.MomentName?.prayerMomentLocale())
        prayerTracker(true)

        val get_prayer_tag = get_prayer_tag_by_name(prayerMomentRangeData?.MomentName.toString())

        val checkTrack = prayerData?.Data?.WaktTracker?.indexOfFirst {
            it.Wakt == get_prayer_tag.getWaktNameByTag() && it.status

        }


        //prayerCheck.isEnabled = !(checkTrack!=null && checkTrack >=0)
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
        else if(prayerMomentRangeData?.MomentName == "Ishraq")
            prayerBG.setBackgroundResource(R.drawable.fajr)
        else {
            //prayerBG.setBackgroundResource(R.drawable.isha)
            prayerTracker(false)
            prayerBG.setBackgroundColor(
                ContextCompat.getColor(getContext,
                    R.color.black
                )
            )

        }
        progressTxt.text = "${getCompletedPrayerCount()}/5".numberLocale()
        prayerMoment.text = prayerMomentRangeData?.MomentName?.prayerMomentLocale()
        namazTask.progress = getCompletedPrayerCount()*20
        prayerMomentRange.text = prayerMomentRangeData?.StartTime?.timeLocale() +" - " + prayerMomentRangeData?.EndTime?.timeLocale()
        nextPrayerName.text = getContext.resources.getString(R.string.billboard_next_prayer,prayerMomentRangeData?.NextPrayerName?.prayerMomentLocale()?:"--")
        nextPrayerTime.text = "-00:00:00".timeLocale()


        val prayerIsChecked = prayerCheck.isChecked

        prayerCheck.setOnClickListener {
            callback?.prayerTask(get_prayer_tag,!prayerIsChecked)
        }

        prayerMomentRangeData?.nextPrayerTimeCount?.let {
            if(it>0) {
                nextPrayerTime.text = "-"+prayerMomentRangeData.nextPrayerTimeCount?.TimeDiffForPrayer().numberLocale()
                countDownTimer?.cancel()
                countDownTimer = object : CountDownTimer(it, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        nextPrayerTime.text = "-" + millisUntilFinished.TimeDiffForPrayer().numberLocale()
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

    private fun widget2_view(position: Int)
    {
        if (billboardData.size>0) {
            if (billboardBanner.isEmpty() || billboardData[position].Text == "PrayerTime")
                return

            billboardBanner[position].setOnClickListener {
                dashboardPatchCallback.dashboardPatchClickd(billboardData[position].Text)
            }

            billboardBanner[position].imageLoad(
                url = billboardData[position].contentBaseUrl + "/" + billboardData[position].imageurl1,
                ic_medium = true
            )
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
        prayerData?.Data?.WaktTracker?.let {
            it.forEach {
                    it1->
                if(it1.status) {
                    count++
                }
            }
        }
        
        return  count
    }

    override fun getItemViewType(position: Int): Int {

         /*if(position == ceil(itemCount.toDouble() / 2).toInt())*/
        return if(billboardData[position].Text == "PrayerTime")
            PRAYER_VIEW
        else
            BILLBOARD_VIEW
    }

    override fun getItemCount(): Int {
        return billboardData.size
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

    internal inner class ViewHolder(itemView: View) : BaseViewHolder(itemView)
    {

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

                when (viewtype) {
                    PRAYER_VIEW -> {
                        widget1_view()
                    }
                    BILLBOARD_VIEW -> {
                        widget2_view(position)
                    }
                }


            if (position == 0) {
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 8.dp
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = 0
            }
            else if (position == itemCount-1) {
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

    fun prayerTask(momentName: String?, b: Boolean)

    fun billboard_prayer_load_complete()
}