package com.deenislam.sdk.views.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deenislam.sdk.R
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.card.MaterialCardView
import com.google.android.material.transition.MaterialSharedAxis

internal class MoreFragment : BaseRegularFragment(),otherFagmentActionCallback {

    private lateinit var settingLayout:MaterialCardView


    override fun OnCreate() {
        super.OnCreate()

        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainview = layoutInflater.inflate(R.layout.fragment_more,container,false)

        //init view
        settingLayout = mainview.findViewById(R.id.settingLayout)

        setupActionForOtherFragment(
            action1 = R.drawable.ic_close,
            action2 = 0,
            callback = this@MoreFragment,
            actionnBartitle = getString(R.string.app_name),
            backEnable = false,
            view = mainview,
            isBackIcon = true
        )

        return mainview
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingLayout.setOnClickListener {
            gotoFrag(R.id.action_moreFragment_to_settingFragment)
        }
    }

    override fun action1() {
        onBackPress()
    }

    override fun action2() {
    }


}