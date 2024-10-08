package com.deenislamic.sdk.views.quran

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.util.Log
import android.view.Gravity
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislamic.sdk.R
import com.deenislamic.sdk.databinding.FragmentAlQuranBinding
import com.deenislamic.sdk.service.callback.AlQuranAyatCallback
import com.deenislamic.sdk.service.callback.common.DownloaderCallback
import com.deenislamic.sdk.service.di.DatabaseProvider
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.alertdialog.CustomAlertDialog
import com.deenislamic.sdk.service.libs.alertdialog.CustomDialogCallback
import com.deenislamic.sdk.service.libs.downloader.QuranDownloadService
import com.deenislamic.sdk.service.libs.media3.AudioManager
import com.deenislamic.sdk.service.libs.media3.AudioPlayerCallback
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.common.OptionList
import com.deenislamic.sdk.service.models.quran.AlQuranResource
import com.deenislamic.sdk.service.models.quran.AlQuranSettingResource
import com.deenislamic.sdk.service.models.quran.quranplayer.FontList
import com.deenislamic.sdk.service.models.quran.quranplayer.PlayerCommonSelectionData
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Ayath
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Qari
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.TafsirList
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Translator
import com.deenislamic.sdk.service.network.response.quran.qurangm.surahlist.Data
import com.deenislamic.sdk.service.repository.quran.AlQuranRepository
import com.deenislamic.sdk.service.repository.quran.quranplayer.PlayerControlRepository
import com.deenislamic.sdk.utils.AlQuranSetting_bn_translator
import com.deenislamic.sdk.utils.AlQuranSetting_en_translator
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.DEFAULT_ARABIC_FONT_ID
import com.deenislamic.sdk.utils.DEFAULT_BN_TAFSIR_ID
import com.deenislamic.sdk.utils.DEFAULT_BN_TRANSLATOR_ID
import com.deenislamic.sdk.utils.DEFAULT_EN_TRANSLATOR_ID
import com.deenislamic.sdk.utils.DEFAULT_QARI_ID
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.copyToClipboard
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.invisible
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.reduceDragSensitivity
import com.deenislamic.sdk.utils.setActiveState
import com.deenislamic.sdk.utils.setInactiveState
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.utils.transformArabicFontData
import com.deenislamic.sdk.utils.transformPlayerReciterData
import com.deenislamic.sdk.utils.transformPlayerTafsirData
import com.deenislamic.sdk.utils.transformPlayerTranslatorData
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.common.PlayerControlVMFactory
import com.deenislamic.sdk.viewmodels.quran.AlQuranViewModel
import com.deenislamic.sdk.viewmodels.quran.quranplayer.PlayerControlViewModel
import com.deenislamic.sdk.views.adapters.MainViewPagerAdapter
import com.deenislamic.sdk.views.adapters.quran.AlQuranAyatAdapter
import com.deenislamic.sdk.views.adapters.quran.Quran3DotAdapter
import com.deenislamic.sdk.views.adapters.quran.SelectSurahAdapter
import com.deenislamic.sdk.views.adapters.quran.SelectSurahCallback
import com.deenislamic.sdk.views.adapters.quran.quranplayer.PlayerCommonSelectionList
import com.deenislamic.sdk.views.base.BaseFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import com.deenislamic.sdk.views.quran.quranplayer.PlayerAudioFragment
import com.deenislamic.sdk.views.quran.quranplayer.PlayerThemeFragment
import com.deenislamic.sdk.views.quran.quranplayer.PlayerTranslationFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Integer.max
import java.lang.Math.min
import java.util.Locale
import java.util.concurrent.TimeUnit


