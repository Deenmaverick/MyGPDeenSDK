package com.deenislam.sdk.views.dashboard.patch;

import android.text.TextUtils
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.DashboardPatchCallback
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.utils.show
import com.google.android.material.button.MaterialButton

internal class DailyVerse {

    private val dashboardPatchCallback:DashboardPatchCallback ? = null
    companion object
    {
        var instance: DailyVerse? = null
    }


    fun getInstance(): DailyVerse {
        if (instance == null)
            instance = DailyVerse()

        return instance as DailyVerse
    }

    fun load(
        widget: View,
        data: com.deenislam.sdk.service.network.response.dashboard.DailyVerse,
        dashboardPatchCallback: DashboardPatchCallback
    ) {

        val icon: AppCompatImageView = widget.findViewById(R.id.icon)
        val titile: AppCompatTextView = widget.findViewById(R.id.titile)
        val banner: AppCompatImageView = widget.findViewById(R.id.banner)
        val textContent: AppCompatTextView = widget.findViewById(R.id.textContent)
        val midContent:AppCompatTextView = widget.findViewById(R.id.midContent)
        val subContent: AppCompatTextView = widget.findViewById(R.id.subContent)
        val mainBtn: MaterialButton = widget.findViewById(R.id.mainBtn)

        widget.setOnClickListener {
            dashboardPatchCallback.dashboardPatchClickd("Verse")
        }

        icon.setImageDrawable(
            AppCompatResources.getDrawable(
                icon.context,
                R.drawable.ic_menu_quran
            )
        )
        titile.text = data.Title

        /*try {

            textContent.let {
                val customFont = ResourcesCompat.getFont(it.context, R.font.kfgqpc_font)
                it.typeface = customFont
            }

        } catch (e: Exception) {
            // Handle or log the exception
        }*/

        textContent.maxLines = 2
        textContent.ellipsize = TextUtils.TruncateAt.END
        textContent.text = data.ArabicText


        midContent.maxLines = 2
        midContent.ellipsize = TextUtils.TruncateAt.END
        midContent.text = data.Text
        midContent.show()

        subContent.text = data.Reference

        if(data.Reference == null)
            subContent.hide()

        banner.imageLoad(data.contentBaseUrl+"/"+data.imageurl1)
        mainBtn.text = mainBtn.context.getString(R.string.read_the_full_surah)
    }
}