package com.deenislamic.sdk.views.pdfviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.utils.FileDownloader
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.pdfview.PDFView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

internal class PdfViewerFragment : BaseRegularFragment() {

    //private lateinit var pdfView: PDFView

    private var fileDownloader: FileDownloader ? = null

    private val navArgs:PdfViewerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_pdf_viewer,container,false)

        //init view

       // pdfView = mainView.findViewById(R.id.pdfView)

        setupActionForOtherFragment(0,0,null,navArgs.pageTitle,true,mainView)

        setupCommonLayout(mainView)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fileDownloader = DeenSDKCore.baseContext?.let { FileDownloader(it) }


      /* if (!isDetached) {
            view.postDelayed({
                loadPdf()
            }, 300)
        }
        else*/
           loadPdf()


    }

    private fun loadPdf(){
        navArgs.pdfUrl?.let {pdfurl->
            CoroutineScope(Dispatchers.IO).launch {
                val result = fileDownloader?.downloadFile(pdfurl,".pdf")
                result?.onSuccess { file ->
                    withContext(Dispatchers.Main) {
                        //pdfView.fromFile(file)
                        //pdfView.show()
                        baseViewState()
                    }
                }?.onFailure { exception ->
                    withContext(Dispatchers.Main) {
                        baseNoInternetState()
                    }
                }
            }
        }

        navArgs.pdfFile?.let {

            //pdfView.fromFile(File(it))
            //pdfView.show()
            baseViewState()
        }

    }

    override fun noInternetRetryClicked() {
        loadPdf()
    }
}