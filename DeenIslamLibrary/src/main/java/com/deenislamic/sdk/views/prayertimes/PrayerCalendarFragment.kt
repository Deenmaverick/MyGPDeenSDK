package com.deenislamic.sdk.views.prayertimes

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.prayer_time.PrayerCalendarResource
import com.deenislamic.sdk.service.network.response.prayer_calendar.Data
import com.deenislamic.sdk.service.repository.PrayerCalendarRespository
import com.deenislamic.sdk.utils.CaptureScreen
import com.deenislamic.sdk.utils.MilliSecondToStringTime
import com.deenislamic.sdk.utils.StringTimeToMillisecond
import com.deenislamic.sdk.utils.bangladeshStateArray
import com.deenislamic.sdk.utils.loadHtmlFromAssets
import com.deenislamic.sdk.utils.monthNameLocale
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.timeLocale
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.viewmodels.PrayerCalendarViewModel
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


internal class PrayerCalendarFragment : BaseRegularFragment(),otherFagmentActionCallback {

    private lateinit var viewmodel:PrayerCalendarViewModel

    private val currentDate = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(Date())
    private lateinit var webview: WebView
    val format = SimpleDateFormat("dd MMMM", Locale.ENGLISH) // or you can add before dd/M/yyyy

    private val navArgs:PrayerCalendarFragmentArgs by navArgs()


    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this)

        // init viewmodel
        val repository = PrayerCalendarRespository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = PrayerCalendarViewModel(repository)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainView = localInflater.inflate(R.layout.fragment_prayer_calendar,container,false)
        webview = mainView.findViewById(R.id.webview)
        setupActionForOtherFragment(0,0,this@PrayerCalendarFragment,localContext.getString(R.string.prayer_calendar),true,mainView)
        setupCommonLayout(mainView)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //init observer
        initObserver()

        //loading start
        loadApiData()
    }

    override fun noInternetRetryClicked() {
        loadApiData()
    }


    private fun initObserver()
    {
        viewmodel.calendarLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> baseNoInternetState()
                CommonResource.EMPTY -> baseEmptyState()
                is PrayerCalendarResource.monthlyData -> viewState(it.data)
            }
        }
    }

    private fun viewState(data: ArrayList<Data>)
    {

        var location = localContext.getString(R.string.location_dhaka)

        val filteredStates = bangladeshStateArray.firstOrNull { state ->
            navArgs.location.lowercase().contains(state.state.lowercase()) ||
                    navArgs.location.lowercase().contains(state.statebn.lowercase())
        }

        filteredStates?.let {
            location = it.stateValue
        }

        var tableContent = ""

        data.forEach {

            val newDate = format.parse(it.Date.StringTimeToMillisecond("yyyy-MM-dd'T'HH:mm:ss").MilliSecondToStringTime("dd MMMM"))


            tableContent += "<tr>\n" +
                    "                            <td style=\"text-align:left;\">${newDate?.let { it1 ->
                        format.format(
                            it1
                        ).numberLocale().monthNameLocale()
                    }}</td>\n" +
                    "                            <td>${it.Fajr.StringTimeToMillisecond().MilliSecondToStringTime("hh:mm aa").timeLocale()}</td>\n" +
                    "                            <td>${it.Juhr.StringTimeToMillisecond().MilliSecondToStringTime("hh:mm aa").timeLocale()}</td>\n" +
                    "                            <td>${it.Asr.StringTimeToMillisecond().MilliSecondToStringTime("hh:mm aa").timeLocale()}</td>\n" +
                    "                            <td>${it.Magrib.StringTimeToMillisecond().MilliSecondToStringTime("hh:mm aa").timeLocale()}</td>\n" +
                    "                            <td>${it.Isha.StringTimeToMillisecond().MilliSecondToStringTime("hh:mm aa").timeLocale()}</td>\n" +
                    "                        </tr>"
        }

        val getHtml = requireContext().loadHtmlFromAssets("prayer_calendar.html")
            .replace("#DATE_TXT#",localContext.getString(R.string.prayer_calendar_date,currentDate).monthNameLocale().numberLocale())
            .replace("#LOCATION_TXT#",location)
            .replace("#INFO_TXT#",localContext.getString(R.string.islamic_foundation_bangladesh))
            .replace("#thDate#",localContext.getString(R.string.date))
            .replace("#thFajr#",localContext.getString(R.string.fajr))
            .replace("#thDhuhr#",localContext.getString(R.string.dhuhr))
            .replace("#thAsr#",localContext.getString(R.string.asr))
            .replace("#thMaghrib#",localContext.getString(R.string.maghrib))
            .replace("#thIsha#",localContext.getString(R.string.isha))
            .replace("#TABLE_CONTENT#",tableContent)
            .replace("#INFO_TXT_FOOTER#",localContext.getString(R.string.prayer_calendar_footer_info))


        val webSettings = webview.settings
        // webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.displayZoomControls = false
        webSettings.builtInZoomControls = true
        webSettings.allowFileAccess = true
        webview.clearCache(true)

        webview.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                baseViewState()
            }
        }

        webview.loadDataWithBaseURL(null, getHtml, "text/html", "UTF-8", null)

        baseViewState()
    }

    fun setRecyclerViewHeightBasedOnChildren(recyclerView: RecyclerView) {
        val adapter = recyclerView.adapter ?: return
        var totalHeight = 0
        for (i in 0 until adapter.itemCount) {
            val viewHolder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i))
            adapter.onBindViewHolder(viewHolder, i)
            viewHolder.itemView.measure(
                View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            totalHeight += viewHolder.itemView.measuredHeight
        }

        val params = recyclerView.layoutParams
        params.height = totalHeight
        recyclerView.layoutParams = params
    }



    private fun loadApiData()
    {
        lifecycleScope.launch {

            viewmodel.getMonthlyData(navArgs.location, getLanguage())
        }
    }


    private fun shareCalendar(view:View):Boolean
    {
        // save bitmap to cache directory
        try {
            val cachePath = File(view.context.cacheDir, "images")
            cachePath.mkdirs() // don't forget to make the directory
            val stream =
                FileOutputStream("$cachePath/image.png") // overwrites this image every time
            CaptureScreen(view)?.compress(Bitmap.CompressFormat.PNG, 100, stream)?:return false
            stream.close()

            val imagePath = File(view.context.cacheDir, "images")
            val newFile = File(imagePath, "image.png")
            val contentUri: Uri =
                FileProvider.getUriForFile(view.context, "com.deenislamic.sdk.fileprovider", newFile)

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, view.context.contentResolver.getType(contentUri))
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            startActivity(Intent.createChooser(shareIntent, "Choose an app"))

        } catch (e: Exception) {
            Log.e("shareCalendar","error",e)
            return false
        }

        return true
    }


    override fun action1() {
    }

    override fun action2() {
        webview.post {

            try {
                if (!shareCalendar(webview))
                {

                    requireContext().toast(localContext.getString(R.string.unable_to_share_try_again))
                }

            }catch (e:Exception)
            {
                requireContext().toast(localContext.getString(R.string.unable_to_share_try_again))

            }
        }
    }
}