internal class AlQuranFragment : BaseFragment<FragmentAlQuranBinding>(FragmentAlQuranBinding::inflate),
    AudioPlayerCallback,
    SelectSurahCallback,
    AlQuranAyatCallback,
    otherFagmentActionCallback,
    PlayerCommonSelectionList.PlayerCommonSelectionListCallback,
    DownloaderCallback,
    CustomDialogCallback
{

    private lateinit var alQuranAyatAdapter: AlQuranAyatAdapter

    private lateinit var alQuranViewModel:AlQuranViewModel
    private lateinit var playerControlViewModel: PlayerControlViewModel

    private val surahDetailsData :ArrayList<Ayath> = arrayListOf()

    // nav args
    private val args:AlQuranFragmentArgs by navArgs()
    private var surahListData: Data? =null
    private var surahList:ArrayList<Data> = arrayListOf()
    private var qarisData: ArrayList<Qari> = arrayListOf()
    private var translatorDataData: ArrayList<Translator> = arrayListOf()
    private var tasfsirList: ArrayList<TafsirList> = arrayListOf()
    private var quranJuz:com.deenislamic.sdk.service.network.response.quran.qurangm.paralist.Data ? =null
    private var quranJuzList:ArrayList<com.deenislamic.sdk.service.network.response.quran.qurangm.paralist.Data> ? =null

    private var dialog: Dialog? = null
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : View
    private lateinit var selectSurahAdapter: SelectSurahAdapter
    private lateinit var playerCommonSelectionList: PlayerCommonSelectionList
    private lateinit var enPlayerCommonSelectionList: PlayerCommonSelectionList
    private lateinit var bnPlayerCommonSelectionList: PlayerCommonSelectionList
    private lateinit var tafsirCommonSelectionList: PlayerCommonSelectionList
    private lateinit var arabicFontCommonSelectionList: PlayerCommonSelectionList
    private var countDownTimer: CountDownTimer?=null
    private lateinit var bottomSheetBehavior:BottomSheetBehavior<View>

    private var isInitialSettingApply = false
    private var isHeaderVisible = true
    private var previousScrollY = 0
    private var isScrollAtEnd = false
    private var pageNo:Int = 1
    private var pageItemCount:Int = 10
    private var isNextEnabled  = false
    private var isReadingMode:Boolean = false
    private var isBnReading = false
    private var isApiLoaded = false

    // player control
    private lateinit var playerControlPages: ArrayList<Fragment>
    private lateinit var playerControlViewPageAdapter: MainViewPagerAdapter
    private var playerControlPagePos:Int = 0
    private var auto_scroll = true

    //page setting
    private var pageTitle = ""
    private var totalVerseCount = 0
    private var isFav = false
    private var isSurahMode:Boolean = true
    private var isMiniPlayerAlreadySet = false
    private var isNextPrevSurahCalled = false
    private var theme_font_size:Float = 0F

    // portable zoom opacity
    private val handler = Handler(Looper.getMainLooper())
    private var scaleFactor = 1.0f
    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var isScrolling = true

    // Runnable to change opacity to 70%
    private val delayedAlphaChangeRunnable = Runnable {
        binding.portableZoomLayout.alpha = 0.7f
    }

    // Default value
    private var tafsirMaker = DEFAULT_BN_TAFSIR_ID

    // tafsir dialog view
    private var tafsirDialogNoInternet:NestedScrollView ? = null
    private var tafsirDialogProgressLayout: LinearLayout? = null
    private var tafsirDialogArabicAyat:AppCompatTextView ? = null
    private var tafsirDialogMakerTxt:AppCompatTextView ? = null
    private var tafsirDialogContent:WebView ? = null
    private var tafsirDialogRetryBtn:MaterialButton ? = null
    private var tafsirDialogNoData:NestedScrollView ? = null

    private var firstload = false

    private var surahID = 0
    private var juzID = 0

    private var surahName:String ? = null
    private var isDownloading = false
    private var downloadFilenameByUser = ""

    private var customAlertDialog: CustomAlertDialog = CustomAlertDialog().getInstance()

    private val fontListData:ArrayList<FontList> = arrayListOf()


    override fun OnCreate() {
        super.OnCreate()


        val repository = PlayerControlRepository(
            playerSettingDao = DatabaseProvider().getInstance().providePlayerSettingDao()
        )

        val factory = PlayerControlVMFactory(repository)
        playerControlViewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[PlayerControlViewModel::class.java]

        val alQuranRepository = AlQuranRepository(
            deenService = NetworkProvider().getInstance().provideDeenService(),
            quranService = null
        )

        alQuranViewModel = AlQuranViewModel(alQuranRepository)


        scaleGestureDetector = ScaleGestureDetector(requireContext(), ScaleListener())

        surahName = args.surahName
        surahID = args.surahID
        surahListData = args.surah
        quranJuz = args.juz
        quranJuz?.let { juzID = it.JuzId }
        quranJuzList = args.juzList?.toCollection(ArrayList())
        pageTitle = surahName?:
                localContext.resources.getString(R.string.quran_para_adapter_title,quranJuz?.JuzId.toString().numberLocale())


        totalVerseCount = surahListData?.TotalAyat?.toInt()?:quranJuz?.TotalAyat?:0

        Log.e("ALQsurahID",surahID.toString())
        if(surahID > 0)
            isSurahMode = true
        else if(quranJuz!=null)
            isSurahMode = false

    }


    override fun ON_CREATE_VIEW(root: View) {
        super.ON_CREATE_VIEW(root)
        setupActionForOtherFragment(
            action1 = R.drawable.ic_settings,
            action2 = R.drawable.ic_reading_mode,
            action3 = R.drawable.ic_download_quran,
            callback = this@AlQuranFragment,
            actionnBartitle = pageTitle,
            backEnable = true,
            actionIconColor = R.color.deen_txt_black_deep,
            view = root
        )

        setCustomPageTitle(pageTitle)

        binding.bottomPlayer.largePlayer.icDownload.hide()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        CallBackProvider.setFragment(this)
        binding.cautionBtn.hide()
        binding.actionbar.action3Progress.isIndeterminate = false
        binding.bottomPlayer.largePlayer.action3Progress.isIndeterminate = false
        if(!isSurahMode)
        {
            binding.bismillahLyOld.show()
            binding.bismillahLy.hide()
            binding.bottomPlayer.largePlayer.icFav.hide()
            binding.bottomPlayer.largePlayer.icDownload.hide()
        }
        else
        {
            binding.bismillahLy.show()
            binding.bismillahLyOld.hide()
        }

        binding.banglaRecBtn.hide()
        binding.arabicRecBtn.hide()


        setupBackPressCallback(this)
        // set header data
        setHeaderData()

        if(!firstload)
        binding.bottomPlayerContainer.hide()

        //binding.actionbar.pageTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(requireContext(),R.drawable.ic_dropdown), null)

        binding.actionbar.title.setOnClickListener {
            dialog_select_surah()
        }

        binding.bottomPlayer.largePlayer.themeBtn.setOnClickListener {
            binding.bottomPlayer.playerViewPager.currentItem = 0
        }

        binding.bottomPlayer.largePlayer.audioBtn.setOnClickListener {
            binding.bottomPlayer.playerViewPager.currentItem = 1
        }

        binding.bottomPlayer.largePlayer.transalationBtn.setOnClickListener {
            binding.bottomPlayer.playerViewPager.currentItem = 2
        }

        binding.bottomPlayer.playerViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                playerControlPagePos = position

                clearPlayerControlBtn()

                when (position) {
                    0 -> {
                        binding.bottomPlayer.largePlayer.themeBtn.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.deen_primary
                            )
                        )
                        binding.bottomPlayer.largePlayer.themeBtn.backgroundTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.deen_white
                                )
                            )
                    }

                    1 -> {
                        binding.bottomPlayer.largePlayer.audioBtn.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.deen_primary
                            )
                        )
                        binding.bottomPlayer.largePlayer.audioBtn.backgroundTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.deen_white
                                )
                            )

                    }

                    2 -> {
                        binding.bottomPlayer.largePlayer.transalationBtn.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.deen_primary
                            )
                        )
                        binding.bottomPlayer.largePlayer.transalationBtn.backgroundTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.deen_white
                                )
                            )

                    }

                }

            }
        })


        ViewCompat.setTranslationZ(binding.progressLayout.root, 10F)
        ViewCompat.setTranslationZ(binding.noInternetLayout.root, 10F)
        ViewCompat.setTranslationZ(binding.nodataLayout.root, 10F)
        ViewCompat.setTranslationZ(binding.dimView, 15F)
        ViewCompat.setTranslationZ(binding.bottomPlayerContainer, 20F)

        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        if(fontListData.isEmpty()) {
            fontListData.add(
                FontList(
                    fontname = localContext.getString(R.string.indopak),
                    fontid = "1"
                )
            )
         /*   fontListData.add(
                FontList(
                    fontname = localContext.getString(R.string.uthmanic_script_hafs_regular),
                    fontid = "2"
                )
            )
            fontListData.add(
                FontList(
                    fontname = localContext.getString(R.string.al_majeed),
                    fontid = "3"
                )
            )*/
        }

        AlQuranSettings.setupDrawer(binding.navdrawer,lifecycleScope,playerControlViewModel)

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // Called when the drawer's position changes due to animation
            }

            override fun onDrawerOpened(drawerView: View) {
                // Called when the drawer is fully opened
            }

            override fun onDrawerClosed(drawerView: View) {
                // Called when the drawer is fully closed
                // Put your code here to handle drawer closed event
                if(AlQuranSettings.isUpdatePending) {
                    //updateAyatAdapterVisibleItems()
                    AlQuranSettings.isUpdatePending = false
                }
            }

            override fun onDrawerStateChanged(newState: Int) {
                // Called when the drawer's state changes (idle, dragging, settling)
            }
        })

        //click retry button for get api data again
        binding.noInternetLayout.noInternetRetry.setOnClickListener {
            loadingState()
            loadApiData(pageNo, pageItemCount)
        }

        binding.navdrawer.btnDrawerClose.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        }

        // search back button


        alQuranAyatAdapter = AlQuranAyatAdapter(this@AlQuranFragment)


        binding.ayatList.apply {
            adapter = alQuranAyatAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
            setItemViewCacheSize(10)

            post {
                binding.container.setPadding(
                    binding.container.paddingLeft,
                    binding.headerLayout.height +
                            binding.actionbar.root.height  - 20,
                    binding.container.paddingRight,
                    binding.container.paddingBottom
                )
            }
        }




        binding.container.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                if (scrollY < binding.headerLayout.height + 100 && !isHeaderVisible)
                    binding.headerLayout.translationY = -scrollY.toFloat()

                if (scrollY > oldScrollY && isHeaderVisible && scrollY > binding.headerLayout.height + 100) {
                    binding.headerLayout.animate()
                        .withEndAction {
                            binding.headerLayout.hide()
                        }
                        .translationY(-binding.headerLayout.height.toFloat()).duration = 200
                    isHeaderVisible = false
                } else if (scrollY < oldScrollY && !isHeaderVisible) {
                    binding.headerLayout.show()
                    binding.headerLayout.animate().translationY(0f).duration = 200
                    isHeaderVisible = true
                }

                previousScrollY = scrollY



            })



        binding.container.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = binding.container.scrollY
            val totalContentHeight = binding.container.getChildAt(0)
                ?.let { it.measuredHeight - binding.container.height }

            if (scrollY >= (totalContentHeight ?: 0) && !isScrollAtEnd) {
                isScrollAtEnd = true
                // NestedScrollView has scrolled to the end
                if (isNextEnabled && alQuranAyatAdapter.itemCount > 0) {
                    //binding.container.setScrollingEnabled(false)
                    morePageBottomLoading(true)
                    fetchNextPageData()
                    Log.e("ALQURAN_SCROLL", "END")

                } else
                    morePageBottomLoading(false)
                // Implement your desired actions here
            }

        }

        // bottom sheet music player
        var previousSlideOffset = 0f
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomPlayer.root)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        binding.dimView.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.bottomPlayer.miniPlayer.root.setOnClickListener {
            binding.bottomPlayer.miniPlayer.root.hide()
            bottomSheetBehavior.state = STATE_EXPANDED
            //alQuranAyatAdapter.miniPlayerCall(true)
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                   /*  if (newState == STATE_EXPANDED) {
                    // The bottom sheet is being expanded
                    binding.dimView.show()
                }
                else
                    binding.dimView.hide()*/

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Not used in this example
                Log.e("bottomSheetBehavior", slideOffset.toString())
                if (slideOffset < previousSlideOffset && slideOffset == 0F) {
                    // Bottom sheet is being dragged up
                    // Perform your desired actions here
                    binding.dimView.hide()
                    binding.bottomPlayer.miniPlayer.root.show()
                } else if (slideOffset > previousSlideOffset) {
                    // Bottom sheet is being dragged down
                    // Perform your desired actions here
                    binding.dimView.show()
                    binding.bottomPlayer.miniPlayer.root.hide()
                    //alQuranAyatAdapter.miniPlayerCall(true)
                } else if (slideOffset == 0f) {
                    // Bottom sheet is released
                    // Perform your desired actions here

                }
                previousSlideOffset = slideOffset
            }
        })



        binding.btnZoomIn.setOnClickListener {
            // Change the opacity to 1 (fully opaque)
            binding.portableZoomLayout.alpha = 1.0f

            // Remove any previous callbacks to prevent multiple delayed changes
            handler.removeCallbacks(delayedAlphaChangeRunnable)

            // Adjust the font size
            theme_font_size += 20F
            if (theme_font_size > 100F) {
                theme_font_size = 100F
            }

            // Update the font size
            lifecycleScope.launch {
                playerControlViewModel.updatePortableZoom(theme_font_size)
            }

            // Schedule the delayed change to 50% opacity after 3 seconds
            handler.postDelayed(
                delayedAlphaChangeRunnable,
                2000 // 3 seconds in milliseconds
            )
        }

        binding.btnZoomOut.setOnClickListener {
            // Change the opacity to 1 (fully opaque)
            binding.portableZoomLayout.alpha = 1.0f

            // Remove any previous callbacks to prevent multiple delayed changes
            handler.removeCallbacks(delayedAlphaChangeRunnable)

            // Adjust the font size
            theme_font_size -= 20F
            if (theme_font_size < 0F) {
                theme_font_size = 0F
            }

            // Update the font size
            lifecycleScope.launch {
                playerControlViewModel.updatePortableZoom(theme_font_size)
            }

            // Schedule the delayed change to 50% opacity after 3 seconds
            handler.postDelayed(
                delayedAlphaChangeRunnable,
                2000 // 3 seconds in milliseconds
            )
        }



        // bn/ ar reading mode

        binding.banglaRecBtn.setOnClickListener {

            binding.cautionBtn.show()
            if(isBnReading)
                return@setOnClickListener


            isBnReading = true
            binding.banglaRecBtn.setActiveState()
            binding.arabicRecBtn.setInactiveState()

            alQuranAyatAdapter.updateReadingMode(isBnReading)
            update_theme_font_size(theme_font_size, isThemeFont = true)
            updateAyatAdapterVisibleItems()
        }

        binding.arabicRecBtn.setOnClickListener {

            binding.cautionBtn.hide()
            if(!isBnReading)
                return@setOnClickListener

            isBnReading = false
            binding.arabicRecBtn.setActiveState()
            binding.banglaRecBtn.setInactiveState()

            alQuranAyatAdapter.updateReadingMode(isBnReading)
            update_theme_font_size(theme_font_size, isThemeFont = true)
            updateAyatAdapterVisibleItems()
        }

        // Caution btn

        binding.cautionBtn.setOnClickListener {
            dialog_caution()
        }

        binding.reciterBtn.setOnClickListener {
            dialog_select_reciter()
        }

        binding.translationBtn.setOnClickListener {
            dialog_select_translator()
        }

        binding.tasfsirBtn.setOnClickListener {
            dialog_select_tafsirMaker()
        }



        //init observer
        initObserver()

        //loading start
        loadingState()


        if(!firstload) {

                loadApiData(pageNo, pageItemCount)

        }
        firstload = true


    }

    override fun option3dotClicked(getdata: OptionList, ayathData: Ayath?) {

        when(getdata.type){

            "download" -> {

                 /*if(!Subscription.isSubscribe){
                    dialog?.dismiss()
                    gotoFrag(R.id.action_global_subscriptionFragment)
                    return
                }*/

                val fileName = "surahinfo.json" // Replace with the actual file name you want to check

                val filePath = File(requireContext().filesDir, "quran/${formatNumber(surahID)}/$fileName")


                if (filePath.exists()) {
                    context?.apply {
                        toast("Surah already downloaded")
                    }

                    return

                }

                dialog?.dismiss()

                customAlertDialog.setupDialog(
                    callback = this@AlQuranFragment,
                    context = requireContext(),
                    btn1Text = localContext.getString(R.string.cancel),
                    btn2Text = localContext.getString(R.string.continueTxt),
                    titileText = localContext.getString(R.string.want_to_download),
                    subTitileText = localContext.getString(R.string.quran_offline_download_hint)
                )

                customAlertDialog.showDialog(true)

            }

            "share" -> {
                customShareAyat(
                    ayathData?.Transliteration_en.toString(),
                    ayathData?.Transliteration_bn.toString(),
                    ayathData?.Arabic_indopak.toString(),
                    ayathData?.VerseKey.toString()
                )
            }

            "copyArabic" -> {
                context?.apply {
                    copyToClipboard(ayathData?.Arabic_indopak.toString())
                    toast("Arabic text copied")
                }

            }

            "copyBangla" -> {

                context?.apply {
                    copyToClipboard(ayathData?.Transliteration_bn.toString())
                    toast("Bangla text copied")
                }
            }

            "copyEnglish" -> {

                context?.apply {
                    copyToClipboard(ayathData?.Transliteration_bn.toString())
                    toast("English text copied")
                }

            }
        }

        dialog?.dismiss()

    }

    override fun option3dotClicked(getdata: Ayath) {

        dialog?.dismiss()

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = localInflater.inflate(R.layout.item_quran_3dot_option, null, false)
        val surahname:AppCompatTextView = customAlertDialogView.findViewById(R.id.surahName)
        val surahAyath:AppCompatTextView = customAlertDialogView.findViewById(R.id.surahAyath)
        val arbSurah:AppCompatTextView = customAlertDialogView.findViewById(R.id.arbSurah)
        val optionList:RecyclerView = customAlertDialogView.findViewById(R.id.optionList)

        val surahID = surahID
        surahname.text = surahName?:
                localContext.resources.getString(R.string.quran_para_adapter_title,quranJuz?.JuzId.toString().numberLocale())
        arbSurah.text = "${if(surahID<10)0 else ""}${if(surahID<100)0 else ""}${surahID}"
        arbSurah.visible(isSurahMode)
        surahAyath.text = localContext.getString(R.string.ayaths_number,getdata.VerseId.toString()).numberLocale()
        val optionList3Dot = arrayListOf(
            OptionList("copyArabic",R.drawable.ic_content_copy,localContext.getString(R.string.copy_arabic_text)),
            OptionList("copyBangla",R.drawable.ic_content_copy,localContext.getString(R.string.copy_bangla_text)),
            OptionList("copyEnglish",R.drawable.ic_content_copy,localContext.getString(R.string.copy_english_text)),
            OptionList("share",R.drawable.ic_share,localContext.getString(R.string.share))

        )

        optionList.apply {
            adapter = Quran3DotAdapter(optionList3Dot,getdata)
        }


        dialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(true)
            .show().apply {
                window?.setGravity(Gravity.CENTER)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isMiniPlayerAlreadySet = false
        AudioManager().getInstance().releasePlayer()
        AlQuranSettings.onDestroy()
    }

    private fun clearPlayerControlBtn()
    {
        binding.bottomPlayer.largePlayer.themeBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))
        binding.bottomPlayer.largePlayer.themeBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_background))

        binding.bottomPlayer.largePlayer.audioBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))
        binding.bottomPlayer.largePlayer.audioBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_background))

        binding.bottomPlayer.largePlayer.transalationBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))
        binding.bottomPlayer.largePlayer.transalationBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_background))

    }

    private fun morePageBottomLoading(bol:Boolean)
    {
        binding.lastItemLoadingProgress.visible(bol)
    }

    private fun setHeaderData()
    {
        // set header data
        if(!isSurahMode) {
            binding.headerLeftLy.hide()
            binding.headerCenterLy.hide()
            binding.headerRightLy.hide()
        }
        binding.surahOrigin.text = surahListData?.SurahOrigin
        binding.surahName.text = pageTitle

        if(surahID>0)
        {
            binding.arbSurah.text = "${if(surahID<10)0 else ""}${if(surahID<100)0 else ""}${surahID}"

        }
        /*surahListData?.let {
            binding.arbSurah.text = "${if(it.SurahId<10)0 else ""}${if(it.SurahId<100)0 else ""}${it.SurahId}"
        }*/

        binding.nameMeaning.text = surahListData?.SurahNameMeaning
        binding.ayatTotal.text = localContext.resources.getString(R.string.quran_popular_surah_ayat,totalVerseCount.toString().numberLocale(),"")

    }

    private fun setupMiniPlayerData()
    {
        if(isMiniPlayerAlreadySet)
            return

        countDownTimer?.cancel()
        binding.bottomPlayer.miniPlayer.icPlayPause.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_quran_play_fill
            )
        )
        binding.bottomPlayer.largePlayer.icPlayBtn.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_quran_play_fill
            )
        )
        binding.bottomPlayer.miniPlayer.playerProgress.progress = 0
        totalVerseCount = quranJuz?.TotalAyat?:totalVerseCount

        binding.bottomPlayer.miniPlayer.surahTitile.text = pageTitle
        binding.bottomPlayer.miniPlayer.surahAyat.text = localContext.resources.getString(R.string.quran_popular_surah_ayat,totalVerseCount.toString().numberLocale(),"")

        binding.bottomPlayer.largePlayer.surahTitile.text = pageTitle
        binding.bottomPlayer.largePlayer.surahAyat.text = localContext.resources.getString(R.string.quran_popular_surah_ayat,totalVerseCount.toString().numberLocale(),"")


        binding.bottomPlayer.largePlayer.largePlayerProgress.setOnTouchListener { _, _ -> true }

        binding.bottomPlayer.miniPlayer.icPlayPause.setOnClickListener {
            alQuranAyatAdapter.miniPlayerCall()
        }

        binding.bottomPlayer.largePlayer.icPlayBtn.setOnClickListener {
            alQuranAyatAdapter.miniPlayerCall()
        }

        binding.bottomPlayer.largePlayer.icPrev.setOnClickListener {
            //alQuranAyatAdapter.miniPlayerPrevCall()
            if(isSurahMode)
            playPrevSurah(true)
            else
                playPrevJuz(true)
        }

        binding.bottomPlayer.largePlayer.icNext.setOnClickListener {

            //alQuranAyatAdapter.miniPlayerNextCall()
            if(isSurahMode)
            playNextSurah(true)
            else
                playNextJuz(true)

        }


        binding.bottomPlayer.largePlayer.icDownload.setOnClickListener {

            option3dotClicked(OptionList("download",0,""))
        }

        binding.bottomPlayer.largePlayer.icFav.setOnClickListener {
            surahFavClicked()
        }

        binding.bottomPlayerContainer.post {

            if (bottomSheetBehavior.lastStableState == STATE_EXPANDED) {
                binding.bottomPlayer.miniPlayer.root.hide()
                bottomSheetBehavior.state = STATE_EXPANDED
                binding.dimView.show()
            }
        }

        //Log.e("ALquranSetMini", bottomSheetBehavior.lastStableState.toString())

        playerControlPages = arrayListOf(
            PlayerThemeFragment(),
            PlayerAudioFragment(qarisData),
            PlayerTranslationFragment(translatorDataData)
        )

        playerControlViewPageAdapter = MainViewPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = lifecycle,
            playerControlPages
        )


        binding.bottomPlayer.playerViewPager.apply {
                adapter = playerControlViewPageAdapter
                isNestedScrollingEnabled = false
                isUserInputEnabled = false
                overScrollMode = View.OVER_SCROLL_NEVER
                reduceDragSensitivity(2)
                offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        }

       /* binding.bottomPlayer.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // This callback is called when the view is laid out and its dimensions are available
                Log.e("botomPlayer", binding.bottomPlayer.miniPlayer.root.height.toString())

                // You can now safely access the height and set the peekHeight
                bottomSheetBehavior.peekHeight = binding.bottomPlayer.miniPlayer.root.height + binding.bottomPlayer.icBar.height + 14.dp

                // It's a good practice to remove the listener once you have the height
                binding.bottomPlayer.miniPlayer.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })*/

        val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // This callback is called when the view is laid out and its dimensions are available
                Log.e("bottomPlayer", "MiniPlayer height: ${binding.bottomPlayer.miniPlayer.root.height}")
                Log.e("bottomPlayer", "icBar height: ${binding.bottomPlayer.icBar.height}")

                // You can now safely access the height and set the peekHeight
                bottomSheetBehavior.peekHeight =
                    binding.bottomPlayer.miniPlayer.root.height + binding.bottomPlayer.icBar.height + 14.dp

                // It's a good practice to remove the listener once you have the height
                binding.bottomPlayer.miniPlayer.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }

        binding.bottomPlayer.miniPlayer.root.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // Remove the listener to avoid multiple callbacks
                binding.bottomPlayer.miniPlayer.root.viewTreeObserver.removeOnPreDrawListener(this)

                // Add the global layout listener
                binding.bottomPlayer.miniPlayer.root.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)

                bottomSheetBehavior.peekHeight =
                    binding.bottomPlayer.miniPlayer.root.height + binding.bottomPlayer.icBar.height + 14.dp

                return true
            }
        })


        isMiniPlayerAlreadySet = true
    }



    private fun initObserver()
    {
        alQuranViewModel.surahDetails.observe(viewLifecycleOwner)
        { response ->
            when(response)
            {
                is AlQuranResource.VersesByChapter ->
                {

                    //surahDetailsData.addAll(it.data)
                    isNextEnabled = response.data.Pagination.isNext
                    viewState(response.data.Data,response.isReadingMode)
                }
                is CommonResource.API_CALL_FAILED -> noInternetState()
                is CommonResource.EMPTY -> emptyState()
            }
        }

        playerControlViewModel.alQuranSettingsLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is AlQuranSettingResource.AlQuranSettings ->
                {
                    AlQuranSettings.updateDrawer(binding.navdrawer,it.setting,"all")

                   /* if(isInitialSettingApply)
                        return@observe
*/

                    it.setting?.theme_font_size?.let { it1 -> update_theme_font_size(it1, isThemeFont = true) }
                    it.setting?.translation_font_size?.let { it1 -> update_theme_font_size(it1, isTranslationFont = true) }
                    it.setting?.english_font_size?.let { it1 -> update_theme_font_size(it1, isEnglishFont = true) }

                    it.setting?.transliteration?.let {
                            it1 -> alQuranAyatAdapter.update_transliteration(it1)
                    }
                    it.setting?.auto_scroll?.let { it1 -> auto_scroll = it1 }
                    it.setting?.auto_play_next?.let {
                            it1 -> alQuranAyatAdapter.update_auto_play_next(it1)
                    }

                    it.setting?.arabic_font?.let {
                        it1 -> alQuranAyatAdapter.setArabicFont(it1)
                    }

                    it.setting?.recitation?.let {
                            it1->
                        updateQuranPlayer(qari = it1)
                        alQuranAyatAdapter.updateQari(it1)
                    }


                    it.setting?.tafsir?.let {
                            it1-> if(it1!=0) tafsirMaker = it1
                    }

                    updateAyatAdapterVisibleItems()

                    isInitialSettingApply = true

                }


                is AlQuranSettingResource.UpdateAlQuranSettings -> {

                    AlQuranSettings.updateDrawer(binding.navdrawer,it.setting,it.type)

                    it.setting?.theme_font_size?.let { it1 -> update_theme_font_size(it1, isThemeFont = true) }
                    it.setting?.translation_font_size?.let { it1 -> update_theme_font_size(it1, isTranslationFont = true) }
                    it.setting?.english_font_size?.let { it1 -> update_theme_font_size(it1, isEnglishFont = true) }

                    it.setting?.transliteration?.let {
                            it1 -> alQuranAyatAdapter.update_transliteration(it1)
                    }
                    it.setting?.bn_meaning?.let {
                            it1 -> alQuranAyatAdapter.update_bn_meaning(it1)
                    }
                    it.setting?.auto_scroll?.let { it1 -> auto_scroll = it1 }
                    it.setting?.auto_play_next?.let {
                            it1 -> alQuranAyatAdapter.update_auto_play_next(it1)
                    }

                    it.setting?.arabic_font?.let {
                            it1 -> alQuranAyatAdapter.setArabicFont(it1)
                    }

                    it.setting?.recitation?.let {
                            it1->
                        updateQuranPlayer(qari = it1)
                        alQuranAyatAdapter.updateQari(it1)
                    }

                    it.setting?.en_translator?.let {
                            it1-> alQuranAyatAdapter.updateEnTranslator(it1)
                    }

                    it.setting?.bn_translator?.let {
                            it1-> alQuranAyatAdapter.updateBnTranslator(it1)
                    }


                    it.setting?.tafsir?.let {
                            it1-> if(it1!=0) tafsirMaker = it1
                    }


                    updateAyatAdapterVisibleItems()
                }

            }
        }

        alQuranViewModel.surahListLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is AlQuranResource.SurahList -> surahList = it.data.Data as ArrayList<Data>
            }
        }

        alQuranViewModel.ayatFavLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is AlQuranResource.ayatFav ->
                {
                    val positionToUpdate = it.position

                    if (positionToUpdate >= 0 && positionToUpdate < alQuranAyatAdapter.itemCount) {
                        // The position is valid, update the item in the adapter
                        alQuranAyatAdapter.updateFavAyat(it.isFav, positionToUpdate)
                    }
                }
            }
        }

        alQuranViewModel.tafsirLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED ->
                {
                    tafsirDialogProgressLayout?.hide()
                    tafsirDialogNoData?.hide()
                    tafsirDialogNoInternet?.show()
                }

                is CommonResource.EMPTY ->
                {
                    tafsirDialogProgressLayout?.hide()
                    tafsirDialogNoData?.show()
                    tafsirDialogNoInternet?.hide()
                }

                is AlQuranResource.Tafsir->
                {
                    val filterData = it.data.firstOrNull  {
                        tafsir->
                        tafsir.TranslatorId == tafsirMaker
                    }

                    filterData?.let {
                        tafsir->

                       /* if(it.arabicFont == 1)
                        {
                            val customFont = ResourcesCompat.getFont(requireContext(), R.font.kfgqpc_font)
                            tafsirDialogArabicAyat?.typeface = customFont
                        }
                        else
                        {*/
                            val customFont = ResourcesCompat.getFont(requireContext(), R.font.indopakv2)
                            tafsirDialogArabicAyat?.typeface = customFont
                       // }

                        tafsirDialogArabicAyat?.text = it.ayatArabic



                        tafsirDialogMakerTxt?.text = "-${tafsir.Reference}"


                        // Use WebView to load HTML content
                        tafsirDialogContent?.loadDataWithBaseURL(null, tafsir.Text, "text/html", "UTF-8", null)

                        tafsirDialogContent?.webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                tafsirDialogMakerTxt?.show()
                                tafsirDialogArabicAyat?.show()
                                tafsirDialogProgressLayout?.hide()
                                tafsirDialogNoData?.hide()
                                tafsirDialogNoInternet?.hide()
                            }
                        }

                    }?: run {

                        tafsirDialogProgressLayout?.hide()
                        tafsirDialogNoData?.show()
                        tafsirDialogNoInternet?.hide()
                    }


                }

            }
        }
    }

    private fun update_theme_font_size(
        fontsize: Float,
        isTranslationFont:Boolean=false,
        isThemeFont:Boolean=false,
        isEnglishFont:Boolean=false
    )
    {

        val minValue = 0F
        val maxValue = 100F


        if(isThemeFont)
            theme_font_size = fontsize.coerceIn(minValue, maxValue)

        when(fontsize)
        {
            0F ->
            {
                if(isThemeFont) {
                    alQuranAyatAdapter.update_theme_font_size(25F)
                    binding.portableZoomLevel.text = "0%".numberLocale()
                }

                if(isTranslationFont)
                    alQuranAyatAdapter.update_translation_font_size(18F)

                if(isEnglishFont)
                    alQuranAyatAdapter.update_english_font_size(16F)

            }

            20F ->
            {
                if(isThemeFont) {
                    alQuranAyatAdapter.update_theme_font_size(29F)
                    binding.portableZoomLevel.text = "20%".numberLocale()
                }

                if(isTranslationFont)
                    alQuranAyatAdapter.update_translation_font_size(20F)

                if(isEnglishFont)
                    alQuranAyatAdapter.update_english_font_size(18F)

            }

            40F ->
            {
                if(isThemeFont) {
                    alQuranAyatAdapter.update_theme_font_size(31F)
                    binding.portableZoomLevel.text = "40%".numberLocale()
                }

                if(isTranslationFont)
                    alQuranAyatAdapter.update_translation_font_size(22F)

                if(isEnglishFont)
                    alQuranAyatAdapter.update_english_font_size(20F)
            }

            60F ->
            {
                if(isThemeFont) {
                    alQuranAyatAdapter.update_theme_font_size(34F)
                    binding.portableZoomLevel.text = "60%".numberLocale()
                }

                if(isTranslationFont)
                    alQuranAyatAdapter.update_translation_font_size(24F)

                if(isEnglishFont)
                    alQuranAyatAdapter.update_english_font_size(22F)
            }

            80F->
            {
                if(isThemeFont) {
                    alQuranAyatAdapter.update_theme_font_size(37F)
                    binding.portableZoomLevel.text = "80%".numberLocale()
                }

                if(isTranslationFont)
                    alQuranAyatAdapter.update_translation_font_size(26F)
                if(isEnglishFont)
                    alQuranAyatAdapter.update_english_font_size(24F)

            }

            100F ->
            {
                if(isThemeFont) {
                    alQuranAyatAdapter.update_theme_font_size(42F)
                    binding.portableZoomLevel.text = "100%".numberLocale()
                }

                if(isTranslationFont)
                    alQuranAyatAdapter.update_translation_font_size(28F)
                if(isEnglishFont)
                    alQuranAyatAdapter.update_english_font_size(26F)

            }
        }

    }

    private fun updateAyatAdapterVisibleItems() {
        val layoutManager = binding.ayatList.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        for (position in firstVisiblePosition..lastVisiblePosition) {
            // Check if you want to update this item
                alQuranAyatAdapter.notifyItemChanged(position)
        }
    }


    private fun fetchNextPageData() {
        lifecycleScope.launch {
            pageNo++
            loadApiData(pageNo, pageItemCount)
            isNextEnabled = false
        }
    }

    private fun loadApiData(page:Int,itemCount:Int,surahNo:Int = surahID,juz_number:Int? = quranJuz?.JuzId)
    {

        lifecycleScope.launch(Dispatchers.IO) {
            if(surahNo>0)
                alQuranViewModel.getVersesByChapter(getLanguage(),page,itemCount,surahNo,isReadingMode)
            else if(juz_number!=null)
                alQuranViewModel.getVersesByJuz(getLanguage(),page,itemCount,juz_number,isReadingMode)

            if(surahList.isEmpty())
                alQuranViewModel.getSurahList(
                    language = getLanguage(),
                    page = 1,
                    contentCount = 114
                )
        }
    }

    private fun loadTafsir(surahID: Int, verseID: Int, ayatArabic: String, arabicFont: Int)
    {
        tafsirDialogNoData?.hide()
        tafsirDialogNoInternet?.hide()
        tafsirDialogProgressLayout?.show()

        lifecycleScope.launch(Dispatchers.IO) {
            alQuranViewModel.getTafsir(
                surahID = surahID,
                verseID = verseID,
                ayatArabic = ayatArabic,
                arabicFont = arabicFont
            )
        }
    }

    private fun loadPlayerSetting()
    {
        lifecycleScope.launch {
            playerControlViewModel.getSetting()
        }
    }
    private fun viewState(
        data: com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Data,
        readingMode: Boolean
    )
    {

        if(this.isReadingMode != readingMode)
            return

        if(qarisData.isEmpty())
            qarisData.addAll(data.Qaris)

        if(translatorDataData.isEmpty())
            translatorDataData.addAll(data.Translators)

        if(tasfsirList.isEmpty())
            tasfsirList.addAll(data.Tafsirs)

        loadPlayerSetting()


        alQuranAyatAdapter.update(data,surahID)

        binding.ayatList.post {
            morePageBottomLoading(false)
            isScrollAtEnd = false
            if(isNextPrevSurahCalled) {
                alQuranAyatAdapter.miniPlayerCall()
                isNextPrevSurahCalled = false
            }
        }

        resetAyatList()

        binding.headerLayout.show()

        data.SurahInfo?.let {
            binding.surahOrigin.text = data.SurahInfo.SurahOrigin
            binding.surahName.text = data.SurahInfo.SurahName
            binding.nameMeaning.text = data.SurahInfo.SurahNameMeaning
            binding.ayatTotal.text = localContext.resources.getString(R.string.quran_popular_surah_ayat,data.SurahInfo.TotalAyat.toString().numberLocale(),"")
            binding.ruku.text = data.SurahInfo.Ruku_number.toString()
            totalVerseCount  = data.SurahInfo.TotalAyat
            pageTitle = data.SurahInfo.SurahName
        }

        isMiniPlayerAlreadySet = false

        // setup mini player data
        setupMiniPlayerData()
        ifQuranDownloading()

        binding.ayatList.post {
            binding.progressLayout.root.hide()
            binding.nodataLayout.root.hide()
            binding.noInternetLayout.root.hide()
            binding.bottomPlayerContainer.show()
        }


        isApiLoaded = true
    }


    private fun emptyState()
    {
        binding.progressLayout.root.hide()
        binding.nodataLayout.root.show()
        binding.noInternetLayout.root.hide()
    }

    private fun noInternetState()
    {
        binding.progressLayout.root.hide()
        binding.nodataLayout.root.hide()
        binding.noInternetLayout.root.show()
        binding.bottomPlayerContainer.hide()
    }

    private fun loadingState()
    {
        binding.progressLayout.root.visible(true)
        binding.nodataLayout.root.visible(false)
        binding.noInternetLayout.root.visible(false)
    }
    private fun dialog_select_surah()
    {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_surah_list, null, false)

        // Initialize and assign variable
        val searchLayout = customAlertDialogView.findViewById<TextInputLayout>(R.id.searchLayout)
        val search_surah = customAlertDialogView.findViewById<TextInputEditText>(R.id.search_surah)
        val surahList = customAlertDialogView.findViewById<RecyclerView>(R.id.surahList)
        val dismissBtn = customAlertDialogView.findViewById<ImageButton>(R.id.closeBtn)
        val title: AppCompatTextView = customAlertDialogView.findViewById(R.id.pageTitle)

        if (isSurahMode)
            title.text = localContext.getString(R.string.select_surah)
        else
            title.text = localContext.getString(R.string.select_juz_para)

        searchLayout.visible(isSurahMode)

        selectSurahAdapter = SelectSurahAdapter(
            surahList = this@AlQuranFragment.surahList.ifEmpty { arrayListOf() },
            juzList = quranJuzList,
            this@AlQuranFragment,
            isSurahMode)

        surahList.adapter = selectSurahAdapter

        dismissBtn?.setOnClickListener {
            dialog?.dismiss()
        }

        search_surah?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                selectSurahAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // show dialog

        dialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(true)
            .show()
    }

    override fun getqariList():ArrayList<Qari>{
        return qarisData
    }

    override fun getTranslatorData():ArrayList<Translator>{
        return translatorDataData
    }

    override fun getTafsirList():ArrayList<TafsirList>{
        return tasfsirList
    }

    override fun getArabicFontList(): ArrayList<FontList> {
        return fontListData
    }

    override fun dialog_select_reciter()
    {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_surah_list, null, false)

        // Initialize and assign variable
        val searchLayout = customAlertDialogView.findViewById<TextInputLayout>(R.id.searchLayout)
        val search_surah = customAlertDialogView.findViewById<TextInputEditText>(R.id.search_surah)
        val surahList = customAlertDialogView.findViewById<RecyclerView>(R.id.surahList)
        val dismissBtn = customAlertDialogView.findViewById<ImageButton>(R.id.closeBtn)
        val title: AppCompatTextView = customAlertDialogView.findViewById(R.id.pageTitle)

        title.text = localContext.getString(R.string.choose_a_reciter)

        searchLayout.hide()

        surahList.apply {
            playerCommonSelectionList = PlayerCommonSelectionList(ArrayList(qarisData.map { transformPlayerReciterData(it) }),this@AlQuranFragment)
            adapter = playerCommonSelectionList
        }

        playerCommonSelectionList.update(updateSelectedQari(alQuranAyatAdapter.getSelectedQari()))


        dismissBtn?.setOnClickListener {
            dialog?.dismiss()
        }

        // show dialog

        dialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(true)
            .show()
    }


    private fun dialog_select_translator()
    {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_translator_list, null, false)

        // Initialize and assign variable
        val banglaTranList:RecyclerView = customAlertDialogView.findViewById(R.id.banglaTranList)
        val englishTranList:RecyclerView = customAlertDialogView.findViewById(R.id.englishTranList)
        val dismissBtn = customAlertDialogView.findViewById<ImageButton>(R.id.closeBtn)
        val title: AppCompatTextView = customAlertDialogView.findViewById(R.id.pageTitle)
        val translationByTxt:AppCompatTextView = customAlertDialogView.findViewById(R.id.translationByTxt)
        val translationByTxt1:AppCompatTextView = customAlertDialogView.findViewById(R.id.translationByTxt1)

        title.text = localContext.getString(R.string.select_translation)
        translationByTxt.show()
        translationByTxt1.show()
        banglaTranList.apply {
            bnPlayerCommonSelectionList = PlayerCommonSelectionList(
                ArrayList(translatorDataData.map { transformPlayerTranslatorData(it) }
                    .filter { it.language == "bn" }
                ),this@AlQuranFragment)
            adapter = bnPlayerCommonSelectionList

        }

        bnPlayerCommonSelectionList.update(updateBnTranslator(alQuranAyatAdapter.getBnTranslator()))


        englishTranList.apply {
            enPlayerCommonSelectionList = PlayerCommonSelectionList(
                ArrayList(translatorDataData.map { transformPlayerTranslatorData(it) }
                    .filter { it.language == "en" }
                ),this@AlQuranFragment)
            adapter = enPlayerCommonSelectionList

        }

        enPlayerCommonSelectionList.update(updateEnTranslator(alQuranAyatAdapter.getEnTranslator()))


        dismissBtn?.setOnClickListener {
            dialog?.dismiss()
        }

        // show dialog

        dialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(true)
            .show()
    }


    override fun dialog_select_translator(lang: String)
    {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_translator_list, null, false)

        // Initialize and assign variable
        val banglaTranList:RecyclerView = customAlertDialogView.findViewById(R.id.banglaTranList)
        val englishTranList:RecyclerView = customAlertDialogView.findViewById(R.id.englishTranList)
        val translationByTxt:AppCompatTextView = customAlertDialogView.findViewById(R.id.translationByTxt)
        val translationByTxt1:AppCompatTextView = customAlertDialogView.findViewById(R.id.translationByTxt1)
        val dismissBtn = customAlertDialogView.findViewById<ImageButton>(R.id.closeBtn)
        val title: AppCompatTextView = customAlertDialogView.findViewById(R.id.pageTitle)


        if(lang == "bn") {
            banglaTranList.apply {
                bnPlayerCommonSelectionList = PlayerCommonSelectionList(
                    ArrayList(translatorDataData.map { transformPlayerTranslatorData(it) }
                        .filter { it.language == "bn" }
                    ), this@AlQuranFragment)
                adapter = bnPlayerCommonSelectionList

            }
            title.text = localContext.getString(R.string.bangla_translator)

            translationByTxt.hide()
            translationByTxt1.hide()
            englishTranList.hide()

            bnPlayerCommonSelectionList.update(updateBnTranslator(alQuranAyatAdapter.getBnTranslator()))

        }


        if(lang == "en") {
            englishTranList.apply {
                enPlayerCommonSelectionList = PlayerCommonSelectionList(
                    ArrayList(translatorDataData.map { transformPlayerTranslatorData(it) }
                        .filter { it.language == "en" }
                    ), this@AlQuranFragment)
                adapter = enPlayerCommonSelectionList

            }
            title.text = localContext.getString(R.string.english_translator)
            translationByTxt1.hide()
            translationByTxt.hide()
            banglaTranList.hide()

            enPlayerCommonSelectionList.update(updateEnTranslator(alQuranAyatAdapter.getEnTranslator()))
        }

        dismissBtn?.setOnClickListener {
            dialog?.dismiss()
        }

        // show dialog

        dialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(true)
            .show()
    }


    override fun dialog_select_tafsirMaker()
    {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_translator_list, null, false)

        // Initialize and assign variable

        val translationByTxt:AppCompatTextView = customAlertDialogView.findViewById(R.id.translationByTxt)
        val banglaTranList:RecyclerView = customAlertDialogView.findViewById(R.id.banglaTranList)
        val translationByTxt1:AppCompatTextView = customAlertDialogView.findViewById(R.id.translationByTxt1)
        val englishTranList:RecyclerView = customAlertDialogView.findViewById(R.id.englishTranList)
        val dismissBtn = customAlertDialogView.findViewById<ImageButton>(R.id.closeBtn)
        val title: AppCompatTextView = customAlertDialogView.findViewById(R.id.pageTitle)

        translationByTxt1.hide()
        englishTranList.hide()

        translationByTxt.text = localContext.getString(R.string.bangla_tafsir)
        title.text = localContext.getString(R.string.bangla_tafsir)

        banglaTranList.apply {
            tafsirCommonSelectionList = PlayerCommonSelectionList(
                ArrayList(tasfsirList.map { transformPlayerTafsirData(it) }
                    .filter { it.language == "bn" }
                ),this@AlQuranFragment)
            adapter = tafsirCommonSelectionList

        }

        tafsirCommonSelectionList.update(updateTafsirMaker(tafsirMaker))

        dismissBtn?.setOnClickListener {
            dialog?.dismiss()
        }

        // show dialog

        dialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(true)
            .show()
    }

    override fun dialog_select_arabic_font()
    {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_translator_list, null, false)

        // Initialize and assign variable

        val translationByTxt:AppCompatTextView = customAlertDialogView.findViewById(R.id.translationByTxt)
        val banglaTranList:RecyclerView = customAlertDialogView.findViewById(R.id.banglaTranList)
        val translationByTxt1:AppCompatTextView = customAlertDialogView.findViewById(R.id.translationByTxt1)
        val englishTranList:RecyclerView = customAlertDialogView.findViewById(R.id.englishTranList)
        val dismissBtn = customAlertDialogView.findViewById<ImageButton>(R.id.closeBtn)
        val title: AppCompatTextView = customAlertDialogView.findViewById(R.id.pageTitle)

        translationByTxt1.hide()
        englishTranList.hide()

        translationByTxt.text = localContext.getString(R.string.font_settings)
        title.text = localContext.getString(R.string.arabic_font)

        banglaTranList.apply {
            arabicFontCommonSelectionList = PlayerCommonSelectionList(
                ArrayList(fontListData.map { transformArabicFontData(it) }),this@AlQuranFragment)
            adapter = arabicFontCommonSelectionList

        }

        arabicFontCommonSelectionList.update(updateSelectedFont(alQuranAyatAdapter.getArabicFont()))

        dismissBtn?.setOnClickListener {
            dialog?.dismiss()
        }

        // show dialog

        dialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(true)
            .show()
    }

    private fun updateSelectedFont(fontid: Int): List<PlayerCommonSelectionData> {

        var finalTransalator = DEFAULT_ARABIC_FONT_ID

        if(fontid != 0)
            finalTransalator = fontid

        val crrentData = arabicFontCommonSelectionList.getData()

        val updatedData = crrentData.map { data ->
            data.copy(isSelected = data.Id == finalTransalator)
        }

        return updatedData
    }


    private fun dialog_tafsir(surahNo: Int, verseID: Int, ayatArabic: String, arabicFont: Int)
    {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_quran_tafsir, null, false)

        // Initialize and assign variable

        tafsirDialogProgressLayout = customAlertDialogView.findViewById(R.id.progressLayout)
        tafsirDialogNoInternet = customAlertDialogView.findViewById(R.id.no_internet_layout)
        tafsirDialogArabicAyat = customAlertDialogView.findViewById(R.id.ayatArabic)
        tafsirDialogMakerTxt = customAlertDialogView.findViewById(R.id.tafsirMaker)
        tafsirDialogContent = customAlertDialogView.findViewById(R.id.tafsirTxt)
        tafsirDialogRetryBtn = tafsirDialogNoInternet?.findViewById(R.id.no_internet_retry)
        tafsirDialogNoData = customAlertDialogView.findViewById(R.id.nodataLayout)
        val header:LinearLayout = customAlertDialogView.findViewById(R.id.header)
        val container:ScrollView = customAlertDialogView.findViewById(R.id.container)
        val dismissBtn = customAlertDialogView.findViewById<ImageButton>(R.id.closeBtn)

        header.post {
            val param = container.layoutParams as ViewGroup.MarginLayoutParams
            param.topMargin = header.height
            container.layoutParams = param
        }

        tafsirDialogProgressLayout?.let {
            ViewCompat.setTranslationZ(it, 10F)
        }

        tafsirDialogNoInternet?.let {
            ViewCompat.setTranslationZ(it, 10F)
        }

        tafsirDialogNoData?.let {
            ViewCompat.setTranslationZ(it, 10F)
        }

        dismissBtn?.setOnClickListener {
            dialog?.dismiss()
        }

        // show dialog

        dialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(true)
            .show()

        loadTafsir(surahNo,verseID,ayatArabic,arabicFont)
    }


    private fun dialog_caution()
    {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_quran_caution, null, false)

        dialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(true)
            .show()
    }


    private fun updateSelectedQari(selectedQariID: Int): List<PlayerCommonSelectionData> {

        var finalQari = DEFAULT_QARI_ID

        if(selectedQariID != 1)
            finalQari = selectedQariID

        val crrentData = playerCommonSelectionList.getData()

        val updatedData = crrentData.map { qari ->
            qari.copy(isSelected = qari.Id == finalQari)
        }

        return updatedData
    }

    private fun updateBnTranslator(selectedTranslator: Int): List<PlayerCommonSelectionData> {

        var finalTransalator = DEFAULT_BN_TRANSLATOR_ID

        if(selectedTranslator != 0)
            finalTransalator = selectedTranslator

        val crrentData = bnPlayerCommonSelectionList.getData()

        val updatedData = crrentData.map { data ->
            data.copy(isSelected = data.Id == finalTransalator)
        }

        return updatedData
    }

    private fun updateEnTranslator(selectedTranslator: Int): List<PlayerCommonSelectionData> {

        var finalTransalator = DEFAULT_EN_TRANSLATOR_ID

        if(selectedTranslator != 0)
            finalTransalator = selectedTranslator

        val crrentData = enPlayerCommonSelectionList.getData()

        val updatedData = crrentData.map { data ->
            data.copy(isSelected = data.Id == finalTransalator)
        }

        return updatedData
    }

    private fun updateTafsirMaker(selectedTafsir: Int): List<PlayerCommonSelectionData> {

        var finalTfsir = tafsirMaker

        if(selectedTafsir != 0)
            finalTfsir = selectedTafsir

        val crrentData = tafsirCommonSelectionList.getData()

        val updatedData = crrentData.map { data ->
            data.copy(isSelected = data.Id == finalTfsir)
        }

        return updatedData
    }


    private fun resetAyatList()
    {
        binding.ayatList.apply {
            adapter = alQuranAyatAdapter

            layoutManager = if(isReadingMode) {
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            } else {
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }

            post {
                binding.container.setPadding(
                    binding.container.paddingLeft,
                    binding.headerLayout.height + binding.actionbar.root.height - 20,
                    binding.container.paddingRight,
                    binding.container.paddingBottom
                )
            }
        }
    }
    private fun setupReadinMode()
    {
        binding.bottomPlayerContainer.hide()
        if(isBnReading) {

            binding.banglaRecBtn.setActiveState()
            binding.arabicRecBtn.setInactiveState()
        }
        else
        {
            binding.arabicRecBtn.setActiveState()
            binding.banglaRecBtn.setInactiveState()
        }


        binding.banglaRecBtn.show()
        binding.arabicRecBtn.show()


        isReadingMode = true
        isNextEnabled = false
        loadingState()

        AudioManager().getInstance().releasePlayer()
        isAyatPause()
        binding.bottomPlayer.miniPlayer.playerProgress.progress = 0

        pageNo = 1
        pageItemCount = 30

        surahDetailsData.clear()
        alQuranAyatAdapter.clear()
        alQuranAyatAdapter.updateMode(isReadingMode)
        resetAyatList()


        setupActionForOtherFragment(
            action1 = R.drawable.ic_settings,
            action2 = R.drawable.ic_list_mode,
            action3 = R.drawable.ic_download_quran,
            callback = this@AlQuranFragment,
            actionnBartitle = pageTitle,
            backEnable = true,
            actionIconColor = R.color.deen_txt_black_deep,
            view = requireView()
        )

        setCustomPageTitle(pageTitle)

        ifQuranDownloading()

        binding.ayatList.post {
            if(isSurahMode)
                loadApiData(pageNo,pageItemCount,surahID)
            else
                loadApiData(pageNo,pageItemCount, juz_number = quranJuz?.JuzId)
        }

        //binding.portableZoomLayout.show()

    }

    private fun setupListMode()
    {
        binding.bottomPlayerContainer.hide()
        binding.banglaRecBtn.hide()
        binding.arabicRecBtn.hide()

        isReadingMode = false
        isNextEnabled = false
        loadingState()

        AudioManager().getInstance().releasePlayer()
        isAyatPause()
        binding.bottomPlayer.miniPlayer.playerProgress.progress = 0

        pageNo = 1
        pageItemCount = 10
        surahDetailsData.clear()
        alQuranAyatAdapter.clear()
        alQuranAyatAdapter.updateMode(isReadingMode)
        resetAyatList()



        setupActionForOtherFragment(
            action1 = R.drawable.ic_settings,
            action2 = R.drawable.ic_reading_mode,
            action3 = R.drawable.ic_download_quran,
            callback = this@AlQuranFragment,
            actionnBartitle = pageTitle,
            backEnable = true,
            actionIconColor = R.color.deen_txt_black_deep,
            view = requireView()
        )

        setCustomPageTitle(pageTitle)

        ifQuranDownloading()

        binding.ayatList.post{
            if(isSurahMode)
                loadApiData(pageNo,pageItemCount,surahID)
            else
                loadApiData(pageNo,pageItemCount, juz_number = quranJuz?.JuzId)
        }

        binding.portableZoomLayout.hide()
    }


    override fun playAudioFromUrl(url: String, position: Int) {

        lifecycleScope.launch(Dispatchers.IO)
        {
            AudioManager().getInstance().playAudioFromUrl(url,position)
        }
    }


    override fun play(position: Int) {

    }

    override fun pause(position: Int) {
        //alQuranAyatAdapter.isPause(position)
            lifecycleScope.launch(Dispatchers.IO)
            {
                AudioManager().getInstance().pauseMediaPlayer(position)
            }
    }


    override fun selectedSurah(position: Int, byService: Boolean) {

        if(position<0)
            return

        //alQuranAyatAdapter.miniPlayerCall(byService = byService)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        dialog?.dismiss()
        pageNo = 1
        isSurahMode = true
        isMiniPlayerAlreadySet = false
        surahDetailsData.clear()
        alQuranAyatAdapter.clear()

        if(surahList.isNotEmpty()) {
            surahListData = surahList[position]
            pageTitle = surahList[position].SurahName
            surahID = surahListData?.SurahId?:surahID
        }


        ifQuranDownloading()


        setupMiniPlayerData()
        setHeaderData()

        if(surahList.isNotEmpty()) {
            loadingState()
            loadApiData(pageNo,pageItemCount,surahList[position].SurahId)
        }
        else if(quranJuzList?.isNotEmpty() == true)
        {
            loadingState()
            loadApiData(pageNo,pageItemCount, juz_number = quranJuz?.JuzId)
        }

        view?.let {
            setupActionForOtherFragment(
                action1 = R.drawable.ic_settings,
                action2 = R.drawable.ic_reading_mode,
                action3 = R.drawable.ic_download_quran,
                callback = this@AlQuranFragment,
                actionnBartitle = pageTitle,
                backEnable = true,
                actionIconColor = R.color.deen_txt_black_deep,
                view = it
            )
            setCustomPageTitle(pageTitle)
        }

    }

    override fun selectedJuz(position: Int, byService: Boolean) {
        //alQuranAyatAdapter.miniPlayerCall(byService = byService)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        dialog?.dismiss()
        loadingState()
        pageNo = 1
        isSurahMode = false
        isMiniPlayerAlreadySet = false
        surahDetailsData.clear()
        alQuranAyatAdapter.clear()
        juzID = position
        quranJuz = quranJuzList?.get(position)
        pageTitle =  localContext.resources.getString(R.string.quran_para_adapter_title,quranJuzList?.get(position)?.JuzId.toString().numberLocale())


        setupActionForOtherFragment(
            action1 = R.drawable.ic_settings,
            action2 = R.drawable.ic_reading_mode,
            action3 = R.drawable.ic_download_quran,
            callback = this@AlQuranFragment,
            actionnBartitle = pageTitle,
            backEnable = true,
            actionIconColor = R.color.deen_txt_black_deep,
            view = requireView()
        )

        setCustomPageTitle(pageTitle)

        ifQuranDownloading()


        setupMiniPlayerData()
        setHeaderData()
        loadApiData(pageNo,pageItemCount, juz_number = quranJuzList?.get(position)?.JuzId)
    }

    override fun playNextAyat(position: Int) {

        binding.ayatList.post {
            if (position < totalVerseCount && !isReadingMode && position < binding.ayatList.childCount) {
                val childView = binding.ayatList.getChildAt(position)
                if (childView != null) {
                    val y: Float = binding.ayatList.y + childView.y
                    if (auto_scroll) {
                        binding.container.smoothScrollTo(0, y.toInt())
                    }
                }
            } else if (position < totalVerseCount) {
                //Log.e("playNextAyat", alQuranAyatAdapter.getTargetIndexOffset().toString())
                if (auto_scroll) {
                    binding.container.smoothScrollTo(0, alQuranAyatAdapter.getTargetIndexOffset())
                }
            } else {
                isAyatPause()
                binding.bottomPlayer.miniPlayer.playerProgress.progress = 0
                binding.bottomPlayer.largePlayer.largePlayerProgress.value = 0.0F
                alQuranAyatAdapter.miniPlayerNextCall()
            }
        }

    }

    override fun isAyatPlaying(position: Int, duration: Long?) {

        if(view == null)
            return

        if(isSurahMode) {
            val qPlayerServiceSurahID = getCurrentSurahIDFromQService()
            if (qPlayerServiceSurahID != 0 && qPlayerServiceSurahID != surahID) {
                //alQuranAyatAdapter.isMediaPause(position, true)
                return
            }
        }else{
            val qPlayerServiceJuzID = getCurrentJuzIDFromQService()
            if (qPlayerServiceJuzID != 0 && qPlayerServiceJuzID != juzID) {
                //alQuranAyatAdapter.isMediaPause(position, true)
                return
            }
        }
        //isLoadingState(false)
        //alQuranAyatAdapter.updatePlayerLoading(false)
        binding.bottomPlayer.miniPlayer.icPlayPause.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_pause_fill
            )
        )

        binding.bottomPlayer.largePlayer.icPlayBtn.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_pause_fill
            )
        )

        val progress:Double = 100.0/totalVerseCount

        var currentProgress = progress * position

        if(currentProgress <= 0.0)
            currentProgress = 0.5
        else if(currentProgress > 100.0)
            currentProgress = 100.0



        Log.e("currentProgress", duration.toString())

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer((duration?:0L).toLong(),1000) {
            override fun onTick(millisUntilFinished: Long) {

                duration?.let {
                    val progressPerSecond = progress  / TimeUnit.MILLISECONDS.toSeconds(duration.toLong())
                    currentProgress +=progressPerSecond

                    val curProgress = currentProgress.toInt()

                    if(currentProgress <= 0.0)
                        currentProgress = 0.5
                    else if(currentProgress > 100.0)
                        currentProgress = 100.0


                    binding.bottomPlayer.miniPlayer.playerProgress.progress = curProgress
                    binding.bottomPlayer.largePlayer.largePlayerProgress.value = currentProgress.toFloat()

                }
            }

            override fun onFinish() {
                countDownTimer?.cancel()
            }
        }
        countDownTimer?.start()

    }

    override fun isAyatPause(byService:Boolean) {

        if(view == null)
            return

        if(!byService)
        pauseQuran()

        countDownTimer?.cancel()
        binding.bottomPlayer.miniPlayer.icPlayPause.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_quran_play_fill
            )
        )

        binding.bottomPlayer.largePlayer.icPlayBtn.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_quran_play_fill
            )
        )

        binding.bottomPlayer.miniPlayer.icPlayPause.show()
        binding.bottomPlayer.largePlayer.icPlayBtn.show()

        binding.bottomPlayer.miniPlayer.playLoading.hide()
        binding.bottomPlayer.largePlayer.playLoading.hide()
    }

    override fun ayatFavClicked(getAyatData: Ayath, absoluteAdapterPosition: Int) {
        lifecycleScope.launch {
            alQuranViewModel.updateFavAyat(getLanguage(),getAyatData.Id,getAyatData.IsFavorite,absoluteAdapterPosition)
        }
    }

    private fun surahFavClicked(){
        lifecycleScope.launch {
            //alQuranViewModel.updateFavSurah(getLanguage(),surahID,surahListData?.IsFavorite)
        }
    }

    override fun isLoadingState(b: Boolean) {

        binding.bottomPlayer.miniPlayer.icPlayPause.visible(!b)
        binding.bottomPlayer.largePlayer.icPlayBtn.visibility = if(b) View.INVISIBLE else View.VISIBLE

        binding.bottomPlayer.miniPlayer.playLoading.visible(b)
        binding.bottomPlayer.largePlayer.playLoading.visible(b)

    }

    override fun playNextSurah(byService: Boolean) {
        if(view == null)
            return
        isNextPrevSurahCalled = true
        //surahListData?.SurahId?.let {
            if(surahID < 114)
                selectedSurah(surahID, byService)
       // }
    }

    override fun playNextJuz(byService: Boolean) {
        if(view == null)
            return
        isNextPrevSurahCalled = true
        //surahListData?.SurahId?.let {
        if(juzID < 30)
            selectedJuz(juzID, byService)
        // }
    }

    override fun playPrevSurah(byService: Boolean) {
        if(view == null)
            return
        isNextPrevSurahCalled = true
       // surahListData?.SurahId?.let {
            if(surahID > 1)
                selectedSurah(surahID-2,byService)
        //}
    }

    override fun playPrevJuz(byService: Boolean) {
        if(view == null)
            return
        isNextPrevSurahCalled = true
        // surahListData?.SurahId?.let {
        if(juzID > 1)
            selectedJuz(juzID-2,byService)
        //}
    }

    override fun tafsirBtnClicked(surahId: Int, verseId: Int, ayatArabic: String, arabicFont: Int) {
        dialog_tafsir(surahId,verseId,ayatArabic,arabicFont)
    }

    override fun action1() {
        //dialog_select_surah()
        if(isApiLoaded)
        binding.drawerLayout.openDrawer(GravityCompat.END)
    }

    override fun zoomBtnClickedADP(){
        if(isApiLoaded)
            binding.drawerLayout.openDrawer(GravityCompat.END)
    }

    override fun action2() {
        if(isReadingMode)
            setupListMode()
        else
            setupReadinMode()
    }

    override fun action3() {

        option3dotClicked(OptionList("download",0,""),null)

    }

    private fun formatNumber(number: Int): String {
        // Format the number with leading zeros
        return String.format(Locale.ENGLISH,"%03d", number)
    }

    private fun downloadOfflineQuran(fileid:String){

        // Assuming `quranDownloadService` is an instance of QuranDownloadService
        val downloadUrl = BASE_CONTENT_URL_SGP +"Content/Quran/Audio/Ayath/MishariRashidAlAfasy/Afasyzip/${fileid}.zip"
        Log.e("downloadUrl",downloadUrl)
        //val destinationFolder = File(requireContext().getExternalFilesDir(null), "quran/test")
        val destinationFolder = File(requireContext().filesDir, "quran")
        val intent = Intent(requireContext(), QuranDownloadService::class.java)

        intent.putExtra("filename", surahName)
        intent.putExtra("filetitle", surahName)
        intent.putExtra("iszip", true)
        intent.putExtra("downloadUrl", downloadUrl)
        intent.putExtra("destinationFolder", destinationFolder.absolutePath)

        requireActivity().startService(intent)
    }

    fun getRunningDownloadList() = QuranDownloadService.downloadFileList

    override fun playerCommonListSelected(
        data: PlayerCommonSelectionData,
        adapter: PlayerCommonSelectionList
    ) {

        if(this::enPlayerCommonSelectionList.isInitialized && adapter == enPlayerCommonSelectionList) {
            lifecycleScope.launch {
                playerControlViewModel.updateTranslator(data.Id, AlQuranSetting_en_translator)
            }
        }
        else if(this::bnPlayerCommonSelectionList.isInitialized && adapter == bnPlayerCommonSelectionList)
        {
            lifecycleScope.launch {

                playerControlViewModel.updateTranslator(data.Id, AlQuranSetting_bn_translator)
            }
        }

        else if(this::playerCommonSelectionList.isInitialized && adapter == playerCommonSelectionList) {

            lifecycleScope.launch {
                playerControlViewModel.updateQari(data.Id)
            }
        }

        else if(this::tafsirCommonSelectionList.isInitialized && adapter == tafsirCommonSelectionList)
        {
            lifecycleScope.launch {
                playerControlViewModel.updateTafsir(
                   data.Id
                )
            }
        }

        else if(this::arabicFontCommonSelectionList.isInitialized && adapter == arabicFontCommonSelectionList)
        {
            lifecycleScope.launch {
                playerControlViewModel.updateArabicFont(
                    data.Id
                )
            }
        }

        dialog?.dismiss()
    }

    //pinch to zoom
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private val zoomLevels = listOf(1.0f, 1.5f, 2.0f, 2.5f, 3.0f)
        private var currentZoomLevelIndex = 0

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor

            // Adjust the scaleFactor to predefined zoom levels
            if (detector.scaleFactor > 1.0f) {
                // Zoom in
                currentZoomLevelIndex = min(currentZoomLevelIndex + 1, zoomLevels.size - 1)
                theme_font_size = (theme_font_size + 20F).coerceAtMost(100F)
            } else {
                // Zoom out
                currentZoomLevelIndex = max(currentZoomLevelIndex - 1, 0)
                theme_font_size = (theme_font_size - 20F).coerceAtLeast(0F)
            }

            scaleFactor = zoomLevels[currentZoomLevelIndex]

            update_theme_font_size(theme_font_size, isThemeFont = true)
            updateAyatAdapterVisibleItems()

            isScrolling = false

            return true
        }
    }

    override fun startPlayingQuran(data: ArrayList<Ayath>, pos: Int)
    {
        Log.e("startPlayingQuran","OK")
        playQuran(
            data = data, pos = pos,
            surahList = surahList,
            surahID = surahID,
            qarisData = qarisData,
            totalVerseCount = totalVerseCount,
            pageNo = pageNo,
            selectedQari = alQuranAyatAdapter.getSelectedQari(),
            isSurahMode = isSurahMode,
            quranJuzList = quranJuzList,
            quranJuz = quranJuz
        )

    }

    override fun setAdapterCallback(viewHolder: AlQuranAyatAdapter.ViewHolder)
    {
        setAdapterCallbackQuranPlayer(viewHolder)
    }

    override fun customShareAyat(enText: String, bnText: String, arText: String, verseKey: String) {

        val bundle = Bundle()
        bundle.putString("enText",enText)
        bundle.putString("bnText",bnText)
        bundle.putString("arText",arText)
        bundle.putString("title",pageTitle)
        bundle.putString("footerText","$pageTitle ${verseKey.numberLocale()}")
        //bundle.putString("customShareText","পবিত্র কুরআন তিলাওয়াত করুন  https://deenislamic.com/app/quran?id=${surahID-1}")
        bundle.putString("customShareText","")

        gotoFrag(R.id.action_global_shareFragment,bundle)
    }

    private fun ifQuranDownloading(){

        binding.bottomPlayer.largePlayer.icDownload.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.deen_txt_black_deep
            )
        )

        binding.actionbar.action3.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.deen_txt_black_deep
            )
        )

        binding.bottomPlayer.largePlayer.icDownload.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_download_quran))
        binding.actionbar.action3.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_download_quran))

        val downloadList = getRunningDownloadList()

            val isFilenameExist = downloadList.containsKey(pageTitle)

            if (isFilenameExist) {
                binding.bottomPlayer.largePlayer.icDownload.invisible()
                binding.bottomPlayer.largePlayer.action3Progress.show()
                binding.bottomPlayer.largePlayer.action3Progress.progress =
                    downloadList[surahName] ?: 10

                binding.actionbar.action3.invisible()
                binding.actionbar.action3Progress.show()
                binding.actionbar.action3Progress.progress =
                    downloadList[surahName] ?: 10

            } else {

                binding.bottomPlayer.largePlayer.icDownload.visible(isSurahMode)
                binding.bottomPlayer.largePlayer.action3Progress.hide()

                binding.actionbar.action3.visible(isSurahMode)
                binding.actionbar.action3Progress.hide()

                val fileName = "surahinfo.json" // Replace with the actual file name you want to check

                val filePath = File(requireContext().filesDir, "quran/${formatNumber(surahID)}/$fileName")


                if (filePath.exists()) {
                    binding.bottomPlayer.largePlayer.icDownload.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_checked_circle))
                    binding.actionbar.action3.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_checked_circle))

                    binding.bottomPlayer.largePlayer.icDownload.isClickable = false
                    binding.actionbar.action3.isClickable = false

                    binding.bottomPlayer.largePlayer.icDownload.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.deen_primary
                        )
                    )
                    binding.actionbar.action3.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.deen_primary
                        )
                    )

                } else {
                    binding.bottomPlayer.largePlayer.icDownload.isClickable = true
                    binding.actionbar.action3.isClickable = true
                }
            }

    }

    override fun updateDownloadProgress(
        filenameByUser: String?,
        progress: Int,
        isComplete: Boolean,
        notificationId: Int,
        isCancelled: Boolean
    ) {

        if (isAdded && !isHidden) {

            lifecycleScope.launch(Dispatchers.Main) {
                downloadFilenameByUser = filenameByUser.toString()

                if (filenameByUser != null && filenameByUser == surahName) {

                    binding.bottomPlayer.largePlayer.icDownload.invisible()
                    binding.bottomPlayer.largePlayer.action3Progress.show()
                    binding.bottomPlayer.largePlayer.action3Progress.progress = progress

                    binding.actionbar.action3.invisible()
                    binding.actionbar.action3Progress.show()
                    binding.actionbar.action3Progress.progress = progress


                    if (isComplete) {
                        ifQuranDownloading()
                    }

                }
            }

        }
    }

    override fun clickBtn1() {
        customAlertDialog.dismissDialog()
    }

    override fun clickBtn2() {
        if(surahID>0)
            downloadOfflineQuran(formatNumber(surahID))
        customAlertDialog.dismissDialog()
    }

    private fun setCustomPageTitle(surahName:String){

        val drawable: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_dropdown)

        // Create a SpannableStringBuilder to append the text and drawable
        val builder = SpannableStringBuilder("$surahName ") // Replace "Your text here" with your actual text

        val space = " "
        builder.append(space)

        drawable?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)
            builder.setSpan(imageSpan, builder.length - space.length, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.actionbar.title.text = builder

    }


}