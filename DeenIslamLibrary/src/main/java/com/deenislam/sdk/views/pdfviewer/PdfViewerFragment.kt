package com.deenislam.sdk.views.pdfviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.FileDownloader
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.pdfview.PDFView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

internal class PdfViewerFragment : BaseRegularFragment() {

    private lateinit var pdfView: PDFView

    private var fileDownloader: FileDownloader ? = null

    private val navArgs:PdfViewerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_pdf_viewer,container,false)

        //init view

        pdfView = mainView.findViewById(R.id.pdfView)

        setupActionForOtherFragment(0,0,null,navArgs.pageTitle,true,mainView)

        setupCommonLayout(mainView)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fileDownloader = DeenSDKCore.baseContext?.let { FileDownloader(it) }


       if (!isDetached) {
            view.postDelayed({
                loadPdf()
            }, 300)
        }
        else
           loadPdf()


    }

    private fun loadPdf(){
        navArgs.pdfUrl?.let {pdfurl->
            CoroutineScope(Dispatchers.IO).launch {
                val result = fileDownloader?.downloadFile(pdfurl)
                result?.onSuccess { file ->
                    pdfView.fromFile(file)
                    baseViewState()
                }?.onFailure { exception ->
                    withContext(Dispatchers.Default) {
                        baseNoInternetState()
                    }
                }
            }
        }

        navArgs.pdfFile?.let {

            pdfView.fromFile(File(it))
            baseViewState()
        }

    }

    override fun noInternetRetryClicked() {
        loadPdf()
    }
}