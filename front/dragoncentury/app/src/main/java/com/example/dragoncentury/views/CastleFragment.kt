package com.example.dragoncentury.views

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
    private lateinit var progressBarDC: ProgressBar
    private var cochesCargados = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBarDC = view.findViewById(R.id.progressBarGC)

        getListCoches(view)
    }

    //Activa la seleccion del item pasando como parametro el coche seleccionado
    private fun onCocheSelected(coche: CocheModel) {
        showDialogGtnCoches(coche)
    }

    //Iniciliza el RecycleView con la lista de objetos obtenida por el ViewModel
    private fun initDataInRecycleView(view: View) {
        rvGtnCoches = view.findViewById(R.id.rvGtnCoches)
        rvGtnCoches.layoutManager = LinearLayoutManager(context)
        rvGtnCoches.adapter = GtnCocheAdapter(gtnCochesList, {onCocheSelected(it)})
    }

    //Trae la lista de objetos(coches) del ViewModel con LiveData - Observer
    private fun getListCoches(view: View) {
        showProgressDialog()
        cocheViewModel.getLiveDataCoches().observe(viewLifecycleOwner, Observer {
            gtnCochesList = it
            cochesCargados = true
            if (cochesCargados) {
                hideProgressDialog()
                initDataInRecycleView(view)
            } else {
                showProgressDialog()
            }
        })
        cocheViewModel.getCoches(requireContext())
    }

    //Muestra el cuadro de dialogo del coche seleccionado
    private fun showDialogGtnCoches(cocheModel: CocheModel) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_gtncoche)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)

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

        val iconEditCond : ImageView = dialog.findViewById(R.id.iconEditCondCoche)
        iconEditCond.setOnClickListener {
            txtViewToEditTxtCondCoche(cocheModel, txtCondic, dialog, iconEditCond)
        }

        val iconAddNumCargas : ImageView = dialog.findViewById(R.id.iconAddNumCargas)
        iconAddNumCargas.setOnClickListener {
            showDialogConfirmar("¿Desea confirmar el incremento en el número de cargas?",
                {addNumCargas(cocheModel, txtNumCargas)})

        }

        val iconNumCambBat : ImageView = dialog.findViewById(R.id.iconAddNumCambBat)
        iconNumCambBat.setOnClickListener {
            showDialogConfirmar("¿Desea confirmar el incremento en el número de cambios de batería?",
                {addNumCambBat(cocheModel, txtCambBatt)})
        }

        dialog.show()
    }

    // Funcion para mostrar el dialog de confirmacion se pasa el mensaje y el metodo a ejecutar
    private fun showDialogConfirmar(msj: String, onAceptarClick: () -> Unit) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_confirmar)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)

        val txtMessage : TextView = dialog.findViewById(R.id.txtDialogConfirmar)
        val btnAceptar : Button = dialog.findViewById(R.id.btnAceptar)
        val btnCancelar : Button = dialog.findViewById(R.id.btnCancelar)

        txtMessage.text = msj

        btnAceptar.setOnClickListener {
            onAceptarClick.invoke()
            dialog.dismiss()
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    // Funcion para enviar el objeto actualizado al viewmodel y lo acualice con volley
    private fun updateCoche(cocheModel: CocheModel) {
        cocheViewModel.updateCoche(requireContext(), cocheModel)
    }

    // Remover el TextView actual y colocar un Edittxt con el valor que estaba
    private fun txtViewToEditTxtCondCoche(cocheModel: CocheModel, txtCondCoche: TextView, dialog: Dialog, iconViewEdit: ImageView) {
        val parentLayout = txtCondCoche.parent as LinearLayout
        val index = parentLayout.indexOfChild(txtCondCoche)
        parentLayout.removeViewAt(index)

        val editTextCondCoche = EditText(dialog.context)
        editTextCondCoche.id = R.id.txtDialogCondCoche
        editTextCondCoche.layoutParams = txtCondCoche.layoutParams

        parentLayout.addView(editTextCondCoche, index)
        editTextCondCoche.requestFocus()
        val imm = dialog.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editTextCondCoche, InputMethodManager.SHOW_IMPLICIT)
        iconViewEdit.setImageResource(R.drawable.icon_save)

        iconViewEdit.setOnClickListener {
            val newCondValue = editTextCondCoche.text.toString().trim()
            if (newCondValue.isNotEmpty()) {
                saveNewCondCoche(cocheModel, newCondValue, txtCondCoche, parentLayout, index, dialog, iconViewEdit)
            } else {
                Toast.makeText(requireContext(), "No ingrese campos vacios", Toast.LENGTH_LONG).show()
                restoreToTxtViewCondCoche(cocheModel, txtCondCoche, parentLayout, index, dialog, iconViewEdit)
            }
        }
    }

    // Función para guardar el nuevo valor de la condicion del coche y restaurar el diseño original
    private fun saveNewCondCoche(
        cocheModel: CocheModel,
        txtValorNew: String,
        txtCondCoche: TextView,
        parentLayout: LinearLayout,
        index: Int,
        dialog: Dialog,
        iconViewEdit: ImageView
    ) {
        cocheModel.condicionCoche = txtValorNew
        updateCoche(cocheModel)
        // Crear un nuevo TextView con el nuevo valor
        val newTxtCondCoche = TextView(dialog.context)
        newTxtCondCoche.id = R.id.txtDialogCondCoche
        newTxtCondCoche.layoutParams = txtCondCoche.layoutParams
        newTxtCondCoche.text = txtValorNew

        // Configurar el nuevo TextView
        newTxtCondCoche.textSize = 20f
        newTxtCondCoche.setTextColor(ContextCompat.getColor(dialog.context, R.color.color_6to))
        newTxtCondCoche.setPaddingRelative(5, 0, 0, 0)

        // Remover el EditText y agregar el nuevo TextView al mismo índice
        parentLayout.removeViewAt(index)
        parentLayout.addView(newTxtCondCoche, index)

        // Restaurar la apariencia original del icono de edición
        iconViewEdit.setImageResource(R.drawable.icon_edit_square)

        dialog.dismiss()
        showDialogGtnCoches(cocheModel)
    }

    // Función para restaurar el diseño original del txtView de condicion coche en caso de campo nulo o con espacios en blanco
    private fun restoreToTxtViewCondCoche(
        cocheModel: CocheModel,
        txtCondCoche: TextView,
        parentLayout: LinearLayout,
        index: Int,
        dialog: Dialog,
        iconViewEdit: ImageView
    ) {
        val newTxtCondCoche = TextView(dialog.context)
        newTxtCondCoche.id = R.id.txtDialogCondCoche
        newTxtCondCoche.layoutParams = txtCondCoche.layoutParams
        newTxtCondCoche.text = txtCondCoche.text

        newTxtCondCoche.textSize = 20f
        newTxtCondCoche.setTextColor(ContextCompat.getColor(dialog.context, R.color.color_6to))
        newTxtCondCoche.setPaddingRelative(5, 0, 0, 0)

        parentLayout.removeViewAt(index)
        parentLayout.addView(newTxtCondCoche, index)

        iconViewEdit.setImageResource(R.drawable.icon_edit_square)

        dialog.dismiss()
        showDialogGtnCoches(cocheModel)
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

    // Funcion para incrementar el numero de cargas en el txtview y despues enviar el objeto actualizado
    private fun addNumCargas(cocheModel: CocheModel, txtNumCargas: TextView) {
        val currentValue = txtNumCargas.text.toString()
        val currentNumCargas = currentValue.toIntOrNull()
        val newNumCargas = currentNumCargas!! + 1
        txtNumCargas.text = newNumCargas.toString()

        cocheModel.numCargasCoche = newNumCargas
        updateCoche(cocheModel)
    }

    // Funcion para incrementar el numero de cambios de bateria en el txtview y despues enviar el objeto actualizado
    private fun addNumCambBat(cocheModel: CocheModel, txtNumCambBat: TextView) {
        val currentValue = txtNumCambBat.text.toString()
        val currentNumCambBat = currentValue.toIntOrNull()
        val newNumCambBat = currentNumCambBat!! + 1
        txtNumCambBat.text = newNumCambBat.toString()

        cocheModel.numCambBat = newNumCambBat
        updateCoche(cocheModel)
    }

    private fun showProgressDialog() {
        progressBarDC.visibility = View.VISIBLE
    }

    private fun hideProgressDialog() {
        progressBarDC.visibility = View.GONE
    }
}