package com.deenislam.sdk.views.islamicname

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.deenislam.sdk.Deen
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

internal class IslamicNameHomeFragment : BaseRegularFragment() {

    private lateinit var boyLayout:MaterialCardView
    private lateinit var girlLayout:MaterialCardView

    override fun OnCreate() {
        super.OnCreate()
        onBackPressedCallback =
            requireActivity().onBackPressedDispatcher.addCallback {
                onBackPress()
            }
        onBackPressedCallback.isEnabled = true
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_islamic_name_home,container,false)

        //init view
        boyLayout = mainView.findViewById(R.id.boyLayout)
        girlLayout = mainView.findViewById(R.id.girlLayout)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        boyLayout.setOnClickListener {
            val bundle = Bundle().apply {
                putString("gender", "male")
                putString("title", localContext.getString(R.string.muslim_boys_names))
            }
            gotoFrag(R.id.action_islamicNameFragment_to_islamicNameViewFragment,data = bundle)
        }

        girlLayout.setOnClickListener {
            val bundle = Bundle().apply {
                putString("gender", "female")
                putString("title", localContext.getString(R.string.muslim_girls_names))
            }
            gotoFrag(R.id.action_islamicNameFragment_to_islamicNameViewFragment,data = bundle)
        }
    }

    override fun onBackPress() {

        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = Deen.msisdn,
                    pagename = "islamic_name",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }

    }
}