package com.deenislam.sdk.views.base

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.repository.UserTrackRepository
import com.deenislam.sdk.utils.*
import com.deenislam.sdk.viewmodels.UserTrackViewModel
import com.deenislam.sdk.views.main.MainActivity
import com.deenislam.sdk.views.main.actionCallback
import com.deenislam.sdk.views.main.searchCallback
import kotlinx.coroutines.launch
import java.util.*


internal abstract class BaseRegularFragment: Fragment() {

    lateinit var onBackPressedCallback: OnBackPressedCallback
    private var actionCallback:otherFagmentActionCallback ? =null
    private var isBacktoHome:Boolean = false
    private var isBackPressed:Boolean = false
    lateinit var userTrackViewModel: UserTrackViewModel

    lateinit var localContext:Context
    lateinit var localInflater: LayoutInflater
    private lateinit var childFragment: Fragment
    private var lastClickTime = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localContext = if (DeenSDKCore.language == "en") {
            LocaleUtil.createLocaleContext(requireContext(), Locale("en"))
        } else {
            LocaleUtil.createLocaleContext(requireContext(), Locale("bn"))
        }

       val themedContext = ContextThemeWrapper(localContext, R.style.DeenSDKTheme) // Replace with your theme

        localInflater = layoutInflater.cloneInContext(themedContext)


        userTrackViewModel = UserTrackViewModel(
            repository = UserTrackRepository(authenticateService = NetworkProvider().getInstance().provideAuthService())
        )

        OnCreate()

    }

    open fun OnCreate(){

    }

    fun setupBackPressCallback(fragment: Fragment,isBacktoHome:Boolean=false)
    {
        //isBacktoHome(isBacktoHome)
        childFragment = fragment
        onBackPressedCallback =
            fragment.requireActivity().onBackPressedDispatcher.addCallback {
                onBackPress()
            }
        onBackPressedCallback.isEnabled = true
    }

    fun changeLanguage(language:String)
    {
        DeenSDKCore.language = language
        (activity as MainActivity).changeLanguage()
    }

    fun setTrackingID(id:Long)
    {
        (activity as MainActivity).setTrackingID(id)
    }

    fun getTrackingID() = (activity as MainActivity).getTrackingID()

    fun getLanguage():String =
        if(DeenSDKCore.language == "en")
            ""
        else
            "bn"


     fun isBacktoHome(bol:Boolean)
    {
        isBacktoHome = bol
    }

    fun isBackPressed():Boolean = isBackPressed


    open fun onBackPress() {


        Log.e("onBackPress","REGULAR")
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime < 500) {
            return  // The click is too fast, ignore it
        }
        lastClickTime = currentTime

        if (findNavController().previousBackStackEntry?.destination?.id?.equals(
                findNavController().graph.startDestinationId
            ) != true &&
            findNavController().previousBackStackEntry?.destination?.id?.equals(
                R.id.blankFragment
            ) != true && findNavController().previousBackStackEntry?.destination?.id != null)
            findNavController().popBackStack()

       /* if(isVisible) {
            isBackPressed = true

            if (isBacktoHome) {
                findNavController().popBackStack().apply {
                    setupOtherFragment(false)
                }
            } else {

                if (findNavController().previousBackStackEntry?.destination?.id?.equals(
                        findNavController().graph.startDestinationId
                    ) != true)
                    findNavController().popBackStack()
                else
                    setupOtherFragment(false)
            }
        }*/

            /*tryCatch {
                lifecycleScope.launch(Dispatchers.IO)
                {
                    AudioManager().getInstance().releasePlayer()
                }
            }*/

    }




    override fun onPause() {
        super.onPause()
        if(this::onBackPressedCallback.isInitialized) {
            onBackPressedCallback.isEnabled = false
            //onBackPressedCallback.remove()
        }
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

        return
        if(!bol)
        {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.msisdn,
                    pagename = "home",
                    trackingID = get9DigitRandom()
                )
            }
        }
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
                this.requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            title.text = actionnBartitle
            title.setTextColor(ContextCompat.getColor(title.context,R.color.deen_txt_black_deep))

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

      /*  if(findNavController().currentDestination?.id?.isBottomNavFragment == false)
            setupOtherFragment(true)
        else
            setupOtherFragment(false)*/
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
            //onBackPressedCallback.remove()
        }

    }

    override fun onResume() {
        super.onResume()
        if(this::onBackPressedCallback.isInitialized && this::childFragment.isInitialized)
            setupBackPressCallback(this,isBacktoHome)
       /* if(this::onBackPressedCallback.isInitialized && this::childFragment.isInitialized)
            onBackPressedCallback.isEnabled = true


        requireView().addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            when (v.visibility) {
                View.VISIBLE -> {

                    if (this::onBackPressedCallback.isInitialized && !onBackPressedCallback.isEnabled) {
                        onBackPressedCallback.isEnabled = true
                    }

                    // View is now visible
                }

                else -> {
                    // View is not visible
                }
            }
        }*/
    }


}

internal interface otherFagmentActionCallback
{
    fun action1()
    fun action2()
}