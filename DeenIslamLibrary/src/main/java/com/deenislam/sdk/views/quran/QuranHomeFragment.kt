package com.deenislam.sdk.views.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislam.sdk.utils.runWhenReady
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.views.adapters.quran.PopularSurahAdapter
import com.deenislam.sdk.views.adapters.quran.RecentlyReadAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.progressindicator.CircularProgressIndicator

internal class QuranHomeFragment : BaseRegularFragment() {

    private val asyncprogressLayout: CircularProgressIndicator by lazy { requireView().findViewById(R.id.progress_circular) }
    private lateinit var recentRC: RecyclerView
    private lateinit var popularRC: RecyclerView
    private lateinit var progressLayout: LinearLayout
    private var firstload:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_async_match_preload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTranslationZ(asyncprogressLayout, 10F)
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

        prepareStubView<View>(requireView().findViewById(R.id.widget),R.layout.fragment_quran_home) {

            recentRC = this.findViewById(R.id.recentRC)
            popularRC = this.findViewById(R.id.popularRC)
            progressLayout = this.findViewById(R.id.progressLayout)

            recentRC.apply {
                adapter = RecentlyReadAdapter()
                ViewPagerHorizontalRecyler().getInstance().load(this)
                overScrollMode = View.OVER_SCROLL_NEVER

            }

            popularRC.apply {
                adapter = PopularSurahAdapter()
                overScrollMode = View.OVER_SCROLL_NEVER

                runWhenReady {
                    progressLayout.visible(false)
                    asyncprogressLayout.visible(false)
                }
            }


        }
    }

}