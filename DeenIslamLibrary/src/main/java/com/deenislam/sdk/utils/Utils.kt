package com.deenislam.sdk.utils

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ParseException
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.R
import com.deenislam.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.Reader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random


fun View.visible(isvisible:Boolean)
{
    visibility = if(isvisible) View.VISIBLE else View.GONE
}

fun View.show()
{
    if(!this.isVisible)
    visibility = View.VISIBLE
}

fun View.hide()
{
    if(this.isVisible)
    visibility = View.GONE
}

fun View.invisible()
{
    visibility = View.INVISIBLE
}

fun Context.toast(msg:String)
{
    Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

@SuppressLint("ServiceCast")
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

fun NavController.isFragmentInBackStack(destinationId: Int) =
    try {
        this.getBackStackEntry(destinationId)
        true
    } catch (e: Exception) {
        Log.e("POPBACKFAILED",e.toString())
        false
    }


fun Fragment.isFragmentInBackStack(destinationId: Int) =
    try {
        findNavController().getBackStackEntry(destinationId)
        true
    } catch (e: Exception) {
        false
    }


val Int.isBottomNavFragment: Boolean
   get() =
       when(this)
           {
               R.id.dashboardFragment -> true
               else -> false
           }



fun String.toRequestBody(type:String): RequestBody {
    val mediaType = MediaType.parse(type)
    val requestBody = RequestBody.create(mediaType, this)

    return requestBody
}


fun String.StringTimeToMillisecond(pattern: String="HH:mm:ss"): Long {
    //String date_ = date;
    val sdf = SimpleDateFormat(pattern, Locale.US)
    try {
        val mDate = sdf.parse(this)
        var timeInMilliseconds = mDate?.time

      /*  if (timeInMilliseconds != null) {
            if(timeInMilliseconds<0L)
                timeInMilliseconds = - timeInMilliseconds
        }*/

        return timeInMilliseconds?:0
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return 0
}

fun Long.MilliSecondToStringTime(pattern:String = "hh:mm aa",plusDate:Int=0):String
{
    val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
    var convertedTime = sdf.format(this)
    if(plusDate!=0)
    {
        try {
            val c = Calendar.getInstance()
            c.time = sdf.parse(convertedTime) as Date
            c.add(Calendar.DAY_OF_YEAR, plusDate)
            val newDate = Date(c.timeInMillis)
            convertedTime = sdf.format(newDate)
        }
        catch (_:java.lang.Exception)
        {

        }
    }

    return convertedTime
}

fun Long.TimeDiffForPrayer():String
{
    val Hours = (this / (1000 * 60 * 60))
    val Mins = (this / (1000 * 60)).toInt() % 60
    val Secs = ((this / 1000).toInt() % 60).toLong()

    var TxtHour = Hours.toString()
    var TxtMins = Mins.toString()
    var TxtSecs = Secs.toString()

    if(Hours<10)
        TxtHour = "0$Hours"

    if(Mins<10)
        TxtMins = "0$Mins"

    if(Secs<10)
        TxtSecs = "0$TxtSecs"

    return "${TxtHour}:${TxtMins}:$TxtSecs"
}

fun Long.convertNotificationAlarmTime()
{
    val Hours = (this / (1000 * 60 * 60))
    val Mins = (this / (1000 * 60)).toInt() % 60
    val Secs = ((this / 1000).toInt() % 60).toLong()


}

fun PrayerRemainingTime(fromTime:Long,toTime:Long,dayPlus:Int=0):Long
{
    val format = SimpleDateFormat("dd/M/yyyy hh:mm:ss aa", Locale.ENGLISH) // or you can add before dd/M/yyyy

    val date1 = format.parse(fromTime.MilliSecondToStringTime("dd/M/yyyy hh:mm:ss aa",dayPlus))
    val date2 = format.parse(toTime.MilliSecondToStringTime("dd/M/yyyy hh:mm:ss aa"))

    val mills = date1.time - date2.time

    return mills
}


fun TimeDiffForPrayerNew(fromTime:Long,toTime:Long,dayPlus:Int=0):String
{
    val format = SimpleDateFormat("dd/M/yyyy hh:mm:ss aa", Locale.ENGLISH) // or you can add before dd/M/yyyy

    val date1 = format.parse(fromTime.MilliSecondToStringTime("dd/M/yyyy hh:mm:ss aa",dayPlus))
    val date2 = format.parse(toTime.MilliSecondToStringTime("dd/M/yyyy hh:mm:ss aa"))

    val mills = date1.time - date2.time

    val hours = (mills / (1000 * 60 * 60)).toInt()
    val mins = (mills / (1000 * 60)).toInt() % 60
    val secs = ((mills / 1000).toInt() % 60).toLong().toString()

    return "$hours:$mins:$secs"
}

internal fun getPrayerTimeName(data: PrayerTimesResponse,nowtime:Long):PrayerMomentRange {

    val fajr = data.Data.Fajr.StringTimeToMillisecond()
    val ishrak = data.Data.Ishrak.StringTimeToMillisecond()
    val noon = data.Data.Noon.StringTimeToMillisecond()
    val sunrise = data.Data.Sunrise.StringTimeToMillisecond()
    val duhur = data.Data.Juhr.StringTimeToMillisecond()
    val asr = data.Data.Asr.StringTimeToMillisecond()
    val magrib = data.Data.Magrib.StringTimeToMillisecond()
    val isha = data.Data.Isha.StringTimeToMillisecond()
    val tahajjut = data.Data.Tahajjut.StringTimeToMillisecond()



    Log.e("UTIL_PRAYER1",nowtime.toString()+" "+isha+" "+tahajjut+" "+ishrak+" ")

     if(nowtime>=isha &&  (tahajjut<nowtime && nowtime>0) || (nowtime<tahajjut && nowtime<0))
        return PrayerMomentRange("Isha",isha.MilliSecondToStringTime(),tahajjut.minus(60000L).MilliSecondToStringTime(),"Tahajjud", PrayerRemainingTime(tahajjut,nowtime,if(nowtime>0)1 else 0))
    else if(nowtime>=tahajjut && nowtime<fajr && nowtime<0)
        return PrayerMomentRange("Tahajjud",tahajjut.MilliSecondToStringTime(),fajr.minus(60000L).MilliSecondToStringTime(),"Fajr",fajr-nowtime)
    else if(nowtime>=fajr && nowtime<sunrise && nowtime<0)
        return PrayerMomentRange("Fajr",fajr.MilliSecondToStringTime(),sunrise.minus(60000L).MilliSecondToStringTime(),"Ishraq",sunrise-nowtime)
    else if(nowtime>=ishrak && nowtime<noon)
        return PrayerMomentRange("Ishraq",ishrak.MilliSecondToStringTime(),duhur.minus(60000L).MilliSecondToStringTime(),"Dhuhr",duhur-nowtime)
     /*else if(nowtime>=noon && nowtime<duhur)
         return PrayerMomentRange("Noon",noon.MilliSecondToStringTime(),duhur.minus(60000L).MilliSecondToStringTime(),"Dhuhr",duhur-nowtime)
     */
     else if(nowtime>=duhur && nowtime<asr)
        return PrayerMomentRange("Dhuhr",duhur.MilliSecondToStringTime(),asr.minus(60000L).MilliSecondToStringTime(),"Asr",asr-nowtime)
    else if(nowtime>=asr && nowtime<magrib)
        return PrayerMomentRange("Asr",asr.MilliSecondToStringTime(),magrib.minus(60000L).MilliSecondToStringTime(),"Maghrib",magrib-nowtime)
    else if(nowtime>=magrib && nowtime<(isha-300000L))
        return PrayerMomentRange("Maghrib",magrib.MilliSecondToStringTime(),(isha-300000L).MilliSecondToStringTime(),"--",(isha-300000L)-nowtime)
    else
    {

        val prayerTimeArray= arrayListOf(
            PrayerRemainingTime(fajr,nowtime,if(nowtime>0)1 else 0),
            PrayerRemainingTime(ishrak,nowtime,if(nowtime>0)1 else 0),
            PrayerRemainingTime(duhur,nowtime,if(nowtime>0)0 else 1),
            PrayerRemainingTime(asr,nowtime,if(nowtime>0)1 else 0),
            PrayerRemainingTime(magrib,nowtime,if(nowtime>0)1 else 0),
            PrayerRemainingTime(isha,nowtime,if(nowtime>0)0 else 1),
            PrayerRemainingTime(tahajjut,nowtime,if(nowtime>0)1 else 0)
            )

        return findNextPrayer(prayerTimeArray,nowtime)
       // return PrayerMomentRange("Not Praying","0:00","0:00","Fajr",fajr-nowtime)
    }
}

internal fun findNextPrayer(timeArray: ArrayList<Long>, nowtime: Long):PrayerMomentRange
{

    var temp = 0L

    val oldTimeArray:ArrayList<Long> = arrayListOf()
    oldTimeArray.addAll(timeArray)

    Log.e("UTIL_PRAYER",Gson().toJson(oldTimeArray))

    for (i in 0 until timeArray.size-1)
    {
        for (j in i+1 until timeArray.size)
        {
            if(timeArray[j]<timeArray[i]) {
                Log.e("J_TIME",timeArray[j].toString())
                temp = timeArray[j]
                timeArray[j] = timeArray[i];
                timeArray[i] = temp;
                /*prayerPosition = j+1
                prayerReaminingTime = timeArray[j]*/
            }
        }
    }

    Log.e("UTIL_PRAYER",Gson().toJson(oldTimeArray))


    return PrayerMomentRange("--",nowtime.MilliSecondToStringTime(),(nowtime+timeArray[0]).minus(60000L).MilliSecondToStringTime(),getPrayerNameByID(oldTimeArray.indexOf(timeArray[0])+1),timeArray[0])

}

fun getPrayerNameByID(id:Int):String
{
    return when(id)
    {
        1-> "Fajr"
        2-> "Ishraq"
        3-> "Dhuhr"
        4-> "Asr"
        5-> "Maghrib"
        6-> "Isha"
        7-> "Tahajjud"
        else -> "--"
    }
}

fun RecyclerView.runWhenReady(action: () -> Unit) {
    val globalLayoutListener = object: ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            action()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    }
    viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
}

fun RecyclerView.setLinearSnapHelper(isReversed: Boolean = false) {
    object : LinearSnapHelper() {

        override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
            val firstVisiblePosition = (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()
            val firstItem = 0
            val lastItem = layoutManager.itemCount - 1
            return when {
                firstItem == firstVisiblePosition -> layoutManager.findViewByPosition(firstVisiblePosition)
                lastItem == lastVisiblePosition -> layoutManager.findViewByPosition(lastVisiblePosition)
                else -> super.findSnapView(layoutManager)
            }
        }
    }.apply { attachToRecyclerView(this@setLinearSnapHelper) }
}

fun CaptureScreen(v: View): Bitmap? {
    try {
    val b =
        Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    v.layout(v.left, v.top, v.right, v.bottom)
    v.draw(c)

        return b
        }catch (e:Exception)
        {
           return null
        }

}

inline fun <reified T> fromJson(json: Reader): T {
    return Gson().fromJson(json, object : TypeToken<T>() {}.type)
}


fun <T> tryCatch(block: () -> T) =
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }


