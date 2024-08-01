package com.deenislamic.sdk.service.libs

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.libs.photoview.PhotoView
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.load
import com.deenislamic.sdk.utils.shareImage
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.utils.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class ImageViewPopupDialog : DialogFragment() {

    private lateinit var photoView: PhotoView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DeenAppTheme_FullScreenDialog)

    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setWindowAnimations(R.style.DeenAppThemeSlide)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.item_custom_imageview_dialog, container, false)

        setupActionForOtherFragment(
            action1 = R.drawable.ic_share,
            action2 = 0,
            //action3 = R.drawable.baseline_close_24,
            actionIconColor = R.color.deen_white,
            actionnBartitle = arguments?.getString("title")?:"Image View",
            backEnable = true,
            isDarkActionBar = true,
            view = view
        )

        photoView = view.findViewById(R.id.photo)

        arguments?.let { bundle ->

            val imgDrawable = bundle.getInt("imgDrawable",-1)
            val imgUrl = bundle.getString("imgUrl")

            if(imgDrawable!=-1)
                photoView.load(imgDrawable)
            else
                photoView.load(imgUrl)


        }

        return view
    }

   /* override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    saveImageToGallery()
                } catch (e: Exception) {
                    context?.apply {
                        toast("Failed to save image!")
                    }
                }
            } else {
                // Permission is denied, show a message or take appropriate action

                activity?.let { activity ->
                    val localContext = context?.getLocalContext()
                    MaterialAlertDialogBuilder(activity, com.google.android.material.R.style.AlertDialog_AppCompat)
                        .setTitle(localContext?.getString(R.string.alert))
                        .setMessage(localContext?.getString(R.string.storage_permission_is_required_to_save_photo))
                        .setPositiveButton(localContext?.getString(R.string.okay)) { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("package:${localContext?.packageName}")
                            activity.startActivity(intent)
                        }
                        .setNegativeButton(localContext?.getString(R.string.cancel), null)
                        .show()
                }
            }

        }
    }*/





    companion object {
        private const val TAG = "imageview_dialog"
        fun display(fragmentManager: FragmentManager?,cusarg: Bundle?): ImageViewPopupDialog {
            val imageViewPopupDialog = ImageViewPopupDialog()
            imageViewPopupDialog.arguments = cusarg
            if (fragmentManager != null) {
                imageViewPopupDialog.show(fragmentManager, TAG)
                //imageViewPopupDialog.registerPermissionLauncher()
            }
            return imageViewPopupDialog
        }
    }

    fun setupActionForOtherFragment(
        action1:Int,
        action2:Int,
        actionnBartitle:String,
        backEnable:Boolean=true,
        view: View,
        isBackIcon:Boolean = false,
        isDarkActionBar:Boolean = false,
        actionIconColor:Int = 0,
        action3:Int=0,
    )
    {

        val actionbar:ConstraintLayout = view.findViewById(R.id.actionbar)
        val action1Btn: AppCompatImageView = view.findViewById(R.id.action1)
        val action2Btn: AppCompatImageView = view.findViewById(R.id.action2)
        val action3Btn: AppCompatImageView? = view.findViewById(R.id.action3)
        val btnBack: AppCompatImageView = view.findViewById(R.id.btnBack)
        val title: AppCompatTextView = view.findViewById(R.id.title)

        actionbar.setBackgroundColor(ContextCompat.getColor(view.context,R.color.deen_gray_secondary))

        if(actionIconColor!=0)
        {
            action1Btn.setColorFilter(ContextCompat.getColor(view.context,actionIconColor))
            action2Btn.setColorFilter(ContextCompat.getColor(view.context,actionIconColor))
            action3Btn?.setColorFilter(ContextCompat.getColor(view.context,actionIconColor))

        }

        if(backEnable)
        {
            btnBack.setImageDrawable(AppCompatResources.getDrawable(view.context, R.drawable.baseline_close_24))
            btnBack.visible(true)
            title.text = actionnBartitle

            if(isDarkActionBar)
                title.setTextColor(ContextCompat.getColor(title.context,R.color.deen_white))
            else
                title.setTextColor(ContextCompat.getColor(title.context,R.color.deen_txt_black_deep))

        }
        else
        {
            (title.layoutParams as ConstraintLayout.LayoutParams).apply {
                leftMargin=16.dp
            }
            title.text = actionnBartitle
            btnBack.visible(isBackIcon)

            if(isDarkActionBar)
                title.setTextColor(ContextCompat.getColor(title.context,R.color.deen_white))
            else
                title.setTextColor(ContextCompat.getColor(title.context,R.color.deen_txt_black_deep))

        }

        if(action1>0) {
            action1Btn.setImageDrawable(AppCompatResources.getDrawable(view.context, action1))
            action1Btn.visible(true)
            action1Btn.setOnClickListener {

                try {
                    val drawable = photoView.drawable
                    // Convert the Drawable to a Bitmap
                    val bitmap = drawable.toBitmap()
                    val content = arguments?.let { it.getString("content") }
                    context?.shareImage(bitmap,content)

                }catch (e:Exception){
                    context?.apply {
                        toast("Failed to share image!")
                    }
                }

            }
        }
        else
            action1Btn.visible(false)

        if(action2>0) {
            action2Btn.setImageDrawable(AppCompatResources.getDrawable(view.context, action2))
            action2Btn.visible(true)
            action2Btn.setOnClickListener {

                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission is already granted, proceed with saving image
                    // Call your saveImageToGallery function here
                    try {
                        saveImageToGallery()
                    }catch (e:Exception){
                        context?.apply {
                            toast("Failed to save image!")
                        }
                    }
                } else {
                  /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        requestPermissions(arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
                    }else{
                        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
                    }*/
                    // Permission has not been granted, request it
                    // Use the permission launcher for handling permission request
                   // requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
        else
            action2Btn.visible(false)

        if(action3>0) {
            action3Btn?.setImageDrawable(AppCompatResources.getDrawable(view.context, action3))
            action3Btn?.show()
            action3Btn?.setOnClickListener {
                dismiss()
            }
        }
        else
            action3Btn?.hide()

        btnBack.setOnClickListener {
            dismiss()
        }

    }


    private fun saveImageToGallery() {

        context?.let {
            if(this::photoView.isInitialized) {

                    val drawable = photoView.drawable
                    // Convert the Drawable to a Bitmap
                    val bitmap = drawable.toBitmap()

                    lifecycleScope.launch {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            saveImageAboveQ(
                                it,
                                bitmap,
                                arguments?.getString("title") ?: "Image View"
                            )
                        } else {
                            saveImageBelowQ(
                                it,
                                bitmap,
                                arguments?.getString("title") ?: "Image View"
                            )
                        }
                    }


            }
        }


    }



    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageAboveQ(context: Context, bitmap: Bitmap, des: String): Uri? {
        val contentResolver: ContentResolver = context.contentResolver
        val imageCollectionUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        // Create a new ContentValues object and populate it with the image details
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, generateFileName())
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.DESCRIPTION, des)
        }

        return saveImage(contentResolver, imageCollectionUri, imageDetails, bitmap)
    }

    private suspend fun saveImageBelowQ(context: Context, bitmap: Bitmap, des: String): Uri? {
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Deen"
        )

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, fileName)

        return try {
            val outputStream: OutputStream =
                withContext(Dispatchers.IO) {
                    FileOutputStream(file)
                }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            withContext(Dispatchers.IO) {
                outputStream.flush()
            }
            withContext(Dispatchers.IO) {
                outputStream.close()
            }

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, fileName)
                put(MediaStore.Images.Media.DESCRIPTION, des)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.DATA, file.absolutePath)
            }

            val contentResolver = context.contentResolver
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            uri?.let {
                // Notify system to scan the saved image
                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            }


            context.apply {
                toast("Image saved to gallery")
            }

            uri
        } catch (e: IOException) {
            context.apply {
                toast("Failed to save image!")
            }
            e.printStackTrace()
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImage(
        contentResolver: ContentResolver,
        collectionUri: Uri,
        imageDetails: ContentValues,
        bitmap: Bitmap
    ): Uri? {
        var imageUri: Uri? = null
        try {
            // Insert the ContentValues into the MediaStore
            imageUri = contentResolver.insert(collectionUri, imageDetails)

            // If the returned Uri is not null, attempt to open an OutputStream with the returned Uri
            imageUri?.let { uri ->
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    // Write the bitmap to the OutputStream
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                }
            }
        } catch (e: IOException) {
            context?.apply {
                toast("Failed to save image!")
            }
            e.printStackTrace()
            // Handle error
        }

        context?.apply {
            toast("Image saved to gallery")
        }

        return imageUri // Return the image Uri
    }

    private fun generateFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        return "IMG_$timeStamp.jpg"
    }
}

