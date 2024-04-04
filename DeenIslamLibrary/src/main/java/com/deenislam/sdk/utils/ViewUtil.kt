package com.deenislam.sdk.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageView
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStateAtLeast
import coil.decode.SvgDecoder
import coil.load
import coil.request.CachePolicy
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


internal inline fun <T : View> prepareStubView(
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

fun AppCompatImageView.imageLoad(
    url:String,
    placeholder_16_9:Boolean=false,
    placeholder_1_1:Boolean=false,
    placeholder_4_3:Boolean=false,
    size:Int = 0,
    custom_placeholder_1_1:Int = R.drawable.placeholder_1_1,
    isCacheEnable:Boolean = true,
    customMemoryKey:String = ""
)
{
    val context = this.context

    var finalUrl = url

    if(size>0)
        finalUrl = url.replace("<size>", size.toString(),true)


    this.load(finalUrl)
    {

        allowHardware(false)
        if(customMemoryKey.isNotEmpty())
            memoryCacheKey(customMemoryKey)

        if(!isCacheEnable) {
            memoryCachePolicy(CachePolicy.DISABLED)
            diskCachePolicy(CachePolicy.DISABLED)
        }

        if(placeholder_16_9) {
            placeholder(R.drawable.deen_placeholder_16_9)
            error(R.drawable.deen_placeholder_16_9)
        }
        else if(placeholder_1_1) {
            placeholder(custom_placeholder_1_1)
            error(custom_placeholder_1_1)
        }
        else if(placeholder_4_3) {
            placeholder(R.drawable.deen_placeholder_4_3)
            error(R.drawable.deen_placeholder_4_3)
        }

        crossfade(false)


    }
}

fun getFileExtension(fileName: String): String {
    val lastDotIndex = fileName.lastIndexOf('.')
    if (lastDotIndex > 0 && lastDotIndex < fileName.length - 1) {
        return fileName.substring(lastDotIndex + 1).toLowerCase()
    }
    return ""
}


fun Context.getLocalContext(): Context {

    val localContext: Context = LocaleUtil.createLocaleContext(this, Locale("bn"))
    /*if (DeenSDKCore.GetDeenLanguage() == "en") {
        LocaleUtil.createLocaleContext(this, Locale("en"))
    } else {
        LocaleUtil.createLocaleContext(this, Locale("bn"))
    }*/

    return ContextThemeWrapper(localContext, R.style.DeenSDKTheme)

}

fun String.numberLocale():String
{
    if(DeenSDKCore.GetDeenLanguage() == "bn") {
        val numberMap = mapOf(
            '0' to '০',
            '1' to '১',
            '2' to '২',
            '3' to '৩',
            '4' to '৪',
            '5' to '৫',
            '6' to '৬',
            '7' to '৭',
            '8' to '৮',
            '9' to '৯'
        )

        return this.map { ch -> numberMap[ch] ?: ch }.joinToString("")
    }
    else
        return this

}

fun String.timeLocale():String
{
    if(DeenSDKCore.GetDeenLanguage() == "bn") {
        val numberMap = mapOf(
            '0' to '০',
            '1' to '১',
            '2' to '২',
            '3' to '৩',
            '4' to '৪',
            '5' to '৫',
            '6' to '৬',
            '7' to '৭',
            '8' to '৮',
            '9' to '৯',
           /* 'A' to 'এ',
            'a' to 'এ',
            'P' to "পি",
            'p' to "পি",
            'M' to "এম",
            'm' to "এম",*/
        )

        return this.map { ch -> numberMap[ch] ?: ch }.joinToString("")
    }
    else
        return this

}

fun String.prayerMomentLocaleForToast():String
{
    if(DeenSDKCore.GetDeenLanguage() == "bn") {

       return when(this)
        {
            "Fajr" -> "ফজরের"
           "Zuhr" -> "জোহরের"
           "Asar" -> "আসরের"
           "Maghrib" -> "মাগরিবের"
           "Isha" -> "এশার"
           else -> this
       }

    }
    else
        return this

}

fun String.prayerMomentLocale():String
{
    if(DeenSDKCore.GetDeenLanguage() == "bn") {

        return when(this)
        {
            "Fajr" -> "ফজর"
            "Ishraq" -> "ইশরাক"
            "Dhuhr" -> "জোহর"
            "Asr" -> "আসর"
            "Maghrib" -> "মাগরিব"
            "Isha" -> "এশা"
            "Tahajjud End" -> "তাহাজ্জুদ শেষ"
            "Tahajjud" -> "তাহাজ্জুদ"
            "Sunrise" ->  "সূর্যোদয়"
            "Suhoor (End)" -> "সেহরি শেষ"
            "Iftaar (Start)" -> "ইফতার শুরু"
            "Midday" -> "মধ্যাহ্ন"

            else -> this
        }

    }
    else
        return this

}

fun Int.getSurahNameBn():String
{
    val surahNameBn = arrayListOf(
        "আল ফাতিহা",
        "আল বাকারাহ",
        "আলে-ইমরান",
        "আন নিসা",
        "আল মায়িদাহ",
        "আল আন-আম",
        "আল আ'রাফ",
        "আল আনফাল ",
        "আত-তাওবাহ্‌",
        "ইউনুস",
        "হুদ",
        "ইউসুফ",
        "আর-রাদ",
        "ইব্রাহীম",
        "সূরা আল হিজর",
        "আন নাহল",
        "বনী-ইসরাঈল",
        "আল কাহফ",
        "মারইয়াম",
        "ত্বোয়া-হা",
        "আল আম্বিয়া",
        "আল হাজ্জ্ব",
        "আল মু'মিনূন",
        "আন নূর",
        "আল ফুরকান",
        "আশ শুআরা",
        "আন নম্‌ল",
        "আল কাসাস",
        "আল আনকাবূত",
        "আর রুম",
        "লোক্‌মান",
        "আস সেজদাহ্",
        "আল আহ্‌যাব",
        "সাবা",
        "ফাতির",
        "ইয়াসীন",
        "আস ছাফ্‌ফাত",
        "ছোয়াদ",
        "আয্‌-যুমার",
        "গাফির",
        "হা-মীম সেজদাহ্‌",
        "আশ্‌-শূরা",
        "আয্‌-যুখরুফ",
        "আদ-দোখান",
        "আল জাসিয়াহ",
        "আল আহ্‌ক্বাফ",
        "মুহাম্মদ",
        "আল ফাত্‌হ",
        "আল হুজুরাত",
        "ক্বাফ",
        "আয-যারিয়াত",
        "আত্ব তূর",
        "আন-নাজম",
        "আল ক্বামার",
        "আর রাহমান",
        "আল-ওয়াকিয়াহ",
        "আল-হাদীদ",
        "আল-মুজাদালাহ",
        "আল-হাশর",
        "আল-মুমতাহিনাহ",
        "আস-সাফ",
        "আল-জুমুআ",
        "আল-মুনাফিকুন",
        "আত-তাগাবুন",
        "আত-তালাক",
        "আত-তাহরীম",
        "আল-মুলক",
        "আল-কলম",
        "আল-হাক্কাহ",
        "আল-মাআরিজ",
        "নূহ",
        "আল জ্বিন",
        "আল মুজাম্মিল",
        "আল মুদ্দাস্সির",
        "আল-ক্বিয়ামাহ",
        "আদ-ইনসান",
        "আল-মুরসালাত ",
        "আন নাবা",
        "আন নাযিয়াত",
        "আবাসা",
        "আত-তাকভীর",
        "আল-ইনফিতার",
        "আত মুত্বাফ্‌ফিফীন",
        "আল ইন‌শিকাক",
        "আল-বুরুজ",
        "আত-তারিক্ব",
        "আল আ'লা",
        "আল গাশিয়াহ্‌",
        "আল ফাজ্‌র",
        "আল বালাদ",
        "আশ-শাম্‌স",
        "আল লাইল",
        "আদ-দুহা",
        "আল ইনশিরাহ",
        "আত ত্বীন",
        "আল আলাক্ব",
        "আল ক্বদর",
        "আল বাইয়্যিনাহ",
        "আল যিলযাল",
        "আল-আদিয়াত",
        "আল ক্বারিয়াহ",
        "আল তাকাসুর",
        "আল আছর",
        "আল হুমাযাহ",
        "আল ফীল",
        "আল কুরাইশ",
        "আল মাউন",
        "আল কাওসার",
        "আল কাফিরুন",
        "আন নাসর",
        "আল লাহাব",
        "আল-ইখলাস",
        "আল-ফালাক",
        "আন-নাস")

    return  surahNameBn[this]
}

fun String.surahNameLocale():String
{
    val surahNameEn = arrayListOf<String>(
            "Al-Fatihah",
            "Al-Baqarah",
            "Ali 'Imran",
            "An-Nisa",
            "Al-Ma'idah",
            "Al-An'am",
            "Al-A'raf",
            "Al-Anfal",
            "At-Tawbah",
            "Yunus",
            "Hud",
            "Yusuf",
            "Ar-Ra'd",
            "Ibrahim",
            "Al-Hijr",
            "An-Nahl",
            "Al-Isra",
            "Al-Kahf",
            "Maryam",
            "Taha",
            "Al-Anbya",
            "Al-Hajj",
            "Al-Mu'minun",
            "An-Nur",
            "Al-Furqan",
            "Ash-Shu'ara",
            "An-Naml",
            "Al-Qasas",
            "Al-'Ankabut",
            "Ar-Rum",
            "Luqman",
            "As-Sajdah",
            "Al-Ahzab",
            "Saba",
            "Fatir",
            "Ya-Sin",
            "As-Saffat",
            "Sad",
            "Az-Zumar",
            "Ghafir",
            "Fussilat",
            "Ash-Shuraa",
            "Az-Zukhruf",
            "Ad-Dukhan",
            "Al-Jathiyah",
            "Al-Ahqaf",
            "Muhammad",
            "Al-Fath",
            "Al-Hujurat",
            "Qaf",
            "Adh-Dhariyat",
            "At-Tur",
            "An-Najm",
            "Al-Qamar",
            "Ar-Rahman",
            "Al-Waqi'ah",
            "Al-Hadid",
            "Al-Mujadila",
            "Al-Hashr",
            "Al-Mumtahanah",
            "As-Saf",
            "Al-Jumu'ah",
            "Al-Munafiqun",
            "At-Taghabun",
            "At-Talaq",
            "At-Tahrim",
            "Al-Mulk",
            "Al-Qalam",
            "Al-Haqqah",
            "Al-Ma'arij",
            "Nuh",
            "Al-Jinn",
            "Al-Muzzammil",
            "Al-Muddaththir",
            "Al-Qiyamah",
            "Al-Insan",
            "Al-Mursalat",
            "An-Naba",
            "An-Nazi'at",
            "'Abasa",
            "At-Takwir",
            "Al-Infitar",
            "Al-Mutaffifin",
            "Al-Inshiqaq",
            "Al-Buruj",
            "At-Tariq",
            "Al-A'la",
            "Al-Ghashiyah",
            "Al-Fajr",
            "Al-Balad",
            "Ash-Shams",
            "Al-Layl",
            "Ad-Duhaa",
            "Ash-Sharh",
            "At-Tin",
            "Al-'Alaq",
            "Al-Qadr",
            "Al-Bayyinah",
            "Az-Zalzalah",
            "Al-'Adiyat",
            "Al-Qari'ah",
            "At-Takathur",
            "Al-'Asr",
            "Al-Humazah",
            "Al-Fil",
            "Quraysh",
            "Al-Ma'un",
            "Al-Kawthar",
            "Al-Kafirun",
            "An-Nasr",
            "Al-Masad",
            "Al-Ikhlas",
            "Al-Falaq",
            "An-Nas"
    )

    return  if(DeenSDKCore.GetDeenLanguage() == "bn") surahNameEn.indexOf(this).getSurahNameBn() else this
}

fun String.surahOriginLocale():String =
    if(DeenSDKCore.GetDeenLanguage() == "bn")
    {
       if(this == "makkah")
           "মক্কা"
        else
            "মদিনা"
    }
    else
        this

fun Context.getString(id:Int):String = this.resources.getString(id)

fun String.monthNameLocale(): String =

     if(DeenSDKCore.GetDeenLanguage() == "bn") {

        val englishMonths = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        val bengaliMonths = listOf(
            "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
            "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
        )

         val regex = "\\b(${englishMonths.joinToString("|")})\\b".toRegex()

          this.replace(regex) { result ->
             val matchedMonth = result.value
             val index = englishMonths.indexOf(matchedMonth)
             if (index != -1) bengaliMonths[index] else matchedMonth
         }

     }
    else this

fun String.dayNameLocale(): String =

    if(DeenSDKCore.GetDeenLanguage() == "bn") {

        val englishDays = listOf(
            "Sunday", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday"
        )

        val bengaliDays = listOf(
            "রবিবার", "সোমবার", "মঙ্গলবার", "বুধবার",
            "বৃহস্পতিবার", "শুক্রবার", "শনিবার"
        )


        val regex = "\\b(${englishDays.joinToString("|")})\\b".toRegex()

        this.replace(regex) { result ->
            val matchedMonth = result.value
            val index = englishDays.indexOf(matchedMonth)
            if (index != -1) bengaliDays[index] else matchedMonth
        }
    }
    else this

fun String.stripHtml(): String {
    return this.replace(Regex("<.*?>"), "")
}

fun Context.shareView(view:View,customShareText:String?=null){

    try {
        val uniqueID = generateUniqueNumber()
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        view.post {
            val cachePath = File(this.cacheDir, "images")
            cachePath.mkdirs() // don't forget to make the directory
            val stream =
                FileOutputStream("$cachePath/$uniqueID.png") // overwrites this image every time
            captureScreen(view)?.compress(Bitmap.CompressFormat.JPEG, 100, stream)?: return@post
            stream.close()

            val imagePath = File(this.cacheDir, "images")
            val newFile = File(imagePath, "$uniqueID.png")
            val contentUri: Uri =
                FileProvider.getUriForFile(this, "com.deenislam.sdk.fileprovider", newFile)


            val textShareContent = customShareText?.let { it }?: "Explore a world of Islamic content on your fingertips. https://shorturl.at/GPSY6"
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, this.contentResolver.getType(contentUri))
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            shareIntent.putExtra(Intent.EXTRA_TEXT, textShareContent) // Add text to the intent
            startActivity(Intent.createChooser(shareIntent, "Choose an app"))

            view.requestLayout()
        }

    } catch (e: IOException) {
        Log.e("shareViewError",e.toString())
    }
}

fun captureScreen(v: View): Bitmap? {
    return try {
        val b =
            Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        b
    } catch (e: Exception) {

        Log.e("shareViewError",e.toString())
        null
    }

}

fun View.setStarMargin(margin:Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = margin
}

internal fun AppCompatImageView.setColorFilter( tintColor: Int) {
    val colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
    this.colorFilter = colorFilter
    this.imageAlpha = 255 // If you want to change the alpha of the image along with the color
}

fun Activity.acquireWakeLock() {

    this.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

fun Activity.releaseWakeLock() {

    this.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

}