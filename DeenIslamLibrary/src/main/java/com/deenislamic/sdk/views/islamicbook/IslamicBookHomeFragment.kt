package com.deenislamic.sdk.views.islamicbook

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.DownloaderCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.downloader.QuranDownloadService
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicBookResource
import com.deenislamic.sdk.service.models.islamicbook.BookDownloadPayload
import com.deenislamic.sdk.service.repository.IslamicBookRepository
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.viewmodels.IslamicBookViewModel
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.islamicbook.downloadedmodel.DownloadedBook
import com.deenislamic.sdk.views.islamicbook.adapter.IslamicBookHomeAdapter
import com.deenislamic.sdk.views.islamicbook.callback.IslamicBookHomeItemCallback
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter


internal class IslamicBookHomeFragment : BaseRegularFragment(), IslamicBookHomeItemCallback,
    DownloaderCallback {

    private val CANCEL_ACTION = "cancel_action"
    private val EXTRA_NOTIFICATION_ID = "extra_notification_id"

    private lateinit var viewmodel: IslamicBookViewModel
    private lateinit var islamicBookHomeAdapter: IslamicBookHomeAdapter

    private lateinit var recyclerViewBook: RecyclerView
    private var firstload = false

    private var bookId: Int = -1
    private var bookTitle: String = ""



    override fun OnCreate() {
        super.OnCreate()

        val repository = IslamicBookRepository(
            deenService = NetworkProvider().getInstance().provideDeenService())

        val quranLearningRepository = QuranLearningRepository(
            quranShikkhaService = NetworkProvider().getInstance().provideQuranShikkhaService(),
            deenService = NetworkProvider().getInstance().provideDeenService(),
            dashboardService = NetworkProvider().getInstance().provideDashboardService()
        )

        viewmodel = IslamicBookViewModel(repository = repository, quranLearningRepository = quranLearningRepository)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_islamic_book_home,container,false)

        //init view
        recyclerViewBook = mainView.findViewById(R.id.listMainBook)

        setupCommonLayout(mainView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        loadpage()

    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if(menuVisible)
            CallBackProvider.setFragment(this)
    }

    private fun loadpage() {
        initObserver()
        initSecureLinkObserver()
        baseLoadingState()

        loadApi()
    }

    private fun initObserver() {
        viewmodel.islamicBookHomeLiveData.observe(viewLifecycleOwner)
        {
            when (it) {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is IslamicBookResource.IslamicBookHomeData -> {

                    islamicBookHomeAdapter = IslamicBookHomeAdapter(it.data)

                    recyclerViewBook.apply {
                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        adapter = islamicBookHomeAdapter
                    }

                    baseViewState()
                }
            }
        }
    }

    private fun initSecureLinkObserver(){

        viewmodel.secureUrlLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is IslamicBookResource.PdfSecureUrl -> {
                    if (it.forDownload){
                        Log.d("TheIdIdDataTest", "id: "+it.bookId)
                        downloadOfflineBook(it.url, it.bookId.toString(), it.bookTitle)
                    } else {
                        Log.d("ThePdfSecuredUrData", "url: "+it.url)
                        //Toast.makeText(requireContext(), "url: "+it.url, Toast.LENGTH_SHORT).show()
                        val bundle = Bundle()
                        bundle.putString("pageTitle", it.bookTitle)
                        bundle.putString("pdfUrl", it.url)
                        gotoFrag(R.id.action_global_pdfViewerFragment, bundle)
                    }

                }
            }
        }
    }

    private fun loadApi() {
        lifecycleScope.launch {
            viewmodel.getIslamicBookHome(getLanguage())
        }
    }

    override fun bookDownloadCancelClicked(notificationId: Int) {

        val cancelIntent = Intent(requireContext(), QuranDownloadService::class.java)
        cancelIntent.action = CANCEL_ACTION
        cancelIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        requireContext().startService(cancelIntent)

    }

    private fun downloadOfflineBook(
        fileid: String,
        name: String,
        title: String
    ){
        //Toast.makeText(requireContext(), "downloadClicked", Toast.LENGTH_SHORT).show()
        // Assuming `quranDownloadService` is an instance of QuranDownloadService
        val downloadUrl =  fileid
        Log.e("downloadUrl",downloadUrl)

        //val destinationFolder = File(requireContext().getExternalFilesDir(null), "quran/test")
        val destinationFolder = File(requireContext().filesDir, "islamicbook/"+name)
        val intent = Intent(requireContext(), QuranDownloadService::class.java)

        intent.putExtra("filename", name+".pdf")
        intent.putExtra("filetitle", title)
        intent.putExtra("iszip", false)
        intent.putExtra("downloadUrl", downloadUrl)
        intent.putExtra("destinationFolder", destinationFolder.absolutePath)

        requireActivity().startService(intent)
    }

    override fun bookItemClieked(Id: Int, contentUrl: String, bookName: String) {

        val filePath = File(requireActivity().getFilesDir(), "islamicbook/${Id.toString()}/${Id.toString()}.pdf")
        if (filePath.exists()){
            val bundle = Bundle()
            bundle.putString("pageTitle", bookName)
            bundle.putString("pdfFile", filePath.absolutePath)
            gotoFrag(R.id.action_global_pdfViewerFragment, bundle)

        } else {
            lifecycleScope.launch {
                viewmodel.getDigitalQuranSecureUrl(contentUrl, false, Id, bookName)
            }
        }
    }

    override fun bookLikeClicked(Id: Int, categoryId: Int, title: String, type: String) {


        if(!Subscription.isSubscribe){
            gotoFrag(R.id.action_global_subscriptionFragment)
            return
        }


        if (type == "PopularBooks"){
            Toast.makeText(requireContext(), "popularBook: "+Id, Toast.LENGTH_SHORT).show()
        }
        if (type == "NewBooks"){
            Toast.makeText(requireContext(), "newBook: "+Id, Toast.LENGTH_SHORT).show()
        }
        Log.d("TheBookIdDataData", "id: "+id)
    }

    override fun bookDownloadClickd(
        contentUrl: String,
        id: Int,
        title: String,
        mText: String,
        bitmap: Bitmap?
    ) {

         if(!Subscription.isSubscribe){
            gotoFrag(R.id.action_global_subscriptionFragment)
            return
        }

        lifecycleScope.launch {
            viewmodel.getDigitalQuranSecureUrl(contentUrl, true, id, title)
        }

        val book = DownloadedBook(id.toString(), title, mText)
        val jsonString = Gson().toJson(book)

        val file: File = File(requireActivity().getFilesDir(), "islamicbook")
        if (!file.exists()) {
            file.mkdir()
        }

        writeToFile(id.toString(), jsonString)
        writeImageToFile(id.toString(), bitmap)

        Log.d("TheBookClickDownload", "bottom")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.launch {
            viewmodel.clearDownloader()
        }
    }

    override fun updateDownloadProgress(
        filenameByUser: String?,
        progress: Int,
        isComplete: Boolean,
        notificationId: Int,
        isCancelled: Boolean
    ) {

        if(!this::islamicBookHomeAdapter.isInitialized) {
            return
        }

        val payload =  BookDownloadPayload(filenameByUser,progress,isComplete,notificationId,isCancelled)

        recyclerViewBook.post {
            islamicBookHomeAdapter.update(payload, filenameByUser)
        }

    }

    private fun writeToFile(fileName: String, data: String) {

        val file: File = File(requireActivity().getFilesDir(), "islamicbook/"+fileName)
        if (!file.exists()) {
            file.mkdir()
        }
        try {
            val gpxfile = File(file, "$fileName"+".json")
            val writer = FileWriter(gpxfile)
            writer.append(data)
            writer.flush()
            writer.close()
        } catch (e: Exception) {
        }
    }

    private fun writeImageToFile(fileName: String, bitmap: Bitmap?){

        val file: File = File(requireActivity().getFilesDir(), "islamicbook/"+fileName+"/"+fileName+".jpg")

        // Convert the Bitmap to a ByteArrayOutputStream
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        // Write the ByteArrayOutputStream to the file
        val fos = FileOutputStream(file)
        fos.write(stream.toByteArray())
        fos.close()
    }
}