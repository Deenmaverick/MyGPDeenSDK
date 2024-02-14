package com.deenislam.sdk.views.main

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.quran.QuranPlayerCallback
import com.deenislam.sdk.service.libs.media3.QuranPlayer
import com.deenislam.sdk.service.libs.media3.QuranPlayerBroadcast
import com.deenislam.sdk.service.libs.media3.QuranPlayerOffline
import com.deenislam.sdk.service.libs.notification.AlarmReceiver
import com.deenislam.sdk.service.libs.sessiontrack.SessionReceiver
import com.deenislam.sdk.service.network.response.quran.qurangm.ayat.Ayath
import com.deenislam.sdk.service.network.response.quran.qurangm.ayat.Qari
import com.deenislam.sdk.service.network.response.quran.qurangm.surahlist.Data
import com.deenislam.sdk.service.weakref.dashboard.DashboardBillboardPatchClass
import com.deenislam.sdk.service.weakref.dashboard.DashboardPatchClass
import com.deenislam.sdk.service.weakref.main.MainActivityInstance
import com.deenislam.sdk.utils.DraggableView
import com.deenislam.sdk.utils.LocaleUtil
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.reduceDragSensitivity
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.views.adapters.MainViewPagerAdapter
import com.deenislam.sdk.views.adapters.quran.AlQuranAyatAdapter
import com.deenislam.sdk.views.dashboard.DashboardFragment
import com.deenislam.sdk.views.dashboard.patch.Billboard
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale


internal class MainActivityDeenSDK : AppCompatActivity(), QuranPlayerCallback {

    private lateinit var navHostFragment:NavHostFragment
    private lateinit var navController:NavController
    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var actionCallback:actionCallback ? =null
    private var searchCallback:searchCallback ? =null

    lateinit var localContext:Context

    private var trackingID:Long = 0
    private var sessionStartTime:Long = System.currentTimeMillis()/1000

    private val _viewPager: ViewPager2 by lazy { findViewById(R.id.viewPager) }
    private val frameContainerView: FragmentContainerView by lazy { findViewById(R.id.fragmentContainerView) }


    private val searchbar:ConstraintLayout by lazy { findViewById(R.id.searchbar) }
    private val searchBackBtn:AppCompatImageView by lazy { searchbar.findViewById(R.id.btnBack) }
    private val searchInput: TextInputEditText by lazy { searchbar.findViewById(R.id.search_input) }


    private val actionbar:ConstraintLayout by lazy { findViewById(R.id.actionbar) }
    private val action1Btn:AppCompatImageView by lazy { actionbar.findViewById(R.id.action1) }
    private val action2Btn:AppCompatImageView by lazy { actionbar.findViewById(R.id.action2) }
    private val btnBack:AppCompatImageView by lazy { actionbar.findViewById(R.id.btnBack) }
    private val title:AppCompatTextView by lazy { actionbar.findViewById(R.id.title) }

    var bottomNavClicked:Boolean = false
    var childFragmentAnimForward:Boolean = false
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private var isDisableBackPress = false

    private var isQuranPlayerBound = false
    private var isQuranOfflinePlayerBound = false

    // Quran player basic data
    private var quranQueuedData: ArrayList<Ayath> = arrayListOf()
    private var quranQueuedPos: Int = -1
    private var quranQueuedSurahList: ArrayList<Data> = arrayListOf()
    private var quranQueuedJuzList:ArrayList<com.deenislam.sdk.service.network.response.quran.qurangm.paralist.Data> = arrayListOf()
    private var quranQueuedSurahID: Int = -1
    private var quranQueuedQarisData: ArrayList<Qari> = arrayListOf()
    private var quranQueuedTotalVerseCount: Int = -1
    private var quranQueuedPageNo: Int = -1
    private var quranQueuedSelectedQari: Int = 931
    private var quranQueuedIsSurahMode: Boolean = true
    private var isQuranPlayInQueue = false
    private var quranPlayerAdapterCallback: AlQuranAyatAdapter.ViewHolder ? = null
    private var isQuranPlayerWasRunning = false

    // offline
    private var quranSurahQueuedData: Data? = null

    // global mini player
    private lateinit var mini_player: DraggableView
    private lateinit var surahTitile:AppCompatTextView
    private lateinit var surahAyat:AppCompatTextView
    private lateinit var ic_prev:AppCompatImageView
    private lateinit var ic_play_pause:AppCompatImageView
    private lateinit var ic_next:AppCompatImageView
    private lateinit var ic_close:AppCompatImageView
    private lateinit var playerProgress: LinearProgressIndicator
    private lateinit var playLoading: CircularProgressIndicator
    private var countDownTimer: CountDownTimer?=null
    private var currentSurahDetails: Data? = null


