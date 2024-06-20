package com.deenislamic.sdk.views.dashboard.patch;

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import java.util.Calendar

internal class Greeting(widget: View, items: List<Item>) {

    private var greetingTxt: AppCompatTextView = widget.findViewById(R.id.greeting_txt)
    private var greetingDate: AppCompatTextView = widget.findViewById(R.id.greetingDate)
    private var greetingIcon: AppCompatImageView = widget.findViewById(R.id.greetingIcon)

    init {

        if (items.isNotEmpty()) {
            val data = items[0]
            greetingTxt.text = data.Title
            greetingDate.text = data.Text
            greetingIcon.imageLoad(data.contentBaseUrl + "/" + data.imageurl1)
        } else {
            widget.hide()
        }
    }

    private fun getGreetingMessage(): String {
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)
        val localContext = greetingTxt.context
        return when (timeOfDay) {
            in 0..11 -> localContext?.getString(R.string.text_good_morning).toString()
            in 12..15 -> localContext?.getString(R.string.text_good_afternoon).toString()
            in 16..20 -> localContext?.getString(R.string.text_good_evening).toString()
            in 21..23 -> localContext?.getString(R.string.text_good_night).toString()
            else -> localContext?.getString(R.string.text_good_afternoon).toString()
        }
    }
}
