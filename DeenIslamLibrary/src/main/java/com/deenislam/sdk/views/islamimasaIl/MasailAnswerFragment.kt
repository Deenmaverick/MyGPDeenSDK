package com.deenislam.sdk.views.islamimasaIl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.IslamiMasailResource
import com.deenislam.sdk.service.network.response.islamimasail.questionbycat.Data
import com.deenislam.sdk.service.repository.IslamiMasailRepository
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.viewmodels.IslamiMasailViewModel
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs


internal class MasailAnswerFragment : BaseRegularFragment() {

    private lateinit var viewmodel: IslamiMasailViewModel
    private val navArgs:MasailAnswerFragmentArgs by navArgs()
    private lateinit var quesNo:AppCompatTextView
    private lateinit var username:AppCompatTextView
    private lateinit var readCount:AppCompatTextView
    private lateinit var question:AppCompatTextView
    private lateinit var answerTxt:AppCompatTextView
    private lateinit var referenceTxt:AppCompatTextView
    private lateinit var answererName:AppCompatTextView
    private lateinit var locTxt:AppCompatTextView
    private lateinit var timeTxt:AppCompatTextView
    private lateinit var boomarkLayout:MaterialButton
    private lateinit var shareLayout:MaterialButton
    private lateinit var hujuzImg:ShapeableImageView

    private var isFav = false
    private var favCount = 0

