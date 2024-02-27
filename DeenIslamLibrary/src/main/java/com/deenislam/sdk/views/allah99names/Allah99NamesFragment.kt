package com.deenislam.sdk.views.allah99names

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.Allah99NameCallback
import com.deenislam.sdk.service.callback.AudioManagerBasicCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.libs.media3.AudioManager
import com.deenislam.sdk.service.models.Allah99NameResource
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.network.response.allah99name.Data
import com.deenislam.sdk.service.repository.DeenServiceRepository
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.get9DigitRandom
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.viewmodels.Allah99NameViewModel
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislamic.views.adapters.allah99names.Allah99NamesListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

internal class Allah99NamesFragment : BaseRegularFragment(), Allah99NameCallback,
    AudioManagerBasicCallback {

    private lateinit var listView: RecyclerView
    private lateinit var actionbar:ConstraintLayout
    private lateinit var container: NestedScrollView
    private lateinit var btnPlayPause: FloatingActionButton
    private lateinit var allah99NamesListAdapter: Allah99NamesListAdapter
    private lateinit var viewmodel: Allah99NameViewModel

    private lateinit var audioManager: AudioManager

    private var data: List<Data> = arrayListOf()

    private var firstload:Boolean = false
    private var hasMoreData = true
    private var itemsToLoadAhead = 8
    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)
        CallBackProvider.setFragment(this)
        // init viewmodel
        val repository = DeenServiceRepository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = Allah99NameViewModel(repository)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        CallBackProvider.setFragment(this)
        audioManager = AudioManager().getInstance()
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_allah99_names,container,false)

        //init view

        listView = mainView.findViewById(R.id.listView)
        actionbar = mainView.findViewById(R.id.actionbar)
        this.container = mainView.findViewById(R.id.container)
        btnPlayPause = mainView.findViewById(R.id.btnPlayPause)
        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.allah_99_name),
            backEnable = true,
            view = mainView
        )

        setupCommonLayout(mainView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!firstload)
        {
            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "asmaul_husna",
                    trackingID = getTrackingID()
                )
            }
        }


        /*if(firstload) {
            loadpage()
        }
        else if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else*/
            loadpage()

    }

    private fun loadpage()
    {
        initObserver()

       /* actionbar.post {
            val param = container.layoutParams as ViewGroup.MarginLayoutParams
            param.topMargin = container.marginTop+ actionbar.height
            container.layoutParams = param
        }*/

        allah99NamesListAdapter = Allah99NamesListAdapter(listView)

        val gridLayoutManager = GridLayoutManager(context, 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // Check if it's the last item
                val isLastItem = position == allah99NamesListAdapter.itemCount - 1

                // Check if the total count is odd
                val isTotalCountOdd = allah99NamesListAdapter.itemCount % 2 != 0

                return when {
                    isLastItem && isTotalCountOdd -> 2
                    else -> 1
                }
            }
        }



        listView.apply {
            post {
                val margins = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    leftMargin = 12.dp
                    rightMargin = 12.dp
                }
                layoutParams = margins
                adapter = allah99NamesListAdapter
                layoutManager = gridLayoutManager

            }

        }


        container.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = container.scrollY
            val totalContentHeight = container.getChildAt(0)?.let { it.measuredHeight - container.height }
            val threshold = 300.dp  // Define your own value based on your needs
            val isCloseToBottom = (scrollY + threshold) >= (totalContentHeight ?: 0)

            if (isCloseToBottom) {

                if (hasMoreData && allah99NamesListAdapter.itemCount < data.size) {
                    Log.e("99NameScroll", "ok")
                    itemsToLoadAhead = 10
                    loadNextPage()
                }
            }
        }


        btnPlayPause.setOnClickListener {
            if(audioManager.getMediaPlayer()?.isPlaying == true && audioManager.getAudioUrl() == BASE_CONTENT_URL_SGP+"Content/Audios/allahnames/AsmaulHusna_The_99_Names.mp3")
                audioManager.pauseMediaPlayer()
            else if( audioManager.getAudioUrl() == BASE_CONTENT_URL_SGP+"Content/Audios/allahnames/AsmaulHusna_The_99_Names.mp3")
                audioManager.resumeMediaPlayer()
            else
                audioManager.playAudioFromUrl(BASE_CONTENT_URL_SGP+"Content/Audios/allahnames/AsmaulHusna_The_99_Names.mp3")
        }

        if(!firstload)
            loadApiData()

        firstload = true
    }

    override fun noInternetRetryClicked() {
        loadApiData()
    }

    private fun loadApiData()
    {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.get99NameOfAllah(getLanguage())
        }
    }

    private fun loadNextPage() {
        val newItems = fetchData(allah99NamesListAdapter.itemCount, itemsToLoadAhead) // You start from the current adapter size as an offset
        if (newItems.size < itemsToLoadAhead) {
            hasMoreData = false
        }
        allah99NamesListAdapter.update(newItems)
    }

    private fun fetchData(offset: Int, limit: Int): List<Data> {
        val end = (offset + limit).coerceAtMost(data.size)  // Ensure we don't go past the end of the list
        return data.subList(offset, end)
    }


    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "asmaul_husna",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }

    private fun initObserver()
    {
        viewmodel.allah99nameLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is Allah99NameResource.getAllah99Name -> viewState(it.data)
            }
        }
    }

    private fun viewState(data: List<Data>)
    {

        this.data = data

        loadNextPage()
        //allah99NamesListAdapter.update(data)

        listView.post {
           baseViewState()
        }
    }



    override fun allah99NameNextClicked(viewHolder: RecyclerView.ViewHolder) {
    }

    override fun allah99NamePrevClicked(viewHolder: RecyclerView.ViewHolder) {
    }

    override fun allahNameClicked(position: Int) {
        val bundle = Bundle()
        bundle.putInt("namePos",position)
        bundle.putParcelableArray("nameList",data.toTypedArray())
        gotoFrag(R.id.action_global_allah99NamesDetailsFragment,bundle)
    }

    override fun allahNamePlayClicked(namedata: Data, absoluteAdapterPosition: Int) {

    }

    override fun allahNamePauseClicked() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioManager.releasePlayer()
        audioManager.clearPlayerData()
    }

    override fun isMedia3PlayComplete() {
        btnPlayPause.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_play_fill
            )
        )
    }

    override fun isMedia3Pause() {
        btnPlayPause.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_play_fill
            )
        )
    }

    override fun isMedia3Playing() {

        btnPlayPause.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_pause_fill
            )
        )
    }

    override fun isMedia3Stop() {
        btnPlayPause.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_play_fill
            )
        )
    }

}