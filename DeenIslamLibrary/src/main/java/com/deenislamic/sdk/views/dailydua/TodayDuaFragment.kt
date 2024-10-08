package com.deenislamic.sdk.views.dailydua

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.DailyDuaResource
import com.deenislamic.sdk.service.network.response.dailydua.todaydua.Data
import com.deenislamic.sdk.service.repository.DailyDuaRepository
import com.deenislamic.sdk.utils.generateUniqueNumber
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.DailyDuaViewModel
import com.deenislamic.sdk.views.adapters.dailydua.TodayDuaAdapter
import com.deenislamic.sdk.views.adapters.dailydua.TodayDuaCallback
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


internal class TodayDuaFragment : BaseRegularFragment(), TodayDuaCallback {

    private lateinit var listView:RecyclerView
    private lateinit var progressLayout: LinearLayout
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var noInternetRetry: MaterialButton

    private var firstload:Boolean = false

    private lateinit var viewmodel: DailyDuaViewModel

    private lateinit var todayDuaAdapter: TodayDuaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init viewmodel
        val repository = DailyDuaRepository(deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = DailyDuaViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_daily_dua_page,container,false)

        //init view

        listView = mainView.findViewById(R.id.listView)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        nodataLayout = mainView.findViewById(R.id.nodataLayout)
        noInternetLayout = mainView.findViewById(R.id.no_internet_layout)
        noInternetRetry = noInternetLayout.findViewById(R.id.no_internet_retry)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadpage()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if(menuVisible)
        {
            if(!firstload)
                loadApiData()
            firstload = true

        }
    }

    private fun loadpage()
    {
        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(noInternetLayout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)

        initObserver()

        todayDuaAdapter = TodayDuaAdapter(this@TodayDuaFragment)

        listView.apply {
                adapter = todayDuaAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        //click retry button for get api data again
        noInternetRetry.setOnClickListener {
            loadApiData()
        }

    }

    private fun loadApiData()
    {
        lifecycleScope.launch {
            viewmodel.getTodayDua(getLanguage())
        }
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "daily_dua",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }

    }
    private fun initObserver()
    {
        viewmodel.todayDuaLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> noInternetState()
                is CommonResource.EMPTY -> emptyState()
                is DailyDuaResource.todayDuaList -> viewState(it.data)
            }
        }

        viewmodel.duaPreviewLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is DailyDuaResource.setFavDua -> updateFavorite(it.position,it.fav)
            }
        }
    }
    private fun loadingState()
    {
        progressLayout.visible(true)
        nodataLayout.visible(false)
        noInternetLayout.visible(false)
    }

    private fun updateFavorite(position: Int, fav: Boolean)
    {
        todayDuaAdapter.update(position,fav)
    }

    private fun emptyState()
    {
        progressLayout.hide()
        nodataLayout.show()
        noInternetLayout.hide()
    }

    private fun noInternetState()
    {
        progressLayout.hide()
        nodataLayout.hide()
        noInternetLayout.show()
    }

    private fun viewState(data: List<Data>)
    {
        todayDuaAdapter.update(data)

        listView.post {
            progressLayout.hide()
            nodataLayout.hide()
            noInternetLayout.hide()
        }
    }

    override fun favClick(isFavorite: Boolean, duaId: Int, position: Int) {
        lifecycleScope.launch {
            viewmodel.setFavDua(isFavorite,duaId,"en",position)
        }
    }

    override fun shareDua(duaImg: Bitmap) {

        try {

            val uniqueID = generateUniqueNumber()
            // Save the bitmap to the cache directory
            val cachePath = File(requireContext().cacheDir, "images")
            cachePath.mkdirs() // Create the directory if it doesn't exist
            val stream = FileOutputStream("$cachePath/$uniqueID.png")
            duaImg.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.close()

            // Get the URI for the saved image

            val imagePath = File(requireContext().cacheDir, "images")
            val newFile = File(imagePath, "$uniqueID.png")
            val contentUri: Uri =
                FileProvider.getUriForFile(
                    requireContext(),
                    "com.deenislamic.sdk.fileprovider",
                    newFile
                )



            // Share the image with text
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.type = "image/*"  // Set the MIME type of the content
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            //shareIntent.putExtra(Intent.EXTRA_TEXT, textShareContent) // Add text to the intent
            // Launch the intent
            startActivity(Intent.createChooser(shareIntent, "Choose an app"))

        } catch (e: IOException) {
            requireContext().toast(localContext.getString(R.string.unable_to_share_try_again))
        }

    }
}