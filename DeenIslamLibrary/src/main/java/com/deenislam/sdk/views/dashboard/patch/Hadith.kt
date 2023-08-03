package com.deenislam.sdk.views.dashboard.patch;

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.imageLoad
import com.google.android.material.button.MaterialButton

internal class Hadith {

    companion object {
        var instance: Hadith? = null
    }

    fun getInstance(): Hadith {
        if (instance == null)
            instance = Hadith()

        return instance as Hadith
    }

    fun load(
        widget: View,
        data: com.deenislam.sdk.service.network.response.dashboard.Hadith
    )
    {
        val icon: AppCompatImageView = widget.findViewById(R.id.icon)
        val titile:AppCompatTextView = widget.findViewById(R.id.titile)
        val banner:AppCompatImageView = widget.findViewById(R.id.banner)
        val textContent:AppCompatTextView = widget.findViewById(R.id.textContent)
        val subContent:AppCompatTextView = widget.findViewById(R.id.subContent)
        val mainBtn:MaterialButton = widget.findViewById(R.id.mainBtn)

        icon.setImageDrawable(
            AppCompatResources.getDrawable(
                icon.context,
                R.drawable.ic_menu_hadith
            )
        )
        titile.text = data.Title
        textContent.text = data.HadithArabicText.substring(0,100)+"..."
        subContent.text = data.HadithText.substring(0,100)+"..."
        banner.imageLoad(data.contentBaseUrl+"/"+data.imageurl1)
        mainBtn.text = mainBtn.context.getString(R.string.read_the_full_hadith)
    }
}