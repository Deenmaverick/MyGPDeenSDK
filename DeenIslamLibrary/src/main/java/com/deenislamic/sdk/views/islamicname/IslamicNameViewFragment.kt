package com.deenislamic.sdk.views.islamicname

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.islamicname.IslamicnameAlphabetCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicNameResource
import com.deenislamic.sdk.service.network.response.islamicname.Data
import com.deenislamic.sdk.service.repository.IslamicNameRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.IslamicNameViewModel
import com.deenislamic.sdk.views.adapters.islamicname.IslamicNameAdapter
import com.deenislamic.sdk.views.adapters.islamicname.IslamicNameAdapterCallback
import com.deenislamic.sdk.views.adapters.islamicname.IslamicNameAlphabetAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch


internal class IslamicNameViewFragment : BaseRegularFragment(),
    IslamicNameAdapterCallback, IslamicnameAlphabetCallback {
    private lateinit var clParent: ConstraintLayout
    private lateinit var mcvAlphabet: MaterialCardView
    private lateinit var listView: RecyclerView
    private lateinit var rvAlphabetic: RecyclerView
    private lateinit var progressLayout: LinearLayout
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var noInternetRetry: MaterialButton

    private lateinit var islamicNameAdapter: IslamicNameAdapter
    private var gender = "male"
    private lateinit var viewmodel: IslamicNameViewModel

    private val args: IslamicNameViewFragmentArgs by navArgs()
    private var alphabet = "A"
    private var firstload = false

    companion object {
        fun newInstance(gender: String,title:String): IslamicNameViewFragment {
            val fragment = IslamicNameViewFragment()
            val args = Bundle()
            args.putString("gender", gender)
            args.putString("title", title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun OnCreate() {
        super.OnCreate()
        val repository = IslamicNameRepository(NetworkProvider().getInstance().provideIslamicNameService())
        viewmodel = IslamicNameViewModel(repository)
        gender = args.gender

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_islamic_name_view, container, false)
        clParent = mainView.findViewById(R.id.clParent)
        mcvAlphabet = mainView.findViewById(R.id.mcvAlphabet)
        listView = mainView.findViewById(R.id.listView)
        rvAlphabetic = mainView.findViewById(R.id.rvAlphabetic)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        nodataLayout = mainView.findViewById(R.id.nodataLayout)
        noInternetLayout = mainView.findViewById(R.id.no_internet_layout)
        noInternetRetry = noInternetLayout.findViewById(R.id.no_internet_retry)
        CallBackProvider.setFragment(this)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (gender == "male") {
            clParent.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.deen_light_blue
                )
            )
            mcvAlphabet.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.deen_blue_card
                )
            )
        } else {
            clParent.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.deen_light_pink
                )
            )
            mcvAlphabet.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.deen_pink_card
                )
            )
        }


            loadpage()


    }

    private fun loadpage() {
        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(noInternetLayout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)

        islamicNameAdapter = IslamicNameAdapter(this@IslamicNameViewFragment)
        listView.apply {
            adapter = islamicNameAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }
        rvAlphabetic.apply {
            adapter = IslamicNameAlphabetAdapter()
        }

        initObserver()
        loadingState()

        if(!firstload)
        loadApiData()
        firstload = true
    }

    private fun loadApiData() {
        loadingState()
        lifecycleScope.launch {
            viewmodel.getNames(gender, getLanguage(), alphabet)
        }
    }


    private fun initObserver() {
        viewmodel.islamicNamesLiveData.observe(viewLifecycleOwner)
        {
            when (it) {
                CommonResource.API_CALL_FAILED -> noInternetState()

                CommonResource.EMPTY -> emptyState()
                is IslamicNameResource.islamicNames -> viewState(it.data)
                is IslamicNameResource.favFailed -> Unit
                is IslamicNameResource.favDone -> islamicNameAdapter.favUpdate(
                    it.adapaterPosition,
                    it.bol
                )

            }
        }

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

    private fun noInternetState() {
        progressLayout.hide()
        nodataLayout.hide()
        noInternetLayout.show()
    }

    private fun viewState(data: List<Data>) {
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
                language = getLanguage(),
                isFav = !data.IsFavorite,
                position = position
            )
        }
    }

    override fun shareName(name: String, meaning: String, arText: String) {

        val bundle = Bundle()

        if(getLanguage() == "en"){
            bundle.putString("enText",name)
        }else{
            bundle.putString("bnText",name)
        }
        bundle.putString("title",args.title)
        bundle.putString("arText",arText)
        bundle.putString("footerText",meaning)
        //bundle.putString("customShareText","পবিত্র কুরআন তিলাওয়াত করুন  https://deenislamic.com/app/quran?id=${surahID-1}")
        bundle.putString("customShareText","")

        gotoFrag(R.id.action_global_shareFragment,bundle)
    }


    override fun onAlphabetClick(item: String) {
        alphabet = item
        loadApiData()
    }
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible){
            CallBackProvider.setFragment(this)
        }
    }
}