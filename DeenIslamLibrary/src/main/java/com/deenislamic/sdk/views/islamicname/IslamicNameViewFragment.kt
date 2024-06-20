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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.alertdialog.CustomAlertDialog
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicNameResource
import com.deenislamic.sdk.service.network.response.islamicname.Data
import com.deenislamic.sdk.service.repository.IslamicNameRepository
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.IslamicNameViewModel
import com.deenislamic.sdk.views.adapters.islamicname.IslamicNameAdapter
import com.deenislamic.sdk.views.adapters.islamicname.IslamicNameAdapterCallback
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.launch

internal class IslamicNameViewFragment : BaseRegularFragment(), IslamicNameAdapterCallback {

    private lateinit var listView: RecyclerView
    private lateinit var progressLayout: LinearLayout
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var noInternetRetry: MaterialButton
    private lateinit var viewBtn:MaterialButton

    private var customAlertDialog: CustomAlertDialog? =null
    private lateinit var islamicNameAdapter: IslamicNameAdapter
    private var islamicNameData: Data? =null
    private var adapterPosition:Int = -1
    private var gender = "male"
    private lateinit var viewmodel: IslamicNameViewModel

    private val args:IslamicNameViewFragmentArgs by navArgs()

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


        gender = args.gender
        //init viewmodel
        val repository = IslamicNameRepository(
            islamicNameService = NetworkProvider().getInstance().provideIslamicNameService()
        )
        viewmodel = IslamicNameViewModel(repository)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_islamic_name_view,container,false)

        listView = mainView.findViewById(R.id.listView)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        nodataLayout = mainView.findViewById(R.id.nodataLayout)
        noInternetLayout = mainView.findViewById(R.id.no_internet_layout)
        noInternetRetry = noInternetLayout.findViewById(R.id.no_internet_retry)
        viewBtn = mainView.findViewById(R.id.viewBtn)

        setupActionForOtherFragment(0,0,null,args.title,true,mainView)


        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingState()
        loadPage()
    /*    view.postDelayed({
            // Code to execute after the animation
            loadPage()
        }, 300)*/

    }

    private fun loadPage()
    {
        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(noInternetLayout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)

        setupViewBtn()

        initObserver()


        islamicNameAdapter = IslamicNameAdapter(this@IslamicNameViewFragment)
        listView.apply {
            adapter = islamicNameAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }
    }

    private fun setupViewBtn()
    {
        if(gender == "male") {
            setupActionForOtherFragment(0,0,null,localContext.getString(R.string.muslim_boys_names),true,requireView())

            viewBtn.text = localContext.getString(R.string.view_muslim_girls_names)
        }
        else {
            setupActionForOtherFragment(0,0,null,localContext.getString(R.string.muslim_girls_names),true,requireView())

            viewBtn.text = localContext.getString(R.string.view_muslim_boys_names)
        }

        viewBtn.setOnClickListener {

            gender = if(gender == "male")
                "female"
            else
                "male"

            loadApiData()
            setupViewBtn()
        }

    }

    override fun onResume() {
        super.onResume()
        setupBackPressCallback(this)
        loadApiData()
    }

    private fun loadApiData()
    {
        lifecycleScope.launch {
            loadingState()
            viewmodel.getNames(gender,getLanguage())
        }
    }


    private fun initObserver()
    {
        viewmodel.islamicNamesLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> noInternetState()

                CommonResource.EMPTY -> emptyState()
                is IslamicNameResource.islamicNames -> viewState(it.data)
                is IslamicNameResource.favFailed -> requireContext().toast(localContext.getString(R.string.failed_to_add_favorite_name))
                is IslamicNameResource.favDone -> islamicNameAdapter.favUpdate(it.adapaterPosition,it.bol)

            }
        }

    }

    private fun loadingState()
    {
        progressLayout.visible(true)
        nodataLayout.visible(false)
        noInternetLayout.visible(false)
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
        islamicNameAdapter.update(data)

        listView.post {
            progressLayout.hide()
            nodataLayout.hide()
            noInternetLayout.hide()
        }
    }

    override fun favClick(data: Data, position: Int) {

        if(!Subscription.isSubscribe){
            gotoFrag(R.id.action_global_subscriptionFragment)
            return
        }

        lifecycleScope.launch {
            viewmodel.modifyFavNames(
                contentId = data.Id,
                language =  getLanguage(),
                isFav = !data.IsFavorite,
                position = position
            )
        }
    }

}