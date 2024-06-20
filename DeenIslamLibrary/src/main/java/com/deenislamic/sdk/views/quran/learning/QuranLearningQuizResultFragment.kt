package com.deenislamic.sdk.views.quran.learning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.quran.learning.QuranLearningResource
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.viewmodels.quran.learning.QuranLearningViewModel
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import kotlinx.coroutines.launch


internal class QuranLearningQuizResultFragment : BaseRegularFragment() {

    private val navArgs:QuranLearningQuizResultFragmentArgs by navArgs()
    private lateinit var totalQuestion:MaterialButton
    private lateinit var rightAnswer:MaterialButton
    private lateinit var wrongAnswer: MaterialButton
    private lateinit var verdict:MaterialButton
    private lateinit var retryBtn:MaterialButton
    private lateinit var nextBtn:MaterialButton

    private lateinit var viewmodel: QuranLearningViewModel

    override fun OnCreate() {
        super.OnCreate()

        viewmodel =QuranLearningViewModel(
            QuranLearningRepository(
                quranShikkhaService = NetworkProvider().getInstance().provideQuranShikkhaService(),
                deenService = NetworkProvider().getInstance().provideDeenService(),
                dashboardService = NetworkProvider().getInstance().provideDashboardService()
            )
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        CallBackProvider.setFragment(this)

        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_quran_learning_quiz_result,container,false)

        totalQuestion = mainview.findViewById(R.id.totalQuestion)
        rightAnswer = mainview.findViewById(R.id.rightAnswer)
        wrongAnswer = mainview.findViewById(R.id.wrongAnswer)
        verdict = mainview.findViewById(R.id.verdict)
        retryBtn = mainview.findViewById(R.id.retryBtn)
        nextBtn = mainview.findViewById(R.id.nextBtn)


        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.quiz_result),
            backEnable = true,
            view = mainview
        )

        setupBackPressCallback(this)

        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*if (!isDetached) {
            view.postDelayed({
                loadPage()
            }, 300)
        }
        else*/
            loadPage()


    }

    private fun loadPage()
    {

        retryBtn.setOnClickListener {
            super.onBackPress()
        }

        nextBtn.setOnClickListener {

            findNavController().popBackStack(R.id.quranLearningDetailsFragment,false)
        }

        initObserver()
        loadapi()
    }

    override fun onBackPress() {
        findNavController().popBackStack(R.id.quranLearningDetailsFragment,false)
    }

    private fun initObserver()
    {
        viewmodel.quranLearningQuizLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is QuranLearningResource.QuranQuizResult ->{

                    val wrongAnswerCount = it.data.NoOfQuestions-it.data.NoOfCorrectAnswer
                    totalQuestion.text = localContext.getString(R.string.total_questions_count,it.data.NoOfQuestions.toString().numberLocale())
                    rightAnswer.text = localContext.getString(R.string.right_answer_count,it.data.NoOfCorrectAnswer.toString().numberLocale())
                    wrongAnswer.text = localContext.getString(R.string.wrong_answer_count,wrongAnswerCount.toString().numberLocale())

                    var result = ""

                    result = if(it.data.NoOfCorrectAnswer == it.data.NoOfQuestions) {
                        localContext.getString(R.string.excellent)
                    } else if (it.data.NoOfCorrectAnswer > wrongAnswerCount) {
                        localContext.getString(R.string.good)
                    } else {
                        localContext.getString(R.string.poor)
                    }

                    verdict.text  = localContext.getString(R.string.verdict,result)

                    baseViewState()

        }
    }
}
}

private fun loadapi()
{
baseLoadingState()
lifecycleScope.launch {
    viewmodel.submitQuizAnswer(Gson().toJson(navArgs.answerSheet))
}
}

override fun noInternetRetryClicked() {
loadapi()
}


}