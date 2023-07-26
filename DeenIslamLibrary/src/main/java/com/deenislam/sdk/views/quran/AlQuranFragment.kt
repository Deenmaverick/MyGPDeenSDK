package com.deenislam.sdk.views.quran

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.R
import com.deenislam.sdk.databinding.FragmentAlQuranBinding
import com.deenislam.sdk.service.callback.AlQuranAyatCallback
import com.deenislam.sdk.service.di.DatabaseProvider
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.libs.media3.AudioManager
import com.deenislam.sdk.service.libs.media3.AudioPlayerCallback
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.quran.AlQuranResource
import com.deenislam.sdk.service.models.quran.quranplayer.ThemeResource
import com.deenislam.sdk.service.network.response.quran.juz.Juz
import com.deenislam.sdk.service.network.response.quran.juz.JuzResponse
import com.deenislam.sdk.service.network.response.quran.qurannew.surah.Chapter
import com.deenislam.sdk.service.repository.quran.AlQuranRepository
import com.deenislam.sdk.service.repository.quran.quranplayer.PlayerControlRepository
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.reduceDragSensitivity
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.quran.AlQuranViewModel
import com.deenislam.sdk.viewmodels.quran.quranplayer.PlayerControlViewModel
import com.deenislam.sdk.views.adapters.MainViewPagerAdapter
import com.deenislam.sdk.views.adapters.quran.AlQuranAyatAdapter
import com.deenislam.sdk.views.adapters.quran.SelectSurahAdapter
import com.deenislam.sdk.views.adapters.quran.SelectSurahCallback
import com.deenislam.sdk.views.base.BaseFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.deenislam.sdk.views.quran.quranplayer.PlayerAudioFragment
import com.deenislam.sdk.views.quran.quranplayer.PlayerThemeFragment
import com.deenislam.sdk.views.quran.quranplayer.PlayerTranslationFragment
import com.deenislam.service.network.response.quran.verses.Verse
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

