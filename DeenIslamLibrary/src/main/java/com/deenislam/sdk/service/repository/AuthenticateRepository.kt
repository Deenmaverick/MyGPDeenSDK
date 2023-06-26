package com.deenislam.sdk.service.repository

import android.util.Log
import com.deenislam.sdk.service.database.dao.UserPrefDao
import com.deenislam.sdk.service.database.entity.UserPref
import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.api.AuthenticateService
import com.deenislam.sdk.service.network.response.auth.login.LoginResponse
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


internal class AuthenticateRepository(
    private val authenticateService: AuthenticateService?,
    private val userPrefDao: UserPrefDao?
) : ApiCall {

    suspend fun login(username:String) = makeApicall {

        val body = JSONObject()
        body.put("username",username)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        authenticateService?.login(parm = requestBody)
    }

    suspend fun storeToken(token:String,username: String,refreshToken: String) =
        withContext(Dispatchers.IO)
        {
            val data = userPrefDao?.select()

            if (data?.isNotEmpty() == true || data?.size!!>0) {
                data[0]?.token = token
                data[0]?.refresh_token = refreshToken
                data[0]?.username = username
                userPrefDao?.update(data)
            } else {
                0
            }
        }

    private suspend fun processLoginResponse(response: ApiResource<LoginResponse?>, msisdn: String): String?
    {

        when(response)
        {
            is ApiResource.Success ->
            {
                response.value?.Data?.let {

                        if(storeToken(it.JWT,msisdn,it.RefreshToken.Token) !=0)
                            return it.JWT
                        else
                        {
                            return  null
                        }

                }?:return  null

            }
            else ->  return  null
        }
    }

    suspend fun authDeen(msisdn:String): String?
    {
        val response = login(msisdn)


        val data = userPrefDao?.select()
        if(data?.isEmpty() == true || data?.size!! <= 0)
        {
            userPrefDao?.insert(UserPref())
        }

        return processLoginResponse(response,msisdn)
    }


}