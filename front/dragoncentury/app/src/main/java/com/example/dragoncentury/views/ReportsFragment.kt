package com.example.dragoncentury.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTxtDateDesde = view.findViewById(R.id.editTxtDateDesde)
        editTxtDateHasta = view.findViewById(R.id.editTxtDateHasta)
        btnGenerateReport = view.findViewById(R.id.btnGenerarReporte)
        btnBuscarReport = view.findViewById(R.id.btnBuscarReporte)
        btnCleanFilter = view.findViewById(R.id.btnCleanFilter)

        editTxtDateDesde.setOnClickListener{
            showDatePickerDialog(editTxtDateDesde)
        }

        editTxtDateHasta.setOnClickListener {
            showDatePickerDialog(editTxtDateHasta)
        }

        btnBuscarReport.setOnClickListener {
            val dateDesde = editTxtDateDesde.text.toString()
            val dateHasta = editTxtDateHasta.text.toString()
            toFindReports(view, dateDesde, dateHasta)
        }
    }

    private fun showDatePickerDialog(editTxtDate: EditText) {
        val datePicker = DatePickerFragment {day, month, year ->  onDateSelected(day, month, year, editTxtDate) }
        datePicker.show(childFragmentManager, "datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int, editTxtDate: EditText) {
        val monthReal = month + 1
        editTxtDate.setText("$year-$monthReal-$day")
    }

    private fun getFilterReports(view: View, dateDesde: String, dateHasta: String) {
        reportViewModel.getLiveDataReports().observe(viewLifecycleOwner, Observer {
            reportsList = it
            showFiltersReports(view)
        })
        reportViewModel.getReports(requireContext(), dateDesde, dateHasta)
    }

    private fun showFiltersReports(view: View) {
        rvReports = view.findViewById(R.id.rvReportes)
        rvReports.layoutManager = LinearLayoutManager(context)
        rvReports.adapter = ReportAdapter(reportsList)
    }

    private fun toFindReports(view: View, dateDesde: String, dateHasta: String) {
        if (!dateDesde.isNullOrBlank() && !dateHasta.isNullOrBlank()) {
            getFilterReports(view, dateDesde, dateHasta)
        } else {
            Toast.makeText(requireContext(), "Se requiere el rango de fechas", Toast.LENGTH_LONG).show()
        }
    }


}