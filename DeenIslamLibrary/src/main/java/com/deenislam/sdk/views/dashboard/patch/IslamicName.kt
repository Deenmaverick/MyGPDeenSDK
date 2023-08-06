package com.deenislam.sdk.views.dashboard.patch;

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.DashboardPatchCallback
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.imageLoad
import com.google.android.material.button.MaterialButton

internal class IslamicName {

    companion object {
        var instance: IslamicName? = null
    }

    fun getInstance(): IslamicName {
        if (instance == null)
            instance = IslamicName()

        return instance as IslamicName
    }

    fun load(
        widget: View,
        data: com.deenislam.sdk.service.network.response.dashboard.IslamicName,
        dashboardPatchCallback: DashboardPatchCallback
    )
    {
        val icon: AppCompatImageView = widget.findViewById(R.id.icon)
        val titile: AppCompatTextView = widget.findViewById(R.id.titile)
        val banner: AppCompatImageView = widget.findViewById(R.id.banner)
        val textContent: AppCompatTextView = widget.findViewById(R.id.textContent)
        val subContent:AppCompatTextView = widget.findViewById(R.id.subContent)
        val mainBtn: MaterialButton = widget.findViewById(R.id.mainBtn)


        widget.setOnClickListener {
            dashboardPatchCallback.dashboardPatchClickd("IslamicName")
        }

        subContent.hide()

        icon.setImageDrawable(
            AppCompatResources.getDrawable(
                icon.context,
                R.drawable.ic_menu_islamic_name
            )
        )

        titile.text = data.Title
        textContent.text = data.Text
        banner.imageLoad(data.contentBaseUrl+"/"+data.imageurl1)
        mainBtn.text = mainBtn.context.getString(R.string.islamic_name)
    }
}