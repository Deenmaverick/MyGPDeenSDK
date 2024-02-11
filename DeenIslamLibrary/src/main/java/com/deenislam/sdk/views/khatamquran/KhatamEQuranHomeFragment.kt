package com.deenislam.sdk.views.khatamquran

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.common.CommonCardCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.KhatamQuranVideoResource
import com.deenislam.sdk.service.network.response.common.CommonCardData
import com.deenislam.sdk.service.repository.KhatamEquranVideoRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.viewmodels.KhatamQuranViewModel
import com.deenislam.sdk.views.adapters.khatamquran.KhatamQuranVideoHomeAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class KhatamEQuranHomeFragment : BaseRegularFragment(), CommonCardCallback {
    private lateinit var viewmodel: KhatamQuranViewModel
    private var firstload = false
    private lateinit var listMainEducationVideo: RecyclerView
    private var videoList: List<CommonCardData> = ArrayList()
    private var recentList: List<CommonCardData> = ArrayList()

    override fun OnCreate() {
        super.OnCreate()
        // init viewmodel
        val repository = KhatamEquranVideoRepository(
            deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = KhatamQuranViewModel(repository)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainview =
            localInflater.inflate(R.layout.fragment_islamic_education_video_home, container, false)
        listMainEducationVideo = mainview.findViewById(R.id.listMainEducationVideo)
        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.title_khatam_e_quran),
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)
        CallBackProvider.setFragment(this)
        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (firstload) {
            loadpage()
        } else if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        } else
            loadpage()

        firstload = true
    }

    private fun loadpage() {
        initObserver()
        baseLoadingState()

        loadApi()
    }

    private fun loadApi() {
        lifecycleScope.launch {
            viewmodel.getKhatamQuranVideo(getLanguage())
        }
    }

    private fun initObserver() {

        viewmodel.recentKhatamquranVideoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is CommonResource.API_CALL_FAILED -> Log.e("RecentKhatamquranVideo", "Failed")
                is CommonResource.EMPTY -> {
                    Log.e("RecentKhatamquranVideo", "Empty")
                }

                is KhatamQuranVideoResource.khatamequranRecentVideos -> {
                    recentList = it.data
                    Log.e("RecentKhatamquranVideo", "${recentList.size}")
                }
            }
        }
        viewmodel.khatamquranVideoLiveData.observe(viewLifecycleOwner)
        {
            when (it) {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is KhatamQuranVideoResource.khatamequranVideo -> {
                    videoList = it.data
                    viewState(recentList, videoList)
                }
            }
        }
    }

    private fun viewState(recentVideos: List<CommonCardData>, data: List<CommonCardData>) {

        listMainEducationVideo.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = KhatamQuranVideoHomeAdapter(recentVideos, data)
        }
        listMainEducationVideo.post {
            baseViewState()
        }
    }

    override fun onDestroyView() {
        lifecycleScope.launch {
            viewmodel.clear()
        }
        super.onDestroyView()
    }

    override fun commonCardClicked(getData: CommonCardData, absoluteAdapterPosition: Int) {
            val bundle = Bundle()
            bundle.putInt("khatamQuranvideoPosition", absoluteAdapterPosition)
            if (getData.category.equals("Recent")) {
                bundle.putParcelableArray("khatamQuranvideoList", recentList.toTypedArray())
            } else {
                bundle.putParcelableArray("khatamQuranvideoList", videoList.toTypedArray())
            }

            gotoFrag(R.id.action_khatam_quran_home_to_video, bundle)


    }


}