internal class AlQuranFragment : BaseFragment<FragmentAlQuranBinding>(FragmentAlQuranBinding::inflate),
    AudioPlayerCallback, SelectSurahCallback, AlQuranAyatCallback, otherFagmentActionCallback {

    private lateinit var alQuranAyatAdapter:AlQuranAyatAdapter

    private lateinit var alQuranViewModel:AlQuranViewModel
    private lateinit var playerControlViewModel: PlayerControlViewModel

    private val surahDetailsData :ArrayList<Verse> = arrayListOf()

    // nav args
    private val args:AlQuranFragmentArgs by navArgs()
    private var surahListData: Chapter? =null
    private var quranJuz: Juz? =null
    private var quranJuzList: JuzResponse? =null

    private var dialog: Dialog? = null
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : View
    private lateinit var selectSurahAdapter: SelectSurahAdapter

    private var countDownTimer: CountDownTimer?=null

    var isHeaderVisible = true
    var previousScrollY = 0
    var isScrollAtEnd = false
    private var pageNo:Int = 1
    private var pageItemCount:Int = 10
    private var nextPageAPICalled:Boolean = false
    private var isNextEnabled  = true
    private var isReadingMode:Boolean = false

    // player control
    private lateinit var playerControlPages: ArrayList<Fragment>
    private lateinit var playerControlViewPageAdapter: MainViewPagerAdapter
    private var playerControlPagePos:Int = 0
    private var auto_scroll = true

    //page setting
    private var pageTitle = ""
    private var totalVerseCount = 0
    private var isSurahMode:Boolean = true

    override fun OnCreate() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        surahListData = args.surah
        quranJuz = args.juz
        quranJuzList = args.juzList
        pageTitle = surahListData?.name_simple?:"Juz (Para) ${quranJuz?.juz_number}"
        totalVerseCount = surahListData?.verses_count?:quranJuz?.verses_count?:0

        if(surahListData!=null)
            isSurahMode = true
        else if(quranJuz!=null)
            isSurahMode = false

        val repository = AlQuranRepository(
            deenService = NetworkProvider().getInstance().provideDeenService(),
            quranService = NetworkProvider().getInstance().provideQuranService()
        )
        alQuranViewModel = AlQuranViewModel(repository)
        val repository1 = PlayerControlRepository(DatabaseProvider().getInstance().providePlayerSettingDao())
        playerControlViewModel = PlayerControlViewModel(repository1)

    }




    override fun ON_CREATE_VIEW(root: View) {
        super.ON_CREATE_VIEW(root)
        setupActionForOtherFragment(R.drawable.ic_search,R.drawable.ic_reading,this@AlQuranFragment,pageTitle,true,root)
    }

    override fun onResume() {
        super.onResume()
        loadApiData(pageNo,pageItemCount)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // set header data
        setHeaderData()

        // setup mini player data
        setupMiniPlayerData()

        playerControlPages = arrayListOf(
            PlayerThemeFragment(),
            PlayerAudioFragment(),
            PlayerTranslationFragment()
        )

        playerControlViewPageAdapter = MainViewPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = lifecycle,
            playerControlPages
        )

        binding.bottomPlayer.playerViewPager.apply {
            adapter = playerControlViewPageAdapter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                isNestedScrollingEnabled = false
            }
            isUserInputEnabled = true
            overScrollMode = View.OVER_SCROLL_NEVER
            reduceDragSensitivity(2)
            offscreenPageLimit = 1
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

        binding.bottomPlayer.playerViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                playerControlPagePos = position

                clearPlayerControlBtn()

                when(position)
                {
                    0-> {
                        binding.bottomPlayer.largePlayer.themeBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.primary))
                        binding.bottomPlayer.largePlayer.themeBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))
                    }
                    1->  {
                        binding.bottomPlayer.largePlayer.audioBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.primary))
                        binding.bottomPlayer.largePlayer.audioBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))

                    }
                    2->  {
                        binding.bottomPlayer.largePlayer.transalationBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.primary))
                        binding.bottomPlayer.largePlayer.transalationBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))

                    }

                }

            }
        })


        ViewCompat.setTranslationZ(binding.progressLayout.root, 10F)
        ViewCompat.setTranslationZ(binding.noInternetLayout.root, 10F)
        ViewCompat.setTranslationZ(binding.nodataLayout.root, 10F)
        ViewCompat.setTranslationZ(binding.dimView, 15F)
        ViewCompat.setTranslationZ(binding.bottomPlayerContainer, 20F)


        //init observer
        initObserver()

        //loading start
        loadingState()

        //click retry button for get api data again
        binding.noInternetLayout.noInternetRetry.setOnClickListener {
            loadApiData(pageNo,pageItemCount)
        }

        // search back button


        alQuranAyatAdapter = AlQuranAyatAdapter(this@AlQuranFragment)

        binding.ayatList.apply {
            adapter = alQuranAyatAdapter
            overScrollMode = View.OVER_SCROLL_NEVER

            post {
                binding.container.setPadding(
                    binding.container.paddingLeft,
                    binding.headerLayout.height + binding.actionbar.root.height - 20,
                    binding.container.paddingRight,
                    binding.container.paddingBottom
                )
            }
        }


        binding.container.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                if (scrollY < binding.headerLayout.height + 100 && !isHeaderVisible)
                    binding.headerLayout.translationY = -scrollY.toFloat()

                if (scrollY > oldScrollY && isHeaderVisible && scrollY > binding.headerLayout.height + 100 && isSurahMode) {
                    binding.headerLayout.animate()
                        .withEndAction {
                            binding.headerLayout.hide()
                        }
                        .translationY(-binding.headerLayout.height.toFloat()).duration = 200
                    isHeaderVisible = false
                } else if (scrollY < oldScrollY && !isHeaderVisible && isSurahMode) {
                    binding.headerLayout.show()
                    binding.headerLayout.animate().translationY(0f).duration = 200
                    isHeaderVisible = true
                }

                previousScrollY = scrollY
            })


        // List scroll listner for pagging

        binding.container.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = binding.container.scrollY
            val totalContentHeight = binding.container.getChildAt(0)?.let { it.measuredHeight - binding.container.height + 20 }

            if (scrollY >= (totalContentHeight ?: 0) && !isScrollAtEnd) {
                isScrollAtEnd = true
                // NestedScrollView has scrolled to the end
                if(isNextEnabled && alQuranAyatAdapter.itemCount>0) {
                    //binding.container.setScrollingEnabled(false)
                    nextPageAPICalled = true
                    fetchNextPageData()
                    Log.e("ALQURAN_SCROLL","END")
                    morePageBottomLoading(true)
                }
                else
                    morePageBottomLoading(false)
                // Implement your desired actions here
            }

        }

        // bottom sheet music player
        var previousSlideOffset = 0f
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomPlayer.root)

        binding.dimView.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.bottomPlayer.miniPlayer.root.setOnClickListener {
            binding.bottomPlayer.miniPlayer.root.hide()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                /*     if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                         // The bottom sheet is being expanded
                         binding.dimView.show()
                     }
                     else
                         binding.dimView.hide()*/

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Not used in this example
                Log.e("bottomSheetBehavior",slideOffset.toString())
                if (slideOffset < previousSlideOffset && slideOffset==0F) {
                    // Bottom sheet is being dragged up
                    // Perform your desired actions here
                    binding.dimView.hide()
                    binding.bottomPlayer.miniPlayer.root.show()
                } else if (slideOffset > previousSlideOffset) {
                    // Bottom sheet is being dragged down
                    // Perform your desired actions here
                    binding.dimView.show()
                    binding.bottomPlayer.miniPlayer.root.hide()
                } else if (slideOffset == 0f) {
                    // Bottom sheet is released
                    // Perform your desired actions here
                }
                previousSlideOffset = slideOffset
            }
        })

    }

    private fun clearPlayerControlBtn()
    {
        binding.bottomPlayer.largePlayer.themeBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))
        binding.bottomPlayer.largePlayer.themeBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.background))

        binding.bottomPlayer.largePlayer.audioBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))
        binding.bottomPlayer.largePlayer.audioBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.background))

        binding.bottomPlayer.largePlayer.transalationBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))
        binding.bottomPlayer.largePlayer.transalationBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.background))

    }

    private fun morePageBottomLoading(bol:Boolean)
    {
        binding.lastItemLoading.root.visible(bol && !isReadingMode)
        binding.lastItemLoadingProgress.visible(bol && isReadingMode)
    }

    private fun setHeaderData()
    {
        // set header data
        if(!isSurahMode)
            binding.headerLayout.hide()
        binding.surahOrigin.text = surahListData?.revelation_place
        binding.surahName.text = pageTitle
        binding.nameMeaning.text = surahListData?.translated_name?.name
        binding.ayatTotal.text = "Ayahs: ${totalVerseCount}"
    }

    private fun setupMiniPlayerData()
    {
        totalVerseCount = surahListData?.verses_count?:quranJuz?.verses_count?:0

        binding.bottomPlayer.miniPlayer.surahTitile.text = pageTitle
        binding.bottomPlayer.miniPlayer.surahAyat.text = "${totalVerseCount} Ayahs"

        binding.bottomPlayer.largePlayer.surahTitile.text = pageTitle
        binding.bottomPlayer.largePlayer.surahAyat.text = "${totalVerseCount} Ayahs"


        binding.bottomPlayer.largePlayer.playerProgress.setOnTouchListener { _, _ -> true }

        binding.bottomPlayer.miniPlayer.icPlayPause.setOnClickListener {
            alQuranAyatAdapter.miniPlayerCall()
        }

        binding.bottomPlayer.largePlayer.icPlayBtn.setOnClickListener {
            alQuranAyatAdapter.miniPlayerCall()
        }

        binding.bottomPlayer.largePlayer.icPrev.setOnClickListener {
            alQuranAyatAdapter.miniPlayerPrevCall()
        }

        binding.bottomPlayer.largePlayer.icNext.setOnClickListener {
            alQuranAyatAdapter.miniPlayerNextCall()
        }

    }



    private fun initObserver()
    {
        alQuranViewModel.surahDetails.observe(viewLifecycleOwner)
        { verses ->
            when(verses)
            {
                is AlQuranResource.versesByChapter ->
                {
                    verses.data.forEach {
                            ayat->
                        val check = surahDetailsData.indexOfFirst { it.id!= ayat.id}
                        if(check <=0)
                            surahDetailsData.add(ayat)

                    }

                    //surahDetailsData.addAll(it.data)
                    isNextEnabled = verses.nextPage!=null
                    viewState()
                }
                is CommonResource.API_CALL_FAILED -> noInternetState()
                is CommonResource.EMPTY -> emptyState()
            }
        }

        playerControlViewModel.themeLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is ThemeResource.playerSettings ->
                {
                    it.setting?.theme_font_size?.let { it1 -> update_theme_font_size(it1, isThemeFont = true) }
                    it.setting?.translation_font_size?.let { it1 -> update_theme_font_size(it1, isTranslationFont = true) }
                    it.setting?.transliteration?.let { it1 -> alQuranAyatAdapter.update_transliteration(it1) }
                    it.setting?.auto_scroll?.let { it1 -> auto_scroll = it1 }
                    it.setting?.auto_play_next?.let { it1 -> alQuranAyatAdapter.update_auto_play_next(it1) }
                }

            }
        }
    }

    private fun update_theme_font_size(fontsize: Float,isTranslationFont:Boolean=false,isThemeFont:Boolean=false)
    {
        when(fontsize)
        {
            0F ->
            {
                if(isThemeFont)
                    alQuranAyatAdapter.update_theme_font_size(24F)

                if(isTranslationFont)
                    alQuranAyatAdapter.update_translation_font_size(14F)
            }

            20F ->
            {
                if(isThemeFont)
                    alQuranAyatAdapter.update_theme_font_size(26F)

                if(isTranslationFont)
                    alQuranAyatAdapter.update_translation_font_size(16F)
            }

            40F ->
            {
                if(isThemeFont)
                    alQuranAyatAdapter.update_theme_font_size(28F)

                if(isTranslationFont)
                    alQuranAyatAdapter.update_translation_font_size(18F)
            }

            60F ->
            {
                if(isThemeFont)
                    alQuranAyatAdapter.update_theme_font_size(30F)

                if(isTranslationFont)
                    alQuranAyatAdapter.update_translation_font_size(20F)
            }

            80F->
            {
                if(isThemeFont)
                    alQuranAyatAdapter.update_theme_font_size(32F)

                if(isTranslationFont)
                    alQuranAyatAdapter.update_translation_font_size(22F)
            }

            100F ->
            {
                if(isThemeFont)
                    alQuranAyatAdapter.update_theme_font_size(34F)

                if(isTranslationFont)
                    alQuranAyatAdapter.update_translation_font_size(24F)
            }
        }
    }

    private fun fetchNextPageData() {
        val from = alQuranAyatAdapter.getDataSize()
        val to = surahDetailsData.size

        if (totalVerseCount != alQuranAyatAdapter.getDataSize() && nextPageAPICalled) {
            lifecycleScope.launch {
                pageNo++
                loadApiData(pageNo,pageItemCount)
                nextPageAPICalled = false
            }
        }
        else
        {
            if (from == 0 && totalVerseCount <= pageItemCount) {
                alQuranAyatAdapter.update(ArrayList(surahDetailsData))
                //alQuranAyatAdapter.notifyItemRangeInserted(from,to)
            }
            else if(alQuranAyatAdapter.getDataSize() != surahDetailsData.size)
            {
                if(from<to) {
                    binding.container.setOnTouchListener { _, _ -> true }
                    binding.ayatList.post {
                        alQuranAyatAdapter.update(ArrayList(surahDetailsData.subList(from, to)))
                        binding.container.setOnTouchListener(null)
                    }
                    //alQuranAyatAdapter.notifyItemRangeInserted(from, to)
                }
            }


            binding.ayatList.post {
                // binding.container.setScrollingEnabled(true)
                isScrollAtEnd = false
                nextPageAPICalled = false
            }
        }
    }

    private fun loadApiData(page:Int,itemCount:Int,surahNo:Int? = surahListData?.id,juz_number:Int? = quranJuz?.juz_number)
    {
        /* lifecycleScope.launch {
             alQuranViewModel.getSurahDetails(surahNo, "en",page,itemCount)
         }*/

        lifecycleScope.launch {
            if(surahNo!=null)
                alQuranViewModel.getVersesByChapter("en",page,itemCount,surahNo)
            else if(juz_number!=null)
                alQuranViewModel.getVersesByJuz("en",page,itemCount,juz_number)
            playerControlViewModel.getSetting()
        }
    }
    private fun viewState()
    {
        fetchNextPageData()
        binding.ayatList.post {
            binding.progressLayout.root.hide()
            binding.nodataLayout.root.hide()
            binding.noInternetLayout.root.hide()
            morePageBottomLoading(false)
        }
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
    }

    private fun loadingState()
    {
        binding.progressLayout.root.visible(true)
        binding.nodataLayout.root.visible(false)
        binding.noInternetLayout.root.visible(false)
    }
    private fun dialog_select_surah()
    {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
        customAlertDialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_surah_list, null, false)

        // Initialize and assign variable
        val searchLayout = customAlertDialogView.findViewById<TextInputLayout>(R.id.searchLayout)
        val search_surah = customAlertDialogView.findViewById<TextInputEditText>(R.id.search_surah)
        val surahList = customAlertDialogView.findViewById<RecyclerView>(R.id.surahList)
        val dismissBtn = customAlertDialogView.findViewById<ImageButton>(R.id.closeBtn)
        val title: AppCompatTextView = customAlertDialogView.findViewById(R.id.title)

        if (isSurahMode)
            title.text = "Select Surah"
        else
            title.text = "Select Juz (Para)"

        searchLayout.visible(isSurahMode)

        selectSurahAdapter = SelectSurahAdapter(
            surahList = args.suraList.chapters,
            juzList = quranJuzList?.juzs,
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

    private fun resetAyatList()
    {
        binding.ayatList.apply {
            adapter = alQuranAyatAdapter

            if(isReadingMode) {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            else {
                layoutManager =
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
        AudioManager().getInstance().releasePlayer()
        isAyatPause()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.bottomPlayer.miniPlayer.playerProgress.setProgress(0, false)
        } else {
            @Suppress("DEPRECATION")
            binding.bottomPlayer.miniPlayer.playerProgress.progress = 0
        }
        isReadingMode = true
        loadingState()
        pageNo = 1
        pageItemCount = 30
        surahDetailsData.clear()
        alQuranAyatAdapter.clear()
        alQuranAyatAdapter = AlQuranAyatAdapter(this@AlQuranFragment,isReadingMode)
        resetAyatList()
        setupActionForOtherFragment(R.drawable.ic_search,R.drawable.ic_list_view,this@AlQuranFragment,pageTitle,true,requireView())

        if(isSurahMode)
            loadApiData(pageNo,pageItemCount,surahListData?.id)
        else
            loadApiData(pageNo,pageItemCount, juz_number = quranJuz?.juz_number)
    }

    private fun setupListMode()
    {
        isReadingMode = false
        loadingState()
        pageNo = 1
        pageItemCount = 10
        surahDetailsData.clear()
        alQuranAyatAdapter.clear()
        alQuranAyatAdapter = AlQuranAyatAdapter(this@AlQuranFragment)
        resetAyatList()
        setupActionForOtherFragment(R.drawable.ic_search,R.drawable.ic_reading,this@AlQuranFragment,pageTitle,true,requireView())

        if(isSurahMode)
            loadApiData(pageNo,pageItemCount,surahListData?.id)
        else
            loadApiData(pageNo,pageItemCount, juz_number = quranJuz?.juz_number)

    }

    override fun onBackPress() {
        lifecycleScope.launch(Dispatchers.IO)
        {
            AudioManager().getInstance().releasePlayer()
        }.apply {
            super.onBackPress()
            setupOtherFragment(false)
        }
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



    override fun selectedSurah(position: Int) {
        dialog?.dismiss()
        loadingState()
        pageNo = 1
        isSurahMode = true
        surahDetailsData.clear()
        alQuranAyatAdapter.clear()
        surahListData = args.suraList.chapters[position]
        pageTitle = args.suraList.chapters[position].name_simple
        setupActionForOtherFragment(R.drawable.ic_search,R.drawable.ic_reading,this@AlQuranFragment,pageTitle,true,requireView())
        setupMiniPlayerData()
        setHeaderData()
        loadApiData(pageNo,pageItemCount,args.suraList.chapters[position].id)
    }

    override fun selectedJuz(position: Int) {
        dialog?.dismiss()
        loadingState()
        pageNo = 1
        isSurahMode = false
        surahDetailsData.clear()
        alQuranAyatAdapter.clear()
        quranJuz = quranJuzList?.juzs?.get(position)
        pageTitle = "Juz (Para) ${quranJuzList?.juzs?.get(position)?.juz_number}"
        setupActionForOtherFragment(R.drawable.ic_search,R.drawable.ic_reading,this@AlQuranFragment,pageTitle,true,requireView())
        setupMiniPlayerData()
        setHeaderData()
        loadApiData(pageNo,pageItemCount, juz_number = quranJuzList?.juzs?.get(position)?.juz_number)
    }

    override fun playNextAyat(position: Int) {

        if(position<totalVerseCount && !isReadingMode) {
            binding.ayatList.post {
                val y: Float =
                    binding.ayatList.y + binding.ayatList.getChildAt(position).y

                if(auto_scroll)
                    binding.container.smoothScrollTo(0, y.toInt())
            }
            // binding.ayatList.smoothScrollToPosition(position)
        }
        else if(position<totalVerseCount && isReadingMode)
        {
            Log.e("playNextAyat",alQuranAyatAdapter.getTargetIndexOffset().toString())
            if(auto_scroll)
                binding.container.smoothScrollTo(0, alQuranAyatAdapter.getTargetIndexOffset())
        }
        else {
            isAyatPause()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.bottomPlayer.miniPlayer.playerProgress.setProgress(0, false)
            } else {
                @Suppress("DEPRECATION")
                binding.bottomPlayer.miniPlayer.playerProgress.progress = 0
            }

        }
    }

    override fun isAyatPlaying(position: Int, duration: Int?) {
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

        if(currentProgress == 0.0)
            currentProgress = 0.5


        Log.e("currentProgress", duration.toString())

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer((duration?:0L).toLong(),1000) {
            override fun onTick(millisUntilFinished: Long) {

                duration?.let {
                    val progressPerSecond = progress  / TimeUnit.MILLISECONDS.toSeconds(duration.toLong())
                    currentProgress +=progressPerSecond

                    val curProgress = currentProgress.toInt()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.bottomPlayer.miniPlayer.playerProgress.setProgress(curProgress, false)
                    } else {
                        @Suppress("DEPRECATION")
                        binding.bottomPlayer.miniPlayer.playerProgress.progress = curProgress
                    }
                    binding.bottomPlayer.largePlayer.playerProgress.value = currentProgress.toFloat()

                }
            }

            override fun onFinish() {
                countDownTimer?.cancel()
            }
        }
        countDownTimer?.start()

    }

    override fun isAyatPause() {
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
    }

    override fun isLoadingState(b: Boolean) {

        binding.bottomPlayer.miniPlayer.icPlayPause.visible(!b)
        binding.bottomPlayer.largePlayer.icPlayBtn.visibility = if(b) View.INVISIBLE else View.VISIBLE

        binding.bottomPlayer.miniPlayer.playLoading.visible(b)
        binding.bottomPlayer.largePlayer.playLoading.visible(b)

    }

    override fun action1() {
        dialog_select_surah()
    }

    override fun action2() {
        if(isReadingMode)
            setupListMode()
        else
            setupReadinMode()
    }

}