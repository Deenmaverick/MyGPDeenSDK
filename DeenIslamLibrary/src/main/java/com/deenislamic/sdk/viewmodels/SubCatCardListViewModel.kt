package com.deenislamic.sdk.viewmodels;

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.SubCatCardListResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data
import com.deenislamic.sdk.service.repository.SubCatCardListRepository
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


internal class SubCatCardListViewModel(
    private val repository: SubCatCardListRepository
) : ViewModel() {

    private val _subCatLiveData: MutableLiveData<SubCatCardListResource> = MutableLiveData()
    val  subCatLiveData: MutableLiveData<SubCatCardListResource> get() = _subCatLiveData


    fun getSubCatList(categoryID:Int,language:String,tag:String)
    {
        viewModelScope.launch {
            when(val response = repository.getSubCatList(categoryID,language,tag))
            {
                is ApiResource.Failure -> _subCatLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    Log.e("getHajjAndUmrahSubCat", Gson().toJson(response.value))
                    if(response.value?.Data?.isNotEmpty() == true)
                        _subCatLiveData.value = SubCatCardListResource.SubcatList(response.value.Data)
                    else
                        _subCatLiveData.value = CommonResource.EMPTY
                }

                else -> _subCatLiveData.value = CommonResource.EMPTY
            }
        }
    }

    fun getSubCatPatch(
        categoryID: String,
        language: String,
        tag: String,
        contentType: String?,
        subid: Int
    )
    {
        viewModelScope.launch {

            var getSubCatResponse: List<Data>? = null

            if(contentType == "pldd"){
                val getSubCat = async {  repository.getSubCatList(subid,language,tag) }.await()

                when(getSubCat){

                    is ApiResource.Success -> {
                        if(getSubCat.value?.Data?.isNotEmpty() == true)
                            getSubCatResponse = getSubCat.value.Data
                    }

                    is ApiResource.Failure -> Unit
                    null -> Unit
                    else -> Unit
                }
            }

            when(val response = repository.getSubCatPatch(categoryID,language,tag))
            {
                is ApiResource.Failure -> _subCatLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Data?.isNotEmpty() == true) {

                        val finalData = if (getSubCatResponse?.isNotEmpty() == true) {
                            val transformedList = getSubCatResponse.map { subCatItem ->
                                com.deenislamic.sdk.service.network.response.dashboard.Data(
                                    AppDesign = "view$contentType",
                                    subContentData = subCatItem
                                )
                            }
                            transformedList + response.value.Data
                        } else {
                            response.value.Data
                        }


                        _subCatLiveData.value = SubCatCardListResource.SubcatListPatch(finalData)
                    }
                    else
                        _subCatLiveData.value = CommonResource.EMPTY
                }

                else -> _subCatLiveData.value = CommonResource.EMPTY
            }
        }
    }
}