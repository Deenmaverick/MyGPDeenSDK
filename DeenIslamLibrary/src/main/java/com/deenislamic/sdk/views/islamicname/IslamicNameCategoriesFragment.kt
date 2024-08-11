package com.deenislamic.sdk.views.islamicname

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.islamicname.IslamicNameCatCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicNameResource
import com.deenislamic.sdk.service.network.response.islamicname.IslamicNameCategoriesResponse
import com.deenislamic.sdk.service.repository.IslamicNameRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.IslamicNameViewModel
import com.deenislamic.sdk.views.adapters.islamicname.IslamicNameCatAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class IslamicNameCategoriesFragment: BaseRegularFragment(), IslamicNameCatCallback {
    private lateinit var viewmodel: IslamicNameViewModel
    private lateinit var progressLayout: LinearLayout
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var listView: RecyclerView
    private var gender = "male"
    private var firstload = false
    private val navargs:IslamicNameCategoriesFragmentArgs by navArgs()

    companion object {
        fun newInstance(gender: String): IslamicNameCategoriesFragment {
            val fragment = IslamicNameCategoriesFragment()
            val args = Bundle()
            args.putString("gender", gender)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = IslamicNameRepository(NetworkProvider().getInstance().provideIslamicNameService())
        viewmodel = IslamicNameViewModel(repository)
        gender = navargs.gender
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainView = localInflater.inflate(R.layout.fragment_islamic_name_categories, container, false)

        progressLayout = mainView.findViewById(R.id.progressLayout)
        nodataLayout = mainView.findViewById(R.id.nodataLayout)
        noInternetLayout = mainView.findViewById(R.id.no_internet_layout)
        listView = mainView.findViewById(R.id.rvNameCat)
        setupCommonLayout(mainView)
        CallBackProvider.setFragment(this)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(noInternetLayout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)

        initObserver()

        loadingState()

        if(!firstload)
        loadApiData()
        firstload = true
    }

    private fun initObserver() {
        viewmodel.islamicNamesCatsLiveData.observe(viewLifecycleOwner)
        {
            when (it) {
                CommonResource.API_CALL_FAILED -> noInternetState()

                CommonResource.EMPTY -> emptyState()
                is IslamicNameResource.islamicNamesCategories -> viewState(it.data)
            }
        }
    }

    private fun viewState(data: List<IslamicNameCategoriesResponse.Data>) {

        listView.apply {
            adapter = IslamicNameCatAdapter(data)
        }
        baseViewState()
    }

    private fun loadApiData() {
        lifecycleScope.launch {
            loadingState()
            viewmodel.getNamesCats(gender,getLanguage())
        }
    }

    private fun noInternetState() {
        progressLayout.hide()
        nodataLayout.hide()
        noInternetLayout.show()
    }
    private fun loadingState() {
        progressLayout.visible(true)
        nodataLayout.visible(false)
        noInternetLayout.visible(false)
    }

    private fun emptyState() {
        progressLayout.hide()
        nodataLayout.show()
        noInternetLayout.hide()
    }

    override fun onCatItemClick(data: IslamicNameCategoriesResponse.Data) {
        val bundle = Bundle()
        bundle.putInt("Catid",data.Id)
        bundle.putString("title",data.Title)
        gotoFrag(R.id.action_global_islamicNameCatwiseViewFragment, bundle)
    }
}