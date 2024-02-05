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
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dragoncentury.R
import com.example.dragoncentury.adapters.ReportAdapter
import com.example.dragoncentury.customcomponents.DatePickerFragment
import com.example.dragoncentury.models.ReportModel
import com.example.dragoncentury.viewmodel.ReportViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReportsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
    private val reportViewModel : ReportViewModel by viewModels()

    private lateinit var editTxtDateDesde: EditText
    private lateinit var editTxtDateHasta: EditText
    private lateinit var btnBuscarReport: Button
    private lateinit var btnGenerateReport: Button
    private lateinit var btnCleanFilter: Button
    private lateinit var txtFechaActual: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            editTxtDateDesde.text.clear()
            editTxtDateHasta.text.clear()
            rvReports.adapter = null
        }
    }

    //Funcion para mostrar el DatePicker
    private fun showDatePickerDialog(editTxtDate: EditText) {
        val datePicker = DatePickerFragment {day, month, year ->  onDateSelected(day, month, year, editTxtDate) }
        datePicker.show(childFragmentManager, "datePicker")
    }

    //Funcion para setear edittxt con las fechas seleccionadas
    private fun onDateSelected(day: Int, month: Int, year: Int, editTxtDate: EditText) {
        val monthReal = month + 1
        editTxtDate.setText("$year-$monthReal-$day")
    }

    // funcion para obtener los reportes filtrados
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

        val txtFechaActualGR : TextView = dialog.findViewById(R.id.txtFechaActualGR)
        txtFechaActualGR.text = captureDateLocateCurrent()

        dialog.show()
    }


    // Capturar fecha local actual
    private fun captureDateLocateCurrent() : String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateWithFormat = dateFormat.format(calendar.time)
        return dateWithFormat
    }
}