package com.deenislam.sdk.views.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.FragmentViewModel
import com.deenislam.sdk.views.main.MainActivity
import com.deenislam.sdk.views.main.actionCallback

abstract class BaseFragment<VB:ViewBinding>(
    private val bindingInflater: (inflater:LayoutInflater)->VB
):Fragment() {

    private val fragmentViewModel by viewModels<FragmentViewModel>()

    private var _binding:VB ? = null
    val binding:VB get() = _binding as VB
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private var actionCallback:otherFagmentActionCallback ? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OnCreate()
        onBackPressedCallback =
            requireActivity().onBackPressedDispatcher.addCallback {
                onBackPress()
            }
        onBackPressedCallback.isEnabled = true


    }


    fun BASE_CHECK_API_STATE()
    {
        fragmentViewModel.check_api_state()
    }
     fun BASE_OBSERVE_API_CALL_STATE()
    {
        fragmentViewModel.isAPILoaded.observe(this)
        {

            when(it)
            {
                true -> Unit
                false -> BASE_API_CALL_STATE()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = bindingInflater.invoke(inflater)

        if(_binding == null)
            throw java.lang.IllegalArgumentException("View cannot null")

        ON_CREATE_VIEW(binding.root)

        return binding.root
    }

    open fun ON_CREATE_VIEW(root: View)
    {

    }


    override fun onResume() {
        super.onResume()
        onBackPressedCallback =
            requireActivity().onBackPressedDispatcher.addCallback {
                onBackPress()
            }
        onBackPressedCallback.isEnabled = true

    }

    open fun BASE_API_CALL_STATE()
    {

    }

    open fun onBackPress() {
        findNavController().popBackStack()
    }

    fun setupOtherFragment(bol:Boolean)
    {
        (activity as MainActivity).setupOtherFragment(bol)
    }

    fun setupAction(action1:Int,action2:Int,callback: actionCallback)
    {
        (activity as MainActivity).setupActionbar(action1,action2,callback)
    }

    fun setupActionForOtherFragment(action1:Int,action2:Int,callback: otherFagmentActionCallback?=null,actionnBartitle:String,backEnable:Boolean=true,view: View)
    {
        val action1Btn: AppCompatImageView = view.findViewById(R.id.action1)
        val action2Btn: AppCompatImageView = view.findViewById(R.id.action2)
        val btnBack: AppCompatImageView = view.findViewById(R.id.btnBack)
        val title: AppCompatTextView = view.findViewById(R.id.title)

        if(backEnable)
        {
            btnBack.setImageDrawable(AppCompatResources.getDrawable(view.context, R.drawable.ic_back))
            btnBack.visible(true)
            btnBack.setOnClickListener {
                onBackPress()
            }
            title.text = actionnBartitle
            title.setTextColor(ContextCompat.getColor(title.context, R.color.txt_black_deep))

        }
        else
        {
            (title.layoutParams as ConstraintLayout.LayoutParams).apply {
                leftMargin=16.dp
            }
            title.text = actionnBartitle
            btnBack.visible(false)
        }

        if(action1>0) {
            action1Btn.setImageDrawable(AppCompatResources.getDrawable(view.context, action1))
            action1Btn.visible(true)
            action1Btn.setOnClickListener {
                callback?.action1()
            }
        }
        else
            action1Btn.visible(false)

        if(action2>0) {
            action2Btn.setImageDrawable(AppCompatResources.getDrawable(view.context, action2))
            action2Btn.visible(true)
            action2Btn.setOnClickListener {
                callback?.action2()
            }
        }
        else
            action2Btn.visible(false)

        actionCallback = callback
    }

    fun changeMainViewPager(page:Int)
    {
        (activity as MainActivity).setViewPager(page)
    }

    fun setupAction(action1:Int,action2:Int,callback: actionCallback,title:String)
    {
        (activity as MainActivity).setupActionbar(title)
        (activity as MainActivity).setupActionbar(action1,action2,callback)
    }

    fun gotoFrag(destination:Int,data:Bundle?=null,navOptions: NavOptions?=null)
    {
        findNavController().navigate(destination,data,navOptions)

    }

    open fun OnCreate(){}

    override fun onDestroyView() {
        super.onDestroyView()
        //unregister listener here
        onBackPressedCallback.isEnabled = false
        onBackPressedCallback.remove()
    }

}