fun String.formateDateTime(stringPattern:String,resultPattern: String):String=
    try {
        this.StringTimeToMillisecond(stringPattern).MilliSecondToStringTime(resultPattern)
    }catch (e:Exception)
    {
        ""
    }

inline fun <T: View> T.afterMeasured(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}


fun ViewPager2.disableSmoothScroll() {
    (getChildAt(0) as? RecyclerView)?.apply {
        val smoothScrollerField = ViewPager2::class.java.getDeclaredField("mSmoothScroller")
        smoothScrollerField.isAccessible = true
        smoothScrollerField.set(this@disableSmoothScroll, null)
    }
}

/*fun Context.startAlarmLooper(loopReceiverClass: Class<out AlarmLoopReceiver>) { // <1>
    sendBroadcast(Intent(this, loopReceiverClass).apply {
        action = ACTION_START_ALARM_LOOPER
    })
}*/

fun ViewPager2.reduceDragSensitivity(f: Int = 4) {
    val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
    recyclerViewField.isAccessible = true
    val recyclerView = recyclerViewField.get(this) as RecyclerView

    val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
    touchSlopField.isAccessible = true
    val touchSlop = touchSlopField.get(recyclerView) as Int
    touchSlopField.set(recyclerView, touchSlop*f)       // "8" was obtained experimentally
}

