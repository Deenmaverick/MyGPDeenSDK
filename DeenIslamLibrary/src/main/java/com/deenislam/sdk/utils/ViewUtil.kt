package com.deenislam.sdk.utils

import android.content.Context
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageView
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import coil.load
import com.deenislam.sdk.Deen
import com.deenislam.sdk.R
import java.util.Locale

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
    ic_small:Boolean=false,
    ic_medium:Boolean=false,
    ic_large:Boolean=false
)
{
    this.load(url)
    {
        if(ic_small)
            error(R.drawable.ic_small_download_empty)
    }
}


fun Context.getLocalContext(): Context {

    var localContext: Context = if (Deen.language == "en") {
        LocaleUtil.createLocaleContext(this, Locale("en"))
    } else {
        LocaleUtil.createLocaleContext(this, Locale("bn"))
    }

    return ContextThemeWrapper(localContext, R.style.Theme_DeenIslam)

}

fun String.numberLocale():String
{
    if(Deen.language == "bn") {
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
    if(Deen.language == "bn") {
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

fun String.prayerMomentLocale():String
{
    if(Deen.language == "bn") {

       return when(this)
        {
            "Fajr" -> "ফজর"
           "Ishraq" -> "ইশরাক"
           "Dhuhr" -> "যোহর"
           "Asr" -> "আসর"
           "Maghrib" -> "মাগরিব"
           "Isha" -> "এশা"
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
    val surahNameBn = arrayListOf<String>("আল ফাতিহা",
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

    return  if(Deen.language == "bn") surahNameEn.indexOf(this).getSurahNameBn() else this
}

fun String.surahOriginLocale():String =
    if(Deen.language == "bn")
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

     if(Deen.language == "bn") {

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

    if(Deen.language == "bn") {

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

