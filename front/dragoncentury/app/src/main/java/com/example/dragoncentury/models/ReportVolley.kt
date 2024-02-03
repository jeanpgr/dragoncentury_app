package com.example.dragoncentury.models

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.dragoncentury.services.ApiUrlManager
import org.json.JSONArray
import org.json.JSONException
import java.math.BigDecimal

class ReportVolley {

    companion object {

        private var reportsList = listOf<ReportModel>()
        private val apiServices = ApiUrlManager

        fun getFiltroReport(context: Context, dateDesde: String, dateHasta: String,
                            callback: (List<ReportModel>)->Unit) {
            val queue = Volley.newRequestQueue(context)
            val url = apiServices.getUrlFiltReport()

            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    reportsList = parseJson(response)
                    // Llama a la devolución de llamada con la lista obtenida
                    callback(reportsList)
                },
                { error ->
                     Toast.makeText(context, "Error del servidor: ${error.message} " , Toast.LENGTH_SHORT).show()
                }){
                override fun getParams(): Map<String, String>? {
                    val params = HashMap<String, String>()
                    params["date_desde"] = dateDesde
                    params["date_hasta"] = dateHasta
                    return params
                }
            }
            queue.add(stringRequest)
        }

        fun parseJson(json: String): List<ReportModel> {
            val reports = mutableListOf<ReportModel>()
            try {
                val jsonArray = JSONArray(json)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    val idReport = jsonObject.getInt("id_reporte")
                    val fecha = jsonObject.getString("fecha")
                    val totalVueltas = jsonObject.getInt("total_vueltas")
                    val totalVenta = BigDecimal(jsonObject.getString("total_venta"))
                    val descripNov = jsonObject.getString("descrip_nov")
                    val gastoTotal = BigDecimal(jsonObject.getString("gasto_total"))
                    val nombsUser = jsonObject.getString("nombs_user")

                    val cochesArray = jsonObject.getJSONArray("coches")
                    val coches = mutableListOf<CocheReportModel>()
                    for (j in 0 until cochesArray.length()) {
                        val cocheObject = cochesArray.getJSONObject(j)
                        val nombCoche = cocheObject.getString("nomb_coche")
                        val lecturaInicial = cocheObject.getInt("lectura_inicial")
                        val lecturaFinal = cocheObject.getInt("lectura_final")
                        val numVueltas = cocheObject.getInt("num_vueltas")

                        val cocheReportModel = CocheReportModel(nombCoche, lecturaInicial, lecturaFinal, numVueltas)
                        coches.add(cocheReportModel)
                    }
                    val reportModel = ReportModel(idReport, fecha, totalVueltas,
                        totalVenta, descripNov, gastoTotal, nombsUser, coches)
                    reports.add(reportModel)
                }
            } catch (e: JSONException) {
                Log.e("JSON parse error", e.toString())
            }
            return reports
        }

    }
}