package com.example.dragoncentury.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dragoncentury.R
import com.example.dragoncentury.models.CocheModel

class GtnCocheAdapter(private val gtnCocheList: List<CocheModel>, private val onClickListener: (CocheModel) -> Unit) : RecyclerView.Adapter<GtnCocheViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GtnCocheViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return GtnCocheViewHolder(layoutInflater.inflate(R.layout.item_gtncoches, parent, false))
    }

    override fun onBindViewHolder(holder: GtnCocheViewHolder, position: Int) {
        val item = gtnCocheList[position]
        holder.render(item, onClickListener)
    }

    override fun getItemCount(): Int = gtnCocheList.size

}