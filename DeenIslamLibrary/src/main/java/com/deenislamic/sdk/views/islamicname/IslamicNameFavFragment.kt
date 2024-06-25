package com.deenislamic.sdk.views.islamicname

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.alertdialog.CustomAlertDialog
import com.deenislamic.sdk.service.libs.alertdialog.CustomDialogCallback
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicNameResource
import com.deenislamic.sdk.service.network.response.islamicname.Data
import com.deenislamic.sdk.service.repository.IslamicNameRepository
import com.deenislamic.sdk.utils.LoadingButton
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.IslamicNameViewModel
import com.deenislamic.sdk.views.adapters.islamicname.IslamicNameFavAdapter
import com.deenislamic.sdk.views.adapters.islamicname.IslamicNameFavAdapterCallback
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch


internal class IslamicNameFavFragment(
    private val isHideActionBar:Boolean = false,
    private val checkFirstload: Boolean = false
) : BaseRegularFragment(), CustomDialogCallback, IslamicNameFavAdapterCallback {


    private lateinit var listView: RecyclerView
    private lateinit var progressLayout: LinearLayout
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var noInternetRetry: MaterialButton
    private lateinit var actionbar:ConstraintLayout

    private var customAlertDialog: CustomAlertDialog? =null

    private var favData: Data? =null
    private var adapterPosition:Int = -1
    private val islamicNameFavAdapter: IslamicNameFavAdapter by lazy { IslamicNameFavAdapter(this@IslamicNameFavFragment) }

    private lateinit var viewmodel: IslamicNameViewModel
    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        val repository = IslamicNameRepository(NetworkProvider().getInstance().provideIslamicNameService())
        viewmodel = IslamicNameViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_islamic_name_fav,container,false)
        //init view
        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.title_fav_names),
            backEnable = true,
            view = mainView
        )
        listView = mainView.findViewById(R.id.listView)
        actionbar = mainView.findViewById(R.id.actionbar)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        nodataLayout = mainView.findViewById(R.id.nodataLayout)
        noInternetLayout = mainView.findViewById(R.id.no_internet_layout)
        noInternetRetry = noInternetLayout.findViewById(R.id.no_internet_retry)

        customAlertDialog = CustomAlertDialog().getInstance()
        customAlertDialog?.setupDialog(
            callback = this@IslamicNameFavFragment,
            context = requireContext(),
            btn1Text = localContext.getString(R.string.cancel),
            btn2Text = localContext.getString(R.string.delete),
            titileText = localContext.getString(R.string.want_to_delete),
            subTitileText = localContext.getString(R.string.do_you_want_to_remove_this_favorite)
        )

        if(isHideActionBar)
            actionbar.hide()

        setupCommonLayout(mainView)
        return mainView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(noInternetLayout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)

        listView.apply {

                adapter = islamicNameFavAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        initObserver()

        if(!checkFirstload)
            loadApiData()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if(menuVisible) {

            customAlertDialog?.setupDialog(
                callback = this@IslamicNameFavFragment,
                context = requireContext(),
                btn1Text = localContext.getString(R.string.cancel),
                btn2Text = localContext.getString(R.string.delete),
                titileText = localContext.getString(R.string.want_to_delete),
                subTitileText = localContext.getString(R.string.do_you_want_to_remove_this_favorite)
            )

            if(checkFirstload && !firstload) {
                loadingState()
                loadApiData()
            }

            firstload = true
        }
    }

    override fun onPause() {
        super.onPause()

        lifecycleScope.launch {
            viewmodel.clear()
        }
    }

    private fun loadApiData()
    {
        lifecycleScope.launch {
            viewmodel.getFavNames("male",getLanguage())
        }
    }

    private fun initObserver()
    {
        viewmodel.favNamesLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> noInternetState()

                CommonResource.EMPTY -> emptyState()
                is IslamicNameResource.favNames -> viewState(it.data)
                is IslamicNameResource.favremovedFailed ->
                {
                    val btn2 = customAlertDialog?.getBtn2()
                    btn2?.isClickable = true
                    LoadingButton().getInstance(requireContext()).removeLoader()
                    customAlertDialog?.dismissDialog()
                    context?.toast(localContext.getString(R.string.failed_to_remove_favorite_item))
                }
                is IslamicNameResource.favremoved ->
                {
                    val btn2 = customAlertDialog?.getBtn2()
                    btn2?.isClickable = true
                    LoadingButton().getInstance(requireContext()).removeLoader()
                    customAlertDialog?.dismissDialog()

                    islamicNameFavAdapter.delItem(adapterPosition)

                    if (islamicNameFavAdapter.itemCount == 0)
                        emptyState()


                    requireContext().toast(localContext.getString(R.string.favorite_list_updated_successful))
                }

                is CommonResource.CLEAR -> Unit

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
        islamicNameFavAdapter.update(data)

        listView.post {
            progressLayout.hide()
            nodataLayout.hide()
            noInternetLayout.hide()
        }
    }

    override fun clickBtn1() {
        customAlertDialog?.dismissDialog()
    }

    override fun clickBtn2() {
        val btn2 = customAlertDialog?.getBtn2()
        btn2?.isClickable = false
        btn2?.text = btn2?.let { LoadingButton().getInstance(requireContext()).loader(it) }

        lifecycleScope.launch {
            favData?.Id?.let { viewmodel.removeFavName(it,getLanguage(),adapterPosition) }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.launch {
            viewmodel.clear()
        }
    }

    override fun delFav(data: Data, position: Int) {
        favData = data
        adapterPosition = position
        customAlertDialog?.showDialog(false)
    }


}