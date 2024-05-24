package com.deenislam.sdk.views.common

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.database.AppPreference
import com.deenislam.sdk.service.models.common.ContentSetting
import com.deenislam.sdk.utils.*
import com.deenislam.sdk.viewmodels.common.ContentSettingViewModel
import com.deenislam.sdk.views.adapters.quran.quranplayer.PlayerCommonSelectionList
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

internal object CommonContentSetting {

    private lateinit var contentSetting: ContentSetting
    private var lifecycleScope:LifecycleCoroutineScope ? = null
    private var dialog: Dialog? = null
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : WeakReference<View>
    private var contentSettingViewModel: ContentSettingViewModel? = null
    private lateinit var arabicFontCommonSelectionList: PlayerCommonSelectionList


    fun setup(viewmodel: ContentSettingViewModel, lifecycleCoroutineScope: LifecycleCoroutineScope){
        this.contentSettingViewModel = viewmodel
        this.lifecycleScope = lifecycleCoroutineScope
    }

    fun showDialog(context: Context,localContext: Context, inflater: LayoutInflater) {

        contentSetting = AppPreference.getContentSetting()

        dialog?.dismiss()

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(context, R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = WeakReference(inflater.inflate(R.layout.dialog_content_setting, null, false))

        customAlertDialogView.get()?.let {

            val arabicFontSlider: Slider = it.findViewById(R.id.arabicFontSlider)
            val arabicFontSize: AppCompatTextView = it.findViewById(R.id.arabicFontSize)
            val banglaFontSlider: Slider = it.findViewById(R.id.banglaFontSlider)
            val banglaFontSize: AppCompatTextView = it.findViewById(R.id.banglaFontSize)
            val chooseArabicFont: MaterialButton = it.findViewById(R.id.chooseArabicFont)


            chooseArabicFont.setOnClickListener {
                dialog_select_arabic_font(context,localContext,inflater)
            }

            arabicFontSlider.addOnChangeListener { _, value, _ ->
                contentSetting.arabicFontSize = value
                AppPreference.setContentSetting(contentSetting)
                lifecycleScope?.launch(Dispatchers.IO) {
                    contentSettingViewModel?.update(contentSetting)
                }
            }

            banglaFontSlider.addOnChangeListener { _, value, _ ->
                contentSetting.banglaFontSize = value
                AppPreference.setContentSetting(contentSetting)
                lifecycleScope?.launch(Dispatchers.IO) {
                    contentSettingViewModel?.update(contentSetting)
                }
            }


            //apply setting
            val arbfontsize = (((contentSetting.arabicFontSize.coerceIn(0F, 100F) + 10) / 20).toInt() * 20).toFloat()
            arabicFontSize.text = "${arbfontsize.toInt()}%".numberLocale()
            arabicFontSlider.value =  arbfontsize

            val bnfontsize = (((contentSetting.banglaFontSize.coerceIn(0F, 100F) + 10) / 20).toInt() * 20).toFloat()
            banglaFontSize.text = "${bnfontsize.toInt()}%".numberLocale()
            banglaFontSlider.value =  bnfontsize

            chooseArabicFont.text = context.getArabicFontList().firstOrNull { trn-> trn.fontid == contentSetting.arabicFont.toString() }?.fontname.toString()


            dialog = materialAlertDialogBuilder
                .setView(it)
                .setCancelable(true)
                .show().apply {
                    window?.setGravity(Gravity.CENTER)
                }
        }

    }

    private fun dialog_select_arabic_font(context: Context,localContext: Context, inflater: LayoutInflater)
    {

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(context, R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = WeakReference(inflater.inflate(R.layout.dialog_translator_list, null, false))

        // Initialize and assign variable

        customAlertDialogView.get()?.let {

            val translationByTxt: AppCompatTextView = it.findViewById(R.id.translationByTxt)
            val banglaTranList: RecyclerView = it.findViewById(R.id.banglaTranList)
            val translationByTxt1: AppCompatTextView = it.findViewById(R.id.translationByTxt1)
            val englishTranList: RecyclerView = it.findViewById(R.id.englishTranList)
            val dismissBtn = it.findViewById<ImageButton>(R.id.closeBtn)
            val title: AppCompatTextView = it.findViewById(R.id.pageTitle)

            translationByTxt1.hide()
            englishTranList.hide()

            translationByTxt.text = localContext.getString(R.string.font_settings)
            title.text = localContext.getString(R.string.arabic_font)

            val callback = CallBackProvider.get<PlayerCommonSelectionList.PlayerCommonSelectionListCallback>()

            callback?.let {getCallback ->
                banglaTranList.apply {
                    arabicFontCommonSelectionList = PlayerCommonSelectionList(
                        ArrayList(localContext.getArabicFontList().map { fdata -> transformArabicFontData(fdata) }),getCallback)
                    adapter = arabicFontCommonSelectionList

                }
            }


            val updatedData = arabicFontCommonSelectionList.getData().map { data ->
                data.copy(isSelected = data.Id == contentSetting.arabicFont)
            }

            arabicFontCommonSelectionList.update(updatedData)

            dismissBtn?.setOnClickListener {
                dialog?.dismiss()
            }

            // show dialog

            dialog = materialAlertDialogBuilder
                .setView(it)
                .setCancelable(true)
                .show()
        }

    }

    fun closeDialog(){
        dialog?.dismiss()
    }
}