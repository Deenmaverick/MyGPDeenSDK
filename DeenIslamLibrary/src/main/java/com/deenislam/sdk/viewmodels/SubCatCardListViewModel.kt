package com.deenislam.sdk.viewmodels;

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.SubCatCardListResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.repository.SubCatCardListRepository
import com.google.gson.Gson
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
}