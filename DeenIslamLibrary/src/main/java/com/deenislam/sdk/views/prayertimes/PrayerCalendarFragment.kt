package com.deenislam.sdk.views.prayertimes

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.prayer_time.PrayerCalendarResource
import com.deenislam.sdk.service.network.response.prayer_calendar.Data
import com.deenislam.sdk.service.repository.PrayerCalendarRespository
import com.deenislam.sdk.utils.CaptureScreen
import com.deenislam.sdk.utils.monthNameLocale
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.utils.toast
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.PrayerCalendarViewModel
import com.deenislam.sdk.views.adapters.prayer_times.PrayerCalendarAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialSharedAxis
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

    private val prayerMain: RecyclerView by lazy { requireView().findViewById(R.id.prayerMain) }
    private val progressLayout: LinearLayout by lazy { requireView().findViewById(R.id.progressLayout) }
    private val no_internet_layout: NestedScrollView by lazy { requireView().findViewById(R.id.no_internet_layout)}
    private val no_internet_retryBtn: MaterialButton by lazy { requireView().findViewById(R.id.no_internet_retry) }
    private val nodataLayout:NestedScrollView by lazy { requireView().findViewById(R.id.nodataLayout) }
    private val container:ConstraintLayout by lazy { requireView().findViewById(R.id.mainView) }
    private val dateTime:AppCompatTextView by lazy { requireView().findViewById(R.id.dateTime) }

    private val currentDate = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).apply {
            duration = 300L
        }
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true).apply {
            duration = 300L
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).apply {
            duration = 300L
        }

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
        setupActionForOtherFragment(0,0,this@PrayerCalendarFragment,localContext.getString(R.string.prayer_calendar),true,mainView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.postDelayed({
            // Code to execute after the animation
            loadPage()
        }, 300)
    }

    private fun loadPage()
    {
        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(no_internet_layout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)

        dateTime.text = localContext.getString(R.string.prayer_calendar_date,currentDate).monthNameLocale().numberLocale()

        //init observer
        initObserver()

        //loading start
        loadingState()


        //click retry button for get api data again
        no_internet_retryBtn.setOnClickListener {
            loadApiData()
        }
    }

    override fun onResume() {
        super.onResume()

        // call api to get monthly data
        loadApiData()
    }

    private fun initObserver()
    {
        viewmodel.calendarLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> nointernetState()
                CommonResource.EMPTY -> emptyState()
                is PrayerCalendarResource.monthlyData -> viewState(it.data)
            }
        }
    }

    private fun viewState(data: ArrayList<Data>)
    {
        val Prayeradapter = PrayerCalendarAdapter(data)

        prayerMain.apply {
            adapter = Prayeradapter
            post {
                dataState()
            }
        }
    }


    private fun loadApiData()
    {
        lifecycleScope.launch {

            viewmodel.getMonthlyData("Dhaka", getLanguage())
        }
    }

    private fun loadingState()
    {
        progressLayout.visible(true)
        nodataLayout.visible(false)
        no_internet_layout.visible(false)
    }

    private fun emptyState()
    {
        nodataLayout.visible(true)
        no_internet_layout.visible(false)
        progressLayout.visible(false)
    }

    private fun nointernetState()
    {
        no_internet_layout.visible(true)
        nodataLayout.visible(false)
        progressLayout.visible(false)
    }

    private fun dataState()
    {
        prayerMain.visible(true)
        nodataLayout.visible(false)
        no_internet_layout.visible(false)
        progressLayout.visible(false)
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
                FileProvider.getUriForFile(view.context, "com.deenislam.sdk.fileprovider", newFile)

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, view.context.contentResolver.getType(contentUri))
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            startActivity(Intent.createChooser(shareIntent, "Choose an app"))

        } catch (e: IOException) {
            return false
        }

        return true
    }


    override fun action1() {
    }

    override fun action2() {
        container.post {
            lifecycleScope.launch(Dispatchers.IO)
            {
                try {
                    if (!shareCalendar(container))
                    {
                        withContext(Dispatchers.Main)
                        {
                            requireContext().toast("Unable to share calendar. Try again")
                        }
                    }

                }catch (e:Exception)
                {
                    withContext(Dispatchers.Main)
                    {
                        requireContext().toast("Unable to share calendar. Try again")
                    }
                }
            }

            }
        }
}