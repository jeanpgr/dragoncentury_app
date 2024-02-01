package com.example.dragoncentury.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dragoncentury.models.ReportModel
import com.example.dragoncentury.models.ReportVolley

class ReportViewModel : ViewModel() {

    private val reportModel = MutableLiveData<List<ReportModel>>()

    fun getReports(context: Context, dateDesde: String, dateHasta: String) {
        ReportVolley.getFiltroReport(
            context,
            dateDesde,
            dateHasta
        ) { reportList: List<ReportModel> ->
            reportModel.postValue(reportList)
        }
    }

    fun getLiveDataReports(): MutableLiveData<List<ReportModel>> {
        return reportModel
    }
}