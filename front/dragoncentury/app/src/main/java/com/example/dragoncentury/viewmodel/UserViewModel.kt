package com.example.dragoncentury.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dragoncentury.models.UserModel
import com.example.dragoncentury.models.UserVolley

class UserViewModel : ViewModel() {

    private val userModel = MutableLiveData<UserModel?>()

    fun getUserLogin(context: Context,  nickUser: String, passwUser: String) {
        UserVolley.logear(context, nickUser, passwUser) {user ->
            userModel.postValue(user)
        }
    }

    fun getLiveDataUserLogin(): LiveData<UserModel?> {
        return userModel
    }
}