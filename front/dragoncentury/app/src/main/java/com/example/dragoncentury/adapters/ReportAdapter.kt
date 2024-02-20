package com.example.dragoncentury.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.dragoncentury.R
import androidx.recyclerview.widget.RecyclerView
import com.example.dragoncentury.models.ReportModel

class ReportAdapter(private val reportsList: List<ReportModel>, private val onClickListener: (ReportModel) -> Unit) : RecyclerView.Adapter<ReportViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ReportViewHolder(layoutInflater.inflate(R.layout.item_view_reporte, parent, false))
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val item = reportsList[position]
        holder.render(item, onClickListener)
    }

    override fun getItemCount(): Int = reportsList.size
}