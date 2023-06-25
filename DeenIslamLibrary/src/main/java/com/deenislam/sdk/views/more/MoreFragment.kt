package com.deenislam.sdk.views.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.MENU_DIGITAL_TASBEEH
import com.deenislam.sdk.utils.runWhenReady
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.views.adapters.MenuAdapter
import com.deenislam.sdk.views.adapters.MenuCallback
import com.deenislam.sdk.views.base.BaseMenu
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.main.MainActivity
import com.google.android.material.progressindicator.CircularProgressIndicator


class MoreFragment : BaseRegularFragment(),MenuCallback {

    private val asyncprogressLayout: CircularProgressIndicator by lazy { requireView().findViewById(R.id.progress_circular) }
    private lateinit var menuRC: RecyclerView
    private var firstload:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.layout_async_match_preload,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTranslationZ(asyncprogressLayout, 10F)
    }

    override fun onResume() {
        super.onResume()
        setupAction(R.drawable.ic_settings,0,null,"More")
      /*  if(MainActivity.instance?.bottomNavClicked == true)
            animateView()
        else*/
            initView()
    }

    private fun animateView()
    {
        val anim: Animation

        if(MainActivity.instance?.childFragmentAnimForward == true)
            anim = AnimationUtils.loadAnimation(requireContext(), R.anim.right_to_left)
        else
            anim = AnimationUtils.loadAnimation(requireContext(), R.anim.left_to_right)

        requireView().startAnimation(anim)

        anim?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                initView()
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })

        MainActivity.instance?.resetBottomNavClick()
    }

    private fun initView()
    {
        if(firstload != 0)
            return
        firstload = 1

        prepareStubView<View>(requireView().findViewById(R.id.widget),R.layout.fragment_more) {

            menuRC = this.findViewById(R.id.menuRC)

            val menu = BaseMenu().getInstance().getMoreMenu()

            menuRC.apply {
                adapter = MenuAdapter(menu, 2, menuCallback = this@MoreFragment)
                overScrollMode = View.OVER_SCROLL_NEVER
                runWhenReady {
                    asyncprogressLayout.visible(false)
                }
            }


        }
    }

    override fun menuClicked(tag: String) {

        when(tag)
        {
             MENU_DIGITAL_TASBEEH -> gotoFrag(R.id.tasbeehFragment)
        }
    }

}