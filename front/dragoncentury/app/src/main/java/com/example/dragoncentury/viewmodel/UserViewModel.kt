package com.example.dragoncentury.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dragoncentury.models.UserModel
import com.example.dragoncentury.models.UserVolley

class UserViewModel : ViewModel() {

    private val userModel = MutableLiveData<UserModel?>()

    fun getUser(context: Context, idUser: Int) {
        UserVolley.getUser(context, idUser) { user ->
            userModel.postValue(user)
        }
    }

    fun getLiveDataUser(): LiveData<UserModel?> {
        return userModel
    }
}