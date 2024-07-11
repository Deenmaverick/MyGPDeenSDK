package com.deenislamic.sdk.views.islamicbook

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.libs.alertdialog.CustomAlertDialog
import com.deenislamic.sdk.service.libs.alertdialog.CustomDialogCallback
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.islamicbook.downloadedmodel.DownloadedBook
import com.deenislamic.sdk.views.islamicbook.adapter.DownloadedBookAdapter
import com.deenislamic.sdk.views.islamicbook.adapter.DownloadedIslamicBookItemCallback
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.File

internal class DownloadedBookFragment : BaseRegularFragment(), DownloadedIslamicBookItemCallback,
    CustomDialogCallback {

    private lateinit var listView: RecyclerView
    private lateinit var offlineDownloadListAdapter: DownloadedBookAdapter

    private var books :ArrayList<DownloadedBook> = arrayListOf()

    private var customAlertDialog: CustomAlertDialog? =null

    private var deletePosition = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_favorite_book,container,false)

        //init view
        listView = mainView.findViewById(R.id.listView)

        setupCommonLayout(mainView)

        customAlertDialog = CustomAlertDialog().getInstance()
        customAlertDialog?.setupDialog(
            callback = this@DownloadedBookFragment,
            context = requireContext(),
            btn1Text = localContext.getString(R.string.cancel),
            btn2Text = localContext.getString(R.string.delete),
            titileText = localContext.getString(R.string.want_to_delete),
            subTitileText = localContext.getString(R.string.do_you_want_to_remove_this_download)
        )

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!this::offlineDownloadListAdapter.isInitialized)
        offlineDownloadListAdapter = DownloadedBookAdapter(this@DownloadedBookFragment)


    }

    private fun getOfflineDownloadList() {
        val destinationFolder = File(requireContext().filesDir, "islamicbook")

        // Ensure the directory exists
        destinationFolder.mkdirs()

        // Get the list of subdirectories
        val subdirectories = destinationFolder.listFiles { file -> file.isDirectory }

        // Create a list to store SurahData objects
        val bookList = mutableListOf<DownloadedBook>()

        // Iterate through subdirectories
        subdirectories?.forEach { subdirectory ->

            if (File(subdirectory, subdirectory.name+".pdf").exists()){

                // Check if the subdirectory contains the specified file
                val fileToCheck = File(subdirectory, subdirectory.name+".json")
                val fileForImage = File(subdirectory, subdirectory.name+".jpg")
                if (fileToCheck.exists()) {
                    // Read the content of the JSON file
                    val jsonContent = fileToCheck.readText()

                    val bitmap = BitmapFactory.decodeFile(fileForImage.absolutePath)

                    Log.d("TheBirMapDataData", ""+fileForImage.absolutePath)

                    // Parse the JSON data into a SurahData object using Gson
                    val surahData = Gson().fromJson(jsonContent, DownloadedBook::class.java)
                    surahData.bookImage = bitmap

                    // Add the SurahData object to the list
                    bookList.add(surahData)
                }
            }
        }

        listView.apply {
            books = bookList as ArrayList<DownloadedBook>
            adapter = offlineDownloadListAdapter
            offlineDownloadListAdapter.update(bookList)

        }

        Log.d("TheSubDirectoriesData", ""+books)



        if (books.isEmpty()){
            baseEmptyState()
        }else
            baseViewState()
    }

    override fun deleteBookClieked(position: Int) {
        deletePosition = position
        customAlertDialog?.showDialog(false)
    }

    override fun bookItemClicked(position: Int) {
        val destinationFolder = File(requireContext().filesDir, "islamicbook/"+books.get(position).bookId+"/"+books.get(position).bookId+".pdf")
        Log.d("TheFileDirOfBook", ""+destinationFolder.absolutePath)

        val bundle = Bundle()
        bundle.putString("pageTitle", books.get(position).bookName)
        bundle.putString("pdfFile", destinationFolder.absolutePath)
        gotoFrag(R.id.action_global_pdfViewerFragment, bundle)
    }

    private fun deleteFolder(folder: File): Boolean {
        if (folder.isDirectory) {
            // Get the list of files and subdirectories in the folder
            val files = folder.listFiles()

            if (files != null) {
                // Delete each file and subdirectory
                for (file in files) {
                    if (file.isDirectory) {
                        // Recursive call to delete subdirectories
                        deleteFolder(file)
                    } else {
                        // Delete the file
                        if (!file.delete()) {
                            return false // Unable to delete file
                        }
                    }
                }
            }
        }

        // Delete the empty folder or the folder with all its contents
        return folder.delete()
    }

    override fun clickBtn1() {
        customAlertDialog?.dismissDialog()
    }

    override fun clickBtn2() {
        val destinationFolder = File(requireContext().filesDir, "islamicbook/"+books.get(deletePosition).bookId)
        deleteFolder(destinationFolder)
        books.removeAt(deletePosition)
        offlineDownloadListAdapter.update(books)

        customAlertDialog?.dismissDialog()

        if (books.isEmpty()){
            baseEmptyState()
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if (menuVisible){

            customAlertDialog?.setupDialog(
                callback = this@DownloadedBookFragment,
                context = requireContext(),
                btn1Text = localContext.getString(R.string.cancel),
                btn2Text = localContext.getString(R.string.delete),
                titileText = localContext.getString(R.string.want_to_delete),
                subTitileText = localContext.getString(R.string.do_you_want_to_remove_this_download)
            )

            CallBackProvider.setFragment(this)

            lifecycleScope.launch {
                getOfflineDownloadList()
            }

        }
    }

}