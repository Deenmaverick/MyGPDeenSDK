package com.deenislam.sdk.views.hajjandumrah

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.common.BasicCardListCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.HajjAndUmrahResource
import com.deenislam.sdk.service.models.MenuModel
import com.deenislam.sdk.service.models.SubCatCardListResource
import com.deenislam.sdk.service.network.response.common.subcatcardlist.Data
import com.deenislam.sdk.service.repository.HajjAndUmrahRepository
import com.deenislam.sdk.service.repository.SubCatCardListRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.HAJJ_GUIDE
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.utils.setActiveState
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.HajjAndUmrahViewModel
import com.deenislam.sdk.viewmodels.SubCatCardListViewModel
import com.deenislam.sdk.views.adapters.common.gridmenu.MenuAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch


internal class HajjGuideFragment : BaseRegularFragment(), BasicCardListCallback {

    private lateinit var contentCard:MaterialCardView
    private lateinit var contentLayout:ConstraintLayout
    private lateinit var icRight:AppCompatImageView
    private lateinit var stepList:RecyclerView
    private lateinit var hajjGuideImageLayout:FrameLayout
    private lateinit var bgHajjGuide:AppCompatImageView
    private lateinit var scrollView:ScrollView
    private lateinit var btnZoomIn:AppCompatImageView
    private lateinit var btnZoomOut:AppCompatImageView
    private lateinit var bottomSelection:ConstraintLayout
    private lateinit var stepTitle:AppCompatTextView
    private lateinit var stepPos:AppCompatTextView
    private lateinit var stepStatus:AppCompatTextView
    private lateinit var contentTxt:AppCompatTextView
    private lateinit var notYetBtn: MaterialButton
    private lateinit var doneBtn: MaterialButton
    private lateinit var guideline:Guideline

    private lateinit var menuAdapter: MenuAdapter

    private lateinit var viewmodel: SubCatCardListViewModel
    private lateinit var hajjAndUmrahViewModel: HajjAndUmrahViewModel

    private val markers = mutableListOf<Marker>()
    private val markerViews = mutableListOf<View>()
    private var currentScale = 1.0f

    //pinch to zoom
    private var lastTouchX: Float = 0f
    private var lastTouchY: Float = 0f
    private var isScrolling = false
    private var scaleFactor = 1.0f
    private var scaleGestureDetector: ScaleGestureDetector? = null

    // bottom sheet music player
    private var previousSlideOffset = 0f
    private lateinit var bottomSheetBehavior:BottomSheetBehavior<View>

    // A flag to check if scaling is in progress
    private var isScaling = false

    private var selectedTrackerIndex = 0
    private var selectedTrackerData: Data ? = null
    private val navArgs:HajjGuideFragmentArgs by navArgs()


