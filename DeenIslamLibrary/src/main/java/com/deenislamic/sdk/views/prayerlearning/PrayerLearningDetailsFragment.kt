package com.deenislamic.sdk.views.prayerlearning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.PrayerLearningCallback
import com.deenislamic.sdk.service.callback.common.MaterialButtonHorizontalListCallback
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.PrayerLearningResource
import com.deenislamic.sdk.service.network.response.prayerlearning.visualization.Data
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.viewmodels.PrayerLearningViewModel
import com.deenislamic.sdk.views.adapters.prayerlearning.PrayerLearningDetailsAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch


internal class PrayerLearningDetailsFragment : BaseRegularFragment(),
    PrayerLearningCallback,
    MaterialButtonHorizontalListCallback {

    private lateinit var _viewPager: ViewPager2
    //private lateinit var actionbar: ConstraintLayout
    //private lateinit var header: RecyclerView
    private lateinit var prevStep:MaterialButton
    private lateinit var NextStep:MaterialButton
    private lateinit var prayerLearningDetailsAdapter: PrayerLearningDetailsAdapter
    //private lateinit var materialButtonHorizontalAdapter: MaterialButtonHorizontalAdapter
    private lateinit var viewmodel: PrayerLearningViewModel
    private var currentIndex = 0
    private var firstload = false

    companion object {
        fun newInstance(cusarg: Bundle?): PrayerLearningDetailsFragment {
            val fragment = PrayerLearningDetailsFragment()
            fragment.arguments = cusarg
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_prayer_learning_details,container,false)

        //init view
        _viewPager = mainView.findViewById(R.id.viewPager)
        //actionbar = mainView.findViewById(R.id.actionbar)
        //header = mainView.findViewById(R.id.header)
        prevStep = mainView.findViewById(R.id.prevStep)
        NextStep = mainView.findViewById(R.id.NextStep)
        setupCommonLayout(mainView)
        CallBackProvider.setFragment(this)
        return mainView
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if(menuVisible){
            if(!firstload)
            loadpage()
            firstload = true
        }
    }

    private fun loadpage()
    {
        prevStep.isEnabled = false

        prevStep.setOnClickListener {
            if(currentIndex>0) {
                //materialButtonHorizontalAdapter.nextPrev(currentIndex - 1)
                //header.smoothScrollToPosition(currentIndex - 1)
                materialButtonHorizontalListClicked(currentIndex - 1)

            }
        }

        NextStep.setOnClickListener {
            if(currentIndex>=0 && currentIndex<prayerLearningDetailsAdapter.itemCount-1) {
                //materialButtonHorizontalAdapter.nextPrev(currentIndex + 1)
                //header.smoothScrollToPosition(currentIndex + 1)
                materialButtonHorizontalListClicked(currentIndex + 1)
            }
        }

        initObserver()
        loadApi()
    }

    private fun loadApi() {
        lifecycleScope.launch {
            viewmodel.getVisualization(language = getLanguage(), gender = arguments?.getString("gender").toString())
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

       /* header.apply {
            val linearLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            layoutManager = linearLayoutManager
            materialButtonHorizontalAdapter = MaterialButtonHorizontalAdapter(data.Head)
            adapter = materialButtonHorizontalAdapter
        }*/

        if(!this::prayerLearningDetailsAdapter.isInitialized)
            prayerLearningDetailsAdapter = PrayerLearningDetailsAdapter(data.Content)

        _viewPager.apply {
            adapter = prayerLearningDetailsAdapter
            isUserInputEnabled = false
        }

    }

    override fun noInternetRetryClicked() {
        loadApi()
    }

    override fun materialButtonHorizontalListClicked(absoluteAdapterPosition: Int) {

        //materialButtonHorizontalAdapter.notifyItemChanged(currentIndex)
        currentIndex = absoluteAdapterPosition
        _viewPager.setCurrentItem(absoluteAdapterPosition,false)
        //materialButtonHorizontalAdapter.notifyItemChanged(currentIndex)
        prevStep.isEnabled = currentIndex != 0
        NextStep.isEnabled = currentIndex != prayerLearningDetailsAdapter.itemCount-1

    }

}