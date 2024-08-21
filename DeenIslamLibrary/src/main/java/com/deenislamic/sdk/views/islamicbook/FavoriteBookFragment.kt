package com.deenislamic.sdk.views.islamicbook

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
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
import com.deenislamic.sdk.viewmodels.IslamicBookViewModel
import com.deenislamic.sdk.views.adapters.islamicbook.IslamicBookItemAdapter
import com.deenislamic.sdk.views.adapters.islamicbook.IslamicBookItemCallback
import com.deenislamic.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch
import java.io.File


internal class FavoriteBookFragment(
    private val checkFirstload: Boolean = false
) : BaseRegularFragment(), IslamicBookItemCallback, DownloaderCallback {

    private val CANCEL_ACTION = "cancel_action"
    private val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    private val EXTRA_FILENAME = "extra_filename"

    private lateinit var listView: RecyclerView
    private lateinit var islamicBookItemAdapter: IslamicBookItemAdapter

    private var books :ArrayList<Data> = arrayListOf()

    private lateinit var viewModel: IslamicBookViewModel

    private var bookId: Int = -1
    private var bookTitle: String = ""
    private var firstload = false

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
        val mainView = localInflater.inflate(R.layout.fragment_favorite_book,container,false)

        //init view
        listView = mainView.findViewById(R.id.listView)

        setupCommonLayout(mainView)
        CallBackProvider.setFragment(this)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        islamicBookItemAdapter = IslamicBookItemAdapter(this@FavoriteBookFragment)

        listView.apply {
            adapter = islamicBookItemAdapter
        }

        initObserver()
        initSecureLinkObserver()
        favoriteBookObserver()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if(menuVisible) {
            CallBackProvider.setFragment(this)

            if(checkFirstload && !firstload) {
                baseLoadingState()
                loadApiData()
            }
            else if(!checkFirstload)
                loadApiData()
            firstload = true

        }
    }

    private fun loadApiData()
    {
            lifecycleScope.launch {
                viewModel.getFavouriteBooks(language = getLanguage(), page = 1, limit = 100)
            }
    }

    private fun initObserver() {

        viewModel.favoritedIslamicBooksLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is IslamicBookResource.BookItemData -> {

                    books = it.data as ArrayList<Data>

                    islamicBookItemAdapter.update(books)

                    if (books.isEmpty()){
                        baseEmptyState()
                    }

                    lifecycleScope.launch {
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

    private fun downloadOfflineBook(
        fileid: String,
        name: String,
        title: String
    ){
        // Assuming `quranDownloadService` is an instance of QuranDownloadService
        val downloadUrl =  fileid
        Log.e("downloadUrl",downloadUrl)
        //val destinationFolder = File(requireContext().getExternalFilesDir(null), "quran/test")
        val destinationFolder = File(requireContext().filesDir, "islamicbook")
        val intent = Intent(requireContext(), QuranDownloadService::class.java)

        intent.putExtra("filename", name+".pdf")
        intent.putExtra("filetitle", title)
        intent.putExtra("iszip", false)
        intent.putExtra("downloadUrl", downloadUrl)
        intent.putExtra("destinationFolder", destinationFolder.absolutePath)

        requireActivity().startService(intent)
    }

    override fun noInternetRetryClicked() {
        loadApiData()
    }

    override fun bookItemClieked(Id: Int, contentUrl: String, bookName: String) {
        lifecycleScope.launch {
            viewModel.getDigitalQuranSecureUrl(contentUrl, false, Id, bookName)
        }
    }

    override fun bookLikeClicked(
        Id: Int,
        categoryId: Int,
        title: String,
        isFavorite: Boolean,
        position: Int
    ) {
        lifecycleScope.launch {
            viewModel.makeBookFavorite(ContentId = Id.toString(), isFavorite = false, language = getLanguage())
        }
        books.removeAt(position)
        islamicBookItemAdapter.update(books)

        if (books.isEmpty()){
            baseEmptyState()
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
}