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
import org.json.JSONObject
import java.math.BigDecimal

class SalesSummaryVolley {

    companion object {

        private val apiServices = ApiUrlManager
        private var salesSummaryList = listOf<SalesSummaryModel>()

        fun getSalesSummary(
            context: Context, dateDsdRv: String, dateHstRv: String,
            callback: (List<SalesSummaryModel>) -> Unit
        ) {
            val queue = Volley.newRequestQueue(context)
            val url = apiServices.getUrlSalesSummary()

            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    salesSummaryList = parseJson(response)
                    if (salesSummaryList.isEmpty()) {
                        Toast.makeText(context, "No se encontraron resumen de ventas para el rango de fechas especificado", Toast.LENGTH_LONG).show()
                    } else {
                        callback(salesSummaryList)
                    }
                },
                {
                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                }) {
                override fun getParams(): Map<String, String>? {
                    val params = HashMap<String, String>()
                    params["date_dsd_rv"] = dateDsdRv
                    params["date_hst_rv"] = dateHstRv
                    return params
                }
            }
            queue.add(stringRequest)
        }

        private fun parseJson(json: String): List<SalesSummaryModel> {
            val salesSummaryList = mutableListOf<SalesSummaryModel>()
            try {
                val jsonObject = JSONObject(json)

                val sumTotVen = jsonObject.getString("sum_tot_ven").toBigDecimalOrNull() ?: BigDecimal.ZERO
                val sumTotGas = jsonObject.getString("sum_tot_gas").toBigDecimalOrNull() ?: BigDecimal.ZERO
                val sumTotCor = jsonObject.getString("sum_tot_cor").toInt()

                val detailSalesArray = jsonObject.getJSONArray("detail_sales")
                val detailSalesList = mutableListOf<DetailSales>()
                for (i in 0 until detailSalesArray.length()) {
                    val detailSalesObject = detailSalesArray.getJSONObject(i)
                    val idReport = detailSalesObject.getInt("id_reporte")
                    val nombUser = detailSalesObject.getString("nomb_user")
                    val apellUser = detailSalesObject.getString("apell_user")
                    val fecha = detailSalesObject.getString("fecha")
                    val totalVueltas = detailSalesObject.getInt("total_vueltas")
                    val totalCortesias = detailSalesObject.getInt("total_cortesias")
                    val totalGasto = detailSalesObject.getString("gasto_total").toBigDecimalOrNull() ?: BigDecimal.ZERO
                    val totalVenta = detailSalesObject.getString("total_venta").toBigDecimalOrNull() ?: BigDecimal.ZERO

                    val detailSales = DetailSales(idReport, nombUser, apellUser, fecha, totalVueltas, totalCortesias,  totalGasto, totalVenta)
                    detailSalesList.add(detailSales)
                }

                val salesSummaryModel = SalesSummaryModel(detailSalesList, sumTotVen, sumTotGas, sumTotCor)
                salesSummaryList.add(salesSummaryModel)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return salesSummaryList
        }

    }
}