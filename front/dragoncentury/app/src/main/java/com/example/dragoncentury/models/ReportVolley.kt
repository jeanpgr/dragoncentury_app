package com.example.dragoncentury.models

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.dragoncentury.services.ApiUrlManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal

class ReportVolley {

    companion object {

        private var reportsList = listOf<ReportModel>()
        private val apiServices = ApiUrlManager

        fun insertReport(context: Context, reportModel: ReportModel) {
            val queue = Volley.newRequestQueue(context)
            val url = apiServices.getUrlGenerateReport()

            val jsonObj = JSONObject()
            jsonObj.put("with_gasto", reportModel.withGasto)
            jsonObj.put("descrip_nov", reportModel.descripNov)
            jsonObj.put("gasto_total", reportModel.gastoTotal)
            jsonObj.put("id_user_per", reportModel.idUserPer)
            jsonObj.put("fecha", reportModel.fecha)
            jsonObj.put("total_vueltas", reportModel.totalVueltas)
            jsonObj.put("total_venta", reportModel.totalVenta)
            val detalleReporteArray = JSONArray()
            reportModel.detalleCoches.forEach { coche ->
                val cocheObject = JSONObject().apply {
                    put("id_coche_per", coche.idCoche)
                    put("lectura_inicial", coche.lecturaInicial)
                    put("lectura_final", coche.lecturaFinal)
                    put("num_vueltas", coche.numVueltas)
                }
                detalleReporteArray.put(cocheObject)
            }
            jsonObj.put("detalle_reporte", detalleReporteArray)

            val jsonObjectRequest = object : JsonObjectRequest(
                Method.POST,
                url,
                jsonObj,
                {
                    Toast.makeText(context, "¡Reporte registrado con éxito!", Toast.LENGTH_LONG).show()
                },
                { error ->
                    Toast.makeText(context,"Error del servidor: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            ) { }
            queue.add(jsonObjectRequest)
        }


        fun getFiltroReport(context: Context, dateDesde: String, dateHasta: String,
                            callback: (List<ReportModel>)->Unit) {
            val queue = Volley.newRequestQueue(context)
            val url = apiServices.getUrlFiltReport()

            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    reportsList = parseJson(response)
                    if (reportsList.isEmpty()) {
                        Toast.makeText(context, "No se encontraron reportes para el rango de fechas especificado", Toast.LENGTH_LONG).show()
                    } else {
                        callback(reportsList)
                    }
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

        private fun parseJson(json: String): List<ReportModel> {
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
                    val idUserPer = jsonObject.getInt("id_user_per")

                    val cochesArray = jsonObject.getJSONArray("coches")
                    val coches = mutableListOf<CocheReportModel>()
                    for (j in 0 until cochesArray.length()) {
                        val cocheObject = cochesArray.getJSONObject(j)
                        val idCoche = cocheObject.getInt("id_coche")
                        val nombCoche = cocheObject.getString("nomb_coche")
                        val lecturaInicial = cocheObject.getInt("lectura_inicial")
                        val lecturaFinal = cocheObject.getInt("lectura_final")
                        val numVueltas = cocheObject.getInt("num_vueltas")

                        val cocheReportModel = CocheReportModel(idCoche, nombCoche, lecturaInicial, lecturaFinal, numVueltas)
                        coches.add(cocheReportModel)
                    }
                    val reportModel = ReportModel(idReport, fecha, totalVueltas,
                        totalVenta, descripNov, gastoTotal, idUserPer,   nombsUser, coches, true)
                    reports.add(reportModel)
                }
            } catch (e: JSONException) {
                Log.e("JSON parse error", e.toString())
            }
            return reports
        }

    }
}