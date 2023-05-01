package com.example.loginandrgister.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginandrgister.data.LoginBody
import com.example.loginandrgister.data.RegisterBody
import com.example.loginandrgister.data.User
import com.example.loginandrgister.repository.AuthRepository
import com.example.loginandrgister.utils.AuthToken
import com.example.loginandrgister.utils.RequestStatus
import kotlinx.coroutines.launch

class LoginActivityViewModel (val authRepository: AuthRepository, val application : Application):ViewModel(){

    private  var isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    private  var errorManager:MutableLiveData<HashMap<String,String>> = MutableLiveData()
    //mail address mutable live data .
    // password mutable live data
    private  var user : MutableLiveData<User> = MutableLiveData()

    fun getIsLoading():LiveData<Boolean> = isLoading
    fun getErrorMessage(): LiveData<HashMap<String,String>> = errorManager
    //fun getIsUnique(): LiveData<Boolean> = isUnique
    fun getUser(): LiveData<User> = user

    //fun validateEmailAddress(body: ValidateEmailBody){}

fun loginUser(body :LoginBody){
    viewModelScope.launch {
          authRepository.loginUser(body).collect{
              when(it){
                  is RequestStatus.Waiting -> {
                      isLoading.value = true
                  }
                  is RequestStatus.Success ->{
                      isLoading.value = false
                      user.value = it.data.user
                      // save token using shared preference
                      AuthToken.getInstance(application.baseContext).token = it.data.token
                  }
                  is RequestStatus.Error ->{
                      isLoading.value = false
                      errorManager.value = it.message
                  }
              }
          }


    }
}


}