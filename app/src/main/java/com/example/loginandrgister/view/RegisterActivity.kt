package com.example.loginandrgister.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.loginandrgister.R
import com.example.loginandrgister.data.RegisterBody
import com.example.loginandrgister.databinding.ActivityRegisterBinding
import com.example.loginandrgister.repository.AuthRepository
import com.example.loginandrgister.utils.APIConsumer
import com.example.loginandrgister.utils.APIService
import com.example.loginandrgister.view_model.RegisterActivityViewModel
import com.example.loginandrgister.view_model.RegisterActivityViewModelFactory

class RegisterActivity : AppCompatActivity(),View.OnClickListener,View.OnFocusChangeListener,View.OnKeyListener {

    private lateinit var mBinding:ActivityRegisterBinding
    private lateinit var  mViewModel :RegisterActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =DataBindingUtil.setContentView(this,R.layout.activity_register)
        mBinding.enterFullName.onFocusChangeListener = this
        mBinding.enterPhoneNo.onFocusChangeListener = this
        mBinding.enterEmail.onFocusChangeListener = this
        mBinding.enterPassword.onFocusChangeListener = this
        mBinding.registerBtn.setOnClickListener(this)

        mViewModel = ViewModelProvider(this,RegisterActivityViewModelFactory(AuthRepository(APIService.getService()),application)).get(RegisterActivityViewModel::class.java)
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
                        "fullName" -> {
                            mBinding.fullnameTil.apply {
                                isErrorEnabled = true
                                error =entry.value
                            }
                        }
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

    private  fun validateFullName():Boolean{
        var errormessage: String? = null
        val value:String = mBinding.enterFullName.text.toString()
        if (value.isEmpty()){
            errormessage ="Full name is required"
        }
        if (errormessage != null){
            mBinding.fullnameTil.apply {
                isErrorEnabled = true
                error = errormessage
            }
        }

        return errormessage == null
    }
    private  fun validateMobileNumber():Boolean{
        var errormessage: String? = null
        val value = mBinding.enterPhoneNo.text.toString()
        if (value.isEmpty()){
            errormessage ="phone number is require"
        }

        if (errormessage != null){
            mBinding.enterPhoneNoTil.apply {
                isErrorEnabled = true
                error = errormessage
            }
        }


        return errormessage == null
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
    override fun onClick(view: View?) {
        if(view !=null && view.id == R.id.registerBtn)
            onSubmit()

    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
       if(view != null){
           when(view.id){
               R.id.enterFullName ->{
                   if (hasFocus){
                       if (mBinding.fullnameTil.isErrorEnabled){
                           mBinding.fullnameTil.isErrorEnabled = false
                       }

                   }else{
                       validateFullName()
                   }

               }
               R.id.enterPhoneNo ->{
                   if (hasFocus){
                       if (mBinding.enterPhoneNoTil.isErrorEnabled){
                           mBinding.enterPhoneNoTil.isErrorEnabled = false
                       }

                   }else{
                       validateMobileNumber()
                   }

               }
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
///////////
    override fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean {
    //if(keyEvent.KEYCODE == keyEvent!!.keyCode&& keyEvent.action == KeyEvent.ACTION_UP )

//}
        return false
    }

    private fun onSubmit(){
        if(validate()){
            ///////////
            mViewModel.registerUser(RegisterBody(mBinding.enterFullName.text!!.toString(),mBinding.enterEmail.text!!.toString(), mBinding.enterPassword.text!!.toString(),mBinding.enterPassword.text!!.toString()))
        }
    }

    private  fun validate():Boolean{
        var isValid = true
        if(!validateFullName()) isValid = false
        if(!validateEmail()) isValid = false
        if(!validatePassword()) isValid = false
        if(!validateMobileNumber()) isValid = false
        return  isValid
    }
}