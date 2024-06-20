package com.deenislamic.sdk.views.ramadan

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.RamadanCallback
import com.deenislamic.sdk.service.callback.ViewInflationListener
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.RamadanResource
import com.deenislamic.sdk.service.models.ramadan.StateModel
import com.deenislamic.sdk.service.network.response.ramadan.Data
import com.deenislamic.sdk.service.network.response.ramadan.FastTracker
import com.deenislamic.sdk.service.repository.RamadanRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.get9DigitRandom
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.viewmodels.RamadanViewModel
import com.deenislamic.sdk.views.adapters.ramadan.OtherRamadanPatchAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

internal class RamadanOtherDayFragment : BaseRegularFragment(), RamadanCallback,
    ViewInflationListener {

    private lateinit var listview:RecyclerView

    private lateinit var viewmodel: RamadanViewModel

    private lateinit var otherRamadanPatchAdapter: OtherRamadanPatchAdapter

    private var stateArray:ArrayList<StateModel> = arrayListOf()
    private var selectedState:StateModel ? = null
    private var fastingCardData: FastTracker? = null
    private var state = "dhaka"

    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)
        // init viewmodel
        val repository = RamadanRepository(
            deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = RamadanViewModel(repository)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_ramadan_other_day,container,false)

        listview = mainview.findViewById(R.id.listview)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.ramadan_titlle),
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)
        CallBackProvider.setFragment(this)
        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!firstload)
        {
            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "ramadan_other_day",
                    trackingID = getTrackingID()
                )
            }
        }

        firstload = true

        /*if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }else*/
            loadpage()

    }

    private fun loadpage()
    {
        stateArray = arrayListOf(
            StateModel("dhaka", "Dhaka (ঢাকা)"),
            StateModel("barisal", "Barisal (বরিশাল)"),
            StateModel("khulna", "Khulna (খুলনা)"),
            StateModel("chittagong", "Chittagong (চট্টগ্রাম)"),
            StateModel("mymensingh", "Mymensingh (ময়মনসিংহ)"),
            StateModel("rangpur", "Rangpur (রংপুর)"),
            StateModel("rajshahi", "Rajshahi (রাজশাহী)"),
            StateModel("sylhet", "Sylhet (সিলেট)"),
            StateModel("bagerhat", "Bagerhat (বাগেরহাট)"),
            StateModel("chuadanga", "Chuadanga (চুয়াডাঙ্গা)"),
            StateModel("jessore", "Jessore (যশোর)"),
            StateModel("jhenaidah", "Jhenaidah (ঝিনাইদহ)"),
            StateModel("kushtia", "Kushtia (কুষ্টিয়া)"),
            StateModel("magura", "Magura (মাগুরা)"),
            StateModel("meherpur", "Meherpur (মেহেরপুর)"),
            StateModel("narail", "Narail (নড়াইল)"),
            StateModel("satkhira", "Satkhira (সাতক্ষীরা)"),
            StateModel("bandarban", "Bandarban (বান্দরবান)"),
            StateModel("brahmanbaria", "Brahmanbaria (ব্রাহ্মণবাড়িয়া)"),
            StateModel("chandpur", "Chandpur (চাঁদপুর)"),
            StateModel("comilla", "Comilla (কুমিল্লা)"),
            StateModel("coxsBazar", "CoxsBazar (কক্সবাজার)"),
            StateModel("feni", "Feni (ফেনী)"),
            StateModel("khagrachhari", "Khagrachhari (খাগড়াছড়ি)"),
            StateModel("lakshmipur", "Lakshmipur (লক্ষ্মীপুর)"),
            StateModel("noakhali", "Noakhali (নোয়াখালী)"),
            StateModel("rangamati", "Rangamati (রাঙ্গামাটি)"),
            StateModel("faridpur", "Faridpur (ফরিদপুর)"),
            StateModel("tangail", "Tangail (টাঙ্গাইল)"),
            StateModel("gazipur", "Gazipur (গাজীপুর)"),
            StateModel("gopalganj", "Gopalganj (গোপালগঞ্জ)"),
            StateModel("kishoreganj", "Kishoreganj (কিশোরগঞ্জ)"),
            StateModel("madaripur", "Madaripur (মাদারীপুর)"),
            StateModel("manikganj", "Manikganj (মানিকগঞ্জ)"),
            StateModel("munshiganj", "Munshiganj (মুন্সীগঞ্জ)"),
            StateModel("narayanganj", "Narayanganj (নারায়ণগঞ্জ)"),
            StateModel("narsingdi", "Narsingdi (নরসিংদী)"),
            StateModel("rajbari", "Rajbari (রাজবাড়ী)"),
            StateModel("shariatpur", "Shariatpur (শরীয়তপুর)"),
            StateModel("barguna", "Barguna (বরগুনা)"),
            StateModel("bhola", "Bhola (ভোলা)"),
            StateModel("jhalokati", "Jhalokati (ঝালকাঠি)"),
            StateModel("patuakhali", "Patuakhali (পটুয়াখালী)"),
            StateModel("pirojpur", "Pirojpur (পিরোজপুর)"),
            StateModel("dinajpur", "Dinajpur (দিনাজপুর)"),
            StateModel("gaibandha", "Gaibandha (গাইবান্ধা)"),
            StateModel("kurigram", "Kurigram (কুড়িগ্রাম)"),
            StateModel("lalmonirhat", "Lalmonirhat (লালমনিরহাট)"),
            StateModel("nilphamari", "Nilphamari (নীলফামারী)"),
            StateModel("panchagarh", "Panchagarh (পঞ্চগড়)"),
            StateModel("thakurgaon", "Thakurgaon (ঠাকুরগাঁও)"),
            StateModel("bogra", "Bogra (বগুড়া)"),
            StateModel("pabna", "Pabna (পাবনা)"),
            StateModel("joypurhat", "Joypurhat (জয়পুরহাট)"),
            StateModel("chapainawabganj", "Chapainawabganj (চাঁপাইনবাবগঞ্জ)"),
            StateModel("naogaon", "Naogaon (নওগাঁ)"),
            StateModel("natore", "Natore (নাটোর)"),
            StateModel("sirajganj", "Sirajganj (সিরাজগঞ্জ)"),
            StateModel("habiganj", "Habiganj (হবিগঞ্জ)"),
            StateModel("moulvibazar", "Moulvibazar (মৌলভীবাজার)"),
            StateModel("sunamganj", "Sunamganj (সুনামগঞ্জ)"),
            StateModel("sherpur", "Sherpur (শেরপুর)"),
            StateModel("jamalpur", "Jamalpur (জামালপুর)"),
            StateModel("netrokona", "Netrokona (নেত্রকোনা)")
        )

        initObserver()

        loadApi()
    }

    private fun initObserver()
    {
        viewmodel.ramadanLiveData.observe(viewLifecycleOwner)
        {

            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is RamadanResource.ramadanTime -> {
                    lifecycleScope.launch {
                        viewmodel.clear()
                    }
                    viewState(it.data)
                }
            }

        }

        viewmodel.ramadanTrackLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is RamadanResource.ramadanTracking -> updateFastingTrack(it.isFasting)
            }
        }
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "ramadan_other_day",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }

    private fun loadApi()
    {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getOtherRamadanTime(state,getLanguage())
        }
    }

    override fun noInternetRetryClicked() {
        loadApi()
    }

    private fun viewState(data: Data)
    {
        fastingCardData = data.FastTracker
        listview.apply {
            otherRamadanPatchAdapter = OtherRamadanPatchAdapter(data,stateArray,selectedState)
            adapter = otherRamadanPatchAdapter
        }
        baseViewState()
    }

    private fun updateFastingTrack(fasting: Boolean)
    {
        otherRamadanPatchAdapter.updateFastingTrack(fasting)
    }

    override fun openMonthlyTracker() {
        fastingCardData = otherRamadanPatchAdapter.getFastingTrackData()
        fastingCardData?.let {
            val bundle = Bundle()
            bundle.putParcelable("fastingCardData",it)
            gotoFrag(R.id.action_global_fastingTrackerFragment,bundle)
        }

    }

    override fun setFastingTrack(isFast: Boolean) {
        if(!Subscription.isSubscribe){
            gotoFrag(R.id.action_global_subscriptionFragment)
            return
        }
        lifecycleScope.launch {
            viewmodel.setRamadanTrack(isFast,getLanguage())
        }
    }

    override fun stateSelected(stateModel: StateModel) {
        otherRamadanPatchAdapter.updateDropdownSelectedState(stateModel)
        state = stateModel.state
        selectedState = stateModel
        loadApi()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.launch {

            viewmodel.clear()
        }
    }

    override fun onAllViewsInflated() {
        baseViewState()
    }

    override fun sehriCardClicked(sehriTime: String) {

        initSetAlarm(sehriTime,1,"Sehri")

    }

    override fun iftarCardClicked(iftaar: String) {
        initSetAlarm(iftaar,0,"Iftar")
    }

    private fun initSetAlarm(time:String,alarmType:Int,title:String){
        val sdf =
            SimpleDateFormat(
                "HH:mm",
                Locale.ENGLISH
            ) // or "hh:mm" for 12 hour format

        val date = sdf.parse(time)

        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val min = calendar.get(Calendar.MINUTE)

            val i = Intent(AlarmClock.ACTION_SET_ALARM)

            i.putExtra(AlarmClock.EXTRA_MESSAGE, title)

            if (alarmType == 0) {
                i.putExtra(AlarmClock.EXTRA_HOUR, 12 + hour)
            } else {
                i.putExtra(AlarmClock.EXTRA_HOUR, hour)
            }

            i.putExtra(AlarmClock.EXTRA_MINUTES, min)
            requireContext().startActivity(i)
        }
    }

}