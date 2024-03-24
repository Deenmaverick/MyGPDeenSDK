package com.deenislam.sdk.views.dashboard.patch;

import android.text.TextUtils
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.AdvertisementCallback
import com.deenislam.sdk.service.callback.DashboardPatchCallback
import com.deenislam.sdk.service.callback.common.PatchCallback
import com.deenislam.sdk.service.network.response.advertisement.Data
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.utils.show
import com.google.android.material.button.MaterialButton

internal class QuranicItem(private val itemView: View) {

    private var banner: AppCompatImageView? = itemView.findViewById(R.id.banner)
    private var textContent: AppCompatTextView? = itemView.findViewById(R.id.textContent)
    private var subContent: AppCompatTextView? = itemView.findViewById(R.id.subContent)
    private var mainBtn: MaterialButton? = itemView.findViewById(R.id.mainBtn)
    private var icon: AppCompatImageView? = itemView.findViewById(R.id.icon)
    private var itemTitle: AppCompatTextView? = itemView.findViewById(R.id.itemTitle)
    private var midContent: AppCompatTextView? = itemView.findViewById(R.id.midContent)
    private val ic_play_oval:AppCompatImageView ? = itemView.findViewById(R.id.ic_play_oval)
    private val dashboardPatchCallback = CallBackProvider.get<DashboardPatchCallback>()
    private val patchCallback = CallBackProvider.get<PatchCallback>()
    private val adCallback = CallBackProvider.get<AdvertisementCallback>()

    fun loadCommomn()
    {
        banner = itemView.findViewById(R.id.banner)
        textContent = itemView.findViewById(R.id.textContent)
        subContent = itemView.findViewById(R.id.subContent)
        mainBtn = itemView.findViewById(R.id.mainBtn)
        icon = itemView.findViewById(R.id.icon)
        itemTitle = itemView.findViewById(R.id.itemTitle)
        midContent = itemView.findViewById(R.id.midContent)

    }

    fun loadDailyVerse(
        items: List<Item>
    )
    {
        /* banner = itemView.findViewById(R.id.banner)
         textContent = itemView.findViewById(R.id.textContent)
         subContent = itemView.findViewById(R.id.subContent)
         mainBtn = itemView.findViewById(R.id.mainBtn)
         icon = itemView.findViewById(R.id.icon)
         itemTitle = itemView.findViewById(R.id.itemTitle)
 */
        loadCommomn()
        if(items.isNotEmpty()) {

            val data = items[0]

            icon?.show()

            icon?.let {
                it.setImageDrawable(
                    AppCompatResources.getDrawable(
                        it.context,
                        R.drawable.ic_menu_quran
                    )
                )
            }

            itemTitle?.show()
            itemTitle?.text = data.Title

            banner?.imageLoad(placeholder_16_9 = true, url =data.contentBaseUrl+"/"+data.imageurl1)

            /*  try {

                  textContent?.let {

                      val customFont = ResourcesCompat.getFont(it.context, R.font.kfgqpc_font)
                      it.typeface = customFont
                  }

              } catch (e: Exception) {
                  // Handle or log the exception
              }*/

            textContent?.maxLines = 1
            textContent?.ellipsize = TextUtils.TruncateAt.END
            textContent?.text = data.ArabicText

            midContent?.maxLines = 2
            midContent?.ellipsize = TextUtils.TruncateAt.END
            midContent?.text = data.Text
            midContent?.show()

            subContent?.text = data.Reference
            if(data.Reference.isEmpty())
                subContent?.hide()

            mainBtn?.text = mainBtn?.context?.getString(R.string.read_the_full_surah)

            itemView.setOnClickListener {
                dashboardPatchCallback?.dashboardPatchClickd(data.ContentType,data)
            }
        }
        else
            itemView.hide()

    }


