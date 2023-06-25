package com.deenislam.sdk.views.quran

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.deenislam.sdk.databinding.FragmentAlQuranBinding
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.libs.media3.APAdapterCallback
import com.deenislam.sdk.service.libs.media3.AudioManager
import com.deenislam.sdk.service.libs.media3.AudioPlayerCallback
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.quran.AlQuranResource
import com.deenislam.sdk.service.network.response.quran.surah_details.Ayath
import com.deenislam.sdk.service.repository.quran.AlQuranRepository
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.quran.AlQuranViewModel
import com.deenislam.sdk.views.adapters.quran.AlQuranAyatAdapter
import com.deenislam.sdk.views.base.BaseFragment
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


internal class AlQuranFragment : BaseFragment<FragmentAlQuranBinding>(FragmentAlQuranBinding::inflate),
    AudioPlayerCallback, APAdapterCallback {

    private val alQuranAyatAdapter by lazy { AlQuranAyatAdapter(this@AlQuranFragment) }
    private lateinit var alQuranViewModel:AlQuranViewModel
    private val surahDetailsData :ArrayList<Ayath> = arrayListOf()
    private val args:AlQuranFragmentArgs by navArgs()

    var isHeaderVisible = true
    var previousScrollY = 0
    var isScrollAtEnd = false
    private var pageNo:Int = 1
    private var pageItemCount:Int = 5
    private var nextPageAPICalled:Boolean = false
    private var IsNextEnabled  = true



    override fun OnCreate() {
        enterTransition = MaterialFadeThrough()
        returnTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()

        val repository = AlQuranRepository(deenService = NetworkProvider().getInstance().provideDeenService())
        alQuranViewModel = AlQuranViewModel(repository)
        Log.e("Alquran",args.surah.Name)
    }


    override fun ON_CREATE_VIEW(root: View) {
        super.ON_CREATE_VIEW(root)
        setupActionForOtherFragment(0,0,null,args.surah.Name,true,root)
    }

    override fun onResume() {
        super.onResume()
        loadApiData(pageNo,pageItemCount)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AudioManager().getInstance().setupAdapterResponseCallback(this@AlQuranFragment)

        // set header data

        binding.surahOrigin.text = args.surah.Origin
        binding.surahName.text = args.surah.Name
        binding.nameMeaning.text = args.surah.NameMeaning
        binding.ayatTotal.text = "Ayahs: ${args.surah.TotalAyat}"



        ViewCompat.setTranslationZ(binding.progressLayout.root, 10F)
        ViewCompat.setTranslationZ(binding.noInternetLayout.root, 10F)
        ViewCompat.setTranslationZ(binding.nodataLayout.root, 10F)

        //init observer
        initObserver()

        //loading start
        loadingState()

        //click retry button for get api data again
        binding.noInternetLayout.noInternetRetry.setOnClickListener {
            loadApiData(pageNo,pageItemCount)
        }

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


        binding.container.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = binding.container.scrollY

            if (scrollY < binding.headerLayout.height + 100 && !isHeaderVisible) {
                binding.headerLayout.translationY = -scrollY.toFloat()
            }

            if (scrollY > previousScrollY && isHeaderVisible && scrollY > binding.headerLayout.height + 100) {
                binding.headerLayout.animate()
                    .withEndAction {
                        binding.headerLayout.hide()
                    }
                    .translationY(-binding.headerLayout.height.toFloat())
                    .duration = 200
                isHeaderVisible = false
            } else if (scrollY < previousScrollY && !isHeaderVisible) {
                binding.headerLayout.show()
                binding.headerLayout.animate().translationY(0f).duration = 200
                isHeaderVisible = true
            }

            previousScrollY = scrollY
        }


        // List scroll listner for pagging

        binding.container.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = binding.container.scrollY
            val totalContentHeight = binding.container.getChildAt(0)?.let { it.measuredHeight - binding.container.height + 20 }

            if (scrollY >= (totalContentHeight ?: 0) && !isScrollAtEnd) {
                isScrollAtEnd = true
                // NestedScrollView has scrolled to the end
                if(IsNextEnabled) {
                    //binding.container.setScrollingEnabled(false)
                    nextPageAPICalled = true
                    fetchNextPageData()
                    Log.e("ALQURAN_SCROLL","END")
                }
                else
                    binding.lastItemLoading.root.hide()
                // Implement your desired actions here
            }

        }
    }


    private fun initObserver()
    {
        alQuranViewModel.surahDetails.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is AlQuranResource.surahDetails ->
                {
                    it.data.AyathList.forEach {
                        ayat->
                        val check = surahDetailsData.indexOfFirst { it.AyatOrder!= ayat.AyatOrder}
                        if(check <=0)
                            surahDetailsData.add(ayat)

                    }
                    //surahDetailsData.addAll(it.data)
                    IsNextEnabled = it.data.IsNextEnabled
                    viewState()
                }
                is CommonResource.API_CALL_FAILED -> noInternetState()
                is CommonResource.EMPTY -> emptyState()
            }
        }
    }

    private fun fetchNextPageData() {
        val from = alQuranAyatAdapter.itemCount
        val to = surahDetailsData.size

        if (args.surah.TotalAyat != alQuranAyatAdapter.itemCount && nextPageAPICalled) {
            lifecycleScope.launch {
                pageNo++
                loadApiData(pageNo,pageItemCount)
                nextPageAPICalled = false
            }
        }
        else
        {
             if (from == 0 && args.surah.TotalAyat <= pageItemCount ) {
            alQuranAyatAdapter.update(ArrayList(surahDetailsData))
            //alQuranAyatAdapter.notifyItemRangeInserted(from,to)
            }
            else if(alQuranAyatAdapter.itemCount != surahDetailsData.size)
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

    private fun loadApiData(page:Int,itemCount:Int)
    {
        lifecycleScope.launch {
            alQuranViewModel.getSurahDetails(args.surah.SurahNo, "en",page,itemCount)
        }
    }
    private fun viewState()
    {
        fetchNextPageData()
        binding.ayatList.post {
            binding.progressLayout.root.hide()
            binding.nodataLayout.root.hide()
            binding.noInternetLayout.root.hide()
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
        alQuranAyatAdapter.isPause(position)
            lifecycleScope.launch(Dispatchers.IO)
            {
                AudioManager().getInstance().pauseMediaPlayer(position)
            }
    }

    override fun isPlaying(position: Int) {
        alQuranAyatAdapter.isPlaying(position)
    }

    override fun isPause(position: Int) {
        binding.ayatList.post {
            alQuranAyatAdapter.isPause(position)
        }
    }

    override fun isStop(position: Int) {
        alQuranAyatAdapter.isPause(position)
    }

}