package com.deenislamic.sdk.views.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.quran.QuranOfflineDownloadCallback
import com.deenislamic.sdk.service.libs.alertdialog.CustomAlertDialog
import com.deenislamic.sdk.service.libs.alertdialog.CustomDialogCallback
import com.deenislamic.sdk.service.network.response.quran.qurangm.surahlist.Data
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.gson.Gson
import java.io.File

internal class QuranDownloadFragment(
    private val isHideActionBar: Boolean = false
) : BaseRegularFragment(), QuranOfflineDownloadCallback,
    CustomDialogCallback {

    private lateinit var listview:RecyclerView
    private lateinit var actionbar:ConstraintLayout
    private lateinit var offlineDownloadListAdapter: OfflineDownloadListAdapter
    private var customAlertDialog: CustomAlertDialog? =null
    private var folderLocation = ""
    private var absoluteAdapterPosition:Int  = -1
    private var surahId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainView = localInflater.inflate(R.layout.fragment_quran_download,container,false)
        setupActionForOtherFragment(0,0,null,localContext.getString(R.string.download_list),true,mainView)

        listview = mainView.findViewById(R.id.listview)
        actionbar = mainView.findViewById(R.id.actionbar)
        CallBackProvider.setFragment(this)
        setupCommonLayout(mainView)

        if(isHideActionBar)
            actionbar.hide()

        customAlertDialog = CustomAlertDialog().getInstance()
        customAlertDialog?.setupDialog(
            callback = this@QuranDownloadFragment,
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

        getOfflineDownloadList()
    }

    private fun getOfflineDownloadList() {
        val destinationFolder = File(requireContext().filesDir, "quran")

        // Ensure the directory exists
        destinationFolder.mkdirs()

        // Get the list of subdirectories
        val subdirectories = destinationFolder.listFiles { file -> file.isDirectory }

        // Define the file name to check
        val fileNameToCheck = "surahinfo.json"

        // Create a list to store SurahData objects
        val surahDataList = mutableListOf<Data>()

        // Iterate through subdirectories
        subdirectories?.forEach { subdirectory ->
            // Check if the subdirectory contains the specified file
            val fileToCheck = File(subdirectory, fileNameToCheck)
            if (fileToCheck.exists()) {
                // Read the content of the JSON file
                val jsonContent = fileToCheck.readText()

                // Parse the JSON data into a SurahData object using Gson
                val surahData = Gson().fromJson(jsonContent, Data::class.java)

                // Set the folder location for each SurahData object
                surahData.folderLocation = subdirectory.absolutePath

                // Add the SurahData object to the list
                surahDataList.add(surahData)
            }
        }

        listview.apply {
            offlineDownloadListAdapter = OfflineDownloadListAdapter(surahDataList)
            adapter = offlineDownloadListAdapter
        }

        if(surahDataList.isEmpty())
            baseEmptyState()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if(menuVisible){
            customAlertDialog?.setupDialog(
                callback = this@QuranDownloadFragment,
                context = requireContext(),
                btn1Text = localContext.getString(R.string.cancel),
                btn2Text = localContext.getString(R.string.delete),
                titileText = localContext.getString(R.string.want_to_delete),
                subTitileText = localContext.getString(R.string.do_you_want_to_remove_this_download)
            )
        }
    }


    override fun playBtnClicked(getData: Data) {

        surahId = getData.SurahId
        playOfflineQuran(getData)
    }

    override fun deleteOfflineQuran(folderLocation: String, absoluteAdapterPosition: Int) {

        this.absoluteAdapterPosition = absoluteAdapterPosition
        this.folderLocation = folderLocation
        customAlertDialog?.showDialog(false)
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

        stopOfflineQuran(surahId)

        val folder = File(folderLocation)

        if(deleteFolder(folder)){
            if(absoluteAdapterPosition!=-1)
            offlineDownloadListAdapter.removeItem(absoluteAdapterPosition)
            if(offlineDownloadListAdapter.itemCount == 0)
                baseEmptyState()

            customAlertDialog?.dismissDialog()
            absoluteAdapterPosition = -1
        }
        else
            context?.toast("Failed to delete")
    }
}