    fun loadHadith(
        items: List<Item>
    )
    {
        /*banner = itemView.findViewById(R.id.banner)
        textContent = itemView.findViewById(R.id.textContent)
        subContent = itemView.findViewById(R.id.subContent)
        mainBtn = itemView.findViewById(R.id.mainBtn)
        icon = itemView.findViewById(R.id.icon)
        itemTitle = itemView.findViewById(R.id.itemTitle)
        midContent = itemView.findViewById(R.id.midContent)*/

        loadCommomn()

        if(items.isNotEmpty()) {

            val data = items[0]

            icon?.show()

            icon?.let {
                it.setImageDrawable(
                    AppCompatResources.getDrawable(
                        it.context,
                        R.drawable.ic_menu_hadith
                    )
                )
            }

            itemTitle?.show()
            itemTitle?.text = data.Title

            banner?.imageLoad(placeholder_16_9 = true, url =data.contentBaseUrl+"/"+data.imageurl1)

            /* try {

                 textContent?.let {

                     val customFont = ResourcesCompat.getFont(it.context, R.font.kfgqpc_font)
                     it.typeface = customFont
                 }

             } catch (e: Exception) {
                 // Handle or log the exception
             }*/

            textContent?.maxLines = 2
            textContent?.ellipsize = TextUtils.TruncateAt.END
            textContent?.text = data.ArabicText

            midContent?.maxLines = 3
            midContent?.ellipsize = TextUtils.TruncateAt.END
            midContent?.text = data.Text
            if(data.Text.isEmpty())
                midContent?.hide()

            subContent?.text = data.Reference
            if(data.Reference.isEmpty())
                subContent?.hide()

            mainBtn?.text = mainBtn?.context?.getString(R.string.read_the_full_hadith)

            itemView.setOnClickListener {
                dashboardPatchCallback?.dashboardPatchClickd(data.ContentType, data)
            }
        }
        else
            itemView.hide()

    }


    fun loadZakat(
        items: List<Item>
    )
    {

        loadCommomn()
        if(items.isNotEmpty()) {

            val data = items[0]

            icon?.show()

            icon?.let {
                it.setImageDrawable(
                    AppCompatResources.getDrawable(
                        it.context,
                        R.drawable.ic_menu_zakat
                    )
                )
            }

            itemTitle?.show()
            itemTitle?.text = data.Title

            banner?.imageLoad(placeholder_16_9 = true, url =data.contentBaseUrl+"/"+data.imageurl1)


            textContent?.text = data.Text
            subContent?.hide()
            mainBtn?.text = mainBtn?.context?.getString(R.string.calculate_zakat)

            itemView.setOnClickListener {
                dashboardPatchCallback?.dashboardPatchClickd(data.ContentType, data)
            }
        }
        else
            itemView.hide()

    }

    fun loadTasbeeh(
        items: List<Item>
    )
    {
        loadCommomn()
        if(items.isNotEmpty()) {

            val data = items[0]

            icon?.show()

            icon?.let {
                it.setImageDrawable(
                    AppCompatResources.getDrawable(
                        it.context,
                        R.drawable.ic_menu_digital_tasbeeh
                    )
                )
            }

            itemTitle?.show()
            itemTitle?.text = data.Title

            banner?.imageLoad(placeholder_16_9 = true, url =data.contentBaseUrl+"/"+data.imageurl1)


            textContent?.text = data.Text
            subContent?.hide()
            mainBtn?.text = mainBtn?.context?.getString(R.string.count_tasbeeh)

            itemView.setOnClickListener {
                dashboardPatchCallback?.dashboardPatchClickd(data.ContentType, data)
            }
        }
        else
            itemView.hide()

    }

