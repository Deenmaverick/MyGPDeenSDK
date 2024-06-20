package com.deenislamic.sdk.views.quran.learning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.QuranLearningCallback
import com.deenislamic.sdk.service.callback.common.MaterialButtonHorizontalListCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.quran.learning.QuranLearningResource
import com.deenislamic.sdk.service.network.response.prayerlearning.visualization.Head
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.viewmodels.quran.learning.QuranLearningViewModel
import com.deenislamic.sdk.views.adapters.common.MaterialButtonHorizontalAdapter
import com.deenislamic.sdk.views.adapters.quran.learning.QuizMultipleChoiceAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class.quiz.AnswerSheet
import com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class.quiz.Data
import com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class.quiz.Option
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch


internal class QuranLearningQuizFragment : BaseRegularFragment(), QuranLearningCallback,
    MaterialButtonHorizontalListCallback {

    private lateinit var viewmodel: QuranLearningViewModel
    private lateinit var quizMultipleChoiceAdapter: QuizMultipleChoiceAdapter
    private lateinit var materialButtonHorizontalAdapter: MaterialButtonHorizontalAdapter
    private lateinit var header: RecyclerView
    private val navArgs:QuranLearningQuizFragmentArgs by navArgs()
    private lateinit var question:AppCompatTextView
    private lateinit var multipleChoise:RecyclerView
    private var currentIndex = 0
    private var questionData: ArrayList<Data> = arrayListOf()
    private lateinit var submitBtn:MaterialButton
    private val answerSheet:ArrayList<AnswerSheet> = arrayListOf()
    private var firstload = false

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
        val mainview = localInflater.inflate(R.layout.fragment_quran_learning_quiz,container,false)

        header = mainview.findViewById(R.id.header)
        question = mainview.findViewById(R.id.question)
        multipleChoise = mainview.findViewById(R.id.multipleChoise)
        submitBtn = mainview.findViewById(R.id.submitBtn)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.quran_class_quiz),
            backEnable = true,
            view = mainview
        )

        currentIndex = 0
        answerSheet.clear()
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
        initObserver()

        if(!firstload)
        loadapi()
        firstload

    }

    private fun initObserver()
    {
        viewmodel.quranLearningQuizLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is QuranLearningResource.QuranClassQuizQuestion ->{

                    val headerList:ArrayList<Head> = arrayListOf()

                    it.data.forEachIndexed { index, value ->
                        headerList.add(Head(index,localContext.getString(R.string.question_with_index,(index+1).toString().numberLocale())))
                    }

                    header.apply {
                        val linearLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                        layoutManager = linearLayoutManager
                        materialButtonHorizontalAdapter = MaterialButtonHorizontalAdapter(headerList)
                        adapter = materialButtonHorizontalAdapter
                    }

                    if(it.data.isNotEmpty())
                    {

                        questionData = ArrayList(it.data)
                        question.text = it.data[0].QuestionTitle
                        multipleChoise.apply {
                        quizMultipleChoiceAdapter = QuizMultipleChoiceAdapter(ArrayList(it.data[0].Options))
                            adapter = quizMultipleChoiceAdapter
                        }
                    }

                    baseViewState()

                }
            }
        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    private fun loadapi()
    {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getQuranClassQuizQuestion(getLanguage(),navArgs.courseID,navArgs.contentID)
        }
    }

    override fun courseQuizAnswerSelected(getData: Option)
    {


        answerSheet.add(
            AnswerSheet(
            contentID = navArgs.contentID,
            courseID = navArgs.courseID,
            language = getLanguage(),
            optionID = getData.OptionId,
            questionID = questionData[currentIndex].QuestionID
        )
        )


        //quizMultipleChoiceAdapter.updateData(getData.copy(selected = true))

        if(currentIndex>=0 && currentIndex<materialButtonHorizontalAdapter.itemCount-1) {
            materialButtonHorizontalAdapter.nextPrev(currentIndex + 1)
            header.smoothScrollToPosition(currentIndex + 1)
            headerListClicked(currentIndex + 1)
        }

        if(answerSheet.size ==  questionData.size)
        {
            val bundle = Bundle()
            bundle.putParcelableArray("answerSheet",answerSheet.toTypedArray())
            gotoFrag(R.id.action_global_quranLearningQuizResultFragment,bundle)
        }

    }

     private fun headerListClicked(absoluteAdapterPosition: Int) {
         materialButtonHorizontalAdapter.notifyItemChanged(currentIndex)
         currentIndex = absoluteAdapterPosition
         question.text = questionData[absoluteAdapterPosition].QuestionTitle
         multipleChoise.apply {
             quizMultipleChoiceAdapter = QuizMultipleChoiceAdapter(ArrayList(questionData[absoluteAdapterPosition].Options))
             adapter = quizMultipleChoiceAdapter
         }
         materialButtonHorizontalAdapter.notifyItemChanged(currentIndex)
    }


}