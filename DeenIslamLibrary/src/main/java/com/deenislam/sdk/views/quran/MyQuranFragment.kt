package com.deenislam.sdk.views.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.views.adapters.quran.MyQuranAdapter
import com.deenislam.sdk.views.adapters.quran.MyQuranCallback
import com.deenislam.sdk.views.base.BaseRegularFragment


internal class MyQuranFragment : BaseRegularFragment(), MyQuranCallback {

    private val  popularRC: RecyclerView by lazy { requireView().findViewById(R.id.surahListRC) }
    private val progressLayout:LinearLayout by lazy { requireView().findViewById(R.id.progressLayout) }
    private val no_internet_layout: NestedScrollView by lazy { requireView().findViewById(R.id.no_internet_layout)}
    private var firstload:Int = 0



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quran_surah, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(no_internet_layout, 10F)

    }


    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView()
    {
        if(firstload != 0)
            return
        firstload = 1

        progressLayout.visible(false)
        no_internet_layout.visible(false)

        popularRC.apply {
            adapter = MyQuranAdapter(this@MyQuranFragment)
            overScrollMode = View.OVER_SCROLL_NEVER
            post {
                progressLayout.visible(false)
            }
        }

    }

    override fun menuClicked(position: Int) {
        when(position)
        {
            0-> gotoFrag(R.id.quranFavoriteFragment)
        }
    }
}