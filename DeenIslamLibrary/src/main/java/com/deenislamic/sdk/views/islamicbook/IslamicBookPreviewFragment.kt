package com.deenislamic.sdk.views.islamicbook

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.DownloaderCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.downloader.QuranDownloadService
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicBookResource
import com.deenislamic.sdk.service.models.islamicbook.BookDownloadPayload
import com.deenislamic.sdk.service.network.response.islamicbook.Data
import com.deenislamic.sdk.service.repository.IslamicBookRepository
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.urlEncode
import com.deenislamic.sdk.viewmodels.IslamicBookViewModel
import com.deenislamic.sdk.views.adapters.islamicbook.IslamicBookItemAdapter
import com.deenislamic.sdk.views.adapters.islamicbook.IslamicBookItemCallback
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.islamicbook.downloadedmodel.DownloadedBook
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter


internal class IslamicBookPreviewFragment : BaseRegularFragment(),
    IslamicBookItemCallback, DownloaderCallback {

    private val CANCEL_ACTION = "cancel_action"
    private val EXTRA_NOTIFICATION_ID = "extra_notification_id"

    private lateinit var listView: RecyclerView
    private lateinit var islamicBookItemAdapter: IslamicBookItemAdapter

    private var books :ArrayList<Data> = arrayListOf()

    private lateinit var viewModel: IslamicBookViewModel

    private val navargs: IslamicBookPreviewFragmentArgs by navArgs()

    private var firstload: Boolean = false

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

        viewModel = IslamicBookViewModel(repository = repository, quranLearningRepository = quranLearningRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_islamic_book_preview,container,false)

        //init view
        listView = mainView.findViewById(R.id.listView)

        setupActionForOtherFragment(0,0,null,navargs.title?.let { it1-> it1 }?:localContext.getString(R.string.islamic_book),true, mainView)

        setupCommonLayout(mainView)

        CallBackProvider.setFragment(this)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        islamicBookItemAdapter = IslamicBookItemAdapter(this@IslamicBookPreviewFragment)

        initObserver()
        initSecureLinkObserver()
        favoriteBookObserver()

        if(!firstload) {
            loadApiData()
        }
        firstload = true

    }

    private fun loadApiData()
    {
        lifecycleScope.launch {
            if (navargs.bookType == "author"){
                viewModel.getBookItemByAuthors(Id = navargs.id, page = 1, limit = 100)
            } else if (navargs.bookType == "category"){
                viewModel.getBookItemByCategory(Id = navargs.id, page = 1, limit = 100)
            }
        }
    }


    private fun initObserver() {

        if (navargs.bookType == "author"){
            viewModel.bookByAuthorLiveData.observe(viewLifecycleOwner)
            {
                when(it)
                {
                    is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                    is CommonResource.EMPTY -> baseEmptyState()
                    is IslamicBookResource.BookItemData -> {

                        listView.apply {
                            adapter = islamicBookItemAdapter
                        }

                        books = it.data as ArrayList<Data>

                        islamicBookItemAdapter.update(books)

                        baseViewState()
                    }

                }
            }
        } else if (navargs.bookType == "category"){
            viewModel.bookByCategoryLiveData.observe(viewLifecycleOwner)
            {
                when(it)
                {
                    is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                    is CommonResource.EMPTY -> baseEmptyState()
                    is IslamicBookResource.BookItemData -> {

                        listView.apply {
                            adapter = islamicBookItemAdapter
                        }

                        books = it.data as ArrayList<Data>

                        islamicBookItemAdapter.update(books)

                        baseViewState()
                    }
                }
            }
        }
    }

    private fun initSecureLinkObserver(){

        viewModel.secureUrlLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is IslamicBookResource.PdfSecureUrl -> {
                    if (it.forDownload){
                        downloadOfflineBook(it.url, bookId.toString(), bookTitle)
                    } else {
                        //Log.d("ThePdfSecuredUrl", "url: "+it.url)
                        //Toast.makeText(requireContext(), "url: "+it.url, Toast.LENGTH_SHORT).show()
                        /*val bundle = Bundle()
                        bundle.putString("pageTitle", it.bookTitle)
                        bundle.putString("pdfUrl", it.url)
                        gotoFrag(R.id.action_global_pdfViewerFragment, bundle)*/

                        /*val bundle = Bundle()
                        bundle.putString("title",it.bookTitle)
                        bundle.putString("weburl","https://deenislamic.com/pdf?file="+ it.url.urlEncode())
                        gotoFrag(R.id.action_global_basicWebViewFragment,bundle)*/

                        val bundle = Bundle()
                        bundle.putString("pageTitle", it.bookTitle)
                        bundle.putString("pdfUrl", it.url)
                        gotoFrag(R.id.action_global_pdfViewerFragment, bundle)
                    }

                }
            }
        }
    }

    private fun favoriteBookObserver(){
        viewModel.favoriteBookLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> {}
                is IslamicBookResource.FavoriteBookItemData -> {
                    //Toast.makeText(requireContext(), "data: "+it.data.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun noInternetRetryClicked() {
        loadApiData()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {

        Log.d("setMenuVisibility", "boyanScholarsFragment")

        super.setMenuVisibility(menuVisible)
        if (menuVisible){
            CallBackProvider.setFragment(this)
        }
    }

    private fun downloadOfflineBook(
        fileid: String,
        name: String,
        title: String
    ){
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
                viewModel.getDigitalQuranSecureUrl(contentUrl, false, Id, bookName)
            }
        }
    }

    override fun bookLikeClicked(
        Id: Int,
        categoryId: Int,
        title: String,
        isFavorite: Boolean,
        position: Int
    ) {

        //Toast.makeText(requireContext(), "isFavorite: "+isFavorite, Toast.LENGTH_SHORT).show()
         /*if(!Subscription.isSubscribe){
            gotoFrag(R.id.action_global_subscriptionFragment)
            return
        }*/

        lifecycleScope.launch {
            viewModel.makeBookFavorite(ContentId = Id.toString(), isFavorite = isFavorite, language = getLanguage())
        }
    }

    override fun bookDownloadClickd(
        contentUrl: String,
        id: Int,
        title: String,
        authorName: String,
        bitmap: Bitmap?
    ) {

         /*if(!Subscription.isSubscribe){
            gotoFrag(R.id.action_global_subscriptionFragment)
            return
        }*/

        bookId = id
        bookTitle = title

        lifecycleScope.launch {
            viewModel.getDigitalQuranSecureUrl(contentUrl, true, id, title)
        }
        val book = DownloadedBook(id.toString(), title, authorName)
        val jsonString = Gson().toJson(book)

        val file: File = File(requireActivity().getFilesDir(), "islamicbook")
        if (!file.exists()) {
            file.mkdir()
        }

        writeToFile(id.toString(), jsonString)
        writeImageToFile(id.toString(), bitmap)
    }

    override fun bookDownloadCancelClicked(notificationId: Int) {

        val cancelIntent = Intent(requireContext(), QuranDownloadService::class.java)
        cancelIntent.action = CANCEL_ACTION
        cancelIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        requireContext().startService(cancelIntent)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.launch {
            viewModel.clearDownloader()
        }
    }

    override fun updateDownloadProgress(
        filenameByUser: String?,
        progress: Int,
        isComplete: Boolean,
        notificationId: Int,
        isCancelled: Boolean

    ) {

        if(!this::islamicBookItemAdapter.isInitialized) {
            return
        }

        val payload =  BookDownloadPayload(filenameByUser,progress,isComplete,notificationId,isCancelled)
        val getPosition = islamicBookItemAdapter.findPositionByData(filenameByUser)


        //Log.e("updateDownloadProgress", getPosition.toString())

        if(getPosition!=-1) {
            listView.post {
                islamicBookItemAdapter.notifyItemChanged(getPosition, payload)
            }
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