package com.deenislam.sdk.views.dashboard

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.deenislam.sdk.R
import com.deenislam.sdk.databinding.FragmentDashboardBinding
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.prayer_time.PrayerNotificationResource
import com.deenislam.sdk.service.models.prayer_time.PrayerTimeResource
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.utils.runWhenReady
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.DashboardViewModel
import com.deenislam.sdk.viewmodels.PrayerTimesViewModel
import com.deenislam.sdk.views.adapters.dashboard.DashboardPatchAdapter
import com.deenislam.sdk.views.adapters.dashboard.prayerTimeCallback
import com.deenislam.sdk.views.base.BaseFragment
import com.deenislam.sdk.views.main.MainActivity
import com.deenislam.sdk.views.main.actionCallback
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class DashboardFragment : BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate),
    actionCallback,
prayerTimeCallback{

    private lateinit var dashboardViewModel: DashboardViewModel

    private val prayerTimeViewModel by viewModels<PrayerTimesViewModel>({requireActivity()})

    private val dashboardPatchMain:DashboardPatchAdapter by lazy { DashboardPatchAdapter(this@DashboardFragment) }
    private var prayerdate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())


    override fun OnCreate() {
        super.OnCreate()
        dashboardViewModel = DashboardViewModel()

    }

    private var prayerTimesResponse:PrayerTimesResponse?=null

    private var firstload:Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingState()
        ViewCompat.setTranslationZ(binding.progressLayout.root, 10F)
        ViewCompat.setTranslationZ(binding.noInternetLayout.root, 10F)
        initObserver()
        loadPage()
        binding.noInternetLayout.noInternetRetry.setOnClickListener {
            loadDataAPI()
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        prayerTimesResponse?.let { updatePrayerAdapterOnly(it) }
    }

    override fun onResume() {
        super.onResume()
        setupAction(R.drawable.ic_notification,R.drawable.ic_avatar,this@DashboardFragment,getString(R.string.app_name))

      /*  if(MainActivity.instance?.bottomNavClicked == true)
            animateView()
        else*/
            loadPage()

      /*  BASE_OBSERVE_API_CALL_STATE()

        lifecycleScope.launch {
            BASE_CHECK_API_STATE()
        }*/

    }


    private fun animateView()
    {
        val anim: Animation

        if(MainActivity.instance?.childFragmentAnimForward == true)
            anim = AnimationUtils.loadAnimation(requireContext(), R.anim.right_to_left)
        else
            anim = AnimationUtils.loadAnimation(requireContext(), R.anim.left_to_right)

        requireView().startAnimation(anim)

        anim?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                loadPage()
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })

        MainActivity.instance?.resetBottomNavClick()
    }


  /* override fun setMenuVisibility(visible: Boolean) {
        super.setMenuVisibility(visible)
        if (visible) {
            loadPage()
            //initObserver()

        }
    }*/

    fun loadDataAPI()
    {
          loadingState()
            lifecycleScope.launch {
                prayerTimeViewModel.getPrayerTimes("Dhaka", "bn", prayerdate)
                prayerTimeViewModel.getDateWisePrayerNotificationData(prayerdate)

            }
    }

   /* override fun BASE_API_CALL_STATE() {
        super.BASE_API_CALL_STATE()
        loadDataAPI()
    }
*/


    private fun initObserver()
    {

        prayerTimeViewModel.prayerTimes.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is PrayerTimeResource.postPrayerTime -> viewState(it.data)
                CommonResource.API_CALL_FAILED -> nointernetState()
                PrayerTimeResource.prayerTimeEmpty -> Unit
                else -> Unit
            }
        }

        prayerTimeViewModel.prayerTimesNotification.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is PrayerNotificationResource.dateWiseNotificationData -> updateprayerTracker(it.data)
                else -> Unit
            }
        }

    }

    private fun viewState(data: PrayerTimesResponse)
    {
        if(prayerTimesResponse==null) {
            prayerTimesResponse = data
            dashboardPatchMain.updatePrayerTime(data)
        }
        
        binding.dashboardMain.post {
            dataState()
        }


        //dashboardPatchMain.notifyDataSetChanged()

       /* binding.dashboardMain.afterMeasured {
            dataState()
        }*/

    }

    private fun updateprayerTracker(data: ArrayList<PrayerNotification>)
    {
        Log.e("updateprayerTracker",Gson().toJson(data))
        binding.dashboardMain.runWhenReady {
            dashboardPatchMain.updatePrayerTracker(data)
        }
    }

    private fun updatePrayerAdapterOnly(data: PrayerTimesResponse)
    {
        Log.e("updatePrayerAdapterOnly","Called")
        binding.dashboardMain.post {
            dashboardPatchMain.updatePrayerTime(data)
            //dashboardPatchMain.notifyDataSetChanged()
        }
    }

    private fun dataState()
    {
        binding.dashboardMain.visible(true)
        binding.progressLayout.root.visible(false)
        binding.noInternetLayout.root.visible(false)
    }

    private fun nointernetState()
    {
        binding.progressLayout.root.visible(false)
        binding.noInternetLayout.root.visible(true)
    }

    private fun loadingState()
    {
        binding.progressLayout.root.visible(true)
        binding.noInternetLayout.root.visible(false)
    }

    fun loadPage()
    {
        if(firstload != 0)
            return
        firstload = 1

        ViewCompat.setTranslationZ(binding.progressLayout.root, 10F)

        //dashboardPatchMain.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        val linearLayoutManager =
            LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)


        binding.dashboardMain.apply {
                adapter = dashboardPatchMain
                isNestedScrollingEnabled = false
                layoutManager = linearLayoutManager
                //overScrollMode = View.OVER_SCROLL_NEVER
                post {

                    //base fragment livedata
                  /* BASE_OBSERVE_API_CALL_STATE()
                    initObserver()

                    lifecycleScope.launch {
                        BASE_CHECK_API_STATE()
                    }

                    if(lifecycle.currentState != Lifecycle.State.RESUMED) {
                        loadDataAPI()
                    }*/
                    loadDataAPI()
                }
            }

    }



    override fun onBackPress() {
        activity?.moveTaskToBack(true)
    }

    override fun action1() {

    }

    override fun action2() {

    }

    override fun nextPrayerCountownFinish() {

        prayerTimesResponse?.let { updatePrayerAdapterOnly(it) }
    }

    override fun allPrayerPage() {
        changeMainViewPager(2)
    }

    override fun prayerTask(momentName: String?) {

        lifecycleScope.launch {
            if (momentName?.isNotEmpty() == true) {
               prayerTimeViewModel.setPrayerTrack(date = prayerdate,prayer_tag=momentName,true)
            }
            else
                prayerTimeViewModel.getDateWisePrayerNotificationData(prayerdate)
        }
    }

}