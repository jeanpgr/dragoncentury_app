package com.example.dragoncentury.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.dragoncentury.databinding.ItemGtncochesBinding
import com.example.dragoncentury.models.CocheModel
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
class GtnCocheViewHolder(view:View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemGtncochesBinding.bind(view)

    // Carga data en el CardView
    fun render(cocheModel: CocheModel, onClickListener: (CocheModel) -> Unit) {
        loadImgCoche(cocheModel)
        binding.txtNameCoche.text = cocheModel.nameCoche
        binding.txtColor.text = cocheModel.colorCoche
        binding.txtNumCargas.text = cocheModel.numCargasCoche.toString()
        binding.txtCondicion.text = cocheModel.condicionCoche

        itemView.setOnClickListener {
            onClickListener(cocheModel)
        }
    }

    private fun loadImgCoche(cocheModel: CocheModel) {
        try {
            // Guardar los bytes en un archivo temporal
            val file = File.createTempFile("tempImage", null, itemView.context.cacheDir)
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(cocheModel.imgCoche)
            fileOutputStream.close()

            // Cargar la imagen utilizando Picasso desde el archivo
            Picasso.get()
                .load(file)
                .into(binding.imgViewCoche)

        }  catch (e: IOException) {
            e.printStackTrace()
        }
    }

}