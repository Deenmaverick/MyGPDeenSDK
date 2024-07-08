package com.deenislamic.sdk.views.adapters.common;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dashboard.Data
import com.deenislamic.sdk.utils.AsyncViewStub
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.PATCH_COMMON_CARD_LIST
import com.deenislamic.sdk.utils.PATCH_SINGLE_IMAGE
import com.deenislamic.sdk.utils.PATCH_VERTICAL_CARD_LIST
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.prepareStubView
import com.deenislamic.sdk.views.adapters.common.gridmenu.MenuCallback
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.views.common.patch.RichContentPatch
import com.deenislamic.sdk.views.dashboard.patch.DailyDua
import com.deenislamic.sdk.views.dashboard.patch.QuranicItem
import com.deenislamic.sdk.views.dashboard.patch.SingleCardList
import com.deenislamic.sdk.views.prayerlearning.patch.ListMenuDetailsPatch
import com.deenislamic.sdk.views.quran.patch.PopularSurah
import com.google.android.material.button.MaterialButton

internal class SubCatPatchAdapter(
    private val patchData: List<Data>
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val DESIGN_MENU = "menu"
    private val POPULAR_SURAH = "PopularSurah"
    private val SUBCAT = "viewpldd"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    {
        val main_view = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_match, parent, false)

        val rootview: AsyncViewStub = main_view.findViewById(R.id.widget)

        when (patchData[viewType].AppDesign) {

            DESIGN_MENU ->
            {
                prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_horizontal_listview) {
                    onBindViewHolder(ViewHolder(main_view,true),viewType)
                }
            }

            PATCH_COMMON_CARD_LIST -> {
                prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {

                    onBindViewHolder(ViewHolder(main_view,true),viewType)

                }
            }

            PATCH_VERTICAL_CARD_LIST -> {
                prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {
                    onBindViewHolder(ViewHolder(main_view,true),viewType)
                }
            }

            PATCH_SINGLE_IMAGE -> {
                prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_quranic_v1) {
                    onBindViewHolder(ViewHolder(main_view,true),viewType)
                }
            }

            "Dua" -> {
                prepareStubView<View>(rootview, R.layout.dashboard_inc_item_horizontal_list) {
                    onBindViewHolder(ViewHolder(main_view, true), viewType)
                }
            }

            "singlemenuitem" -> {

                prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.item_islamic_event_home) {
                    onBindViewHolder(ViewHolder(main_view,true),viewType)
                }
            }

            POPULAR_SURAH -> {
                prepareStubView<View>(
                    rootview.findViewById(R.id.widget),
                    R.layout.layout_horizontal_listview_v2
                ) {
                    onBindViewHolder(ViewHolder(main_view, true), viewType)
                }
            }

            SUBCAT -> {

                prepareStubView<View>(
                    rootview.findViewById(R.id.widget),
                    R.layout.item_qurbani_common_content
                ) {
                    onBindViewHolder(ViewHolder(main_view, true), viewType)
                }
            }

        }

        return  ViewHolder(main_view)
    }

    override fun getItemCount(): Int = patchData.size

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }


    fun updateRichContent(position: Int,isExpanded:Boolean){
        patchData[position].subContentData?.isExpanded = isExpanded
    }





    inner class ViewHolder(itemView: View,private val loaded:Boolean = false) : BaseViewHolder(itemView) {

        // Card banner
        private val textContent: AppCompatTextView by lazy { itemView.findViewById(R.id.textContent) }
        private val subContent: AppCompatTextView by lazy { itemView.findViewById(R.id.subContent) }
        private val mainBtn: MaterialButton by lazy { itemView.findViewById(R.id.mainBtn) }
        private val banner: AppCompatImageView by lazy { itemView.findViewById(R.id.banner) }
        private val container: ConstraintLayout by lazy { itemView.findViewById(R.id.inf) }

        // Single menu item

        private val menuName: AppCompatTextView by lazy { itemView.findViewById(R.id.menuName)}
        private val ivEvent: AppCompatImageView by lazy {itemView.findViewById(R.id.ivEvent)}


        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            if(loaded)
            {
                val data = patchData[position]
                data.isloaded = true

                when(data.AppDesign)
                {
                    DESIGN_MENU -> ListMenuDetailsPatch(itemView,data.Items)

                    PATCH_COMMON_CARD_LIST -> {
                        //(itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                        SingleCardList(
                            itemView,
                            data
                        ).load()
                    }
                    PATCH_SINGLE_IMAGE -> {
                        val helper = QuranicItem(itemView)
                        helper.loadSingleImage(data.Items)
                    }

                    "Dua" -> {
                        DailyDua(itemView, data)
                    }

                    "singlemenuitem" -> {

                        val getdata = data.Items.getOrNull(0)

                        menuName.text = getdata?.ArabicText
                        ivEvent.imageLoad(BASE_CONTENT_URL_SGP + getdata?.imageurl1, placeholder_1_1 = true)

                        itemView.setOnClickListener {

                            val menuCallback = CallBackProvider.get<MenuCallback>()

                            getdata?.Text?.let { it1 -> menuCallback?.menuClicked(it1,getdata) }
                        }
                    }

                    PATCH_VERTICAL_CARD_LIST -> {
                        //(itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                        SingleCardList(
                            itemView,
                            data,
                            orientation = RecyclerView.VERTICAL
                        ).load()
                    }

                    POPULAR_SURAH ->
                    {
                        PopularSurah(itemView,data.Items,false)
                    }

                    SUBCAT -> {
                        RichContentPatch(itemView,data.subContentData,position)

                    }

                }
            }else{
                val data = patchData[position]

                if(data.isloaded){

                    when(data.AppDesign)
                    {
                        SUBCAT -> {
                            //Log.e("SUBCATPATCH",richContentPatch.toString())
                            //richContentPatch?.update()
                            RichContentPatch(itemView,data.subContentData,position)
                        }
                    }
                }
            }
        }
    }
}