package com.deenislam.sdk.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.AuthenticateResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.response.auth.SignupResponse
import com.deenislam.sdk.service.repository.AuthenticateRepository
import com.deenislam.sdk.utils.fromJson
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authenticateRepository: AuthenticateRepository
):ViewModel() {

    val _authenticate: MutableLiveData<AuthenticateResource> = MutableLiveData()
    val authenticate : MutableLiveData<AuthenticateResource> get() = _authenticate

    fun signup(ccode:String,username:String,password:String,confirmpassword:String)
    {
        val special: Pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val lowercase: Pattern = Pattern.compile("[a-z]", Pattern.CASE_INSENSITIVE)
        val uppercase: Pattern = Pattern.compile("[A-Z]", Pattern.CASE_INSENSITIVE)
        val specialMatcher: Matcher = special.matcher(password)
        val lowercaseMatcher: Matcher = lowercase.matcher(password)
        val uppercaseMatcher: Matcher = uppercase.matcher(password)

        if(username.isEmpty())
            _authenticate.value = AuthenticateResource.signupFailed("Please enter your phone number")
        else if(password.isEmpty())
            _authenticate.value = AuthenticateResource.signupFailed("Please enter your password")
        else if(password.length<6)
            _authenticate.value = AuthenticateResource.signupFailed("Password length require at least 6")
   /*     else if(password.length<6 || password.length>20)
            _authenticate.value = AuthenticateResource.signupFailed("Password must be between 6 and 20 characters.")
        else if(!specialMatcher.find() || !uppercaseMatcher.find() || !lowercaseMatcher.find())
            _authenticate.value = AuthenticateResource.signupFailed("Password must contain at least one uppercase letter, one lowercase letter, one numeric digit, and one special character.")
      */  else if(confirmpassword.isEmpty())
            _authenticate.value = AuthenticateResource.signupFailed("Please enter confirm password")
        else if(!password.equals(confirmpassword))
            _authenticate.value = AuthenticateResource.signupFailed("Password and confirm password not matched")
        else
            viewModelScope.launch {
                processSignupResponse(authenticateRepository.signup(ccode+username,password,confirmpassword))
            }
    }

    fun processSignupResponse(response: ApiResource<SignupResponse>)
    {
        when(response)
        {
            is ApiResource.Success ->
            {
                if(response.value.Success)
                    _authenticate.value = AuthenticateResource.signupSuccess
                else
                {

                    if(response.value.Message.isNotEmpty())
                    _authenticate.value = AuthenticateResource.signupFailed(response.value.Message)
                    else
                        _authenticate.value = AuthenticateResource.signupFailed("Registration failed! Try again")

                }

            }
            is ApiResource.Failure ->
            {

               /* val body: SignupResponse = Gson().fromJson(
                    response.errorResponse?.charStream(),
                    object : TypeToken<SignupResponse>() {}.type
                )*/

                val body: SignupResponse? = response.errorResponse?.charStream()
                    ?.let { fromJson(it) }


                 if(body?.Message?.isNotEmpty() == true)
                     _authenticate.value = AuthenticateResource.signupFailed(body.Message)
                 else
                     _authenticate.value = AuthenticateResource.signupFailed("Registration failed! Try again")
            }
        }
    }


}