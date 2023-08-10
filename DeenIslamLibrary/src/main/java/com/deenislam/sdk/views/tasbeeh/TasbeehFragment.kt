package com.deenislam.sdk.views.tasbeeh

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.Deen
import com.deenislam.sdk.R
import com.deenislam.sdk.service.database.entity.Tasbeeh
import com.deenislam.sdk.service.database.entity.UserPref
import com.deenislam.sdk.service.di.DatabaseProvider
import com.deenislam.sdk.service.libs.media3.AudioManager
import com.deenislam.sdk.service.models.TasbeehResource
import com.deenislam.sdk.service.repository.TasbeehRepository
import com.deenislam.sdk.utils.*
import com.deenislam.sdk.viewmodels.TasbeehViewModel
import com.deenislam.sdk.views.adapters.tasbeeh.TasbeehDuaAdapter
import com.deenislam.sdk.views.adapters.tasbeeh.TasbeehRecentCountAdapter
import com.deenislam.sdk.views.adapters.tasbeeh.tasbeehDuaCallback
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

internal class TasbeehFragment : BaseRegularFragment(),tasbeehDuaCallback {

    private val duaListRC: RecyclerView by lazy { requireView().findViewById(R.id.duaList) }
    private val recentCountList:RecyclerView by lazy { requireView().findViewById(R.id.recentCountList) }
    private val container:NestedScrollView by lazy { requireView().findViewById(R.id.container) }
    private val countViewLoading:LinearLayout by lazy { requireView().findViewById(R.id.countViewLoading) }
    private val countTxt:AppCompatTextView by lazy { requireView().findViewById(R.id.countTxt) }
    private val todaysCountTxt:AppCompatTextView by lazy { requireView().findViewById(R.id.todaysCountTxt) }
    private val totalCountTxt:AppCompatTextView by lazy { requireView().findViewById(R.id.totalCountTxt) }
    private val countRadioGroup:RadioGroup by lazy { requireView().findViewById(R.id.countRadioGroup) }
    private val countView:HalfGaugeView by lazy { requireView().findViewById(R.id.countView) }
    private val norecentData:AppCompatTextView by lazy { requireView().findViewById(R.id.norecentData) }
    private lateinit var progressLayout:LinearLayout


    private val countRadio1Txt:AppCompatTextView by lazy { requireView().findViewById(R.id.countRadio1Txt) }
    private val countRadio2Txt:AppCompatTextView by lazy { requireView().findViewById(R.id.countRadio2Txt) }
    private val countRadio3Txt:AppCompatTextView by lazy { requireView().findViewById(R.id.countRadio3Txt) }
    private val countRadio4Txt:AppCompatTextView by lazy { requireView().findViewById(R.id.countRadio4Txt) }
    private val targetCountTxt:AppCompatTextView by lazy { requireView().findViewById(R.id.targetCountTxt) }

    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : View
    private var tasbeehResetDialog: AlertDialog? = null
    private var todayResetBtn:MaterialButton ? = null
    private var totalResetBtn:MaterialButton ? = null

    private val bottom_navigation: BottomNavigationView by lazy { requireView().findViewById<CoordinatorLayout?>(R.id.bottom_nav).findViewById(R.id.nav_view) }
    private val countBtn:FloatingActionButton by lazy { requireView().findViewById<CoordinatorLayout?>(R.id.bottom_nav).findViewById(R.id.btnAdd) }

    private var tasbeehdata:Tasbeeh ? = null
    private var userPref:UserPref ? = null


    private val tasbeehDuaAdapter:TasbeehDuaAdapter by lazy { TasbeehDuaAdapter(this@TasbeehFragment) }
    private val tasbeehRecentCountAdapter:TasbeehRecentCountAdapter by lazy { TasbeehRecentCountAdapter() }

    private lateinit var viewmodel:TasbeehViewModel

