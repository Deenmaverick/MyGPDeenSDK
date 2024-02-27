package com.deenislam.sdk.views.islamimasaIl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.RamadanCallback
import com.deenislam.sdk.service.database.AppPreference
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.IslamiMasailResource
import com.deenislam.sdk.service.models.ramadan.StateModel
import com.deenislam.sdk.service.repository.IslamiMasailRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.LoadingButton
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.utils.toast
import com.deenislam.sdk.viewmodels.IslamiMasailViewModel
import com.deenislam.sdk.views.adapters.common.CommonStateList
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch


internal class CreateMasailQuestionFragment : BaseRegularFragment(), RamadanCallback {

    private lateinit var viewmodel:IslamiMasailViewModel
    private lateinit var categoryLayout:MaterialCardView
    private lateinit var category:AppCompatTextView
    private lateinit var commonStateList: CommonStateList
    private lateinit var nameHideCheckbox:AppCompatCheckBox
    private lateinit var highPrioCheckbox:AppCompatCheckBox
    private lateinit var questionInput:TextInputEditText
    private lateinit var postBtn:MaterialButton
    private lateinit var charCount:AppCompatTextView

    override fun OnCreate() {
        super.OnCreate()

        val repository = IslamiMasailRepository(
            deenService = NetworkProvider().getInstance().provideDeenService())

        val factory = VMFactory(repository)
        viewmodel = ViewModelProvider(
            requireActivity(),
            factory
        )[IslamiMasailViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_create_masail_question,container,false)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.create_a_question),
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)

        CallBackProvider.setFragment(this)

        categoryLayout = mainview.findViewById(R.id.categoryLayout)
        category = mainview.findViewById(R.id.category)
        nameHideCheckbox = mainview.findViewById(R.id.nameHideCheckbox)
        highPrioCheckbox = mainview.findViewById(R.id.highPrioCheckbox)
        questionInput = mainview.findViewById(R.id.questionInput)
        postBtn = mainview.findViewById(R.id.postBtn)
        commonStateList = CommonStateList(categoryLayout)
        commonStateList.setCustomState(arrayListOf())
        commonStateList.setCustomTitle(localContext.getString(R.string.question_category))
        charCount = mainview.findViewById(R.id.charCount)


        questionInput.addTextChangedListener {
            charCount.text = "${questionInput.text?.length}/800".numberLocale()
            postBtn.isEnabled = questionInput.text?.isNotEmpty() == true
        }

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else*/
            loadpage()

    }

    private fun loadpage(){

        postBtn.setOnClickListener {
            postQuestion()
        }

        initObserver()
        loadapi()
    }

    private fun loadapi(){

        lifecycleScope.launch {
            viewmodel.getMasailCat(getLanguage(),1,100)
        }
    }

    private fun postQuestion(){
        if(questionInput.text?.isEmpty() == true){
            context?.toast(localContext.getString(R.string.please_enter_your_question_here))
            return
        }
       /* else if((questionInput.text?.length ?: 0) < 50){
            requireContext().toast("Question minimum length 50 character")
            return
        }*/
        lifecycleScope.launch {
            commonStateList.getSelectedState()?.state?.toInt()
                ?.let {
                    postBtn.isClickable = false
                    postBtn.text = LoadingButton().getInstance(requireContext()).loader(postBtn)
                    viewmodel.postQuestion(
                    language = getLanguage(),
                    catid = it,
                    title = questionInput.text.toString(),
                    place = AppPreference.getUserCurrentState().toString(),
                    isAnonymous = nameHideCheckbox.isChecked,
                    isUrgent = highPrioCheckbox.isChecked
                )
                }
        }
    }

    private fun initObserver(){

        viewmodel.islamiMasailLivedata.observe(viewLifecycleOwner){
            when(it){
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseNoInternetState()
                is IslamiMasailResource.MasailCatList -> {
                    val customState = it.data.Data.map { it1-> StateModel(state = it1.Id.toString(), stateValue = it1.category) }
                    commonStateList.setCustomState(customState)
                    if(customState.isNotEmpty()){
                        category.text = customState[0].stateValue
                    }
                    baseViewState()
                }
                is IslamiMasailResource.postQuestion -> {
                    viewmodel.postSuccess()
                    requireContext().toast(localContext.getString(R.string.you_have_successfully_created_a_question))
                    postBtn.isClickable = true
                    postBtn.text = localContext.getText(R.string.please_submit)
                    LoadingButton().getInstance(requireContext()).removeLoader()
                    onBackPress()
                }
            }
        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    override fun stateSelected(stateModel: StateModel) {
        commonStateList.stateSelected(stateModel)
        category.text = stateModel.stateValue
    }


    inner class VMFactory(
        private val islamiMasailRepository: IslamiMasailRepository
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IslamiMasailViewModel(islamiMasailRepository) as T
        }
    }
}