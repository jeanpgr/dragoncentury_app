package com.example.dragoncentury.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dragoncentury.models.SalesSummaryModel
import com.example.dragoncentury.models.SalesSummaryVolley

class SalesSummaryViewModel : ViewModel() {

    private val salesSummaryModel = MutableLiveData<List<SalesSummaryModel>>()

    fun getSalesSummary(context: Context, dateDsdRv: String, dateHstRv: String) {
        SalesSummaryVolley.getSalesSummary(context, dateDsdRv, dateHstRv) {
            salesSummaryList: List<SalesSummaryModel> ->
            salesSummaryModel.postValue(salesSummaryList)
        }
    }

    fun getLiveDataSalesSumm(): MutableLiveData<List<SalesSummaryModel>> {
        return salesSummaryModel
    }
}