    private var todayDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    private var selectedPos:Int = 0
    private var selectedCount:Int = 1
    private var track4Count:Int = 0
    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()

        setupBackPressCallback(this)

        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)

        val repository = TasbeehRepository(
            tasbeehDao = DatabaseProvider().getInstance().provideTasbeehDao(),
            userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
        )
        viewmodel = TasbeehViewModel(repository)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainView = localInflater.inflate(R.layout.fragment_tasbeeh,container,false)
        setupActionForOtherFragment(0,0,null,localContext.getString(R.string.digital_tasbeeh),true,mainView)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        return mainView
    }

    override fun onResume() {
        super.onResume()
        loadpage()
    }



    private fun loadpage()
    {

        if(!firstload)
        {
            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = Deen.msisdn,
                    pagename = "digital_tasbeeh",
                    trackingID = getTrackingID()
                )
            }
        }
        firstload = true


        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(),R.style.MaterialAlertDialog_rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_tasbeeh_reset, null, false)

        countRadio1Txt.text = "33".numberLocale()
        countRadio2Txt.text = "34".numberLocale()
        countRadio3Txt.text = "99".numberLocale()
        countTxt.text = "00".numberLocale()

        //init observer
        initObserver()

        duaListRC.apply {
            adapter = tasbeehDuaAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
            ViewPagerHorizontalRecyler().getInstance().load(this)
        }

        recentCountList.apply {
            adapter = tasbeehRecentCountAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        countRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.countRadio1 -> {
                    activeRadioText(countRadio1Txt)
                    selectedCount = 1

                }
                R.id.countRadio2 -> {
                    activeRadioText(countRadio2Txt)
                    selectedCount = 2
                }

                R.id.countRadio3 -> {
                    activeRadioText(countRadio3Txt)
                    selectedCount = 3
                }

                R.id.countRadio4 -> {

                    if(selectedCount!=4)
                        track4Count = 0

                    activeRadioText(countRadio4Txt)
                    selectedCount = 4
                }
            }

            updateCountView(selectedCount)
        }


        bottom_navigation.setOnItemSelectedListener { item ->

            MainActivity.instance?.bottomNavClicked = true

            when (item.itemId) {

                R.id.action_volume ->
                {
                    lifecycleScope.launch {
                        viewmodel.updateTasbeehSound()
                    }
                }

                R.id.action_reset ->
                {
                    tasbeehdata?.let {

                        showResetDialog(it.id)
                    }
                }


                else -> Unit
            }

            true
        }

        countBtn.setOnClickListener {

            it.isEnabled = false
            if(userPref?.tasbeeh_sound==true) {
                lifecycleScope.launch(Dispatchers.IO)
                {
                    AudioManager().getInstance()
                        .playRawAudioFile(requireContext(), R.raw.tasbeeh_count)
                }
            }

            lifecycleScope.launch {
                tasbeehdata?.let { it1 -> viewmodel.setTasbeehCount(selectedCount, it1,todayDate) }
            }

        }

        loadAPI(selectedPos+1)

    }

    private fun showResetDialog(duaid:Int)
    {

        todayResetBtn = customAlertDialogView.findViewById(R.id.todayBtn)
        totalResetBtn = customAlertDialogView.findViewById(R.id.totalBtn)

        if(tasbeehResetDialog?.isShowing == true)
            tasbeehResetDialog?.dismiss()

        if(customAlertDialogView.parent!=null)
            (customAlertDialogView.getParent() as ViewGroup).removeView(customAlertDialogView)

        tasbeehResetDialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(true)
            .show().apply {
                window?.setGravity(Gravity.BOTTOM)
            }

        todayResetBtn?.setOnClickListener{
            totalResetBtn?.isEnabled = false
            lifecycleScope.launch {
                viewmodel.resetTasbeeh(2,duaid)
            }
        }

        totalResetBtn?.setOnClickListener{
            todayResetBtn?.isEnabled = false
            lifecycleScope.launch {
                viewmodel.resetTasbeeh(1,duaid)
            }
        }

    }

    private fun activeRadioText(view:AppCompatTextView)
    {
        countRadio1Txt.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))
        countRadio2Txt.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))
        countRadio3Txt.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))
        countRadio4Txt.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))
        view.setTextColor(ContextCompat.getColor(requireContext(),R.color.primary))
    }

    private fun updateCountView(count:Int)
    {
        tasbeehdata?.apply {

            var trackcount = -1

            var targetCount = -1

            if(count == 1) {
                trackcount = this.track1
                targetCount = 33
            }
            else if(count == 2) {
                trackcount = this.track2
                targetCount = 34
            }
            else if(count == 3) {
                trackcount = this.track3
                targetCount = 99
            }
            else {
                trackcount = track4Count++
            }

            if(trackcount>targetCount && targetCount!=-1)
            {
                countView.setProgress(100F)
                countTxt.text = trackcount.toString().numberLocale()
                targetCountTxt.text = "/${targetCount.toString()}"
            }
            else if(trackcount<targetCount && count!=-1)
            {
                countView.setProgress(
                    if((trackcount.toFloat()*100)/targetCount>100)100F
                    else if((trackcount.toFloat()*100)/targetCount<0)0F
                    else (trackcount.toFloat()*100)/targetCount
                )
                countTxt.text = trackcount.toString().numberLocale()
                targetCountTxt.text = "/${targetCount}".numberLocale()
            }
            else {
                countView.setProgress(100F)
                countTxt.text = trackcount.toString().numberLocale()
                targetCountTxt.text = "/∞"
            }
        }
    }

    private fun initObserver()
    {
        viewmodel.duaLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is TasbeehResource.duaData -> viewState(it.data)
                is TasbeehResource.userPref -> soundViewState(it.userPref)
                is TasbeehResource.resetDuaData ->
                {
                    requireContext().toast("Tasbeeh count reset successfully")
                    track4Count = 0
                    todayResetBtn?.isEnabled = true
                    totalResetBtn?.isEnabled = true
                    tasbeehResetDialog?.dismiss()
                    viewState(it.data)
                }

                is TasbeehResource.recentCount ->
                {
                    if(it.data.isNotEmpty()) {
                        norecentData.hide()
                        recentCountList.show()
                    }
                    tasbeehRecentCountAdapter.update(it.data)
                }
            }
        }
    }

    private fun loadAPI(duaid:Int)
    {
        countViewLoading.show()
        lifecycleScope.launch {
            viewmodel.fetchDuaData(duaid)
            viewmodel.fetchUserPref()
            viewmodel.fetchRecentCount()
        }
    }

    private fun soundViewState(userPref: UserPref?)
    {
        this.userPref = userPref

        userPref?.apply {
            if(this.tasbeeh_sound)
            bottom_navigation.menu.findItem(R.id.action_volume).setIcon(R.drawable.ic_volume)
            else
                bottom_navigation.menu.findItem(R.id.action_volume).setIcon(R.drawable.ic_volume_off)
        }
    }
    private fun viewState(data: Tasbeeh)
    {
        countBtn.isEnabled = true
        countViewLoading.hide()

        tasbeehdata = data
        countTxt.text = data.dua_count.toString()
        if(todayDate == data.date)
        todaysCountTxt.text = data.daily_count.toString().numberLocale()
        else
            todaysCountTxt.text = "0".numberLocale()

        totalCountTxt.text = data.dua_count.toString().numberLocale()

        updateCountView(selectedCount)

        tasbeehDuaAdapter.update(data.id-1)
        duaListRC.smoothScrollToPosition(data.id-1)

        progressLayout.hide()

    }


    override fun selectedDua(duaid: Int) {

        track4Count = 0
        loadAPI(duaid)
    }

    override fun onBackPress() {

        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = Deen.msisdn,
                    pagename = "digital_tasbeeh",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }

    }

}