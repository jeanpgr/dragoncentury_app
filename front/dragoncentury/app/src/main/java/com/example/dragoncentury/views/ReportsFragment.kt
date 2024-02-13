package com.example.dragoncentury.views

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dragoncentury.R
import com.example.dragoncentury.adapters.ReportAdapter
import com.example.dragoncentury.customcomponents.DatePickerFragment
import com.example.dragoncentury.models.CocheModel
import com.example.dragoncentury.models.CocheReportModel
import com.example.dragoncentury.models.ReportModel
import com.example.dragoncentury.models.UserModel
import com.example.dragoncentury.viewmodel.CocheViewModel
import com.example.dragoncentury.viewmodel.ReportViewModel
import com.example.dragoncentury.viewmodel.UserViewModel
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.min

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
    private var cochesList: List<CocheModel> = listOf()
    private val reportViewModel : ReportViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val cocheViewModel : CocheViewModel by viewModels()

    private lateinit var sharedPref: SharedPreferences
    private var userModel : UserModel? = null

    private lateinit var editTxtDateDesde: EditText
    private lateinit var editTxtDateHasta: EditText
    private lateinit var btnBuscarReport: Button
    private lateinit var btnGenerateReport: Button
    private lateinit var btnCleanFilter: Button
    private lateinit var txtFechaActual: TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = requireContext().getSharedPreferences("login_data", Context.MODE_PRIVATE)
        val idUser = sharedPref.getInt("id_user", 0)

        getUser(idUser)
        getListCoches()

        editTxtDateDesde = view.findViewById(R.id.editTxtDateDesde)
        editTxtDateHasta = view.findViewById(R.id.editTxtDateHasta)
        btnGenerateReport = view.findViewById(R.id.btnGenerarReporte)
        btnBuscarReport = view.findViewById(R.id.btnBuscarReporte)
        btnCleanFilter = view.findViewById(R.id.btnCleanFilter)
        txtFechaActual = view.findViewById(R.id.txtFechaActual)

        txtFechaActual.text = captureDateLocateCurrent()

        editTxtDateDesde.setOnClickListener{
            showDatePickerDialog(editTxtDateDesde)
        }

        editTxtDateHasta.setOnClickListener {
            showDatePickerDialog(editTxtDateHasta)
        }

        btnGenerateReport.setOnClickListener {
            showDialogGenerateReport()
        }

        btnBuscarReport.setOnClickListener {
            val dateDesde = editTxtDateDesde.text.toString()
            val dateHasta = editTxtDateHasta.text.toString()
            toFindReports(view, dateDesde, dateHasta)
        }

        btnCleanFilter.setOnClickListener {
            try {
                editTxtDateDesde.text.clear()
                editTxtDateHasta.text.clear()
                rvReports.adapter = null
            } catch (e: Exception) {
                e.printStackTrace()
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

    // funcion para mosrar los reportes filtrados
    private fun showFiltersReports(view: View) {
        rvReports = view.findViewById(R.id.rvReportes)
        rvReports.layoutManager = LinearLayoutManager(context)
        rvReports.adapter = ReportAdapter(reportsList)
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
    private fun showDialogGenerateReport() {
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
        val linLayt: LinearLayout = dialog.findViewById(R.id.linLaytGastos)

        linLayt.visibility = View.GONE

        btnAddGastos.setOnClickListener {
            toggleLinearLayout(linLayt, btnAddGastos, editTxtDescGasto, editTxtTotalGasto)
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
                            editTxtLectFinDraDC, editTxtDescGasto, editTxtTotalGasto
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

    // Función para recuperar el usuario logeado enviando su id
    private fun getUser(idUser: Int) {
        userViewModel.getUser(requireContext(), idUser)

        userViewModel.getLiveDataUser().observe(viewLifecycleOwner) { userModel ->
            this.userModel = userModel
        }
    }

    // Función para obter lista de coches
    private fun getListCoches() {
        cochesList = mutableListOf()
        cocheViewModel.getLiveDataCoches().observe(viewLifecycleOwner, Observer {
            cochesList = it
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
                               editTxtDescGasto: EditText, editTxtTotalGasto: EditText) {

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
}