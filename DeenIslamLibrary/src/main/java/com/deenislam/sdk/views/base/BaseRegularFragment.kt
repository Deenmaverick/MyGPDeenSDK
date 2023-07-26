package com.deenislam.sdk.views.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.AsyncViewStub
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.isBottomNavFragment
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.FragmentViewModel
import com.deenislam.sdk.views.main.MainActivity
import com.deenislam.sdk.views.main.actionCallback
import com.deenislam.sdk.views.main.searchCallback


internal abstract class BaseRegularFragment: Fragment() {

    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private var actionCallback:otherFagmentActionCallback ? =null
    private var isOnlyback:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OnCreate()

    }

    override fun onResume() {
        super.onResume()
        onBackPressedCallback =
            requireActivity().onBackPressedDispatcher.addCallback {
                onBackPress()
            }
        onBackPressedCallback.isEnabled = true

    }

    fun isOnlyBack(bol:Boolean)
    {
        isOnlyback = bol
    }


    open fun onBackPress() {
        if(!isOnlyback) {
            findNavController().popBackStack().apply {
                setupOtherFragment(false)
            }
        }
        else
            findNavController().popBackStack()

        Log.e("isOnlyback",isOnlyback.toString())
    }

    open fun OnCreate(){
        onBackPressedCallback =
            requireActivity().onBackPressedDispatcher.addCallback {
                onBackPress()
            }
        onBackPressedCallback.isEnabled = true
    }



    fun gotoFrag(
        destination: Int,
        data: Bundle? = null,
        navOptions: NavOptions? = null,
        navExtras: Navigator.Extras? = null
    ) {

        val navController = findNavController()
        navController.navigate(destination, data, navOptions, navExtras)

    }


    fun changeMainViewPager(page:Int)
    {
        (activity as MainActivity).setViewPager(page)
    }

    fun gotoDashboard()
    {
        (activity as MainActivity).initDashboard()
    }

    fun showBottomNav(bol:Boolean)
    {
        (activity as MainActivity).showBottomNav(bol)
    }

    fun setupOtherFragment(bol:Boolean)
    {
        (requireActivity() as MainActivity).setupOtherFragment(bol)
    }

    fun setupActionForOtherFragment(action1:Int,action2:Int,callback: otherFagmentActionCallback?=null,actionnBartitle:String,backEnable:Boolean=true,view: View,isBackIcon:Boolean = false)
    {
        val action1Btn:AppCompatImageView = view.findViewById(R.id.action1)
        val action2Btn:AppCompatImageView = view.findViewById(R.id.action2)
        val btnBack:AppCompatImageView = view.findViewById(R.id.btnBack)
        val title:AppCompatTextView = view.findViewById(R.id.title)

        if(backEnable)
        {
            btnBack.setImageDrawable(AppCompatResources.getDrawable(view.context, R.drawable.ic_back))
            btnBack.visible(true)
            btnBack.setOnClickListener {
                onBackPress()
            }
            title.text = actionnBartitle
            title.setTextColor(ContextCompat.getColor(title.context,R.color.txt_black_deep))

        }
        else
        {
            (title.layoutParams as ConstraintLayout.LayoutParams).apply {
                leftMargin=16.dp
            }
            title.text = actionnBartitle
            btnBack.visible(isBackIcon)
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

        if(findNavController().currentDestination?.id?.isBottomNavFragment == false)
            setupOtherFragment(true)
        else
            setupOtherFragment(false)
    }


    fun setupAction(action1:Int,action2:Int,callback: actionCallback)
    {
        (activity as MainActivity).setupActionbar(action1,action2,callback)
    }

    fun setupAction(action1:Int,action2:Int,callback: actionCallback?=null,title:String,backEnable:Boolean=false)
    {
        (activity as MainActivity).setupActionbar(title,backEnable)
        (activity as MainActivity).setupActionbar(action1,action2,callback)
    }

    fun setupSearchbar(callback: searchCallback?=null)
    {
        (activity as MainActivity).setupSearchbar(callback)
    }

    fun hideSearchbar()
    {
        (activity as MainActivity).hideSearch()
    }

    fun setupAction(action1:Int,action2:Int,title:String,backEnable:Boolean=false)
    {
        (activity as MainActivity).setupActionbar(title,backEnable)
        (activity as MainActivity).setupActionbar(action1,action2)
    }

    inline fun <T : View> prepareStubView(
        stub: AsyncViewStub,
        layoutID:Int,
        crossinline prepareBlock: T.() -> Unit
    ) {
        stub.layoutRes = layoutID
        val inflatedView = stub.inflatedId as? T

        if (inflatedView != null) {
            inflatedView.prepareBlock()
        } else {
            stub.inflate(AsyncLayoutInflater.OnInflateFinishedListener { view, _, _ ->
                (view as? T)?.prepareBlock()
            })
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //unregister listener here
        if(this::onBackPressedCallback.isInitialized) {
            onBackPressedCallback.isEnabled = false
            onBackPressedCallback.remove()
        }
    }
}

interface otherFagmentActionCallback
{
    fun action1()
    fun action2()
}