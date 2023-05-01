package com.example.loginandrgister.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.loginandrgister.R
import com.example.loginandrgister.data.LoginBody
import com.example.loginandrgister.databinding.ActivityLoginBinding
import com.example.loginandrgister.repository.AuthRepository
import com.example.loginandrgister.utils.APIService
import com.example.loginandrgister.view_model.LoginActivityViewModel
import com.example.loginandrgister.view_model.LoginActivityViewModelFactory

class LoginActivity : AppCompatActivity() , View.OnFocusChangeListener  {  //,View.OnKeyListener

    private lateinit var mBinding :ActivityLoginBinding
    private lateinit var mViewModel:LoginActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "onCreate: Hello wolrd1", )
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
//        setContentView(mBinding.root)
        Log.e("TAG", "onCreate: Hello wolrd2", )
        mBinding.loginBtn.setOnClickListener{

            Log.e("TAG", "onCreate: Hello wolrd3", )
            submitForm()
        }

        mBinding.registertxt.setOnClickListener{
            startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
        }
        mBinding.enterEmail.onFocusChangeListener = this
        mBinding.enterPassword.onFocusChangeListener = this
        //mBinding.enterPassword.setOnKeyListener(this)

        mViewModel = ViewModelProvider(this,LoginActivityViewModelFactory(AuthRepository(APIService.getService())
            ,application)).get(LoginActivityViewModel::class.java)

        setupObservers()

    }
    private fun setupObservers(){
        mViewModel.getIsLoading().observe(this){
            mBinding.progressBar.isVisible = it

        }

        mViewModel.getErrorMessage().observe(this){
            // fullName,email,phone,password
            val fromErrorKeys = arrayOf("fullname","email","password")
            val message = StringBuilder()
            it.map { entry ->
                if(fromErrorKeys.contains(entry.key)){
                    when(entry.key){
                        "email" ->{
                            mBinding.enterEmailtil.apply {
                                isErrorEnabled = true
                                error =entry.value
                            }

                        }
                        "password"->{
                            mBinding.enterPasswordTil.apply {
                                isErrorEnabled = true
                                error =entry.value
                            }

                        }
                    }
                }else{
                    message.append(entry.value).append("\n")

                }
                if (message.isNotEmpty()){
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.info)
                        .setTitle("INFORMATION")
                        .setMessage(message)
                        .setPositiveButton("OK"){dialog, _ -> dialog!!.dismiss()}
                        .show()

                }

            }

        }
        mViewModel.getUser().observe(this){
            if(it != null){
                startActivity(Intent(this,HomeActivity::class.java))
            }
        }
    }


    private  fun validateEmail():Boolean{
        var errormessage: String? = null
        val value = mBinding.enterEmail.text.toString()
        if (value.isEmpty()){
            errormessage ="email is required"
        }else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()){
            errormessage ="Email address is invalid"
        }
//////////
        if (errormessage != null   ){
            mBinding.enterEmailtil.apply {
                isErrorEnabled = true
                error = errormessage
            }
        }
        return errormessage == null
    }

    private  fun validatePassword():Boolean{
        var errormessage: String? = null
        val value = mBinding.enterPassword.text.toString()
        if (value.isEmpty()){
            errormessage ="password is required"
        }else if(value.length<6){
            errormessage ="password must be 6 characters long "
        }

        if (errormessage != null){
            mBinding.enterPasswordTil.apply {
                isErrorEnabled = true
                error = errormessage
            }
        }
        return errormessage == null
    }

    private  fun validate():Boolean{
        var isValid = true
        if(!validateEmail()) isValid = false
        if(!validatePassword()) isValid = false

        return  isValid
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {

        if(view != null){
            when(view.id){
                R.id.enterEmail ->{
                    if (hasFocus){

                        if (mBinding.enterEmailtil.isErrorEnabled){
                            mBinding.enterEmailtil.isErrorEnabled = false
                        }
                    }else{
                        (validateEmail())
                        // do validate for its unique

                    }

                }

                R.id.enterPassword ->{
                    if (hasFocus){
                        if (mBinding.enterPasswordTil.isErrorEnabled){
                            mBinding.enterPasswordTil.isErrorEnabled = false
                        }

                    }else{
                        validatePassword()
                    }
                }

            }

        }
    }

///
    //override fun onKey(view: View?, keyCode: Int, keyEvent: KeyEvent?): Boolean {
      // if (keyCode == keyEvent.KEYCODE_ENTER && keyEvent!!.action = keyEvent.ACTION_UP)
        //submitForm()


    //return false
   // }

    private fun submitForm() {
        if (validate()){
            // verify user credentials
        mViewModel.loginUser((LoginBody(mBinding.enterEmail.text!!.toString(),mBinding.enterPassword.text!!.toString())))

        }
    }
}