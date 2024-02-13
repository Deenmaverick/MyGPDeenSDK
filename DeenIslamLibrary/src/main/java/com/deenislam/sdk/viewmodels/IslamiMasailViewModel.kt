package com.deenislam.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.IslamiMasailResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.repository.IslamiMasailRepository
import kotlinx.coroutines.launch


internal class IslamiMasailViewModel(
    private val repository: IslamiMasailRepository
) : ViewModel() {

    private val _islamiMasailLivedata:MutableLiveData<IslamiMasailResource> = MutableLiveData()
    val islamiMasailLivedata:MutableLiveData<IslamiMasailResource> get() = _islamiMasailLivedata


    private val _islamiMasailFavLivedata:MutableLiveData<IslamiMasailResource> = MutableLiveData()
    val islamiMasailFavLivedata:MutableLiveData<IslamiMasailResource> get() = _islamiMasailFavLivedata

    private val _islamiMasailMainLivedata:MutableLiveData<IslamiMasailResource> = MutableLiveData()
    val islamiMasailMainLivedata:MutableLiveData<IslamiMasailResource> get() = _islamiMasailMainLivedata


    suspend fun getMasailCat(language: String, pageNo: Int, pageItemCount: Int){
        viewModelScope.launch {

            when(val response = repository.getMasailCat(language,pageNo,pageItemCount)){
                is ApiResource.Failure -> _islamiMasailLivedata.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {

                    if(response.value?.Data?.isNotEmpty() == true)
                        _islamiMasailLivedata.value = IslamiMasailResource.MasailCatList(response.value)
                    else
                        _islamiMasailLivedata.value = CommonResource.EMPTY
                }
            }
        }
    }

    suspend fun getMasailQuestionByCat(language: String, pageNo: Int, pageItemCount: Int,catid:Int){
        viewModelScope.launch {

            when(val response = repository.getMasailQuestionByCat(language,pageNo,pageItemCount,catid)){
                is ApiResource.Failure -> _islamiMasailLivedata.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {

                    if(response.value?.Data?.isNotEmpty() == true)
                        _islamiMasailLivedata.value = IslamiMasailResource.MasailQuestionList(response.value)
                    else
                        _islamiMasailLivedata.value = CommonResource.EMPTY
                }
            }
        }
    }

    suspend fun questionBookmark(getdata: com.deenislam.sdk.service.network.response.islamimasail.questionbycat.Data, language: String) {

        viewModelScope.launch {
            val isFav = getdata.IsFavorite
            var favcount = getdata.favCount
            when(val response = repository.questionBookmar(language,getdata)){
                is ApiResource.Failure -> Unit
                is ApiResource.Success -> {
                    if (response.value?.Success == true) {

                        if(isFav)
                            favcount--
                        else
                            favcount++

                        _islamiMasailLivedata.value =
                            IslamiMasailResource.QuestionBookmar(getdata.copy(IsFavorite = !isFav, favCount = favcount))
                    }
                }
            }

        }
    }

    suspend fun getFavList(language: String, pageNo: Int, pageItemCount: Int){
        viewModelScope.launch {
            when(val response = repository.getFavList(language,pageNo,pageItemCount)){
                is ApiResource.Failure -> _islamiMasailFavLivedata.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {

                    if(response.value?.Data?.isNotEmpty() == true)
                        _islamiMasailFavLivedata.value = IslamiMasailResource.MasailQuestionList(response.value)
                    else
                        _islamiMasailFavLivedata.value = CommonResource.EMPTY
                }
            }
        }
    }

    suspend fun postQuestion(language: String, catid: Int, title: String,place:String,isAnonymous:Boolean,isUrgent:Boolean){
        viewModelScope.launch {

            when(val response = repository.postQuestion(
                language = language,
                catid = catid,
                title = title,
                place = place,
                isAnonymous = isAnonymous,
                isUrgent = isUrgent
            )){
                is ApiResource.Failure -> _islamiMasailLivedata.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if(response.value?.Success == true)
                        _islamiMasailLivedata.value = IslamiMasailResource.postQuestion
                    else
                        _islamiMasailLivedata.value = CommonResource.API_CALL_FAILED
                }
            }
        }
    }

    fun clearMainLive(){
        _islamiMasailMainLivedata.value = CommonResource.CLEAR
        _islamiMasailLivedata.value = CommonResource.CLEAR
    }

    fun postSuccess(){
        _islamiMasailMainLivedata.value = IslamiMasailResource.postQuestion
    }

    suspend fun getMyQuestions(language: String,page:Int,content:Int){
        viewModelScope.launch {

            when(val response = repository.getMyMasailQuestionList(language,page,content)){
                is ApiResource.Failure -> _islamiMasailLivedata.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {

                    if(response.value?.Data?.isNotEmpty() == true)
                        _islamiMasailLivedata.value = IslamiMasailResource.MasailQuestionList(response.value)
                    else
                        _islamiMasailLivedata.value = CommonResource.EMPTY
                }
            }
        }
    }

    suspend fun getHomePatch(language: String){
        viewModelScope.launch {

            when(val response = repository.getHomePatch(language)){
                is ApiResource.Failure -> _islamiMasailLivedata.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {

                    if(response.value?.Data?.isNotEmpty() == true)
                        _islamiMasailLivedata.value = IslamiMasailResource.HomePatch(response.value.Data)
                    else
                        _islamiMasailLivedata.value = CommonResource.EMPTY
                }
            }
        }
    }

    suspend fun getAnswer(id: Int){
        viewModelScope.launch {

            when(val response = repository.getAnswer(id)){
                is ApiResource.Failure -> _islamiMasailLivedata.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {

                    if(response.value?.Data?.isNotEmpty() == true && 0 in 0 until response.value.Data.size)
                        _islamiMasailLivedata.value = IslamiMasailResource.AnswerData(response.value.Data[0])
                    else
                        _islamiMasailLivedata.value = CommonResource.EMPTY
                }
            }
        }
    }

}