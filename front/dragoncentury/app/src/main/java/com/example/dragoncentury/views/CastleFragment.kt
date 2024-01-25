package com.example.dragoncentury.views

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dragoncentury.R
import com.example.dragoncentury.adapters.GtnCocheAdapter
import com.example.dragoncentury.models.CocheModel
import com.example.dragoncentury.viewmodel.CocheViewModel
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CastleFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_castle, container, false)
    }

    companion object {
        @JvmStatic fun newInstance(param1: String, param2: String) =
                CastleFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    private lateinit var rvGtnCoches: RecyclerView
    private var gtnCochesList: List<CocheModel> = listOf()
    private val cocheViewModel : CocheViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getListCoches(view)
    }

    private fun onCocheSelected(coche: CocheModel) {
        //Toast.makeText(requireContext(), coche.nameCoche, Toast.LENGTH_LONG).show()
        showDialogGtnCoches(coche)
    }
    private fun initDataInRecycleView(view: View) {
        rvGtnCoches = view.findViewById(R.id.rvGtnCoches)
        rvGtnCoches.layoutManager = LinearLayoutManager(context)
        rvGtnCoches.adapter = GtnCocheAdapter(gtnCochesList, {onCocheSelected(it)})
    }
    private fun getListCoches(view: View) {
        cocheViewModel.getLiveData().observe(viewLifecycleOwner, Observer {
            gtnCochesList = it
            initDataInRecycleView(view)
        })
        cocheViewModel.getCoches(requireContext())
    }

    private fun showDialogGtnCoches(cocheModel: CocheModel) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_gtncoche)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val imgGtnCoche : ImageView = dialog.findViewById(R.id.imgViewDialogCoche)
        val txtNameCoche : TextView = dialog.findViewById(R.id.txtDialogNameCoche)
        val txtColorCoche : TextView = dialog.findViewById(R.id.txtDialogColorCoche)
        val txtNumVueltas : TextView = dialog.findViewById(R.id.txtDialogNumVueltas)
        val txtNumCargas : TextView = dialog.findViewById(R.id.txtDialogNumCargas)
        val txtCambBatt : TextView = dialog.findViewById(R.id.txtDialogNumCambBat)
        val txtCondic : TextView = dialog.findViewById(R.id.txtDialogCondCoche)

        loadImgCoche(cocheModel, imgGtnCoche)
        txtNameCoche.text = cocheModel.nameCoche
        txtColorCoche.text = cocheModel.colorCoche
        txtNumVueltas.text = cocheModel.numVueltas.toString()
        txtNumCargas.text = cocheModel.numCargasCoche.toString()
        txtCambBatt.text = cocheModel.numCambBat.toString()
        txtCondic.text = cocheModel.condicionCoche

        dialog.show()
    }

    private fun loadImgCoche(cocheModel: CocheModel, imgGtnCoche: ImageView) {
        try {
            // Guardar los bytes en un archivo temporal
            val file = File.createTempFile("tempImage", null, requireView().context.cacheDir)
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(cocheModel.imgCoche)
            fileOutputStream.close()

            // Cargar la imagen utilizando Picasso desde el archivo
            Picasso.get()
                .load(file)
                .into(imgGtnCoche)

        }  catch (e: IOException) {
            e.printStackTrace()
        }
    }


}