    override fun OnCreate() {
        super.OnCreate()
        CallBackProvider.setFragment(this)

        // init viewmodel
        val repository = SubCatCardListRepository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = SubCatCardListViewModel(repository)

        val hajjAndUmrahRepository = HajjAndUmrahRepository(
            deenService = NetworkProvider().getInstance().provideDeenService()
        )

        hajjAndUmrahViewModel = HajjAndUmrahViewModel(repository = hajjAndUmrahRepository)

    }

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_hajj_guide,container,false)

        contentCard = mainview.findViewById(R.id.contentCard)
        contentLayout = mainview.findViewById(R.id.ContentLayout)
        icRight = mainview.findViewById(R.id.icRight)
        stepList = mainview.findViewById(R.id.stepList)
        hajjGuideImageLayout = mainview.findViewById(R.id.hajjGuideImageLayout)
        scrollView = mainview.findViewById(R.id.scrollView)
        bgHajjGuide = mainview.findViewById(R.id.bgHajjGuide)
        btnZoomIn = mainview.findViewById(R.id.btnZoomIn)
        btnZoomOut = mainview.findViewById(R.id.btnZoomOut)
        bottomSelection = mainview.findViewById(R.id.bottomSelection)
        stepTitle = bottomSelection.findViewById(R.id.stepTitle)
        stepPos = bottomSelection.findViewById(R.id.stepPos)
        stepStatus = bottomSelection.findViewById(R.id.status)
        contentTxt = bottomSelection.findViewById(R.id.contentTxt)
        notYetBtn = bottomSelection.findViewById(R.id.notYetBtn)
        doneBtn = bottomSelection.findViewById(R.id.doneBtn)
        guideline = mainview.findViewById(R.id.guideline)

        bottomSheetBehavior = BottomSheetBehavior.from(mainview.findViewById(R.id.bottomSelection))


        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.hajj_guide),
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentCard.setOnClickListener {
            contentLayout.visible(!contentLayout.isVisible)
            if(contentLayout.isVisible)
                icRight.load(R.drawable.deen_ic_dropdown_expand)
            else
                icRight.load(R.drawable.ic_dropdown)

            contentLayout.post {
                bottomSheetBehavior.peekHeight = bottomSelection.height - 8.dp
                bottomSheetBehavior.state = if(contentLayout.isVisible) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
            }

            /*   bottomSelection.post {
                  // bottomSheetBehavior.peekHeight = bottomSelection.height - 8.dp
                   //bottomSheetBehavior.state = if(!ContentLayout.isVisible) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
                   bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
               }*/

        }

        var menuList:ArrayList<MenuModel> = arrayListOf()
        menuList.add(MenuModel(R.drawable.deen_ic_hajj_white,"", ""))
        menuList.add(MenuModel(R.drawable.deen_ic_hajj_white,"", ""))
        menuList.add(MenuModel(R.drawable.deen_ic_hajj_white,"", ""))
        menuList.add(MenuModel(R.drawable.deen_ic_hajj_white,"", ""))
        menuList.add(MenuModel(R.drawable.deen_ic_hajj_white,"", ""))


        scaleGestureDetector = ScaleGestureDetector(requireContext(), ScaleListener())

        scrollView.isHorizontalScrollBarEnabled = true
        scrollView.isVerticalScrollBarEnabled = true

        btnZoomIn.setOnClickListener {
            setScale(currentScale + 0.3f)
        }

        btnZoomOut.setOnClickListener {
            setScale(currentScale - 0.3f)
        }


        val markerData = listOf(
            Pair(R.layout.item_hajj_guide_marker, Pair(0.132f, 0.700f)),
            Pair(R.layout.item_hajj_guide_marker, Pair(0.125f, 0.593f)),
            Pair(R.layout.item_hajj_guide_marker, Pair(0.145f, 0.348f)),
            Pair(R.layout.item_hajj_guide_marker, Pair(0.385f, 0.225f)),
            Pair(R.layout.item_hajj_guide_marker, Pair(0.612f, 0.110f)),
            Pair(R.layout.item_hajj_guide_marker, Pair(0.815f, 0.180f)),
            Pair(R.layout.item_hajj_guide_marker, Pair(0.890f, 0.412f)),
            Pair(R.layout.item_hajj_guide_marker, Pair(0.770f, 0.545f)),
            Pair(R.layout.item_hajj_guide_marker, Pair(0.689f, 0.615f)),
            Pair(R.layout.item_hajj_guide_marker, Pair(0.538f, 0.695f)),
            Pair(R.layout.item_hajj_guide_marker, Pair(0.335f, 0.493f)),
            Pair(R.layout.item_hajj_guide_marker, Pair(0.460f, 0.400f))
        )


        // Create a view tree observer to listen for layout changes
        val vto = bgHajjGuide.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // This callback will be triggered when the view is measured and laid out
                val bgImageWidth = bgHajjGuide.width
                val bgImageHeight = bgHajjGuide.height

                // Remove the listener to avoid redundant calls
                bgHajjGuide.viewTreeObserver.removeOnGlobalLayoutListener(this)

                hajjGuideImageLayout.post {
                    // Wait until the layout is ready
                    val screenWidth = hajjGuideImageLayout.width
                    val screenHeight = hajjGuideImageLayout.height

                    for ((index, data) in markerData.withIndex()) {
                        val (layoutResId, coords) = data
                        val (xRatio, yRatio) = coords

                        // Calculate marker positions based on the image dimensions
                        val x = xRatio * bgImageWidth
                        val y = yRatio * bgImageHeight

                        val markerView = LayoutInflater.from(requireContext()).inflate(layoutResId, hajjGuideImageLayout, false)

                        // Find the TextView within the marker view
                        val markerTxt: AppCompatTextView = markerView.findViewById(R.id.markerTxt)

                        // Set the text using the index
                        markerTxt.text = (index + 1).toString()

                        val params = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                        )

                        params.leftMargin = (x * screenWidth / bgImageWidth).toInt()
                        params.topMargin = (y * screenHeight / bgImageHeight).toInt()

                        markerView.layoutParams = params
                        hajjGuideImageLayout.addView(markerView)
                        markerViews.add(markerView)

                    }
                }

            }
        })


        //pinch to zoom


        scrollView.setOnTouchListener { v, event ->
            // If scaling is in progress, return and don't handle scrolling
            //if (isScaling) return@setOnTouchListener true

            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchX = event.x
                    lastTouchY = event.y
                    isScrolling = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.x - lastTouchX
                    val deltaY = event.y - lastTouchY

                    /* val maxTransX = (hajjGuideImageLayout.width * scaleFactor - scrollView.width) / 2
                     val maxTransY = (hajjGuideImageLayout.height * scaleFactor - scrollView.height) / 2
 */

                    val maxTransX = ((hajjGuideImageLayout.width * currentScale - (scrollView.width/3)) / 2).coerceAtLeast(0f)
                    val maxTransY = ((hajjGuideImageLayout.height * currentScale - (scrollView.height/3)) / 2).coerceAtLeast(0f)


                    hajjGuideImageLayout.translationX = (hajjGuideImageLayout.translationX + deltaX).coerceIn(-maxTransX, maxTransX)
                    hajjGuideImageLayout.translationY = (hajjGuideImageLayout.translationY + deltaY).coerceIn(-maxTransY, maxTransY)

                    isScrolling = true

                    lastTouchX = event.x
                    lastTouchY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    if (isScrolling) {
                        return@setOnTouchListener true
                    }
                }
            }
            scaleGestureDetector?.onTouchEvent(event)
            true
        }



        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Not used in this example
                Log.e("bottomSheetBehavior",slideOffset.toString())
                if (slideOffset < previousSlideOffset && slideOffset==0F) {
                    contentLayout.hide()
                    bottomSelection.post {
                        bottomSheetBehavior.peekHeight = bottomSelection.height - 8.dp
                    }

                } else if (slideOffset > previousSlideOffset) {
                    contentLayout.show()
                }
                previousSlideOffset = slideOffset
            }
        })

        doneBtn.setOnClickListener {

            selectedTrackerData?.let {
                    data ->
                data.reference?.let {

                    if(!data.IsTracked) {
                        lifecycleScope.launch {
                            hajjAndUmrahViewModel.updateHajjMaptracking(
                                mapTag = data.reference,
                                isTrack = true,
                                indexPos = selectedTrackerIndex,
                                language = getLanguage()

                            )
                        }
                    }
                }

            }

        }

        notYetBtn.setOnClickListener {

            selectedTrackerData?.let {
                    data ->
                data.reference?.let {
                    if(data.IsTracked) {
                        lifecycleScope.launch {
                            hajjAndUmrahViewModel.updateHajjMaptracking(
                                mapTag = data.reference,
                                isTrack = false,
                                indexPos = selectedTrackerIndex,
                                language = getLanguage()

                            )
                        }
                    }
                }
            }
        }

        initOvserver()
        loadApi()

    }


    private fun setScale(newScale: Float) {
        currentScale = newScale.coerceIn(1.0f, 2.5f)

        hajjGuideImageLayout.apply {
            scaleX = currentScale
            scaleY = currentScale
        }

        updateButtonsState()
    }


    private fun updateButtonsState() {
        if (currentScale >= 2.5f) {
            btnZoomOut.setColorFilter(ContextCompat.getColor(requireContext(), R.color.deen_txt_black_deep))
            btnZoomIn.setColorFilter(ContextCompat.getColor(requireContext(), R.color.deen_txt_ash))
        } else if (currentScale <= 1.0f) {
            btnZoomIn.setColorFilter(ContextCompat.getColor(requireContext(), R.color.deen_txt_black_deep))
            btnZoomOut.setColorFilter(ContextCompat.getColor(requireContext(), R.color.deen_txt_ash))
        } else {
            btnZoomIn.setColorFilter(ContextCompat.getColor(requireContext(), R.color.deen_txt_black_deep))
            btnZoomOut.setColorFilter(ContextCompat.getColor(requireContext(), R.color.deen_txt_black_deep))
        }
    }


    private fun initOvserver()
    {
        viewmodel.subCatLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is SubCatCardListResource.SubcatList -> viewState(it.data)
            }
        }

        hajjAndUmrahViewModel.hajjMapTrackerLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is HajjAndUmrahResource.hajjMapTracker ->
                {
                    selectedTrackerData?.let {
                        data ->
                        data.IsTracked = it.isTrack
                        basicCardListItemSelect(data,it.indexPos)
                        updateMarkerView(it.indexPos,it.isTrack)
                    }

                }
            }
        }
    }

    private fun loadApi()
    {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getSubCatList(navArgs.categoryId,getLanguage(), HAJJ_GUIDE)
        }
    }

    override fun noInternetRetryClicked() {
        loadApi()
    }

    private fun viewState(data: List<Data>)
    {
        stepList.apply {
            menuAdapter = MenuAdapter(
                hajjGuideStepList = data,
                viewType = 4
            )
            adapter = menuAdapter
        }

        if(data.isNotEmpty())
        {
            val vto = bgHajjGuide.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    hajjGuideImageLayout.post {
                        data.forEachIndexed {  index, value ->
                            updateMarkerView(index,value.IsTracked)
                        }
                    }
                }
            })



            basicCardListItemSelect(data = data[0], pos = 0)
        }

        bottomSelection.post {
            bottomSheetBehavior.peekHeight = bottomSelection.height - 8.dp
        }

        baseViewState()
    }


    private fun updateMarkerView(markerIndexToUpdate:Int,isTrack:Boolean)
    {
        Log.e("updateMarkerView", markerViews.size.toString())
        if (markerIndexToUpdate < markerViews.size) {
            val markerViewToUpdate = markerViews[markerIndexToUpdate]

            // Access the TextView inside the marker view
            val markerIcon: AppCompatImageView = markerViewToUpdate.findViewById(R.id.markerIcon)
            val markerTxt: AppCompatTextView = markerViewToUpdate.findViewById(R.id.markerTxt)

            if(isTrack)
            {
                markerIcon.setColorFilter(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                markerTxt.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))
            }
            else
            {
                markerIcon.setColorFilter(ContextCompat.getColor(requireContext(),R.color.deen_txt_orange))
                markerTxt.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_black))
            }

        }
    }

    internal data class Marker(val view: View, val originalX: Float, val originalY: Float)

    //pinch to zoom
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = java.lang.Float.max(
                1.0f,
                java.lang.Float.min(scaleFactor, 2.5f)
            ) // set the minimum value to 1.0f

            hajjGuideImageLayout.apply {
                pivotX = detector.focusX
                pivotY = detector.focusY
                scaleX = scaleFactor
                scaleY = scaleFactor
            }

            currentScale = scaleFactor

            if(currentScale>=2.5)
            {
                btnZoomOut.setColorFilter(ContextCompat.getColor(requireContext(),R.color.deen_txt_black_deep))
                btnZoomIn.setColorFilter(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))
            }

            if(currentScale<=1.0)
            {
                btnZoomIn.setColorFilter(ContextCompat.getColor(requireContext(),R.color.deen_txt_black_deep))
                btnZoomOut.setColorFilter(ContextCompat.getColor(
                    requireContext(),
                    R.color.deen_txt_ash
                ))
            }

            isScrolling = false
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            isScaling = true
            return super.onScaleBegin(detector)
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            super.onScaleEnd(detector)
            isScaling = false
        }

    }

    override fun basicCardListItemSelect(data: Data,pos:Int) {
        selectedTrackerIndex = pos
        selectedTrackerData = data
        //menuAdapter.updateSelectedIndex(pos)
        stepPos.text = if(pos<10) "0${pos+1}".numberLocale() else "${pos+1}".numberLocale()
        stepTitle.text = data.Title
        contentTxt.text = data.Text
        if(data.IsTracked) {
            stepStatus.text = localContext.getString(R.string.done)
            doneBtn.setActiveState(textColor = R.color.deen_white, BackgroundColor = R.color.deen_primary)
            notYetBtn.setActiveState(textColor = R.color.deen_primary, BackgroundColor = R.color.deen_white)


        }
        else {
            stepStatus.text = localContext.getString(R.string.not_yet)
            doneBtn.setActiveState(textColor = R.color.deen_primary, BackgroundColor = R.color.deen_white)
            notYetBtn.setActiveState(textColor = R.color.deen_white, BackgroundColor = R.color.deen_primary)


        }
    }


}