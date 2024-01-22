package com.example.dragoncentury.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.dragoncentury.databinding.ItemGtncochesBinding
import com.example.dragoncentury.models.CocheModel

class GtnCocheViewHolder(view:View) : RecyclerView.ViewHolder(view) {

    val binding = ItemGtncochesBinding.bind(view)
    fun render(cocheModel: CocheModel, onClickListener: (CocheModel) -> Unit) {
        //gtnImgCoche = gtnCocheModel.imgCoche
        binding.txtNameCoche.text = cocheModel.nameCoche
        binding.txtColor.text = cocheModel.colorCoche
        binding.txtNumCargas.text = cocheModel.numCargasCoche
        binding.txtNumCambBatt.text = cocheModel.numCambBat
        binding.txtCondicion.text = cocheModel.condicionCoche

        itemView.setOnClickListener {
            onClickListener(cocheModel)
        }
    }
}