    override fun OnCreate() {
        super.OnCreate()
        // init viewmodel
        val repository = IslamiMasailRepository(
            deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = IslamiMasailViewModel(repository)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_masail_answer,container,false)

        quesNo = mainview.findViewById(R.id.quesNo)
        username = mainview.findViewById(R.id.username)
        readCount = mainview.findViewById(R.id.readCount)
        question = mainview.findViewById(R.id.question)
        answerTxt = mainview.findViewById(R.id.answerTxt)
        referenceTxt = mainview.findViewById(R.id.referenceTxt)
        answererName = mainview.findViewById(R.id.answererName)
        locTxt = mainview.findViewById(R.id.locTxt)
        timeTxt = mainview.findViewById(R.id.timeTxt)
        boomarkLayout = mainview.findViewById(R.id.boomarkLayout)
        shareLayout = mainview.findViewById(R.id.shareLayout)
        hujuzImg = mainview.findViewById(R.id.hujuzImg)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.islami_masail),
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else*/
            loadpage()
    }

    private fun loadpage(){
        initObserver()
        loadapi()
    }

    private fun loadapi(){
        lifecycleScope.launch {
            viewmodel.getAnswer(navArgs.qid)
        }
    }

    private fun initObserver(){

        viewmodel.islamiMasailLivedata.observe(viewLifecycleOwner){

            when(it){
                is CommonResource.EMPTY -> baseEmptyState()
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is IslamiMasailResource.AnswerData ->{
                    quesNo.text = it.data.QuestionID.toString()
                    username.text = it.data.QRaiserName
                    readCount.text = localContext.getString(R.string.masailquesReadCount,it.data.viewCount.toString().numberLocale())
                    question.text = it.data.title.trim()
                    answerTxt.text = it.data.text
                    if(it.data.reference.isNotEmpty())
                    referenceTxt.text = "— ${it.data.reference}"
                    else
                        referenceTxt.hide()
                    answererName.text = it.data.huzur
                    if(it.data.Place?.isNotEmpty() == true)
                    locTxt.text = it.data.Place
                    else
                        locTxt.text = "--"
                    timeTxt.text = getTimeAgo(it.data.TimeStamp)

                    isFav = it.data.IsFavorite
                    favCount = it.data.favCount

                    if(isFav)
                        boomarkLayout.setIconResource(R.drawable.deen_ic_bookmark_active)


                    boomarkLayout.text = localContext.getString(R.string.masailquesFavCount,favCount.toString().numberLocale())
                    shareLayout.text = localContext.getString(R.string.masailquesShareCount, "0".numberLocale())

                    val shareText = "${localContext.getString(R.string.question)}:\n${it.data.title}\n\n" +
                            "${localContext.getString(R.string.answer)}:\n${it.data.text}"+
                            if(it.data.reference.isNotEmpty()) "\n${it.data.reference}" else ""


                    /*if(navArgs.isShare){

                        context?.shareText(shareText)
                    }*/

                   /* shareLayout.setOnClickListener {
                        context?.shareText(shareText)
                    }*/

                    hujuzImg.imageLoad(BASE_CONTENT_URL_SGP+it.data.imageurl, placeholder_1_1 = true, custom_placeholder_1_1 = R.drawable.deen_ic_hujur_default)


                    boomarkLayout.setOnClickListener {_->


                        lifecycleScope.launch {
                            viewmodel.questionBookmark(
                                Data(Id = it.data.Id,
                                    IsFavorite = isFav,
                                    Place = it.data.Place,
                                    QRaiserName = it.data.QRaiserName,
                                    categoryId = it.data.categoryId,
                                    categoryName = it.data.categoryName,
                                    contenturl = it.data.contenturl,
                                    favCount = favCount,
                                    imageurl = it.data.imageurl,
                                    isAnonymous = it.data.isAnonymous,
                                    isUrgent = it.data.isUrgent,
                                    language = it.data.language,
                                    msisdn = it.data.msisdn,
                                    title = it.data.title,
                                    viewCount = it.data.viewCount),
                                getLanguage())
                        }
                    }

                    baseViewState()
                }

                is IslamiMasailResource.QuestionBookmar ->{

                    isFav = it.copy.IsFavorite
                    favCount = it.copy.favCount
                    if(it.copy.IsFavorite)
                        boomarkLayout.setIconResource(R.drawable.deen_ic_bookmark_active)
                    else
                        boomarkLayout.setIconResource(R.drawable.deen_ic_bookmark)

                    boomarkLayout.text = localContext.getString(R.string.masailquesFavCount,it.copy.favCount.toString().numberLocale())

                }
            }
        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    private fun getTimeAgo(timestamp: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH)
        val parsedDate = sdf.parse(timestamp)
        val calendar = Calendar.getInstance()
        calendar.time = parsedDate!!

        val now = Calendar.getInstance()

        val diffInMillis = abs(now.timeInMillis - calendar.timeInMillis)
        val seconds = diffInMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val months = days / 30
        val years = days / 365


        val result = when {
            years > 0 -> getTimeAgoString(years.toInt(), "year", getLanguage())
            months > 0 -> getTimeAgoString(months.toInt(), "month", getLanguage())
            days > 0 -> getTimeAgoString(days.toInt(), "day", getLanguage())
            hours > 0 -> getTimeAgoString(hours.toInt(), "hour",getLanguage())
            minutes > 0 -> getTimeAgoString(minutes.toInt(), "minute",getLanguage())
            else -> getTimeAgoString(seconds.toInt(), "second",getLanguage())
        }

        return result
    }

    private fun getTimeAgoString(value: Int, unit: String, language: String): String {
        return when (language) {
            "en" -> "${value.toString().numberLocale()} $unit${if (value > 1) "s" else ""} ago"
            "bn" -> {
                val suffix = when (unit) {
                    "year" -> "বছর"
                    "month" -> "মাস"
                    "day" -> "দিন"
                    "hour" -> "ঘণ্টা"
                    "minute" -> "মিনিট"
                    "second" -> "সেকেন্ড"
                    else -> throw IllegalArgumentException("Unsupported unit: $unit")
                }
                // Handle pluralization for Bengali
                when {
                    value == 1 -> "১ $suffix আগে"
                    value > 1 -> "${value.toString().numberLocale()} $suffix আগে"
                    else ->  "--"
                }
            }
            else -> "--"
        }
    }


}