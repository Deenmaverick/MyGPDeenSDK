package com.deenislam.sdk.service.repository

import android.util.Log
import com.deenislam.sdk.Deen
import com.deenislam.sdk.service.database.dao.UserPrefDao
import com.deenislam.sdk.service.database.entity.UserPref
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.api.AuthenticateService
import com.deenislam.sdk.service.network.response.auth.login.LoginResponse
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.get9DigitRandom
import com.deenislam.sdk.utils.toRequestBody
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject


internal class AuthenticateRepository(
    private val authenticateService: AuthenticateService?,
    private val userPrefDao: UserPrefDao?
) : ApiCall {

    suspend fun login(username:String) = makeApicall {

        val body = JSONObject()
        body.put("msisdn",username)
        body.put("client_id","")
        body.put("client_secret","")

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        authenticateService?.login(parm = requestBody)
    }

    suspend fun storeToken(token:String,username: String,refreshToken: String) =
        withContext(Dispatchers.IO)
        {
            val data = userPrefDao?.select()

            data?.let {
                if (it.isNotEmpty()) {
                    it[0]?.token = token
                    it[0]?.refresh_token = refreshToken
                    it[0]?.username = username
                    userPrefDao?.update(it)
                } else {
                    0
                }
            }?:0

        }

    private suspend fun processLoginResponse(response: ApiResource<LoginResponse?>, msisdn: String): String?
    {

        when(response)
        {
            is ApiResource.Success ->
            {
                response.value?.Data?.let {

                        if(storeToken(it.JWT,msisdn,"") > 0) {

                            makeApicall {



                                val body = JSONObject()
                                body.put("language", Deen.language)
                                body.put("msisdn", msisdn)
                                body.put("pagename", "dashboard")
                                body.put("trackingID", get9DigitRandom())
                                body.put("device", "sdk")
                                val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

                                NetworkProvider().getInstance().provideDeenService()
                                authenticateService?.userTrack(requestBody)
                            }


                            return it.JWT
                        }
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

        data?.let {
            Log.e("AUTH_DATA",Gson().toJson(it))
            if(it.isEmpty())
            {
                userPrefDao?.insert(UserPref())
            }
        }

        return processLoginResponse(response,msisdn)
    }


}