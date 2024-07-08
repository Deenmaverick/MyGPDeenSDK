package com.deenislamic.sdk.views.common.patch

import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.QurbaniCallback
import com.deenislamic.sdk.service.callback.common.SubCntentCallback
import com.deenislamic.sdk.service.database.AppPreference
import com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.qurbani.getBanglaSize
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.adapters.qurbani.QurbaniContentAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import org.jsoup.Jsoup

internal class RichContentPatch(private val itemView: View,
                       private val subCatResponse: Data?,
                       private val absoluteAdapterPosition: Int
): SubCntentCallback {
/*
    private val listView: RecyclerView = itemView.findViewById(R.id.listview)
    private val rootview: ConstraintLayout = itemView.findViewById(R.id.inf)
    private val itemTitle: AppCompatTextView = itemView.findViewById(R.id.itemTitle)
    private lateinit var qurbaniCommonContentAdapter: QurbaniCommonContentAdapter

    init {

        (listView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
        (rootview.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 8.dp
        itemTitle.text = itemTitle.context.getString(R.string.popular_sura)

        itemTitle.setPadding(12.dp,0,0,0)

        itemTitle.hide()

        subCatResponse?.let {
            if(!this::qurbaniCommonContentAdapter.isInitialized)
                qurbaniCommonContentAdapter = QurbaniCommonContentAdapter(it)

            listView.apply {
                layoutManager = LinearLayoutManager(this.context,RecyclerView.VERTICAL,false)
                adapter = qurbaniCommonContentAdapter
            }
        }

    }*/

    private var contentSetting = AppPreference.getContentSetting()
    private val callback = CallBackProvider.get<QurbaniCallback>()


    private val title: AppCompatTextView? = itemView.findViewById(R.id.title)
    private val subText: AppCompatTextView? = itemView.findViewById(R.id.subText)
    private val seeBtn: MaterialButton? = itemView.findViewById(R.id.seeBtn)
    private val contentList:RecyclerView? = itemView.findViewById(R.id.contentList)
    private val dotMenu: AppCompatImageView? = itemView.findViewById(R.id.dotMenu)
    private val container: MaterialCardView? = itemView.findViewById(R.id.inf)

    init {
        itemView.setPadding(16.dp,0,16.dp,0)
        load()
    }

    fun load() {


        val getsubtext = subCatResponse?.details?.firstOrNull()
        val subtext = if(getsubtext?.Text?.isNotEmpty() == true) getsubtext.Text else getsubtext?.Pronunciation

        title?.text = subCatResponse?.Title
        subText?.text = subtext?.let { Jsoup.parse(it).text() }

        title?.apply {
            setTextSize(
                TypedValue.COMPLEX_UNIT_SP,contentSetting.banglaFontSize.getBanglaSize(18F)
            )
        }

        subText?.apply {
            setTextSize(
                TypedValue.COMPLEX_UNIT_SP,contentSetting.banglaFontSize.getBanglaSize(16F)
            )
        }


        contentList?.apply {
            animation = null
            adapter = subCatResponse?.details?.let { QurbaniContentAdapter(it,this@RichContentPatch,absoluteAdapterPosition) }

        }

        if (subCatResponse?.isExpanded == true) {
            seeBtn?.text = seeBtn?.context?.getString(R.string.see_less)
            seeBtn?.icon = seeBtn?.context?.let { ContextCompat.getDrawable(it, R.drawable.deen_ic_dropdown_expand) }
            contentList?.show()
            subText?.hide()
            dotMenu?.show()

        } else {
            seeBtn?.text = seeBtn?.context?.getString(R.string.details)
            seeBtn?.icon = seeBtn?.context?.let { ContextCompat.getDrawable(it, R.drawable.ic_dropdown) }
            contentList?.hide()
            subText?.show()
            dotMenu?.hide()
        }

        itemView.setOnClickListener {

            subCatResponse?.let {

                it.isExpanded = !it.isExpanded
                container?.cardElevation = 1f
                load()
                callback?.qurbaniCommonContentClicked(absoluteAdapterPosition,it.isExpanded)
            }


        }



        if(subCatResponse?.Title.toString().isEmpty()) {
            title?.hide()
            subText?.setPadding(0,0,0,0)
        }
        if(subtext?.isEmpty() == true)
            subText?.hide()


        dotMenu?.setOnClickListener {
            if (subCatResponse != null) {
                callback?.menu3dotClicked(subCatResponse)
            }
        }
        container?.cardElevation = 1f
    }

    override fun subItemClicked(parentPosition: Int) {

        subCatResponse?.let {
            it.isExpanded = !it.isExpanded
            container?.cardElevation = 1f
            load()
            callback?.qurbaniCommonContentClicked(absoluteAdapterPosition,it.isExpanded)
        }

    }
    fun update(){
        contentSetting = AppPreference.getContentSetting()
        load()
    }

}
