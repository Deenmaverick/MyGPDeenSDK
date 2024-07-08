package com.deenislamic.sdk.views.share

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.ShareCallback
import com.deenislamic.sdk.service.callback.common.MaterialButtonHorizontalListCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.ShareResource
import com.deenislamic.sdk.service.models.share.ColorList
import com.deenislamic.sdk.service.network.response.prayerlearning.visualization.Head
import com.deenislamic.sdk.service.network.response.share.Data
import com.deenislamic.sdk.service.repository.ShareRepository
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.FullDraggableView
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.shareText
import com.deenislamic.sdk.utils.shareView
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.viewmodels.ShareViewModel
import com.deenislamic.sdk.views.adapters.MainViewPagerAdapter
import com.deenislamic.sdk.views.adapters.common.MaterialButtonHorizontalAdapter
import com.deenislamic.sdk.views.adapters.share.ColorListAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch


internal class ShareFragment : BaseRegularFragment(), MaterialButtonHorizontalListCallback,
    ShareCallback {

    private lateinit var headerText:AppCompatTextView
    private lateinit var shareTxt: AppCompatTextView
    private lateinit var shareImg:AppCompatImageView
    private lateinit var bottom_navigation: BottomNavigationView
    private lateinit var TextEditLayout:ConstraintLayout
    private lateinit var TxtStyleLayout:ConstraintLayout
    private lateinit var fontList:RecyclerView
    private lateinit var materialButtonHorizontalAdapter: MaterialButtonHorizontalAdapter
    private lateinit var wallpaperCatAdapter:MaterialButtonHorizontalAdapter
    private lateinit var colorListAdapter: ColorListAdapter
    private lateinit var fontControl: Slider
    private lateinit var textShadowControl:Slider
    private lateinit var bgBlurControl:Slider
    private lateinit var fontsizeOutput:AppCompatTextView
    private lateinit var textShadowOutput:AppCompatTextView
    private lateinit var bgBlurOutput:AppCompatTextView
    private lateinit var txtLeftAlignBtn:MaterialButton
    private lateinit var txtCenterAlignBtn:MaterialButton
    private lateinit var txtRightAlignBtn:MaterialButton
    private lateinit var dragView: FullDraggableView
    private lateinit var scrollContainer: NestedScrollView
    private lateinit var fontColorList:RecyclerView
    private var shareBgBitmap:Bitmap? = null
    private lateinit var shareLayout:ConstraintLayout
    private lateinit var banglaBtn:MaterialButton
    private lateinit var arabicBtn:MaterialButton
    private lateinit var txtChooseLayout:ConstraintLayout
    private lateinit var englishBtn:MaterialButton
    private lateinit var wallpaperSection:ConstraintLayout
    private lateinit var actionBtnWallpaper:MaterialButton
    private lateinit var viewPager:ViewPager2
    private lateinit var wallpaperCategory:RecyclerView
    private lateinit var footerText:AppCompatTextView
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private  var mPageDestination: ArrayList<Fragment> = arrayListOf()

    private val navArgs:ShareFragmentArgs by navArgs()
    private lateinit var viewmodel: ShareViewModel

    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        val repository = ShareRepository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = ShareViewModel(repository)
        setupBackPressCallback(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_share, container, false)

        CallBackProvider.setFragment(this)

        headerText = mainview.findViewById(R.id.headerText)
        shareTxt = mainview.findViewById(R.id.shareTxt)
        shareImg = mainview.findViewById(R.id.shareImg)
        bottom_navigation = mainview.findViewById(R.id.inc_bottomNav)
        TextEditLayout = mainview.findViewById(R.id.TextEditLayout)
        TxtStyleLayout = mainview.findViewById(R.id.TxtStyleLayout)
        fontList = mainview.findViewById(R.id.fontList)
        fontControl = mainview.findViewById(R.id.fontControl)
        fontsizeOutput = mainview.findViewById(R.id.fontsizeOutput)
        txtLeftAlignBtn = mainview.findViewById(R.id.txtLeftAlignBtn)
        txtCenterAlignBtn = mainview.findViewById(R.id.txtCenterAlignBtn)
        txtRightAlignBtn = mainview.findViewById(R.id.txtRightAlignBtn)
        textShadowControl = mainview.findViewById(R.id.textShadowControl)
        dragView = mainview.findViewById(R.id.dragView)
        scrollContainer = mainview.findViewById(R.id.scrollContainer)
        fontColorList = mainview.findViewById(R.id.fontColorList)
        textShadowOutput = mainview.findViewById(R.id.textShadowOutput)
        bgBlurOutput = mainview.findViewById(R.id.bgBlurOutput)
        bgBlurControl = mainview.findViewById(R.id.bgBlurControl)
        shareLayout = mainview.findViewById(R.id.shareLayout)
        banglaBtn = mainview.findViewById(R.id.banglaBtn)
        arabicBtn = mainview.findViewById(R.id.arabicBtn)
        txtChooseLayout = mainview.findViewById(R.id.txtChooseLayout)
        englishBtn = mainview.findViewById(R.id.englishBtn)
        wallpaperSection = mainview.findViewById(R.id.wallpaperSection)
        actionBtnWallpaper = mainview.findViewById(R.id.btnCalendar)
        wallpaperCategory = mainview.findViewById(R.id.wallpaperCategory)
        viewPager = mainview.findViewById(R.id.viewPager)
        footerText = mainview.findViewById(R.id.footerText)

        textShadowOutput.text = "0%".numberLocale()
        fontsizeOutput.text = "0%".numberLocale()
        bgBlurOutput.text = "0%".numberLocale()
        actionBtnWallpaper.hide()
        actionBtnWallpaper.text = localContext.getString(R.string.background)
        actionBtnWallpaper.icon = null

        setupActionForOtherFragment(0,0,null, navArgs.title?.let { it }?: localContext.getString(R.string.background),true,mainview)
        setupCommonLayout(mainview)
        setupFontList()

        val originalShareTextSize = resources.getDimension(R.dimen.font_12) // Provide a default text size in resources

        fontControl.addOnChangeListener { slider, value, fromUser ->
            // Scale the font size based on the slider value, starting from the initial size
            val scaleFactor = value / 100.0f // Assuming the slider ranges from 0 to 100
            val scaledTextSize = originalShareTextSize + (originalShareTextSize * scaleFactor)

            // Apply the scaled text size to the TextView
            shareTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX, scaledTextSize)

            fontsizeOutput.text = "${value.toInt()}%".numberLocale()
        }

        txtLeftAlignBtn.setOnClickListener {
            clearTextAlign()
            setActiveTextAlign(txtLeftAlignBtn)
            shareTxt.gravity = android.view.Gravity.START

        }

        txtCenterAlignBtn.setOnClickListener {
            clearTextAlign()
            setActiveTextAlign(txtCenterAlignBtn)
            shareTxt.gravity = android.view.Gravity.CENTER_HORIZONTAL
        }

        txtRightAlignBtn.setOnClickListener {
            clearTextAlign()
            setActiveTextAlign(txtRightAlignBtn)
            shareTxt.gravity = android.view.Gravity.END
        }

        dragView.setParentScrollview(scrollContainer)
        dragView.setChildviews(shareTxt,footerText)

        textShadowControl.addOnChangeListener { _, value, _ ->
            // Adjust the shadow based on the slider value
            val shadowRadius = value / 2.0f // Scale down the value if needed
            val shadowDx = value / 4.0f
            val shadowDy = value / 4.0f

            // Apply the shadow to the TextView
            shareTxt.setShadowLayer(shadowRadius, shadowDx, shadowDy, Color.BLACK)
            textShadowOutput.text = "${value.toInt()}%".numberLocale()
        }

        // Assuming shareImg is your ImageView reference
       /* shareImg.post {
            // Enable drawing cache
            shareImg.isDrawingCacheEnabled = true

            // Create a Bitmap from the drawing cache
            shareBgBitmap = Bitmap.createBitmap(shareImg.drawingCache)

            // Disable drawing cache to release resources
            shareImg.isDrawingCacheEnabled = false
        }*/


        bgBlurControl.addOnChangeListener { _, value, _ ->
            // Ensure the blur radius is within the valid range (0 to 24)
            val clampedBlurRadius = value.coerceIn(0f, 24f).toInt()

            // If the blur radius is 0, set the background to the original unblurred image
            if (clampedBlurRadius == 0) {
                shareImg.setImageBitmap(shareBgBitmap) // Replace with your unblurred image resource
            } else {
                // Apply the blur effect for non-zero blur radius
                updateBlur(clampedBlurRadius)
            }

            bgBlurOutput.text = "${value.toInt()}%".numberLocale()
        }

        val countTotalShareText = listOf(navArgs.enText, navArgs.bnText, navArgs.arText).count { it?.isNotEmpty() == true }

        shareTxt.text = if(navArgs.enText?.isNotEmpty() == true) navArgs.enText.toString()
        else if(navArgs.bnText?.isNotEmpty() == true) navArgs.bnText.toString()
        else if(navArgs.arText?.isNotEmpty() == true) navArgs.arText.toString()
        else ""

        if(countTotalShareText <=1)
            txtChooseLayout.hide()

        if(navArgs.enText?.isNotEmpty() == true)
            englishBtn.show()
        else
            englishBtn.hide()

        if(navArgs.bnText?.isNotEmpty() == true)
            banglaBtn.show()
        else
            banglaBtn.hide()

        if(navArgs.arText?.isNotEmpty() == true)
            arabicBtn.show()
        else
            arabicBtn.hide()

        navArgs.footerText?.let {
            footerText.text = it
        }

        navArgs.heading?.let {
            headerText.text = it
        }

        englishBtn.setOnClickListener {
            clearActiveTextChooseButton()
            setActiveTextAlign(englishBtn)
            shareTxt.text = navArgs.enText
            shareTxt.typeface = null

        }

        banglaBtn.setOnClickListener {
            clearActiveTextChooseButton()
            setActiveTextAlign(banglaBtn)
            shareTxt.text = navArgs.bnText
            shareTxt.typeface = null

        }

        arabicBtn.setOnClickListener {
            clearActiveTextChooseButton()
            setActiveTextAlign(arabicBtn)
            shareTxt.text = navArgs.arText
            context?.let { it1 -> shareTxt.typeface = ResourcesCompat.getFont(it1, R.font.indopakv2) }
        }


        actionBtnWallpaper.setOnClickListener {
            setupActionForOtherFragment(0,0,null,navArgs.title?.let { it }?:localContext.getString(R.string.background),true,requireView())
            actionBtnWallpaper.hide()
            bottom_navigation.hide()
            wallpaperSection.show()
            scrollContainer.hide()
        }

       /* when (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
            Configuration.SCREENLAYOUT_SIZE_XLARGE,
            Configuration.SCREENLAYOUT_SIZE_LARGE -> {
                guideline.setGuidelinePercent(0.6F)
            }
            else -> {
                // It's a phone
                guideline.setGuidelinePercent(0.5F)
            }
        }*/

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var lastSelectedItemId: Int = -1 // Initialize with an invalid ID

        initObserver()
        bottom_navigation.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.ShareTxtEdit ->
                {
                    TxtStyleLayout.hide()
                    TextEditLayout.show()
                }

                R.id.ShareTextStyle ->
                {
                    TextEditLayout.hide()
                    TxtStyleLayout.show()

                }

                R.id.ShareBtn ->
                {
                    if(lastSelectedItemId==-1)
                        bottom_navigation.selectedItemId = R.id.ShareTxtEdit
                    else
                        bottom_navigation.selectedItemId = lastSelectedItemId

                    context?.shareView(shareLayout,navArgs.customShareText)

                    return@setOnNavigationItemSelectedListener false
                }

                else -> Unit
            }

            lastSelectedItemId = item.itemId

            true
        }




        if(navArgs.enText?.isNotEmpty() == true)
            setActiveTextChoosenButton(englishBtn)
        else if(navArgs.bnText?.isNotEmpty() == true)
            setActiveTextChoosenButton(banglaBtn)
        else if(navArgs.arText?.isNotEmpty() == true)
            setActiveTextChoosenButton(banglaBtn)

        if (firstload){
            scrollContainer.show()
            wallpaperSection.hide()
            bottom_navigation.show()
        }
        else{
            scrollContainer.hide()
            bottom_navigation.hide()
            wallpaperSection.show()
            loadapi()
        }

    }


    private fun setupFontList(){

        val fontListArray = arrayListOf(
            Head(0,localContext.getString(R.string.default_txt))
        )

        fontList.apply {
            materialButtonHorizontalAdapter = MaterialButtonHorizontalAdapter(fontListArray)
            adapter = materialButtonHorizontalAdapter
        }

        val textColorArray = arrayListOf(
            ColorList("#FFFFFF"),
            ColorList("#F3F3F9"),
            ColorList("#DDEDED"),
            ColorList("#1E8787"),
            ColorList("#2FB68E"),
            ColorList("#A3DE7C"),
            ColorList("#EBCA28"),
            ColorList("#FFBB86FC"),
            ColorList("#FF6200EE"),
            ColorList("#FF3700B3"),
            ColorList("#FF03DAC5"),
            ColorList("#FF018786"),
            ColorList("#FF000000"),
            ColorList("#FFFFFFFF"),
            ColorList("#1E8787"),
            ColorList("#EBCA28"),
            ColorList("#B3B3B3"),
            ColorList("#F8F8F8"),
            ColorList("#1F1F1F"),
            ColorList("#1E8787"),
            ColorList("#6B6B6B"),
            ColorList("#4DFFFFFF"),
            ColorList("#DDEDED"),
            ColorList("#EBEBEB"),
            ColorList("#141414"),
            ColorList("#30535A"),
            ColorList("#2FB68E"),
            ColorList("#F3F9F9"),
            ColorList("#F3F3F9"),
            ColorList("#dddddd"),
            ColorList("#F5922F"),
            ColorList("#656565"),
            ColorList("#4B878B"),
            ColorList("#4EABE0"),
            ColorList("#F99941"),
            ColorList("#E65645"),
            ColorList("#FF3F58"),
            ColorList("#E2D5B5"),
            ColorList("#f90303"),
            ColorList("#1650e4"),
            ColorList("#10e430"),
            ColorList("#ef990e"),
            ColorList("#b117dc"),
            ColorList("#2F937E"),
            ColorList("#A3DE7C"),
            ColorList("#261E8787"),
            ColorList("#222326"),
            ColorList("#CCE9FA"),
            ColorList("#FDE3EC"),
            ColorList("#DBF0FC"),
            ColorList("#FEEBF2")
        )

        fontColorList.apply {
            colorListAdapter = ColorListAdapter(textColorArray)
            adapter = colorListAdapter
        }
    }


    override fun materialButtonHorizontalListClicked(absoluteAdapterPosition: Int) {
        materialButtonHorizontalAdapter.notifyItemChanged(absoluteAdapterPosition)
        fontList.smoothScrollToPosition(absoluteAdapterPosition)
    }


    private fun clearTextAlign()
    {
        txtLeftAlignBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))
        txtCenterAlignBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))
        txtRightAlignBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

        txtLeftAlignBtn.iconTint = AppCompatResources.getColorStateList(requireContext(),R.color.deen_txt_ash)
        txtCenterAlignBtn.iconTint = AppCompatResources.getColorStateList(requireContext(),R.color.deen_txt_ash)
        txtRightAlignBtn.iconTint = AppCompatResources.getColorStateList(requireContext(),R.color.deen_txt_ash)

        txtLeftAlignBtn.backgroundTintList = AppCompatResources.getColorStateList(requireContext(),R.color.deen_background)
        txtCenterAlignBtn.backgroundTintList = AppCompatResources.getColorStateList(requireContext(),R.color.deen_background)
        txtRightAlignBtn.backgroundTintList = AppCompatResources.getColorStateList(requireContext(),R.color.deen_background)
    }

    private fun setActiveTextAlign(button:MaterialButton)
    {
        button.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_primary))
        button.iconTint = AppCompatResources.getColorStateList(requireContext(),R.color.deen_primary)
        button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
    }

    override fun selectedTextColor(absoluteAdapterPosition: Int, getData: ColorList) {
        //materialButtonHorizontalAdapter.notifyItemChanged(absoluteAdapterPosition)
        shareTxt.setTextColor(Color.parseColor(getData.code))
        fontColorList.smoothScrollToPosition(absoluteAdapterPosition)
    }

    override fun selectedWallpaper(absoluteAdapterPosition: Int, bitmap: Bitmap?, data: Data?) {

        if(!isAdded)
            return

        if(absoluteAdapterPosition == 0){
            navArgs.bnText?.let { context?.shareText(it) }
            return
        }

        setupActionForOtherFragment(0,0,null,navArgs.title?.let { it }?:localContext.getString(R.string.share),true,requireView())
        actionBtnWallpaper.show()
        wallpaperSection.hide()
        bottom_navigation.show()
        //val targetBmp: Bitmap? = bitmap?.copy(Bitmap.Config.ARGB_8888, false)
        //shareImg.setImageBitmap(targetBmp)
        shareImg.imageLoad(url = BASE_CONTENT_URL_SGP +data?.imageurl, placeholder_1_1 = true, custom_placeholder_1_1 = R.drawable.deen_bg_sample_share, customMemoryKey = "custom_share_bg_${data?.Id}")
        shareBgBitmap = bitmap
        scrollContainer.show()

    }

    override fun onBackPress() {

        if(!firstload)
            super.onBackPress()
        else if(actionBtnWallpaper.isShown){
            super.onBackPress()
        }else{
            setupActionForOtherFragment(0,0,null,navArgs.title?.let { it }?:localContext.getString(R.string.background),true,requireView())
            actionBtnWallpaper.hide()
            bottom_navigation.hide()
            wallpaperSection.show()
            scrollContainer.hide()

        }
    }

    private fun setActiveTextChoosenButton(button:MaterialButton)
    {
        button.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_primary))
        button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
    }

    private fun clearActiveTextChooseButton(){

        englishBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))
        englishBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_background))

        banglaBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))
        banglaBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_background))

        arabicBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))
        arabicBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_background))

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun updateBlur(blurRadius: Int) {

        val mutableBitmap = shareBgBitmap?.copy(Bitmap.Config.ARGB_8888, true)

        // Apply the blur using RenderScript
        val blurredBitmap = mutableBitmap?.let { applyRenderScriptBlur(it, blurRadius.toFloat()) }

        shareImg.setImageBitmap(blurredBitmap)
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun applyRenderScriptBlur(inputBitmap: Bitmap, blurRadius: Float): Bitmap {

        val outputBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)

        val rs = RenderScript.create(requireContext())

        // Allocate memory for Renderscript to work with
        val input = Allocation.createFromBitmap(rs, inputBitmap)
        val output = Allocation.createFromBitmap(rs, outputBitmap)

        // Create a blur script using the built-in Renderscript function
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

        // Set the blur radius
        blurScript.setRadius(blurRadius)

        // Perform the blur
        blurScript.setInput(input)
        blurScript.forEach(output)

        // Copy the output to the blurred bitmap
        output.copyTo(outputBitmap)

        // Destroy the Renderscript
        rs.destroy()

        return outputBitmap
    }

    private fun initObserver(){

        viewmodel.shareLiveData.observe(viewLifecycleOwner){

            when(it){

                is ShareResource.WallpaperCat ->{
                    viewWallpaperCat(it.data)
                }

                is CommonResource.API_CALL_FAILED -> baseNoInternetState()

            }

        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    private fun viewWallpaperCat(data: List<Head>) {

        wallpaperCategory.apply {
            wallpaperCatAdapter = MaterialButtonHorizontalAdapter(data)
            adapter = wallpaperCatAdapter
        }

        if(data.isNotEmpty()) {

            data.forEach {
                mPageDestination.add(BackgroundFragment(it.Serial))
            }

            mainViewPagerAdapter = MainViewPagerAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = lifecycle,
                mPageDestination
            )

            viewPager.apply {
                adapter = mainViewPagerAdapter
                isNestedScrollingEnabled = false
                isUserInputEnabled = false
                overScrollMode = View.OVER_SCROLL_NEVER
                offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
            }



            baseViewState()
        }else
            baseNoInternetState()
    }

    private fun loadapi(){
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getWallpaperCat(getLanguage())
        }
    }
}