package com.deenislam.sdk.service.repository

import com.deenislam.sdk.service.database.dao.UserPrefDao
import com.deenislam.sdk.service.database.entity.UserPref
import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.api.AuthenticateService
import com.deenislam.sdk.service.network.response.auth.login.LoginResponse
import com.deenislam.sdk.utils.RequestBodyMediaType
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
            data?.get(0)?.token = token
            data?.get(0)?.refresh_token = refreshToken
            data?.get(0)?.username = username

            if (data != null) {
                userPrefDao?.update(data)
            } else {
                0
            }
        }

    private suspend fun processLoginResponse(response: ApiResource<LoginResponse?>, msisdn: String):String
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
                            return  ""
                        }

                }?:return  ""

            }
            else ->  return  ""
        }
    }

    suspend fun authDeen(msisdn:String):String
    {
        val response = login(msisdn)

        val data = userPrefDao?.select()
        if(data == null)
        {

            userPrefDao?.insert(
                UserPref(
                    language = "en",
                    location_setting = true
                )
            )
        }

        return processLoginResponse(response,msisdn)
    }


}