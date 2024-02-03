package com.example.dragoncentury.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dragoncentury.models.CocheModel
import com.example.dragoncentury.models.CocheVolley

class CocheViewModel : ViewModel() {

    private val cocheModel = MutableLiveData<List<CocheModel>>()

    //Llena la Lista LiveData con la lista retorna del modelo
    fun getCoches(context: Context) {
        CocheVolley.getCochesList(context) { cochesList: List<CocheModel> ->
            cocheModel.postValue(cochesList)
        }
    }

    //Retorna la lista LiveData
    fun getLiveDataCoches(): MutableLiveData<List<CocheModel>> {
        return cocheModel
    }

    fun updateCoche(context: Context, cocheModel: CocheModel) {
        CocheVolley.updateCoche(context, cocheModel )
    }
}