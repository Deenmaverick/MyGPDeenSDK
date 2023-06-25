package com.deenislam.sdk.service.network

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

internal interface ApiCall {

    suspend fun <T> makeApicall(
        apiCall: suspend () -> T
    ): ApiResource<T>
    {
        return  withContext(Dispatchers.IO)
        {
            try {
                ApiResource.Success(apiCall.invoke())
            }catch (throwable:Throwable)
            {
                Log.e("userPrefDao",throwable.toString())
                when(throwable)
                {
                    is HttpException ->
                    {
                        //Log.e("API_LOG", Gson().toJson(throwable.response()))
                        ApiResource.Failure(false,throwable.code(),throwable.response()?.errorBody())
                    }
                    else -> ApiResource.Failure(true, null, null)

                }

            }
        }
    }
}