private var currentItemAnimator: ValueAnimator? = null

fun ViewPager2.setCurrentItem(
    item: Int,
    duration: Long,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
    pagePxWidth: Int = width // Default value taken from getWidth() from ViewPager2 view
) {

    // Cancel the ongoing animation, if any
    currentItemAnimator?.cancel()
    currentItemAnimator?.end()

    Log.e("isFakeDragging", currentItemAnimator.toString())

    val pxToDrag: Int = pagePxWidth * (item - currentItem)
    val animator = ValueAnimator.ofInt(0, pxToDrag)
    var previousValue = 0
    animator.addUpdateListener { valueAnimator ->
        val currentValue = valueAnimator.animatedValue as Int
        val currentPxToDrag = (currentValue - previousValue).toFloat()
        if(!isFakeDragging) beginFakeDrag()
        fakeDragBy(-currentPxToDrag)
        previousValue = currentValue
    }
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) { if(!isFakeDragging) beginFakeDrag() }
        override fun onAnimationEnd(p0: Animator) { endFakeDrag() }
        override fun onAnimationCancel(p0: Animator) { /* Ignored */  endFakeDrag()  }
        override fun onAnimationRepeat(p0: Animator) { /* Ignored */  }
    })
    animator.interpolator = interpolator
    animator.duration = duration
    animator.startDelay = 0

    animator.start()

    currentItemAnimator = animator

}

fun get9DigitRandom(): Long {
    val random = Random
    val part1 = random.nextInt(1000, 10000)  // generates a random 4-digit number
    val part2 = random.nextInt(10000, 100000)  // generates a random 5-digit number
    val trackingID = "$part1$part2".toLong() // concatenate the two numbers

    return trackingID
}
