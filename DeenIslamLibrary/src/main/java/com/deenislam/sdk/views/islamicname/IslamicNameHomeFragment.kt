package com.deenislam.sdk.views.islamicname

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deenislam.sdk.R
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton

internal class IslamicNameHomeFragment : BaseRegularFragment() {

    private lateinit var boyNameBtn:MaterialButton
    private lateinit var girlNameBtn:MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_islamic_name_home,container,false)

        //init view
        boyNameBtn = mainView.findViewById(R.id.boyNameBtn)
        girlNameBtn = mainView.findViewById(R.id.girlNameBtn)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        boyNameBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putString("gender", "male")
                putString("title", localContext.getString(R.string.muslim_boys_names))
            }
            gotoFrag(R.id.action_islamicNameFragment_to_islamicNameViewFragment,data = bundle)
        }

        girlNameBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putString("gender", "female")
                putString("title", localContext.getString(R.string.muslim_girls_names))
            }
            gotoFrag(R.id.action_islamicNameFragment_to_islamicNameViewFragment,data = bundle)
        }
    }

}