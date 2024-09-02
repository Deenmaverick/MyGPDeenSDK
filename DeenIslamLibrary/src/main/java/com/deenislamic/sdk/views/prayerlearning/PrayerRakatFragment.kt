package com.deenislamic.sdk.views.prayerlearning

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.models.prayerlearning.Rakat
import com.deenislamic.sdk.utils.load
import com.deenislamic.sdk.utils.shareView
import com.deenislamic.sdk.views.adapters.prayerlearning.PrayerRakatAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

internal class PrayerRakatFragment : BaseRegularFragment(), otherFagmentActionCallback {

    private lateinit var fajrlayout:ConstraintLayout
    private lateinit var dhuhrLayout:ConstraintLayout
    private lateinit var jummaLayout:ConstraintLayout
    private lateinit var asrLayout:ConstraintLayout
    private lateinit var maghribLayout:ConstraintLayout
    private lateinit var ishaLayout:ConstraintLayout

    private lateinit var fajrRakatList: RecyclerView
    private lateinit var dhuhrRakatList: RecyclerView
    private lateinit var jummaRakatList: RecyclerView
    private lateinit var asrRakatList: RecyclerView
    private lateinit var maghribRakatList: RecyclerView
    private lateinit var ishaRakatList: RecyclerView

    private lateinit var redHint:ConstraintLayout
    private lateinit var orangeHint:ConstraintLayout
    private lateinit var greenHint:ConstraintLayout
    private lateinit var skyBlueHint:ConstraintLayout
    private lateinit var blueHint:ConstraintLayout

    private lateinit var fajrRakaIcon: AppCompatImageView
    private lateinit var dhuhrRakaIcon: AppCompatImageView
    private lateinit var jummaRakaIcon: AppCompatImageView
    private lateinit var asrRakaIcon: AppCompatImageView
    private lateinit var maghribRakaIcon: AppCompatImageView
    private lateinit var ishaRakaIcon: AppCompatImageView

    private lateinit var nestedScrollView:NestedScrollView
    private lateinit var cardView:MaterialCardView


    private val navArgs:PrayerRakatFragmentArgs by navArgs()

    private val fajr = arrayListOf(
        Rakat(2, R.color.deen_brand_secondary),
        Rakat(2,R.color.deen_brand_error),
        Rakat(),
        Rakat(),
        Rakat(),
        Rakat()
    )

    private val dhuhr = arrayListOf(
        Rakat(4,R.color.deen_brand_secondary),
        Rakat(4,R.color.deen_brand_error),
        Rakat(2,R.color.deen_brand_secondary),
        Rakat(2,R.color.deen_brand_blue),
        Rakat(),
        Rakat()
    )

    private val jummah = arrayListOf(
        Rakat(4,R.color.deen_brand_secondary),
        Rakat(2,R.color.deen_brand_error),
        Rakat(4,R.color.deen_brand_secondary),
        Rakat(2,R.color.deen_brand_secondary),
        Rakat(2,R.color.deen_brand_blue),
        Rakat()
    )

    private val asr = arrayListOf(
        Rakat(4,R.color.deen_brand_sky_blue),
        Rakat(4,R.color.deen_brand_error),
        Rakat(),
        Rakat(),
        Rakat(),
        Rakat()
    )

    private val maghrib = arrayListOf(
        Rakat(),
        Rakat(3,R.color.deen_brand_error),
        Rakat(2,R.color.deen_brand_secondary),
        Rakat(2,R.color.deen_brand_blue),
        Rakat(),
        Rakat()
    )

    private val isha = arrayListOf(
        Rakat(4,R.color.deen_brand_sky_blue),
        Rakat(4,R.color.deen_brand_error),
        Rakat(2,R.color.deen_brand_secondary),
        Rakat(2,R.color.deen_brand_blue),
        Rakat(3,R.color.deen_brand_orange),
        Rakat(2,R.color.deen_brand_blue),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_prayer_rakat,container,false)

        nestedScrollView = mainView.findViewById(R.id.nestedScrollView)
        cardView = mainView.findViewById(R.id.container)

        fajrlayout = mainView.findViewById(R.id.fajrlayout)
        dhuhrLayout = mainView.findViewById(R.id.dhuhrLayout)
        jummaLayout = mainView.findViewById(R.id.jummaLayout)
        asrLayout = mainView.findViewById(R.id.asrLayout)
        maghribLayout = mainView.findViewById(R.id.maghribLayout)
        ishaLayout = mainView.findViewById(R.id.ishaLayout)

        // Initialize RecyclerViews for each prayer
        fajrRakatList = fajrlayout.findViewById(R.id.rakatList)
        dhuhrRakatList = dhuhrLayout.findViewById(R.id.rakatList)
        jummaRakatList = jummaLayout.findViewById(R.id.rakatList)
        asrRakatList = asrLayout.findViewById(R.id.rakatList)
        maghribRakatList = maghribLayout.findViewById(R.id.rakatList)
        ishaRakatList = ishaLayout.findViewById(R.id.rakatList)

