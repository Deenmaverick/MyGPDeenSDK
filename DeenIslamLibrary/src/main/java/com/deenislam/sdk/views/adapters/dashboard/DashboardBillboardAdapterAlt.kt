package com.deenislam.sdk.views.adapters.dashboard;

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.utils.*
import com.deenislam.sdk.views.base.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

private const val PRAYER_VIEW = 1
private const val BILLBOARD_VIEW = 2
