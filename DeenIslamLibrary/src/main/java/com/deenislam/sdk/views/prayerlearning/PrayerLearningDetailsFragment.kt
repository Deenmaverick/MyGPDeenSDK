package com.deenislam.sdk.views.prayerlearning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.PrayerLearningCallback
import com.deenislam.sdk.service.callback.common.MaterialButtonHorizontalListCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.PrayerLearningResource
import com.deenislam.sdk.service.network.response.prayerlearning.visualization.Data
import com.deenislam.sdk.service.repository.PrayerLearningRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.viewmodels.PrayerLearningViewModel
import com.deenislam.sdk.views.adapters.common.MaterialButtonHorizontalAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.adapters.prayerlearning.PrayerLearningDetailsAdapter
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch


internal class PrayerLearningDetailsFragment : BaseRegularFragment(), PrayerLearningCallback,
    MaterialButtonHorizontalListCallback {

    private lateinit var _viewPager: ViewPager2
    private lateinit var actionbar: ConstraintLayout
    private lateinit var header: RecyclerView
    private lateinit var prevStep:MaterialButton
    private lateinit var NextStep:MaterialButton
    private lateinit var materialButtonHorizontalAdapter: MaterialButtonHorizontalAdapter
    private lateinit var viewmodel:PrayerLearningViewModel
    private val navArgs:PrayerLearningDetailsFragmentArgs by navArgs()
    private var currentIndex = 0

    override fun OnCreate() {
        super.OnCreate()

        CallBackProvider.setFragment(this)

        // init viewmodel
        val repository = PrayerLearningRepository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = PrayerLearningViewModel(repository)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_prayer_learning_details,container,false)

        //init view
        _viewPager = mainView.findViewById(R.id.viewPager)
        actionbar = mainView.findViewById(R.id.actionbar)
        header = mainView.findViewById(R.id.header)
        prevStep = mainView.findViewById(R.id.prevStep)
        NextStep = mainView.findViewById(R.id.NextStep)

        setupActionForOtherFragment(0,0,null,navArgs.pageTitle,true,mainView)
        setupCommonLayout(mainView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else
            loadpage()

    }

    private fun loadpage()
    {
        prevStep.isEnabled = false

        prevStep.setOnClickListener {
            if(currentIndex>0) {
                materialButtonHorizontalAdapter.nextPrev(currentIndex - 1)
                header.smoothScrollToPosition(currentIndex - 1)
                materialButtonHorizontalListClicked(currentIndex - 1)

            }
        }

        NextStep.setOnClickListener {
            if(currentIndex>=0 && currentIndex<materialButtonHorizontalAdapter.itemCount-1) {
                materialButtonHorizontalAdapter.nextPrev(currentIndex + 1)
                header.smoothScrollToPosition(currentIndex + 1)
                materialButtonHorizontalListClicked(currentIndex + 1)
            }
        }

        initObserver()
        loadApi()
    }

    private fun loadApi()
    {
        lifecycleScope.launch {
            viewmodel.getVisualization(language = getLanguage(), gender = navArgs.gender)
        }
    }

    private fun initObserver()
    {
        viewmodel.prayerLearningVisualizationLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is PrayerLearningResource.Visualization -> viewState(it.data)
            }
        }
    }

    private fun viewState(data: Data)
    {
        baseViewState()

        header.apply {
            val linearLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            layoutManager = linearLayoutManager
            materialButtonHorizontalAdapter = MaterialButtonHorizontalAdapter(data.Head)
            adapter = materialButtonHorizontalAdapter
        }

        _viewPager.apply {
            adapter = PrayerLearningDetailsAdapter(data.Content)
            isUserInputEnabled = false
        }

    }

    override fun noInternetRetryClicked() {
        loadApi()
    }

    override fun materialButtonHorizontalListClicked(absoluteAdapterPosition: Int) {

        materialButtonHorizontalAdapter.notifyItemChanged(currentIndex)
        currentIndex = absoluteAdapterPosition
        _viewPager.setCurrentItem(absoluteAdapterPosition,false)
        materialButtonHorizontalAdapter.notifyItemChanged(currentIndex)
        prevStep.isEnabled = currentIndex != 0
        NextStep.isEnabled = currentIndex != materialButtonHorizontalAdapter.itemCount-1

    }

}