        // Initialize fajrRakaIcon and other icons in their respective layouts
        fajrRakaIcon = fajrlayout.findViewById(R.id.icon)
        dhuhrRakaIcon = dhuhrLayout.findViewById(R.id.icon)
        jummaRakaIcon = jummaLayout.findViewById(R.id.icon)
        asrRakaIcon = asrLayout.findViewById(R.id.icon)
        maghribRakaIcon = maghribLayout.findViewById(R.id.icon)
        ishaRakaIcon = ishaLayout.findViewById(R.id.icon)

        // Load image for fajrRakaIcon
        fajrRakaIcon.load(R.drawable.deen_ic_pl_fajr)

        dhuhrRakaIcon.load(R.drawable.deen_ic_pl_duhr)

        jummaRakaIcon.load(R.drawable.deen_ic_pl_jummah)

        asrRakaIcon.load(R.drawable.deen_ic_pl_asr)

        maghribRakaIcon.load(R.drawable.deen_ic_pl_maghrib)

        ishaRakaIcon.load(R.drawable.deen_ic_pl_isha)



        // Set title text for each prayer layout
        fajrlayout.findViewById<AppCompatTextView>(R.id.title).text = localContext.getString(R.string.prayer_fajr)
        dhuhrLayout.findViewById<AppCompatTextView>(R.id.title).text = localContext.getString(R.string.prayer_dhuhr)
        jummaLayout.findViewById<AppCompatTextView>(R.id.title).text = localContext.getString(R.string.jummah)
        asrLayout.findViewById<AppCompatTextView>(R.id.title).text = localContext.getString(R.string.prayer_asr)
        maghribLayout.findViewById<AppCompatTextView>(R.id.title).text = localContext.getString(R.string.prayer_maghrib)
        ishaLayout.findViewById<AppCompatTextView>(R.id.title).text = localContext.getString(R.string.prayer_isha)

        redHint = mainView.findViewById(R.id.redHint)
        orangeHint = mainView.findViewById(R.id.orangeHint)
        greenHint = mainView.findViewById(R.id.greenHint)
        skyBlueHint = mainView.findViewById(R.id.skyBlueHint)
        blueHint = mainView.findViewById(R.id.blueHint)

        // Store references to MaterialButtons
        val redHintButton = redHint.findViewById<MaterialButton>(R.id.hintBtn)
        val orangeHintButton = orangeHint.findViewById<MaterialButton>(R.id.hintBtn)
        val greenHintButton = greenHint.findViewById<MaterialButton>(R.id.hintBtn)
        val skyBlueHintButton = skyBlueHint.findViewById<MaterialButton>(R.id.hintBtn)
        val blueHintButton = blueHint.findViewById<MaterialButton>(R.id.hintBtn)



        context?.let {

            redHintButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(it, R.color.deen_brand_error))
            orangeHintButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(it, R.color.deen_brand_orange))
            greenHintButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(it, R.color.deen_brand_secondary))
            skyBlueHintButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(it, R.color.deen_brand_sky_blue))
            skyBlueHintButton.setTextColor(ContextCompat.getColor(it, R.color.deen_txt_black_deep))
            blueHintButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(it, R.color.deen_brand_blue))
        }


        // Set text using localContext.getString()
        redHintButton.text = localContext.getString(R.string.fardh)
        orangeHintButton.text = localContext.getString(R.string.wajib)
        greenHintButton.text = localContext.getString(R.string.sunnah_muakkadah)
        skyBlueHintButton.text = localContext.getString(R.string.sunnah_ghiar_muakkadah)
        blueHintButton.text = localContext.getString(R.string.nafl)



        redHint.findViewById<AppCompatTextView>(R.id.hint).text = localContext.getString(R.string.pt_rakat_red_hint)
        orangeHint.findViewById<AppCompatTextView>(R.id.hint).text = localContext.getString(R.string.pt_rakat_orange_hint)
        greenHint.findViewById<AppCompatTextView>(R.id.hint).text = localContext.getString(R.string.pt_rakat_green_hint)
        skyBlueHint.findViewById<AppCompatTextView>(R.id.hint).text = localContext.getString(R.string.pt_rakat_sky_blue_hint)
        blueHint.findViewById<AppCompatTextView>(R.id.hint).text = localContext.getString(R.string.pt_rakat_blue_hint)



        setupActionForOtherFragment(R.drawable.ic_share,0,this,navArgs.title,true,mainView, actionIconColor = R.color.deen_txt_black_deep)
        setupCommonLayout(mainView)
        // Inflate the layout for this fragment
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Set up RecyclerView adapters for each prayer
        fajrRakatList.apply {
            adapter = PrayerRakatAdapter(fajr)
        }

        dhuhrRakatList.apply {
            adapter = PrayerRakatAdapter(dhuhr)
        }

        jummaRakatList.apply {
            adapter = PrayerRakatAdapter(jummah)
        }

        asrRakatList.apply {
            adapter = PrayerRakatAdapter(asr)
        }

        maghribRakatList.apply {
            adapter = PrayerRakatAdapter(maghrib)
        }

        ishaRakatList.apply {
            adapter = PrayerRakatAdapter(isha)
        }

        baseViewState()

    }

    override fun action1() {
        //activity?.window?.let { context?.shareView(cardView, it) }
        context?.shareView(cardView)

    }

    override fun action2() {

    }



}