package com.example.dragoncentury.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dragoncentury.R
import com.example.dragoncentury.adapters.ReportAdapter
import com.example.dragoncentury.customcomponents.DatePickerFragment
import com.example.dragoncentury.customcomponents.ReportPDFGenerator
import com.example.dragoncentury.models.CocheModel
import com.example.dragoncentury.models.CocheReportModel
import com.example.dragoncentury.models.ReportModel
import com.example.dragoncentury.models.UserModel
import com.example.dragoncentury.viewmodel.CocheViewModel
import com.example.dragoncentury.viewmodel.ReportViewModel
import com.google.android.material.snackbar.Snackbar
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ReportsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reports, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReportsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var rvReports : RecyclerView
    private var reportsList : List<ReportModel> = listOf()
    private var cochesList: MutableList<CocheModel> = mutableListOf()
    private val reportViewModel : ReportViewModel by viewModels()
    private val cocheViewModel : CocheViewModel by viewModels()

    private lateinit var sharedPref: SharedPreferences
    private var userModel : UserModel? = null

    // Definición de componentes para el view
    private lateinit var editTxtDateDesde: EditText
    private lateinit var editTxtDateHasta: EditText
    private lateinit var btnGenerateReport: Button
    private lateinit var txtFechaActual: TextView
    private lateinit var iconSearchReport: ImageView
    private lateinit var iconCancel: ImageView

    private var cochesCargados = false
    private var dialogoMostrado = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        isAceptado ->
        if (isAceptado) Toast.makeText(requireContext(), "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show()
        else Toast.makeText(requireContext(), "PERMISOS DENEGADOS", Toast.LENGTH_SHORT).show()
    }

    private lateinit var progressBar: ProgressBar

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTxtDateDesde = view.findViewById(R.id.editTxtDateDesde)
        editTxtDateHasta = view.findViewById(R.id.editTxtDateHasta)
        btnGenerateReport = view.findViewById(R.id.btnGenerarReporte)
        txtFechaActual = view.findViewById(R.id.txtFechaActual)
        progressBar = view.findViewById(R.id.progressBar)
        iconSearchReport = view.findViewById(R.id.iconSearchReport)
        iconCancel = view.findViewById(R.id.iconCancel)

        hideProgressDialog()
        getUser()
        initWithFiveReports(view)

        txtFechaActual.text = captureDateLocateCurrent()


        editTxtDateDesde.setOnClickListener{
            showDatePickerDialog(editTxtDateDesde)
        }

        editTxtDateHasta.setOnClickListener {
            showDatePickerDialog(editTxtDateHasta)
        }

        btnGenerateReport.setOnClickListener {
            getListCoches(view)
            if (cochesCargados) {
                showDialogGenerateReport(view)
            } else {
                showProgressDialog()
                btnGenerateReport.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    btnGenerateReport.isEnabled = true
                }, 2000)
            }
        }

        iconSearchReport.setOnTouchListener { viewic, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    animateIconDown(viewic)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    animateIconUp(viewic)
                    // Ejecutar la acción al hacer clic en el icono de búsqueda
                    val dateDesde = editTxtDateDesde.text.toString()
                    val dateHasta = editTxtDateHasta.text.toString()
                    toFindReports(view, dateDesde, dateHasta)
                }
            }
            false
        }

        // Aplicar el efecto de touch y acciones al icono de cancelar
        iconCancel.setOnTouchListener { viewic, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    animateIconDown(viewic)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    animateIconUp(viewic)
                    // Ejecutar la acción al hacer clic en el icono de cancelar
                    try {
                        editTxtDateDesde.text.clear()
                        editTxtDateHasta.text.clear()
                        initWithFiveReports(view)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            false
        }
    }

    //Función para verificar permisos para generar PDFs
    private fun verificarPermisos(view: View, reportModel: ReportModel) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(requireContext(), "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show()
                ReportPDFGenerator.generatePDF(requireContext(), reportModel, "Reporte")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                Snackbar.make(view, "Permisos necesarios para crear archivos PDFs", Snackbar.LENGTH_INDEFINITE).setAction(
                    "Aceptar"
                ) {
                    requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }.show()
            }
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    //Función para mostrar el DatePicker
    private fun showDatePickerDialog(editTxtDate: EditText) {
        val datePicker = DatePickerFragment {day, month, year ->  onDateSelected(day, month, year, editTxtDate) }
        datePicker.show(childFragmentManager, "datePicker")
    }

    //Función para setear edittxt con las fechas seleccionadas
    private fun onDateSelected(day: Int, month: Int, year: Int, editTxtDate: EditText) {
        val monthReal = month + 1
        editTxtDate.setText("$year-$monthReal-$day")
    }

    // función para obtener los reportes filtrados
    private fun getFilterReports(view: View, dateDesde: String, dateHasta: String) {
        reportViewModel.getLiveDataReports().observe(viewLifecycleOwner, Observer {
            reportsList = it
            showFiltersReports(view)
        })
        reportViewModel.getReports(requireContext(), dateDesde, dateHasta)
    }

    // función para cargar los 5 ultimos reportes apenas se inicia el fragment
    private fun initWithFiveReports(view: View) {
        reportViewModel.getLiveDataReports().observe(viewLifecycleOwner, Observer {
            reportsList = it
            showFiltersReports(view)
        })
        reportViewModel.getUltReports(requireContext())
    }

    // funcion para mosrar los reportes filtrados
    private fun showFiltersReports(view: View) {
        rvReports = view.findViewById(R.id.rvReportes)
        rvReports.layoutManager = LinearLayoutManager(context)
        rvReports.adapter = ReportAdapter(reportsList) { verificarPermisos(view, it) }
    }

    //función para comprobar campos nulos y llamar al metodo obtner filtro de reportes
    private fun toFindReports(view: View, dateDesde: String, dateHasta: String) {
        if (!dateDesde.isNullOrBlank() && !dateHasta.isNullOrBlank()) {
            getFilterReports(view, dateDesde, dateHasta)
        } else {
            Toast.makeText(requireContext(), "Se requiere el rango de fechas", Toast.LENGTH_LONG).show()
        }
    }

    // Funcion para mostrar el dialog de generar reporte
    private fun showDialogGenerateReport(view: View) {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(R.layout.dialog_generate_report)
            val window = dialog.window
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val btnGenerateReport : Button = dialog.findViewById(R.id.btnGenerateReport)

            val txtNameUser : TextView = dialog.findViewById(R.id.txtNameUser)
            txtNameUser.text = "${userModel?.nombUser} ${userModel?.apellUser}"

            val txtFechaActualGR : TextView = dialog.findViewById(R.id.txtFechaActualGR)
            txtFechaActualGR.text = captureDateLocateCurrent()

            val txtDraRoj : TextView = dialog.findViewById(R.id.txtDraRoj)
            txtDraRoj.text = cochesList.getOrNull(0)?.nameCoche.toString()
            val txtDraChi : TextView = dialog.findViewById(R.id.txtDraChi)
            txtDraChi. text = cochesList.getOrNull(1)?.nameCoche.toString()
            val txtDraAma : TextView = dialog.findViewById(R.id.txtDraAma)
            txtDraAma.text = cochesList.getOrNull(2)?.nameCoche.toString()
            val txtDraDC : TextView = dialog.findViewById(R.id.txtDraDC)
            txtDraDC.text = cochesList.getOrNull(3)?.nameCoche.toString()

            val txtLectIniDraRoj : TextView = dialog.findViewById(R.id.txtLectIniDraRoj)
            txtLectIniDraRoj.text = cochesList.getOrNull(0)?.numVueltas.toString()
            val txtLectIniDraChi : TextView = dialog.findViewById(R.id.txtLectIniDraChi)
            txtLectIniDraChi.text = cochesList.getOrNull(1)?.numVueltas.toString()
            val txtLectIniDraAma : TextView = dialog.findViewById(R.id.txtLectIniDraAma)
            txtLectIniDraAma.text = cochesList.getOrNull(2)?.numVueltas.toString()
            val txtLectIniDraDC : TextView = dialog.findViewById(R.id.txtLectIniDraDC)
            txtLectIniDraDC.text = cochesList.getOrNull(3)?.numVueltas.toString()

            val editTxtLectFinDraRoj : EditText = dialog.findViewById(R.id.editTxtLectFinDraRoj)
            val editTxtLectFinDraAma : EditText = dialog.findViewById(R.id.editTxtLectFinDraAma)
            val editTxtLectFinDraChi : EditText = dialog.findViewById(R.id.editTxtLectFinDraChi)
            val editTxtLectFinDraDC : EditText = dialog.findViewById(R.id.editTxtLectFinDraDC)

            //Control de valores de lectura (Lectura final no sea menor a Lectura Inicial)
            cochesList.getOrNull(0)?.let { controlInputLectFinal(editTxtLectFinDraRoj, it.numVueltas) }
            cochesList.getOrNull(1)?.let { controlInputLectFinal(editTxtLectFinDraChi, it.numVueltas) }
            cochesList.getOrNull(2)?.let { controlInputLectFinal(editTxtLectFinDraAma, it.numVueltas) }
            cochesList.getOrNull(3)?.let { controlInputLectFinal(editTxtLectFinDraDC, it.numVueltas) }

            val editTxtDescGasto: EditText = dialog.findViewById(R.id.editTxtDescrGasto)
            val editTxtTotalGasto: EditText = dialog.findViewById(R.id.editTxtTotalGasto)

            val btnAddGastos : Button = dialog.findViewById(R.id.btnAddGastos)
            val linearLayoutGastos: LinearLayout = dialog.findViewById(R.id.linLaytGastos)

            linearLayoutGastos.visibility = View.GONE

            btnAddGastos.setOnClickListener {
                toggleLinearLayout(linearLayoutGastos, btnAddGastos, editTxtDescGasto, editTxtTotalGasto)
            }

            btnGenerateReport.setOnClickListener {
                removeFocusEditTxt(editTxtLectFinDraRoj,editTxtLectFinDraChi, editTxtLectFinDraAma, editTxtLectFinDraDC)

                if (!editTxtLectFinDraRoj.text.isNullOrBlank() &&
                    !editTxtLectFinDraChi.text.isNullOrBlank() &&
                    !editTxtLectFinDraAma.text.isNullOrBlank() &&
                    !editTxtLectFinDraDC.text.isNullOrBlank()
                ) {
                    if (editTxtDescGasto.text.isNullOrBlank() && !editTxtTotalGasto.text.isNullOrBlank()) {
                        // Si la descripción está vacía pero el total gasto está lleno
                        Toast.makeText(
                            requireContext(),
                            "Se requiere el campo Descripción Gasto",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (editTxtTotalGasto.text.isNullOrBlank() && !editTxtDescGasto.text.isNullOrBlank()) {
                        // Si el total gasto está vacío pero la descripción está lleno
                        Toast.makeText(
                            requireContext(),
                            "Se requiere el campo Total Gasto",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // Ambos campos de descripción y total gasto están llenos o vacíos
                        showDialogConfirmar(dialog, "¿Estás seguro de que deseas registrar este reporte?") {
                            generateReport(
                                editTxtLectFinDraRoj, editTxtLectFinDraChi, editTxtLectFinDraAma,
                                editTxtLectFinDraDC, editTxtDescGasto, editTxtTotalGasto, view
                            )
                        }
                    }
                } else {
                    // Al menos uno de los campos de lectura final está vacío
                    Toast.makeText(
                        requireContext(),
                        "Todos los campos de lectura final son requeridos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            dialog.show()
    }

    // Funcion para desplegar y contraer gastos, si se contrae se limpia los campos y cambio te texto el boton de agregar a quitar
    private fun toggleLinearLayout(linLayt: LinearLayout, btnAddGastos: Button,
                                   editTxtDescGasto: EditText, editTxtTotalGasto: EditText) {
        if (linLayt.visibility == View.VISIBLE) {
            linLayt.visibility = View.GONE
            btnAddGastos.text = "Agregar Gastos"
            editTxtDescGasto.text = null
            editTxtTotalGasto.text = null
        } else {
            btnAddGastos.text = "Quitar Gastos"
            linLayt.visibility = View.VISIBLE
        }
    }

    //Función para quitar el focus de los editTxt y verificar si son correctos los valores ingresados
    private fun removeFocusEditTxt(editTxtLectFinDraRoj: EditText, editTxtLectFinDraChi: EditText,
                                   editTxtLectFinDraAma: EditText, editTxtLectFinDraDC: EditText) {
        editTxtLectFinDraRoj.clearFocus()
        editTxtLectFinDraChi.clearFocus()
        editTxtLectFinDraAma.clearFocus()
        editTxtLectFinDraDC.clearFocus()
    }

    // Capturar fecha local actual
    private fun captureDateLocateCurrent() : String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateWithFormat = dateFormat.format(calendar.time)
        return dateWithFormat
    }

    // Función para recuperar el usuario logeado
    private fun getUser() {
        sharedPref = requireContext().getSharedPreferences("login_data", Context.MODE_PRIVATE)
        val idUser = sharedPref.getInt("id_user", 0)
        val nombUser = sharedPref.getString("nomb_user", "").toString()
        val apellUser = sharedPref.getString("apell_user", "").toString()
        val rolUser = sharedPref.getString("rol_user", "").toString()
        userModel = UserModel(idUser, nombUser, apellUser, rolUser)
    }

    // Función para obter lista de coches
    private fun getListCoches(view: View) {
        cochesCargados = false
        cocheViewModel.getLiveDataCoches().observe(viewLifecycleOwner, Observer { coches ->
            cochesList.clear() // Vaciar la lista antes de agregar nuevos coches
            cochesList.addAll(coches) // Agregar los nuevos coches a la lista
            cochesCargados = true
            // Verificar si el botón de generar reporte está visible y los coches están cargados
            if (!dialogoMostrado && cochesCargados) {
                showDialogGenerateReport(view)
                hideProgressDialog()
                dialogoMostrado = true
            }
        })
        cocheViewModel.getCoches(requireContext())
    }

    //Función para enviar reporte para su inserción
    private fun sendReport(reportModel: ReportModel) {
        reportViewModel.insertReport(requireContext(), reportModel)
    }

    private fun controlInputLectFinal(editTxt: EditText, minLect: Int) {
        editTxt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val currentValue = editTxt.text.toString().toIntOrNull() ?: 0
                if (currentValue < minLect) {
                    // Si el valor es menor que el mínimo, borrar el texto
                    editTxt.text.clear()
                }
            }
        }
    }

    private fun generateReport(editTxtLectFinDraRoj: EditText, editTxtLectFinDraChi: EditText,
                               editTxtLectFinDraAma: EditText, editTxtLectFinDraDC: EditText,
                               editTxtDescGasto: EditText, editTxtTotalGasto: EditText, view: View) {

        val draRoj = cochesList.getOrNull(0)
        val draChi = cochesList.getOrNull(1)
        val draAma = cochesList.getOrNull(2)
        val draDC = cochesList.getOrNull(3)

        val lectFinDraRoj = editTxtLectFinDraRoj.text.toString().toIntOrNull()
        val lectFinDraChi = editTxtLectFinDraChi.text.toString().toIntOrNull()
        val lectFinDraAma = editTxtLectFinDraAma.text.toString().toIntOrNull()
        val lectFinDraDC = editTxtLectFinDraDC.text.toString().toIntOrNull()

        val difLectDraRoj = lectFinDraRoj?.minus(draRoj?.numVueltas ?: 0) ?: 0
        val difLectDraChi = lectFinDraChi?.minus(draChi?.numVueltas ?: 0) ?: 0
        val difLectDraAma = lectFinDraAma?.minus(draAma?.numVueltas ?: 0) ?: 0
        val difLectDC = lectFinDraDC?.minus(draDC?.numVueltas ?: 0) ?: 0

        val totalVueltasAux = (difLectDraRoj) + (difLectDraChi) + (difLectDraAma) + (difLectDC)

        val detalleReportList = mutableListOf<CocheReportModel>()

        val cocheReportDraRoj = CocheReportModel(draRoj?.idCoche?:0, draRoj?.nameCoche?:"",
            draRoj?.numVueltas?:0, lectFinDraRoj?:0, difLectDraRoj)
        val cocheReportDraChi = CocheReportModel(draChi?.idCoche?:0, draChi?.nameCoche?:"",
            draChi?.numVueltas?:0, lectFinDraChi?:0, difLectDraChi)
        val cocheReportDraAma = CocheReportModel(draAma?.idCoche?:0, draAma?.nameCoche?:"",
            draAma?.numVueltas?:0, lectFinDraAma?:0, difLectDraAma)
        val cocheReportDraDC = CocheReportModel(draDC?.idCoche?:0, draDC?.nameCoche?:"",
            draDC?.numVueltas?:0, lectFinDraDC?:0, difLectDC)

        detalleReportList.add(cocheReportDraRoj)
        detalleReportList.add(cocheReportDraChi)
        detalleReportList.add(cocheReportDraAma)
        detalleReportList.add(cocheReportDraDC)

        if (editTxtDescGasto.text.isNullOrBlank() && editTxtTotalGasto.text.isNullOrBlank()) {

            val withGasto = false
            val descripNov = editTxtDescGasto.text.toString()
            val gastoTotal =  if (editTxtTotalGasto.text.isNotBlank())
            { BigDecimal(editTxtTotalGasto.text.toString()) } else { BigDecimal.ZERO }
            val idUserPer = userModel?.idUser
            val nombUser = userModel?.nombUser
            val fecha = captureDateLocateCurrent()
            val totalVueltas = totalVueltasAux
            val totalVenta = BigDecimal(totalVueltas) - gastoTotal
            val detalleReport = detalleReportList

            val reportModel = ReportModel(0, fecha, totalVueltas, totalVenta, descripNov,
                                            gastoTotal, idUserPer?:0, nombUser?:"", detalleReport, withGasto)
            sendReport(reportModel)
            verificarPermisos(view, reportModel)

        } else {
            val withGasto = true
            val descripNov = editTxtDescGasto.text.toString()
            val gastoTotal =  if (editTxtTotalGasto.text.isNotBlank())
            { BigDecimal(editTxtTotalGasto.text.toString()) } else { BigDecimal.ZERO }
            val idUserPer = userModel?.idUser
            val nombUser = userModel?.nombUser
            val fecha = captureDateLocateCurrent()
            val totalVueltas = totalVueltasAux
            val totalVenta = BigDecimal(totalVueltas) - gastoTotal
            val detalleReport = detalleReportList

            val reportModel = ReportModel(0, fecha, totalVueltas, totalVenta, descripNov,
                gastoTotal, idUserPer?:0, nombUser?:"", detalleReport, withGasto)
            sendReport(reportModel)
            verificarPermisos(view, reportModel)
        }
    }

    // Función para mostrar el dialog de confirmacion se pasa el mensaje y el metodo a ejecutar
    private fun showDialogConfirmar(dialogGR: Dialog, msj: String, onAceptarClick: () -> Unit) {
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
            dialogGR.dismiss()
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showProgressDialog() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressDialog() {
        progressBar.visibility = View.GONE
    }

    private fun animateIconDown(view: View) {
        // Escalar la imagen hacia abajo
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.8f)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f)
        scaleDownX.duration = 200
        scaleDownY.duration = 200
        scaleDownX.interpolator = AccelerateDecelerateInterpolator()
        scaleDownY.interpolator = AccelerateDecelerateInterpolator()
        val scaleDown = AnimatorSet()
        scaleDown.play(scaleDownX).with(scaleDownY)
        scaleDown.start()
    }

    private fun animateIconUp(view: View) {
        // Escalar la imagen hacia arriba
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 1f)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 1f)
        scaleUpX.duration = 200
        scaleUpY.duration = 200
        scaleUpX.interpolator = AccelerateDecelerateInterpolator()
        scaleUpY.interpolator = AccelerateDecelerateInterpolator()
        val scaleUp = AnimatorSet()
        scaleUp.play(scaleUpX).with(scaleUpY)
        scaleUp.start()
    }
}