package com.deenislam.sdk.views.dashboard.patch;

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.DashboardPatchCallback
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.utils.show

internal class Compass(private val itemView: View) {

    private var textContent: AppCompatTextView? = itemView.findViewById(R.id.textContent)
    private var subContent: AppCompatTextView? = itemView.findViewById(R.id.subContent)
    private var icon: AppCompatImageView? = itemView.findViewById(R.id.icon)
    private var itemTitle: AppCompatTextView? = itemView.findViewById(R.id.itemTitle)
    private var compassDial: ConstraintLayout? = itemView.findViewById(R.id.compassDial)
    private var compassKaaba: AppCompatImageView? = itemView.findViewById(R.id.compassKaaba)
    private val dashboardPatchCallback = CallBackProvider.get<DashboardPatchCallback>()
    private val compassBg:AppCompatImageView = itemView.findViewById(R.id.compassBg)

    fun load(items: List<Item>) {
        // No need for instance reference anymore

        if (items.isNotEmpty()) {
            val data = items[0]

            icon?.show()

            icon?.let {
                it.setImageDrawable(
                    AppCompatResources.getDrawable(
                        it.context,
                        R.drawable.deen_ic_qibla_compass
                    )
                )
                textContent?.text = it.context.getString(R.string.compass_degree_txt, "--")
            }

            compassBg.imageLoad(url = BASE_CONTENT_URL_SGP+data.imageurl1, placeholder_1_1 = true, custom_placeholder_1_1 = R.drawable.compass_dial, customMemoryKey = "compassBG")

            itemTitle?.show()
            itemTitle?.text = data.Title

            itemView.setOnClickListener {
                dashboardPatchCallback?.dashboardPatchClickd(data.ContentType, data)
            }
        } else
            itemView.hide()
    }

    fun updateCompass(degree: Float, degreeTxt: String) {
        compassDial?.rotation = degree
        textContent?.text = degreeTxt
        textContent?.show()
    }

    fun updateCompass(distance: String) {
        subContent?.text = distance
        subContent?.show()

    }
}
