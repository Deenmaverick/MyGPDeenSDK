package com.deenislamic.sdk.views.pdfviewer

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.Common3DotMenuCallback
import com.deenislamic.sdk.service.database.AppPreference
import com.deenislamic.sdk.service.models.common.OptionList
import com.deenislamic.sdk.service.models.common.PdfSetting
import com.deenislamic.sdk.service.models.quran.quranplayer.PlayerCommonSelectionData
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.FileDownloader
import com.deenislamic.sdk.utils.enterFullScreen
import com.deenislamic.sdk.utils.exitFullScreen
import com.deenislamic.sdk.utils.getPdfPageStyleList
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.pdfviewer.PdfViewCustomScrollbar
import com.deenislamic.sdk.utils.pdfviewer.ZoomableRecyclerView
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.transformArabicFontData
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.views.adapters.common.Common3DotMenuAdapter
import com.deenislamic.sdk.views.adapters.pdfviewer.PdfViwerAdapter
import com.deenislamic.sdk.views.adapters.quran.quranplayer.PlayerCommonSelectionList
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

internal class PdfViewerFragment : BaseRegularFragment()
    , otherFagmentActionCallback, PlayerCommonSelectionList.PlayerCommonSelectionListCallback,
    Common3DotMenuCallback,ZoomableRecyclerView.Callback,FileDownloader.ProgressListener {

    private lateinit var pdfView: ZoomableRecyclerView
    private lateinit var pdfViwerAdapter: PdfViwerAdapter
    private lateinit var customScrollbarVertical: PdfViewCustomScrollbar
    private lateinit var customScrollbarHorizontal: PdfViewCustomScrollbar
    private var fileDownloader: FileDownloader? = null
    private lateinit var actionbar: ConstraintLayout

    private val navArgs:PdfViewerFragmentArgs by navArgs()

    private lateinit var currentPage: AppCompatTextView
    private lateinit var totalPage: AppCompatTextView
    private lateinit var pageInfo: ConstraintLayout
    private lateinit var downloadProgress:LinearProgressIndicator

    private var dialog: Dialog? = null
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : View

    private var readSettingdialog: Dialog? = null
    private lateinit var materialAlertDialogBuilder1: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView1 : View

    private lateinit var pdfPageStyleList: PlayerCommonSelectionList

    private lateinit var pdfSetting: PdfSetting

    private lateinit var pdfFile:File
    private var lastPage = 0

    private var isFullScreen = false
    private var isPortrait = true

    private var isContentShowing = false

    private var snapHelper: LinearSnapHelper? = null

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_pdf_viewer,container,false)

        currentPage = mainView.findViewById(R.id.currentPage)
        totalPage = mainView.findViewById(R.id.totalPage)
        pageInfo = mainView.findViewById(R.id.pageInfo)
        actionbar = mainView.findViewById(R.id.actionbar)
        downloadProgress = mainView.findViewById(R.id.downloadProgress)
        //init view

        pdfView = mainView.findViewById(R.id.pdfView)
        pdfView.setCallback(this)
        customScrollbarVertical = mainView.findViewById(R.id.custom_scrollbar_vertical)
        customScrollbarVertical.setOrientation(PdfViewCustomScrollbar.Orientation.VERTICAL)
        customScrollbarHorizontal = mainView.findViewById(R.id.custom_scrollbar_horizontal)
        customScrollbarHorizontal.setOrientation(PdfViewCustomScrollbar.Orientation.HORIZONTAL)
        setupActionForOtherFragment(R.drawable.ic_search,R.drawable.ic_settings,this,navArgs.pageTitle,true,mainView)

        CallBackProvider.setFragment(this)
        setupCommonLayout(mainView)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        /*pdfViewLib = PDFView(requireContext(), null)
        pdfView = pdfViewLib*/

        pdfSetting = AppPreference.getPdfSetting()
        fileDownloader = context?.let { FileDownloader(it) }


        loadPdf()


    }

    private fun loadPdf(){
        navArgs.pdfUrl?.let { pdfurl ->
            CoroutineScope(Dispatchers.IO).launch {
                val result = fileDownloader?.downloadFile(pdfurl,".pdf",this@PdfViewerFragment)
                result?.onSuccess { file ->
                    if(!isAdded)
                        return@onSuccess
                    withContext(Dispatchers.Main) {
                        pdfFile = file
                        //pdfConfig = pdfViewLib.fromFile(file)
                        renderpdf(file)
                        setupPdfPageSetting(pdfSetting.pageViewStyle)
                        //pdfView.show()
                        baseViewState()
                    }
                }?.onFailure { exception ->
                    if(!isAdded)
                        return@onFailure
                    withContext(Dispatchers.Main) {
                        baseNoInternetState()
                    }
                }
            }
        }

        navArgs.pdfFile?.let {

            pdfFile = File(it)
            //pdfView.fromFile(File(it)).load()
            //pdfConfig = pdfViewLib.fromFile(File(it))
            renderpdf(File(it))
            setupPdfPageSetting(pdfSetting.pageViewStyle)
            //pdfView.show()
            baseViewState()
        }

    }

    override fun noInternetRetryClicked() {
        loadPdf()
    }

    private fun show_setting() {

        dialog?.dismiss()

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_pdf_settings, null, false)

        // Initialize and assign variable

        customAlertDialogView.let {

            val chooseReadSettingBtn: MaterialButton = it.findViewById(R.id.chooseReadSettingBtn)
            val optionList: RecyclerView = it.findViewById(R.id.optionList)


            var optionList3Dot = arrayListOf(
                OptionList("portrait",R.drawable.deen_ic_file,localContext.getString(R.string.portrait_mode)),
                OptionList("landscape",R.drawable.deen_ic_file,localContext.getString(R.string.landscape_mode))

            )

            optionList3Dot = if(isPortrait)
                ArrayList(optionList3Dot.filter { it1-> it1.type == "landscape" })
            else
                ArrayList(optionList3Dot.filter { it1-> it1.type == "portrait" })

            optionList.apply {
                adapter = Common3DotMenuAdapter(optionList3Dot,null)
            }

            chooseReadSettingBtn.setOnClickListener {
                dialogPagetSetting()
            }


            pdfSetting = AppPreference.getPdfSetting()


            chooseReadSettingBtn.text = getPdfPageStyleList().firstOrNull { it1-> it1.fontid == pdfSetting.pageViewStyle.toString() }?.fontname


            // show dialog

            dialog = materialAlertDialogBuilder
                .setView(it)
                .setCancelable(true)
                .show()

            dialog?.setOnDismissListener {
                if(!isPortrait)
                    Common3DotMenuOptionClicked(OptionList("landscape",0,""),null)
            }

            dialog?.setOnCancelListener {
                if(!isPortrait)
                    Common3DotMenuOptionClicked(OptionList("landscape",0,""),null)
            }

        }

    }

    private fun dialogPagetSetting() {

        readSettingdialog?.dismiss()

        materialAlertDialogBuilder1 = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView1 = localInflater.inflate(R.layout.dialog_translator_list, null, false)
        // Initialize and assign variable

        customAlertDialogView1.let {

            val translationByTxt: AppCompatTextView = it.findViewById(R.id.translationByTxt)
            val banglaTranList: RecyclerView = it.findViewById(R.id.banglaTranList)
            val translationByTxt1: AppCompatTextView = it.findViewById(R.id.translationByTxt1)
            val englishTranList: RecyclerView = it.findViewById(R.id.englishTranList)
            val dismissBtn = it.findViewById<ImageButton>(R.id.closeBtn)
            val title: AppCompatTextView = it.findViewById(R.id.pageTitle)

            translationByTxt1.hide()
            englishTranList.hide()
            translationByTxt.hide()

            title.text = localContext.getString(R.string.page_view_setting)


            banglaTranList.apply {
                pdfPageStyleList = PlayerCommonSelectionList(
                    ArrayList(getPdfPageStyleList().map { fdata -> transformArabicFontData(fdata) }),this@PdfViewerFragment)
                adapter = pdfPageStyleList

            }


            pdfSetting = AppPreference.getPdfSetting()

            if(this::pdfPageStyleList.isInitialized) {
                val updatedData = pdfPageStyleList.getData().map { data ->
                    data.copy(isSelected = data.Id == pdfSetting.pageViewStyle)
                }

                pdfPageStyleList.update(updatedData)
            }
            dismissBtn?.setOnClickListener {
                readSettingdialog?.dismiss()
            }




            // show dialog

            readSettingdialog = materialAlertDialogBuilder1
                .setView(it)
                .setCancelable(true)
                .show()



        }

    }

    override fun action1() {
        if(!isContentShowing)
            return
        show_search()
    }

    override fun action2() {
        if(!isContentShowing)
            return
        show_setting()
    }

    override fun playerCommonListSelected(
        data: PlayerCommonSelectionData,
        adapter: PlayerCommonSelectionList
    ) {

        pdfSetting = AppPreference.getPdfSetting()
        pdfSetting.pageViewStyle = data.Id
        AppPreference.setPdfSetting(pdfSetting)

        if(this::pdfPageStyleList.isInitialized) {
            val updatedData = pdfPageStyleList.getData().map { pdata ->
                pdata.copy(isSelected = pdata.Id == pdfSetting.pageViewStyle)
            }

            pdfPageStyleList.update(updatedData)
        }

        if(this::customAlertDialogView.isInitialized) {
            val chooseReadSettingBtn: MaterialButton = customAlertDialogView.findViewById(R.id.chooseReadSettingBtn)
            chooseReadSettingBtn.text = getPdfPageStyleList().firstOrNull { it.fontid == pdfSetting.pageViewStyle.toString() }?.fontname

        }

        setupPdfPageSetting(data.Id)
        readSettingdialog?.dismiss()
    }

    private fun renderpdf(file: File) {

        if(!isAdded)
            return

        pdfView.apply {
            val RclayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            layoutManager = RclayoutManager
            customScrollbarVertical.setRecyclerView(this,pageInfo)
            customScrollbarHorizontal.setRecyclerView(this,pageInfo)
            if(!this@PdfViewerFragment::pdfViwerAdapter.isInitialized){
                val parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(parcelFileDescriptor)
                pdfViwerAdapter = PdfViwerAdapter(pdfRenderer)
            }

            adapter = pdfViwerAdapter
        }
    }

    private fun setupPdfPageSetting(style: Int) {
        if (!this@PdfViewerFragment::pdfFile.isInitialized) return

        isContentShowing = true

        pdfView.apply {
            when (style) {
                1 -> {
                    customScrollbarHorizontal.show()
                    customScrollbarVertical.hide()
                    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                    // Attach LinearSnapHelper
                    snapHelper?.attachToRecyclerView(null)
                    snapHelper = LinearSnapHelper().apply {
                        attachToRecyclerView(pdfView)
                    }
                }
                2 -> {
                    customScrollbarHorizontal.hide()
                    customScrollbarVertical.show()
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    // Remove LinearSnapHelper if attached
                    snapHelper?.attachToRecyclerView(null)
                    snapHelper = null
                }
                3 -> {
                    customScrollbarHorizontal.hide()
                    customScrollbarVertical.show()
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    // Attach LinearSnapHelper
                    snapHelper?.attachToRecyclerView(null)
                    snapHelper = LinearSnapHelper().apply {
                        attachToRecyclerView(pdfView)
                    }
                }
            }
            adapter = pdfViwerAdapter // Make sure adapter is set
        }
    }

    // Full screen method


    private fun toggleFullScreen() {
        val activity = activity as? AppCompatActivity ?: return

        if (isFullScreen) {
            // Exit full-screen mode
            activity.exitFullScreen()
            actionbar.show()
        } else {
            // Enter full-screen mode
            activity.enterFullScreen()
            actionbar.hide()
        }
        isFullScreen = !isFullScreen
    }

    override fun <T> Common3DotMenuOptionClicked(getdata: OptionList, data: T) {

        when(getdata.type){

            "portrait" -> {

                isPortrait = true
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                isFullScreen = true
                toggleFullScreen()
                actionbar.show()
            }

            "landscape" -> {
                isPortrait = false
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                isFullScreen = false
                toggleFullScreen()
                actionbar.hide()

            }
        }

        dialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Common3DotMenuOptionClicked(OptionList("portrait",0,""),null)
        fileDownloader?.cancelDownload()
    }


    override fun onBackPress() {
        if(!isPortrait){
            Common3DotMenuOptionClicked(OptionList("portrait",0,""),null)
        }else
            super.onBackPress()
    }


    private fun show_search() {

        dialog?.dismiss()

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_pdf_search, null, false)

        // Initialize and assign variable

        customAlertDialogView.let {

            val pageInput: TextInputEditText = it.findViewById(R.id.pageInput)
            val pageCount: AppCompatTextView = it.findViewById(R.id.pageCount)
            val searchBtn: MaterialButton = it.findViewById(R.id.searchBtn)

            pageInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s != null) {
                        val input = s.toString().toIntOrNull()
                        if (input != null && input > pdfViwerAdapter.itemCount) {
                            // Remove the last character if it causes the input to exceed the total page count
                            pageInput.removeTextChangedListener(this)
                            s.delete(s.length - 1, s.length)
                            pageInput.addTextChangedListener(this)
                        }
                    }
                }
            })

            pageCount.text = localContext.getString(R.string.pdf_page_search_out_of,pdfViwerAdapter.itemCount.toString())

            searchBtn.setOnClickListener {
                val pageNumber = pageInput.text.toString().toIntOrNull()
                if (pageNumber != null && pageNumber in 1..pdfViwerAdapter.itemCount) {
                    pdfView.scrollToPosition(pageNumber-1)
                }
                dialog?.dismiss()

            }

            // show dialog

            dialog = materialAlertDialogBuilder
                .setView(it)
                .setCancelable(true)
                .show()

            dialog?.setOnDismissListener {
                if(!isPortrait)
                    Common3DotMenuOptionClicked(OptionList("landscape",0,""),null)
            }

            dialog?.setOnCancelListener {
                if(!isPortrait)
                    Common3DotMenuOptionClicked(OptionList("landscape",0,""),null)
            }

        }

    }

    override fun zoomableRecyleviewSingleTap() {
        customScrollbarVertical.showThumb()
        customScrollbarHorizontal.showThumb()
        if(!isContentShowing)
            return
        if(!isPortrait){
            actionbar.isVisible.let { it1 -> actionbar.visible(!it1) }
            if(!actionbar.isVisible){
                Common3DotMenuOptionClicked(OptionList("landscape",0,""),null)
            }
        }
        else
            toggleFullScreen()
    }

    override fun onProgressUpdate(progress: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            downloadProgress.post {
                downloadProgress.progress = progress
            }

        }

    }

}