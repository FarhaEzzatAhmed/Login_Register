package com.example.loginandrgister.repository

import com.example.loginandrgister.data.LoginBody
import com.example.loginandrgister.data.RegisterBody
import com.example.loginandrgister.utils.APIConsumer
import com.example.loginandrgister.utils.RequestStatus
import com.example.loginandrgister.utils.SimplifiedMessage
import kotlinx.coroutines.flow.flow

class AuthRepository(val consumer :APIConsumer) {



fun registerUser(body: RegisterBody) = flow {
    emit(RequestStatus.Waiting)
    val response = consumer.registerUser(body)
    if (response.isSuccessful){
        emit(((RequestStatus.Success(response.body()!!))))

    }else{
        emit(
            RequestStatus.Error(
                SimplifiedMessage.get(
                    response.errorBody()!!.byteStream().reader().readText()

                )
            )
        )
    }
}


    fun loginUser(body: LoginBody) = flow {
        emit(RequestStatus.Waiting)
        val response = consumer.loginUser(body)
        if (response.isSuccessful){
            emit(((RequestStatus.Success(response.body()!!))))

        }else{
            emit(
                RequestStatus.Error(
                    SimplifiedMessage.get(
                        response.errorBody()!!.byteStream().reader().readText()

                    )
                )
            )
        }
    }



}