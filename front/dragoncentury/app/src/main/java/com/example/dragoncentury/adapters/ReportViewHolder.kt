package com.example.dragoncentury.adapters

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.dragoncentury.databinding.ItemViewReporteBinding
import com.example.dragoncentury.models.ReportModel

class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemViewReporteBinding.bind(view)

    fun render(reportModel: ReportModel, onClickListener: (ReportModel) -> Unit) {
        binding.txtNumReport.text = reportModel.idReporte.toString()
        binding.txtFechaReport.text = reportModel.fecha
        binding.txtTotalVueltas.text = reportModel.totalVueltas.toString()
        binding.txtTotalGastos.text = reportModel.gastoTotal.toString()
        binding.txtTotalVenta.text = reportModel.totalVenta.toString()

        binding.iconOpenFile.setOnClickListener{
            onClickListener(reportModel)
        }
    }
}