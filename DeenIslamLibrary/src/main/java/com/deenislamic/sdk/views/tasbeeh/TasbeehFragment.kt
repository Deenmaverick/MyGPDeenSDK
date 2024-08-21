package com.deenislamic.sdk.views.tasbeeh

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.database.entity.Tasbeeh
import com.deenislamic.sdk.service.database.entity.UserPref
import com.deenislamic.sdk.service.di.DatabaseProvider
import com.deenislamic.sdk.service.libs.media3.AudioManager
import com.deenislamic.sdk.service.models.TasbeehResource
import com.deenislamic.sdk.service.models.tasbeeh.CountCycle
import com.deenislamic.sdk.service.repository.TasbeehRepository
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.FullCircleGaugeView
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.getDrawable
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.viewmodels.TasbeehViewModel
import com.deenislamic.sdk.views.adapters.tasbeeh.SelectDhikirAdapter
import com.deenislamic.sdk.views.adapters.tasbeeh.TasbeehCountCycleAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


internal class TasbeehFragment : BaseRegularFragment(),
    otherFagmentActionCallback {

    private lateinit var countViewLoading:LinearLayout
    private lateinit var countTxt:AppCompatTextView
    private lateinit var countView: FullCircleGaugeView

    private lateinit var targetCountTxt:AppCompatTextView

    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : View
    private var tasbeehResetDialog: AlertDialog? = null
    private var todayResetBtn:MaterialButton ? = null
    private var totalResetBtn:MaterialButton ? = null
    private lateinit var lottieAnim:LottieAnimationView
    private lateinit var duaArabicTxt:AppCompatTextView
    private lateinit var duaTxt:AppCompatTextView
    private lateinit var playBtn:MaterialButton
    private lateinit var leftBtn:AppCompatImageView
    private lateinit var rightBtn:AppCompatImageView
    private lateinit var countBtn:MaterialButton
    private lateinit var btnShare:MaterialButton
    private lateinit var duaLayout:ConstraintLayout
    private lateinit var deenBGTasbeehCount:AppCompatImageView

    private var tasbeehdata:Tasbeeh ? = null
    private var userPref: UserPref? = null

    private lateinit var progressLayout:LinearLayout

    private lateinit var tasbeehCountCycleAdapter: TasbeehCountCycleAdapter
    private lateinit var viewmodel: TasbeehViewModel
    private var todayDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
    private var selectedPos:Int = 0
    private var selectedCount:Int = 1
    private var track4Count:Int = 0
    private var dialog: Dialog? = null
    private var firstload = false

    private val audioManager: AudioManager = AudioManager().getInstance()

    private val duaList:ArrayList<Tasbeeh> = arrayListOf(
        Tasbeeh(duaid=1,dua="Subhan Allah", dua_bn = "সুবহান আল্লাহ", dua_arb = "سُبْحَانَ ٱللَّ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Subhanallah.mp3"),
        Tasbeeh(duaid=2,dua="Alhamdulillah", dua_bn = "আলহামদুলিল্লাহ", dua_arb = "ٱلْحَمْدُ لِلَّهِ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Alhamdulihhah.mp3"),
        Tasbeeh(duaid=3,dua="Bismillah", dua_bn = "বিসমিল্লাহ", dua_arb = "بِسْمِ اللهِ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Bismillah.mp3"),
        Tasbeeh(duaid=4,dua="Allahu Akbar", dua_bn = "আল্লাহু আকবার", dua_arb = "الله أكبر",audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Allahuakbar.mp3"),
        Tasbeeh(duaid=5,dua="Astagfirullah", dua_bn = "আস্তাগফিরুল্লাহ", dua_arb = "أَسْتَغْفِرُ اللّٰهَ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Astagfirullah.mp3"),
        Tasbeeh(duaid=6,dua="Allah", dua_bn = "আল্লাহ", dua_arb = "الله",audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Allahu.mp3"),
        Tasbeeh(duaid=7,dua="La Ilaha illa Allah", dua_bn = "লা ইলাহা ইল্লাল্লাহ", dua_arb = "لَا إِلَٰهَ إِلَّا ٱللَّ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Lailaha_Illalahu.mp3"),
        Tasbeeh(duaid=8,dua="Subhanallahi Wa-Bihamdihi", dua_bn = "সুবহানাল্লাহি ওয়া-বিহামদিহি", dua_arb = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Subhanallahi_Wa_Bihamdihi.mp3")
    )

    private val countCycleList:ArrayList<CountCycle> = arrayListOf(
        CountCycle(countValue = 33, countPos = 1),
        CountCycle(countValue = 34, countPos = 2),
        CountCycle(countValue = 99, countPos = 3),
        CountCycle(countValue = 0, countPos = 0),
    )

    private val navArgs:TasbeehFragmentArgs by navArgs()

    override fun OnCreate() {
        super.OnCreate()

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
        setupActionForOtherFragment(
            action1 = R.drawable.deen_ic_graph,
            action2 = R.drawable.ic_refresh,
            action3 = R.drawable.ic_volume,
            callback = this@TasbeehFragment,
            actionnBartitle = localContext.getString(R.string.digital_tasbeeh),
            backEnable = true,
            view = mainView,
            actionIconColor = R.color.deen_txt_black_deep
        )
        progressLayout = mainView.findViewById(R.id.progressLayout)
        lottieAnim = mainView.findViewById(R.id.lottieAnim)
        duaArabicTxt = mainView.findViewById(R.id.duaArabicTxt)
        duaTxt = mainView.findViewById(R.id.duaTxt)
        playBtn = mainView.findViewById(R.id.playBtn)
        leftBtn = mainView.findViewById(R.id.leftBtn)
        rightBtn = mainView.findViewById(R.id.rightBtn)
        countBtn = mainView.findViewById(R.id.countBtn)
        targetCountTxt = mainView.findViewById(R.id.targetCountTxt)
        countViewLoading = mainView.findViewById(R.id.countViewLoading)
        countTxt = mainView.findViewById(R.id.countTxt)
        countView = mainView.findViewById(R.id.countView)
        btnShare = mainView.findViewById(R.id.btnShare)
        duaLayout = mainView.findViewById(R.id.duaLayout)
        deenBGTasbeehCount = mainView.findViewById(R.id.deenBGTasbeehCount)


        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       /* duaLayout.setOnClickListener {
            selectDhikirDialog()
        }*/

        deenBGTasbeehCount.imageLoad(url = "deen_bg_tasbeeh_count.png".getDrawable())

        btnShare.setOnClickListener {
            val bundle = Bundle()

            if(getLanguage() == "en"){
                bundle.putString("enText",duaTxt.text.toString())
            }else{
                bundle.putString("bnText",duaTxt.text.toString())
            }
            bundle.putString("title",localContext.getString(R.string.digital_tasbeeh))
            bundle.putString("arText",duaArabicTxt.text.toString())
            //bundle.putString("customShareText","পবিত্র কুরআন তিলাওয়াত করুন  https://deenislamic.com/app/quran?id=${surahID-1}")
            bundle.putString("customShareText","")

            gotoFrag(R.id.action_global_shareFragment,bundle)
        }

        if(navArgs.duaid!=-1)
            selectedPos = navArgs.duaid

      if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else
            loadpage()

    }

    private fun loadpage()
    {

        setDuaText()

        countTxt.text = "0".numberLocale()

        //init observer
        initObserver()


        lottieAnim.setOnClickListener {

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

        playBtn.setOnClickListener {

            audioManager.playAudioFromUrl(duaList[selectedPos].audioUrl)
        }

        countBtn.setOnClickListener {
            dialog_select_count_cycle()
        }

        leftBtn.setOnClickListener {
            if(selectedPos==0)
            selectedPos = duaList.size-1
            else
                selectedPos -= 1

            track4Count = 0
            loadAPI(selectedPos)
            setDuaText()
        }

        rightBtn.setOnClickListener {
            if(selectedPos==duaList.size-1)
                selectedPos = 0
            else
                selectedPos++
            track4Count = 0
            loadAPI(selectedPos)
            setDuaText()
        }

        if(!firstload)
        loadAPI(selectedPos)
        firstload = true

    }

    private fun showResetDialog(duaid:Int)
    {

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(),R.style.DeenMaterialAlertDialog_rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_tasbeeh_reset, null, false)

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
                viewmodel.resetTasbeeh(2,duaid,todayDate)
            }
        }

        totalResetBtn?.setOnClickListener{
            /*todayResetBtn?.isEnabled = false
            lifecycleScope.launch {
                viewmodel.resetTasbeeh(1,duaid,todayDate)
            }*/

            tasbeehResetDialog?.dismiss()
        }

    }

    private fun setDuaText()
    {
        duaArabicTxt.text = duaList[selectedPos].dua_arb
        duaTxt.text =  if(getLanguage() == "en")
            duaList[selectedPos].dua
        else
            duaList[selectedPos].dua_bn
    }
    override fun onDestroyView() {
        super.onDestroyView()
        audioManager.releasePlayer()
    }

   /* private fun activeRadioText(view:AppCompatTextView)
    {
        countRadio1Txt.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))
        countRadio2Txt.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))
        countRadio3Txt.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))
        countRadio4Txt.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))
        view.setTextColor(ContextCompat.getColor(requireContext(),R.color.primary))
    }*/

    private fun updateCountView(count:Int)
    {
        Log.e("updateCountView",Gson().toJson(tasbeehdata)+count)
        tasbeehdata?.apply {

            var trackcount = -1

            var targetCount = 33

            when (count) {
                1 -> {
                    trackcount = this.track1
                    targetCount = 33
                }
                2 -> {
                    trackcount = this.track2
                    targetCount = 34
                }
                3 -> {
                    trackcount = this.track3
                    targetCount = 99
                }
                else -> {
                    trackcount = track4Count++
                }
            }

            Log.e("trackcount",trackcount.toString())

            if(trackcount>=targetCount && targetCount!=-1)
            {
                countView.setProgress(100F)
                countTxt.text = trackcount.toString().numberLocale()
                targetCountTxt.text = "/${targetCount}".numberLocale()
                countBtn.text = localContext.getString(R.string.count_input,targetCount.toString().numberLocale())
            }
            else if(trackcount<=targetCount && count!=-1)
            {
                countView.setProgress(
                    if((trackcount.toFloat()*100)/targetCount>100)100F
                    else if((trackcount.toFloat()*100)/targetCount<0)0F
                    else (trackcount.toFloat()*100)/targetCount
                )
                countTxt.text = trackcount.toString().numberLocale()
                targetCountTxt.text = "/${targetCount}".numberLocale()
                countBtn.text = localContext.getString(R.string.count_input,targetCount.toString().numberLocale())
            }
            else {
                countView.setProgress(100F)
                countTxt.text = trackcount.toString().numberLocale()
                targetCountTxt.text = "/∞"
                countBtn.text = "∞"
            }
        }
    }

    private fun initObserver()
    {
        viewmodel.tasbeehLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is TasbeehResource.DuaData -> viewState(it.data)
                is TasbeehResource.userPref -> soundViewState(it.userPref)
                is TasbeehResource.resetDuaData ->
                {
                    context?.toast(getString(R.string.tasbeeh_count_reset_successfully))
                    track4Count = 0
                    todayResetBtn?.isEnabled = true
                    totalResetBtn?.isEnabled = true
                    tasbeehResetDialog?.dismiss()
                    viewState(it.data)
                }

              /*  is TasbeehResource.recentCount ->
                {
                    if(it.data.isNotEmpty()) {
                        //norecentData.hide()
                        //recentCountList.show()
                    }
                    tasbeehRecentCountAdapter.update(it.data)
                }*/
            }
        }
    }

    private fun loadAPI(duaid:Int)
    {
        countViewLoading.show()
        lifecycleScope.launch {
            viewmodel.fetchDuaData(duaid, todayDate)
            viewmodel.fetchUserPref()
            viewmodel.fetchRecentCount()
        }
    }

    private fun soundViewState(userPref: UserPref?)
    {
        this.userPref = userPref

        userPref?.apply {
            if(this.tasbeeh_sound)
                setupActionForOtherFragment(
                    action1 = R.drawable.deen_ic_graph,
                    action2 = R.drawable.ic_refresh,
                    action3 = R.drawable.ic_volume,
                    callback = this@TasbeehFragment,
                    actionnBartitle = localContext.getString(R.string.digital_tasbeeh),
                    backEnable = true,
                    view = requireView(),
                    actionIconColor = R.color.deen_txt_black_deep
                )
            else
                setupActionForOtherFragment(
                    action1 = R.drawable.deen_ic_graph,
                    action2 = R.drawable.ic_refresh,
                    action3 = R.drawable.ic_volume_off,
                    callback = this@TasbeehFragment,
                    actionnBartitle = localContext.getString(R.string.digital_tasbeeh),
                    backEnable = true,
                    view = requireView(),
                    actionIconColor = R.color.deen_txt_black_deep
                )

        }
    }
    private fun viewState(data: Tasbeeh)
    {
        lottieAnim.isEnabled = true
        countViewLoading.hide()

        tasbeehdata = data
        countTxt.text = data.dua_count.toString()
       /* if(todayDate == data.date)
            todaysCountTxt.text = data.daily_count.toString().numberLocale()
        else
            todaysCountTxt.text = "0".numberLocale()

        totalCountTxt.text = data.dua_count.toString().numberLocale()
*/
        updateCountView(selectedCount)

       // tasbeehDuaAdapter.update(selectedPos)
       /* duaListRC.post {
            duaListRC.smoothScrollToPosition(selectedPos)
        }*/

        progressLayout.hide()
    }

    /*override fun selectedDua(duaid: Int) {

        selectedPos = duaid
        track4Count = 0
        loadAPI(duaid)
    }*/

    override fun action1() {
        /*if(!Subscription.isSubscribe){
            gotoFrag(R.id.action_global_subscriptionFragment)
            return
        }*/
      gotoFrag(R.id.action_global_tasbeehHistoryFragment)
    }

    override fun action2() {
        tasbeehdata?.let {
            showResetDialog(it.duaid)
        }
    }

    override fun action3() {
        lifecycleScope.launch {
            viewmodel.updateTasbeehSound()
        }
    }

    private fun dialog_select_count_cycle()
    {
        dialog?.dismiss()
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_tasbeeh_count_cycle, null, false)

        // Initialize and assign variable

        val cycleList = customAlertDialogView.findViewById<RecyclerView>(R.id.cycleList)
        val cancelBtn = customAlertDialogView.findViewById<MaterialButton>(R.id.cancelBtn)
        val setBtn = customAlertDialogView.findViewById<MaterialButton>(R.id.setBtn)

        cycleList.apply {
            tasbeehCountCycleAdapter = TasbeehCountCycleAdapter(countCycleList,selectedCount)
            adapter = tasbeehCountCycleAdapter
        }


        cancelBtn?.setOnClickListener {
            dialog?.dismiss()
        }

        setBtn.setOnClickListener {
            selectedCount = tasbeehCountCycleAdapter.getSelectedCount()
            updateCountView(selectedCount)
            dialog?.dismiss()
        }

        // show dialog

        dialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(true)
            .show()
    }

    private fun selectDhikirDialog(){

            materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(),R.style.DeenMaterialAlertDialog_rounded)
            customAlertDialogView = localInflater.inflate(R.layout.dialog_select_dhikir, null, false)

        val listview:RecyclerView = customAlertDialogView.findViewById(R.id.listview)
        val cancelBtn:MaterialButton = customAlertDialogView.findViewById(R.id.cancelBtn)
        val okBtn:MaterialButton = customAlertDialogView.findViewById(R.id.setBtn)
        val closeBtn:ImageButton = customAlertDialogView.findViewById(R.id.closeBtn)

        listview.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            adapter = SelectDhikirAdapter(duaList)
        }


            if(dialog?.isShowing == true)
                dialog?.dismiss()

            if(customAlertDialogView.parent!=null)
                (customAlertDialogView.getParent() as ViewGroup).removeView(customAlertDialogView)

        dialog = materialAlertDialogBuilder
                .setView(customAlertDialogView)
                .setCancelable(true)
                .show().apply {
                    window?.setGravity(Gravity.BOTTOM)
                }

        okBtn.setOnClickListener {

        }

        closeBtn.setOnClickListener {
            dialog?.dismiss()
        }

        cancelBtn.setOnClickListener{
                dialog?.dismiss()
            }

    }

}