    fun loadIslamicName(
        items: List<Item>
    )
    {
        loadCommomn()
        if(items.isNotEmpty()) {

            val data = items[0]

            icon?.show()

            icon?.let {
                it.setImageDrawable(
                    AppCompatResources.getDrawable(
                        it.context,
                        R.drawable.ic_menu_islamic_name
                    )
                )
            }

            itemTitle?.show()
            itemTitle?.text = data.Title

            banner?.imageLoad(placeholder_16_9 = true, url =data.contentBaseUrl+"/"+data.imageurl1)


            textContent?.text = data.Text
            subContent?.hide()
            mainBtn?.text = mainBtn?.context?.getString(R.string.islamic_name)

            itemView.setOnClickListener {
                dashboardPatchCallback?.dashboardPatchClickd(data.ContentType, data)
            }

        }
        else
            itemView.hide()

    }



    fun loadSingleCard(
        items: List<Item>
    ) {
        loadCommomn()
        if(items.isNotEmpty()) {

            val data = items[0]

            if(!data.FeatureLogo.isNullOrEmpty()){
                icon?.show()
                icon?.imageLoad(url = BASE_CONTENT_URL_SGP+data.FeatureLogo, placeholder_1_1 = true)
            }
            else
                icon?.hide()


            if(data.FeatureTitle.isNotEmpty()){
                itemTitle?.show()
                itemTitle?.text = data.FeatureTitle
            }
            else
                itemTitle?.hide()


            banner?.imageLoad(placeholder_16_9 = true, url =data.contentBaseUrl+"/"+data.imageurl1)


            if(data.ArabicText.isNotEmpty()){
                textContent?.maxLines = 2
                textContent?.ellipsize = TextUtils.TruncateAt.END
                textContent?.show()
                textContent?.text = data.ArabicText
            }
            else
                textContent?.hide()



            if(data.Text.isNotEmpty()){
                midContent?.maxLines = 2
                midContent?.ellipsize = TextUtils.TruncateAt.END
                midContent?.show()
                midContent?.text = data.Text
            }
            else
                midContent?.hide()

            if(data.Reference.isNotEmpty()){
                subContent?.show()
                subContent?.text = data.Reference
            }
            else
                subContent?.hide()

            if(!data.FeatureButton.isNullOrEmpty()){
                mainBtn?.show()
                mainBtn?.text = data.FeatureButton
            }
            else
                mainBtn?.hide()


            itemView.setOnClickListener {
                dashboardPatchCallback?.dashboardPatchClickd(data.ContentType, data)
            }

        }
        else
            itemView.hide()

    }

    fun loadSingleImage(
        items: List<Item>
    ) {
        loadCommomn()
        if(items.isNotEmpty()) {
            val data = items[0]

            if(data.isVideo)
                ic_play_oval?.show()

            icon?.hide()
            itemTitle?.hide()

            banner?.imageLoad(placeholder_16_9 = true, url =data.contentBaseUrl+"/"+data.imageurl1)

            textContent?.hide()
            subContent?.hide()
            mainBtn?.hide()

            itemView.setOnClickListener {
                patchCallback?.patchClicked(data)
                dashboardPatchCallback?.dashboardPatchClickd(data.ContentType, data)
            }

        }
        else
            itemView.hide()

    }

    fun loadImageAd(
        items: Data
    ) {
        loadCommomn()

            icon?.hide()
            itemTitle?.hide()

            banner?.imageLoad(placeholder_16_9 = true, url =items.imageurl)

            textContent?.hide()
            subContent?.hide()
            mainBtn?.hide()

            itemView.setOnClickListener {
                adCallback?.adClicked(items)
            }


    }

    fun widget(
        items: List<Item>
    ) {
        loadCommomn()
        if(items.isNotEmpty()) {
            val data = items[0]

            icon?.hide()
            itemTitle?.hide()

            banner?.imageLoad(placeholder_16_9 = true, url =data.contentBaseUrl+"/"+data.imageurl1)

            textContent?.hide()
            subContent?.hide()
            mainBtn?.hide()

            itemView.setOnClickListener {
                dashboardPatchCallback?.dashboardPatchClickd(data.ContentType, data)
            }

        }
        else
            itemView.hide()

    }


}