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
import okhttp3.MediaType.Companion.toMediaType
import java.io.Reader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


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

fun getFlagByCC(country_code:String): Int {
    return when (country_code) {
        "ad" -> R.drawable.flag_andorra
        "ae" -> R.drawable.flag_uae
        "af" -> R.drawable.flag_afghanistan
        "ag" -> R.drawable.flag_antigua_and_barbuda
        "ai" -> R.drawable.flag_anguilla
        "al" -> R.drawable.flag_albania
        "am" -> R.drawable.flag_armenia
        "ao" -> R.drawable.flag_angola
        "aq" -> R.drawable.flag_antarctica
        "ar" -> R.drawable.flag_argentina
        "as" -> R.drawable.flag_american_samoa
        "at" -> R.drawable.flag_austria
        "au" -> R.drawable.flag_australia
        "aw" -> R.drawable.flag_aruba
        "ax" -> R.drawable.flag_aland
        "az" -> R.drawable.flag_azerbaijan
        "ba" -> R.drawable.flag_bosnia
        "bb" -> R.drawable.flag_barbados
        "bd" -> R.drawable.flag_bangladesh
        "be" -> R.drawable.flag_belgium
        "bf" -> R.drawable.flag_burkina_faso
        "bg" -> R.drawable.flag_bulgaria
        "bh" -> R.drawable.flag_bahrain
        "bi" -> R.drawable.flag_burundi
        "bj" -> R.drawable.flag_benin
        "bl" -> R.drawable.flag_saint_barthelemy // custom
        "bm" -> R.drawable.flag_bermuda
        "bn" -> R.drawable.flag_brunei
        "bo" -> R.drawable.flag_bolivia
        "br" -> R.drawable.flag_brazil
        "bs" -> R.drawable.flag_bahamas
        "bt" -> R.drawable.flag_bhutan
        "bw" -> R.drawable.flag_botswana
        "by" -> R.drawable.flag_belarus
        "bz" -> R.drawable.flag_belize
        "ca" -> R.drawable.flag_canada
        "cc" -> R.drawable.flag_cocos // custom
        "cd" -> R.drawable.flag_democratic_republic_of_the_congo
        "cf" -> R.drawable.flag_central_african_republic
        "cg" -> R.drawable.flag_republic_of_the_congo
        "ch" -> R.drawable.flag_switzerland
        "ci" -> R.drawable.flag_cote_divoire
        "ck" -> R.drawable.flag_cook_islands
        "cl" -> R.drawable.flag_chile
        "cm" -> R.drawable.flag_cameroon
        "cn" -> R.drawable.flag_china
        "co" -> R.drawable.flag_colombia
        "cr" -> R.drawable.flag_costa_rica
        "cu" -> R.drawable.flag_cuba
        "cv" -> R.drawable.flag_cape_verde
        "cw" -> R.drawable.flag_curacao
        "cx" -> R.drawable.flag_christmas_island
        "cy" -> R.drawable.flag_cyprus
        "cz" -> R.drawable.flag_czech_republic
        "de" -> R.drawable.flag_germany
        "dj" -> R.drawable.flag_djibouti
        "dk" -> R.drawable.flag_denmark
        "dm" -> R.drawable.flag_dominica
        "do" -> R.drawable.flag_dominican_republic
        "dz" -> R.drawable.flag_algeria
        "ec" -> R.drawable.flag_ecuador
        "ee" -> R.drawable.flag_estonia
        "eg" -> R.drawable.flag_egypt
        "er" -> R.drawable.flag_eritrea
        "es" -> R.drawable.flag_spain
        "et" -> R.drawable.flag_ethiopia
        "fi" -> R.drawable.flag_finland
        "fj" -> R.drawable.flag_fiji
        "fk" -> R.drawable.flag_falkland_islands
        "fm" -> R.drawable.flag_micronesia
        "fo" -> R.drawable.flag_faroe_islands
        "fr" -> R.drawable.flag_france
        "ga" -> R.drawable.flag_gabon
        "gb" -> R.drawable.flag_united_kingdom
        "gd" -> R.drawable.flag_grenada
        "ge" -> R.drawable.flag_georgia
        "gf" -> R.drawable.flag_guyane
        "gg" -> R.drawable.flag_guernsey
        "gh" -> R.drawable.flag_ghana
        "gi" -> R.drawable.flag_gibraltar
        "gl" -> R.drawable.flag_greenland
        "gm" -> R.drawable.flag_gambia
        "gn" -> R.drawable.flag_guinea
        "gp" -> R.drawable.flag_guadeloupe
        "gq" -> R.drawable.flag_equatorial_guinea
        "gr" -> R.drawable.flag_greece
        "gt" -> R.drawable.flag_guatemala
        "gu" -> R.drawable.flag_guam
        "gw" -> R.drawable.flag_guinea_bissau
        "gy" -> R.drawable.flag_guyana
        "hk" -> R.drawable.flag_hong_kong
        "hn" -> R.drawable.flag_honduras
        "hr" -> R.drawable.flag_croatia
        "ht" -> R.drawable.flag_haiti
        "hu" -> R.drawable.flag_hungary
        "id" -> R.drawable.flag_indonesia
        "ie" -> R.drawable.flag_ireland
        "il" -> R.drawable.flag_israel
        "im" -> R.drawable.flag_isleof_man // custom
        "is" -> R.drawable.flag_iceland
        "in" -> R.drawable.flag_india
        "io" -> R.drawable.flag_british_indian_ocean_territory
        "iq" -> R.drawable.flag_iraq_new
        "ir" -> R.drawable.flag_iran
        "it" -> R.drawable.flag_italy
        "je" -> R.drawable.flag_jersey
        "jm" -> R.drawable.flag_jamaica
        "jo" -> R.drawable.flag_jordan
        "jp" -> R.drawable.flag_japan
        "ke" -> R.drawable.flag_kenya
        "kg" -> R.drawable.flag_kyrgyzstan
        "kh" -> R.drawable.flag_cambodia
        "ki" -> R.drawable.flag_kiribati
        "km" -> R.drawable.flag_comoros
        "kn" -> R.drawable.flag_saint_kitts_and_nevis
        "kp" -> R.drawable.flag_north_korea
        "kr" -> R.drawable.flag_south_korea
        "kw" -> R.drawable.flag_kuwait
        "ky" -> R.drawable.flag_cayman_islands
        "kz" -> R.drawable.flag_kazakhstan
        "la" -> R.drawable.flag_laos
        "lb" -> R.drawable.flag_lebanon
        "lc" -> R.drawable.flag_saint_lucia
        "li" -> R.drawable.flag_liechtenstein
        "lk" -> R.drawable.flag_sri_lanka
        "lr" -> R.drawable.flag_liberia
        "ls" -> R.drawable.flag_lesotho
        "lt" -> R.drawable.flag_lithuania
        "lu" -> R.drawable.flag_luxembourg
        "lv" -> R.drawable.flag_latvia
        "ly" -> R.drawable.flag_libya
        "ma" -> R.drawable.flag_morocco
        "mc" -> R.drawable.flag_monaco
        "md" -> R.drawable.flag_moldova
        "me" -> R.drawable.flag_of_montenegro // custom
        "mf" -> R.drawable.flag_saint_martin
        "mg" -> R.drawable.flag_madagascar
        "mh" -> R.drawable.flag_marshall_islands
        "mk" -> R.drawable.flag_macedonia
        "ml" -> R.drawable.flag_mali
        "mm" -> R.drawable.flag_myanmar
        "mn" -> R.drawable.flag_mongolia
        "mo" -> R.drawable.flag_macao
        "mp" -> R.drawable.flag_northern_mariana_islands
        "mq" -> R.drawable.flag_martinique
        "mr" -> R.drawable.flag_mauritania
        "ms" -> R.drawable.flag_montserrat
        "mt" -> R.drawable.flag_malta
        "mu" -> R.drawable.flag_mauritius
        "mv" -> R.drawable.flag_maldives
        "mw" -> R.drawable.flag_malawi
        "mx" -> R.drawable.flag_mexico
        "my" -> R.drawable.flag_malaysia
        "mz" -> R.drawable.flag_mozambique
        "na" -> R.drawable.flag_namibia
        "nc" -> R.drawable.flag_new_caledonia // custom
        "ne" -> R.drawable.flag_niger
        "nf" -> R.drawable.flag_norfolk_island
        "ng" -> R.drawable.flag_nigeria
        "ni" -> R.drawable.flag_nicaragua
        "nl" -> R.drawable.flag_netherlands
        "no" -> R.drawable.flag_norway
        "np" -> R.drawable.flag_nepal
        "nr" -> R.drawable.flag_nauru
        "nu" -> R.drawable.flag_niue
        "nz" -> R.drawable.flag_new_zealand
        "om" -> R.drawable.flag_oman
        "pa" -> R.drawable.flag_panama
        "pe" -> R.drawable.flag_peru
        "pf" -> R.drawable.flag_french_polynesia
        "pg" -> R.drawable.flag_papua_new_guinea
        "ph" -> R.drawable.flag_philippines
        "pk" -> R.drawable.flag_pakistan
        "pl" -> R.drawable.flag_poland
        "pm" -> R.drawable.flag_saint_pierre
        "pn" -> R.drawable.flag_pitcairn_islands
        "pr" -> R.drawable.flag_puerto_rico
        "ps" -> R.drawable.flag_palestine
        "pt" -> R.drawable.flag_portugal
        "pw" -> R.drawable.flag_palau
        "py" -> R.drawable.flag_paraguay
        "qa" -> R.drawable.flag_qatar
        "re" -> R.drawable.flag_martinique // no exact flag found
        "ro" -> R.drawable.flag_romania
        "rs" -> R.drawable.flag_serbia // custom
        "ru" -> R.drawable.flag_russian_federation
        "rw" -> R.drawable.flag_rwanda
        "sa" -> R.drawable.flag_saudi_arabia
        "sb" -> R.drawable.flag_soloman_islands
        "sc" -> R.drawable.flag_seychelles
        "sd" -> R.drawable.flag_sudan
        "se" -> R.drawable.flag_sweden
        "sg" -> R.drawable.flag_singapore
        "sh" -> R.drawable.flag_saint_helena // custom
        "si" -> R.drawable.flag_slovenia
        "sk" -> R.drawable.flag_slovakia
        "sl" -> R.drawable.flag_sierra_leone
        "sm" -> R.drawable.flag_san_marino
        "sn" -> R.drawable.flag_senegal
        "so" -> R.drawable.flag_somalia
        "sr" -> R.drawable.flag_suriname
        "ss" -> R.drawable.flag_south_sudan
        "st" -> R.drawable.flag_sao_tome_and_principe
        "sv" -> R.drawable.flag_el_salvador
        "sx" -> R.drawable.flag_sint_maarten
        "sy" -> R.drawable.flag_syria
        "sz" -> R.drawable.flag_swaziland
        "tc" -> R.drawable.flag_turks_and_caicos_islands
        "td" -> R.drawable.flag_chad
        "tg" -> R.drawable.flag_togo
        "th" -> R.drawable.flag_thailand
        "tj" -> R.drawable.flag_tajikistan
        "tk" -> R.drawable.flag_tokelau // custom
        "tl" -> R.drawable.flag_timor_leste
        "tm" -> R.drawable.flag_turkmenistan
        "tn" -> R.drawable.flag_tunisia
        "to" -> R.drawable.flag_tonga
        "tr" -> R.drawable.flag_turkey
        "tt" -> R.drawable.flag_trinidad_and_tobago
        "tv" -> R.drawable.flag_tuvalu
        "tw" -> R.drawable.flag_taiwan
        "tz" -> R.drawable.flag_tanzania
        "ua" -> R.drawable.flag_ukraine
        "ug" -> R.drawable.flag_uganda
        "us" -> R.drawable.flag_united_states_of_america
        "uy" -> R.drawable.flag_uruguay
        "uz" -> R.drawable.flag_uzbekistan
        "va" -> R.drawable.flag_vatican_city
        "vc" -> R.drawable.flag_saint_vicent_and_the_grenadines
        "ve" -> R.drawable.flag_venezuela
        "vg" -> R.drawable.flag_british_virgin_islands
        "vi" -> R.drawable.flag_us_virgin_islands
        "vn" -> R.drawable.flag_vietnam
        "vu" -> R.drawable.flag_vanuatu
        "wf" -> R.drawable.flag_wallis_and_futuna
        "ws" -> R.drawable.flag_samoa
        "xk" -> R.drawable.flag_kosovo
        "ye" -> R.drawable.flag_yemen
        "yt" -> R.drawable.flag_martinique // no exact flag found
        "za" -> R.drawable.flag_south_africa
        "zm" -> R.drawable.flag_zambia
        "zw" -> R.drawable.flag_zimbabwe
        else -> R.drawable.flag_transparent
    }
}


val Int.isBottomNavFragment: Boolean
   get() =
       when(this)
           {
               R.id.dashboardFragment -> true
               R.id.quranFragment -> true
               R.id.prayerTimesFragment -> true
               R.id.moreFragment -> true
               else -> false
           }


val RequestBodyMediaType = "application/json; charset=utf-8".toMediaType()


fun String.StringTimeToMillisecond(pattern: String="HH:mm:ss"): Long {
    //String date_ = date;
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
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

fun getPrayerTimeName(data: PrayerTimesResponse,nowtime:Long):PrayerMomentRange {

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
        return PrayerMomentRange("Isha",isha.MilliSecondToStringTime(),tahajjut.minus(60000L).MilliSecondToStringTime(),"Tahajjut", PrayerRemainingTime(tahajjut,nowtime,if(nowtime>0)1 else 0))
    else if(nowtime>=tahajjut && nowtime<fajr && nowtime<0)
        return PrayerMomentRange("Tahajjut",tahajjut.MilliSecondToStringTime(),fajr.minus(60000L).MilliSecondToStringTime(),"Fajr",fajr-nowtime)
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

fun findNextPrayer(timeArray: ArrayList<Long>, nowtime: Long):PrayerMomentRange
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
        7-> "Tahajjut"
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
