package com.example.dragoncentury.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dragoncentury.models.CocheModel
import com.example.dragoncentury.models.CocheVolley

class CocheViewModel : ViewModel() {

    private val cocheModel = MutableLiveData<List<CocheModel>>()
    fun getCoches(context: Context) {
        CocheVolley.getCochesList(context) { cochesList: List<CocheModel> ->
            cocheModel.postValue(cochesList)
        }
    }
    fun getLiveData(): MutableLiveData<List<CocheModel>> {
        return cocheModel
    }
}