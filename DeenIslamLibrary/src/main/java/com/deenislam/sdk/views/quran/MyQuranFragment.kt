package com.deenislam.sdk.views.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.quran.QuranPlayerCallback
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.views.adapters.quran.MyQuranAdapter
import com.deenislam.sdk.views.adapters.quran.MyQuranCallback
import com.deenislam.sdk.views.base.BaseRegularFragment


internal class MyQuranFragment : BaseRegularFragment(), MyQuranCallback, QuranPlayerCallback {

    private lateinit var  listView: RecyclerView
    private lateinit var mainContainer:ConstraintLayout
    private var firstload:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_quran_surah, container, false)

        //init view
        listView = mainview.findViewById(R.id.surahListRC)
        mainContainer = mainview.findViewById(R.id.mainContainer)
        setupCommonLayout(mainview)

        return mainview
    }

    fun setupActionBar()
    {
        val actionbar  =  (parentFragment as? QuranFragment)?.getActionbar() as ConstraintLayout
        setupActionForOtherFragment(0,0,null,localContext.resources.getString(R.string.my_quran),true,actionbar)

        listView.post {
            val miniPlayerHeight = getMiniPlayerHeight()
            listView.setPadding(listView.paddingStart,listView.paddingTop,listView.paddingEnd,if(miniPlayerHeight>0) miniPlayerHeight else listView.paddingBottom)
        }

    }

    override fun onResume() {
        super.onResume()
        setupActionBar()
        initView()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if(menuVisible) {
            CallBackProvider.setFragment(this)
        }
    }


    private fun initView()
    {
        if(firstload != 0)
            return
        firstload = 1

        listView.apply {
            adapter = MyQuranAdapter(this@MyQuranFragment)
            overScrollMode = View.OVER_SCROLL_NEVER
            post {
                baseViewState()
            }
        }

    }

    override fun menuClicked(position: Int) {
        when(position)
        {
            0 -> gotoFrag(R.id.action_global_quranDownloadFragment)
           /* 1 -> gotoFrag(R.id.dailyVerseFragment)
            4 -> gotoFrag(R.id.quranicDuaFragment)*/
        }
    }

    override fun globalMiniPlayerClosed(){
        listView.setPadding(listView.paddingStart,listView.paddingTop,listView.paddingEnd,0)
    }
}