    companion object
    {
        var instance: MainActivityDeenSDK? = null
    }


    val quranOnlineserviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as QuranPlayer.LocalBinder
            MainActivityInstance.updateQuranPlayer(binder.getService())
            isQuranPlayerBound = true
            if(isQuranPlayInQueue)
            {

                isQuranPlayInQueue = false

                MainActivityInstance.getQuranPlayerInstance()?.playQuran(
                    data = quranQueuedData,
                    pos = quranQueuedPos,
                    surahList = quranQueuedSurahList,
                    surahID = quranQueuedSurahID,
                    qarisData = quranQueuedQarisData,
                    totalVerseCount = quranQueuedTotalVerseCount,
                    pageNo = quranQueuedPageNo,
                    selectedQari = quranQueuedSelectedQari,
                    isSurahMode = quranQueuedIsSurahMode,
                    quranJuzList = quranQueuedJuzList
                )

                quranPlayerAdapterCallback?.let { setAdapterCallbackQuranPlayer(it) }
            }

            MainActivityInstance.getQuranPlayerInstance()?.setGlobalMiniPlayerCallback(this@MainActivityDeenSDK)
            MainActivityInstance.getQuranPlayerInstance()?.initGlobalMiniPlayer()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isQuranPlayerBound = false
            mini_player.hide()
        }
    }


    val quranOfflineserviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as QuranPlayerOffline.LocalBinder
            MainActivityInstance.updateQuranPlayerOffline(binder.getService())
            isQuranOfflinePlayerBound = true
            if(isQuranPlayInQueue)
            {
                isQuranPlayInQueue = false

                quranSurahQueuedData?.let {
                    MainActivityInstance.getQuranPlayerOfflineInstance()?.playQuran(
                        data = it,
                    )
                }
            }

            MainActivityInstance.getQuranPlayerOfflineInstance()?.setGlobalMiniPlayerCallback(this@MainActivityDeenSDK)
            MainActivityInstance.getQuranPlayerOfflineInstance()?.initGlobalMiniPlayer()

            (mini_player.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = 0
            mini_player.show()
            ic_prev.hide()
            ic_next.hide()
            mini_player.post {
                frameContainerView.setPadding(0,0,0,mini_player.height)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isQuranOfflinePlayerBound = false
            mini_player.hide()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setContentView(R.layout.activity_main_deen)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        frameContainerView.visible(true)
        changeLanguage()
        //bottom_navigation.setupWithNavController(navController)
        searchInput.hint = localContext.getString(R.string.search)
        // test notification
        createChannel("Prayer Time","Prayer Time","Prayer Alert")
        //Log.e("CUR_TIME_NOTIFY",Calendar.getInstance().timeInMillis.toString())
      /*  setNotification(  SystemClock.elapsedRealtime()+600,1)
        setNotification(SystemClock.elapsedRealtime()+1200,2)
*/

        //action button click
        action1Btn.setOnClickListener {
            actionCallback?.action1()
        }

        action2Btn.setOnClickListener {
            actionCallback?.action2()
        }

        searchBackBtn.setOnClickListener {
            searchCallback?.searchBack()
        }


        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCallback?.searchSubmit(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchInput.setOnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (searchInput.right - searchInput.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    // Right drawable clicked
                    // Add your desired action here
                    searchCallback?.searchSubmit(searchInput.text.toString())
                    return@setOnTouchListener true
                }
            }
            false
        }

        intent.getIntExtra("destination",0).let {
            if(it>0)
                stackNavigation(destination = it,intent)
        }

        intent.getStringExtra("logout").let {

            if(it == "ok") {
                logout()
            }
        }

        setupBackPressCallback()

    }

    override fun onResume() {
        super.onResume()
        if(!isDisableBackPress) {
            setupBackPressCallback()
        }

        sessionStartTime = System.currentTimeMillis()/1000

    }

    fun closeDeenSDK()
    {
        finish()
    }
 /*   override fun onBackPressed() {
        if(this::onBackPressedCallback.isInitialized)
        onBackPressedCallback.handleOnBackPressed()
    }*/


    private fun setupBackPressCallback()
    {
        onBackPressedCallback =
            this.onBackPressedDispatcher.addCallback {
                Log.e("setupBackPressCallback",navController.previousBackStackEntry?.destination?.id.toString())
                // Handle the back button event
                if (navController.previousBackStackEntry?.destination?.id?.equals(
                        navController.graph.startDestination
                    ) != true &&
                    navController.previousBackStackEntry?.destination?.id?.equals(
                        R.id.blankFragment
                    ) != true && navController.previousBackStackEntry?.destination?.id != null)
                    navController.popBackStack()
                /*else
                    navController.popBackStack()*/

            }
        onBackPressedCallback.isEnabled = true

    }

    fun playOfflineQuran(
        data: Data
    ) {


        if(isQuranPlayerBound){
            MainActivityInstance.getQuranPlayerInstance()?.stopQuranPlayer()
        }

        if (isQuranOfflinePlayerBound && MainActivityInstance.getQuranPlayerOfflineInstance() != null) {
            // Call methods on the audioService as needed
            MainActivityInstance.getQuranPlayerOfflineInstance()?.playQuran(
                data = data
            )

            (mini_player.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = 0
            mini_player.show()
            ic_prev.hide()
            ic_next.hide()
            mini_player.post {
                frameContainerView.setPadding(0,0,0,mini_player.height)
            }
        }
        else
        {
            quranSurahQueuedData = data
            isQuranPlayInQueue = true
            startQuranPlayerOfflineService()
        }
    }

    private fun startQuranPlayerOfflineService()
    {
        // Start and bind to the quran player service
        val intent = Intent(this, QuranPlayerOffline::class.java)
        startService(intent)
        bindService(intent, quranOfflineserviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun isQuranMiniPlayerRunning(): Boolean {
        return if(QuranPlayer.isServiceRunning)
            true
        else QuranPlayerOffline.isServiceRunning
    }

    fun stopOfflineQuran(surahId: Int) {

        if(surahId == 0)
            return

        if(MainActivityInstance.getQuranPlayerOfflineInstance()?.getCurrentSurahID() == surahId)
            MainActivityInstance.getQuranPlayerOfflineInstance()?.stopQuranPlayer()
    }

    fun playQuran(
        data: ArrayList<Ayath>,
        pos: Int,
        surahList: ArrayList<Data>,
        surahID: Int,
        qarisData: ArrayList<Qari>,
        totalVerseCount: Int,
        pageNo: Int,
        selectedQari: Int,
        isSurahMode: Boolean,
        quranJuzList: ArrayList<com.deenislam.sdk.service.network.response.quran.qurangm.paralist.Data>?
    ) {

        if(isQuranOfflinePlayerBound){
            MainActivityInstance.getQuranPlayerOfflineInstance()?.stopQuranPlayer()
        }

        if (isQuranPlayerBound && MainActivityInstance.getQuranPlayerInstance() != null) {
            // Call methods on the audioService as needed
            MainActivityInstance.getQuranPlayerInstance()?.playQuran(
                data = data,
                pos = pos,
                surahList = surahList,
                surahID = surahID,
                qarisData = qarisData,
                totalVerseCount = totalVerseCount,
                pageNo = pageNo,
                selectedQari = selectedQari,
                isSurahMode = isSurahMode,
                quranJuzList = quranJuzList
            )
        }
        else
        {
            quranQueuedData = data
            quranQueuedPos = pos
            quranQueuedSurahList = surahList
            quranQueuedJuzList = quranJuzList?: arrayListOf()
            quranQueuedSurahID = surahID
            quranQueuedQarisData = qarisData
            quranQueuedTotalVerseCount = totalVerseCount
            quranQueuedPageNo = pageNo
            quranQueuedIsSurahMode = isSurahMode
            quranQueuedSelectedQari = selectedQari
            isQuranPlayInQueue = true
            startQuranPlayerService()
        }
    }

    private fun startQuranPlayerService()
    {
        // Start and bind to the quran player service
        val intent = Intent(this, QuranPlayer::class.java)
        startService(intent)
        bindService(intent, quranOnlineserviceConnection, Context.BIND_AUTO_CREATE)
    }


    fun updateQuranPlayer(qari: Int?=null) {
        if (isQuranPlayerBound && MainActivityInstance.getQuranPlayerInstance() != null) {
            MainActivityInstance.getQuranPlayerInstance()?.updateQuranPlayer(qari)
        }
    }

    fun getCurrentSurahID(): Int {
        return if (isQuranPlayerBound && MainActivityInstance.getQuranPlayerInstance() != null) {
            // Call methods on the audioService as needed
            MainActivityInstance.getQuranPlayerInstance()?.getCurrentSurahID()?:0
        } else
            0
    }

    fun pauseQuran()
    {
        val intent = Intent(this, QuranPlayerBroadcast::class.java)
        intent.action = "pause_action"
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        pendingIntent.send()
    }

    fun setAdapterCallbackQuranPlayer(viewHolder: AlQuranAyatAdapter.ViewHolder)
    {
        if (isQuranPlayerBound && MainActivityInstance.getQuranPlayerInstance() != null) {
            // Call methods on the audioService as needed
            MainActivityInstance.getQuranPlayerInstance()?.updateAdapterCallback(viewHolder)
        }
        else{
            quranPlayerAdapterCallback = viewHolder
        }
    }


    override fun onPause() {
        sendSessionToServer()

        if(this::onBackPressedCallback.isInitialized) {
            onBackPressedCallback.isEnabled = false
            onBackPressedCallback.remove()
        }

        super.onPause()
    }
    override fun onDestroy() {

        if(this::onBackPressedCallback.isInitialized) {
            onBackPressedCallback.isEnabled = false
            onBackPressedCallback.remove()
        }

        DashboardPatchClass.clearReferences()
        DashboardBillboardPatchClass.clearReferences()
        super.onDestroy()

    }

    private fun sendSessionToServer()
    {
      /*  val service = Intent(this, SessionReceiverService::class.java)
        service.putExtra("msisdn",DeenSDKCore.GetDeenMsisdn())
        service.putExtra("sessionStart",sessionStartTime)
        service.putExtra("sessionEnd",System.currentTimeMillis()/1000)
        this.startService(service)
*/
        Log.e("SDKDEEN_DES","OK")

        val notifyIntent = Intent(DeenSDKCore.appContext, SessionReceiver::class.java)
        notifyIntent.putExtra("msisdn",DeenSDKCore.GetDeenMsisdn())
        notifyIntent.putExtra("sessionStart",sessionStartTime)
        notifyIntent.putExtra("sessionEnd",System.currentTimeMillis()/1000)

        val notifyPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                DeenSDKCore.appContext,
                200,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

        val alarmManager = DeenSDKCore.appContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 1000,
            notifyPendingIntent
        )


    }

    fun changeLanguage()
    {
        localContext = LocaleUtil.createLocaleContext(this, Locale("bn"))
    /*if (DeenSDKCore.GetDeenLanguage() == "en") {
            LocaleUtil.createLocaleContext(this, Locale("en"))
        } else {
            LocaleUtil.createLocaleContext(this, Locale("bn"))
        }*/
    }

    fun disableBackPress()
    {
        isDisableBackPress = true
        if(this::onBackPressedCallback.isInitialized)
            onBackPressedCallback.isEnabled = false
    }

    fun enableBackPress()
    {
        isDisableBackPress = false
        Log.e("enableBackPress",this::onBackPressedCallback.isInitialized.toString())
        setupBackPressCallback()
    }

    fun getTrackingID() = trackingID
    fun setTrackingID(id:Long)
    {
        trackingID =id
    }

   /* override fun attachBaseContext(newBase: Context) {
        // replace "bn" with the desired locale
        val locale = Locale(getLanguage())
        val context: Context = ContextWrapper.wrap(newBase, locale)
        super.attachBaseContext(context)
    }*/


   /* fun getInstance():MainActivityDeenSDK
    {
        if (instance == null)
            instance = this

        return instance as MainActivityDeenSDK
    }*/

    private fun createChannel(channelId: String, channelName: String,description:String) {
        //create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // change importance of notication
                NotificationManager.IMPORTANCE_HIGH
            )//disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = description

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    private fun setNotification(time:Long,reqcode:Int)
    {
        val notifyIntent = Intent(this, AlarmReceiver::class.java)
        notifyIntent.putExtra("pid",reqcode)

        val notifyPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                this,
                reqcode,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            time,
            notifyPendingIntent
        )
    }

    fun isDashboardVisible() = _viewPager.isVisible

     fun dashboardComponent(bol:Boolean)
    {
        _viewPager.visible(bol)
        //bottom_navigation.visible(bol)
        actionbar.visible(bol)
        if (bol) {
            setupActionbar(localContext.resources.getString(R.string.app_name))
        }

        frameContainerView.post {
            frameContainerView.visible(!bol)
        }
    }

    fun setupOtherFragment(bol:Boolean)
    {
        _viewPager.visible(!bol)
        frameContainerView.visible(bol)
        actionbar.visible(!bol)
       // bottom_navigation.visible(!bol)

        Log.e("setupOtherFragment",bol.toString())
    }

     fun logout()
    {
       /* lifecycleScope.launch(Dispatchers.Main)
        {
            instance?.toast("Session expired! Please login again.")
            clearAllInstance()
            dashboardComponent(false)
            stackNavigation(destination = R.id.loginFragment)
        }*/

    }


    fun setViewPager(page:Int)
    {
        //instance?.bottomNavClicked = true
        childFragmentAnimForward = _viewPager.currentItem<page
        _viewPager.setCurrentItem(page,false)
    }

    //dashboard
    fun setupActionbar(titile:String,isBackVisible:Boolean = true,dashboard:Boolean)
    {
        title.text = titile

        if(!isBackVisible)
        btnBack.visible(isBackVisible)

        btnBack.setOnClickListener {
           onBackPressedDispatcher.onBackPressed()
        }
    }

    fun setupActionbar(titile:String,backEnable:Boolean=false)
    {
        if(titile.isEmpty() || titile == localContext.resources.getString(R.string.app_name) && !backEnable) {
            title.text = localContext.resources.getString(R.string.app_name)
            btnBack.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bl_islamic_icon))
            btnBack.visible(true)

            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
        else if(backEnable)
        {
            btnBack.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_keyboard_backspace_24))
            btnBack.visible(true)
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            title.text = titile
            title.setTextColor(ContextCompat.getColor(title.context,R.color.deen_txt_black_deep))

        }
        else
        {

            (title.layoutParams as ConstraintLayout.LayoutParams).apply {
                leftMargin=16.dp
            }

            title.text = titile
            btnBack.visible(false)

        }
    }


    fun setupActionbar(titile:String,view: View,isBackVisible:Boolean = true)
    {
        val btnBack:ImageButton = view.findViewById(R.id.btnBack)
        val title:AppCompatTextView = view.findViewById(R.id.title)

        title.text = titile

        if(!isBackVisible)
            (title.layoutParams as ConstraintLayout.LayoutParams).apply {
                marginStart = 16.dp
            }

        btnBack.visible(isBackVisible)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    fun setupActionbar(action1:Int,action2:Int,callback: actionCallback?=null)
    {
        if(action1>0) {
            action1Btn.setImageDrawable(AppCompatResources.getDrawable(this, action1))
            action1Btn.visible(true)
        }
        else
            action1Btn.visible(false)

        if(action2>0) {
            action2Btn.setImageDrawable(AppCompatResources.getDrawable(this, action2))
            action2Btn.visible(true)
        }
        else
            action2Btn.visible(false)

        actionCallback = callback
    }

    fun setupSearchbar(callback: searchCallback?)
    {
        searchCallback = callback
        actionbar.hide()
        searchbar.show()
    }

    fun hideSearch()
    {
        searchCallback = null
        //actionbar.show()
        searchbar.hide()
    }

    fun setupActionbar(action1:Int,action2:Int)
    {
        if(action1>0) {
            action1Btn.setImageDrawable(AppCompatResources.getDrawable(this, action1))
            action1Btn.visible(true)
        }
        else
            action1Btn.visible(false)

        if(action2>0) {
            action2Btn.setImageDrawable(AppCompatResources.getDrawable(this, action2))
            action2Btn.visible(true)
        }
        else
            action2Btn.visible(false)

    }

    fun showBottomNav(bol:Boolean)
    {
       // bottom_navigation.visible(bol)
    }

    private fun navigateDestination(destination:Int)
    {
        navController.navigate(destination)
    }

    private fun stackNavigation(destination:Int,intent: Intent)
    {
        //showBottomNav(destination.isBottomNavFragment)
        when(destination)
        {
            R.id.dashboardFakeFragment ->
            {

                navController.navigate(destination,intent.extras)

            }
            else ->
            {
                navController.navigate(R.id.dashboardFakeFragment)
                navController.navigate(destination)

               /* lifecycleScope.launch {

                    withContext(Dispatchers.Main)
                    {

                        navController.navigate(destination)
                        val dashboard =  async {
                           // initDashboard()
                            //setupOtherFragment(true)
                        }
                        dashboard.await()
                        //setupOtherFragment(true)
                    }

                }*/

            }
        }

    }
}

internal interface actionCallback
{
    fun action1()
    fun action2()
}
internal interface searchCallback
{
    fun searchBack()
    fun searchSubmit(query: String)
}