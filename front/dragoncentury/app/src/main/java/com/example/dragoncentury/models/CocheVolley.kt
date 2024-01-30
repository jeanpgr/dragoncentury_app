package com.example.dragoncentury.models

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.dragoncentury.services.ApiUrlManager
import android.util.Base64
import com.android.volley.Response
import org.json.JSONArray
import org.json.JSONException

class CocheVolley {

    companion object {

        private var cochesList = listOf<CocheModel>()
        private val apiServices = ApiUrlManager

        fun updateCoche(context: Context, cocheModel: CocheModel) {
            val queue = Volley.newRequestQueue(context)
            val url = apiServices.getUrlUpdateCoche()

            val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener {
                Toast.makeText(context, "¡Cambios guardados con éxito!", Toast.LENGTH_LONG).show()
            }, Response.ErrorListener { error ->
                    Toast.makeText(context, "Error del servidor: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getParams(): Map<String, String>? {
                val params = HashMap<String, String>()
                    params["id_coche"] =cocheModel.idCoche.toString()
                    params["num_charges"] =cocheModel.numCargasCoche.toString()
                    params["num_change_battery"] =cocheModel.numCambBat.toString()
                    params["condic_coche"] =cocheModel.condicionCoche

                    return params
                }
            }
            queue.add(stringRequest)
        }
        fun getCochesList(context: Context, callback: (List<CocheModel>) -> Unit) {
            val queue = Volley.newRequestQueue(context)
            val url = apiServices.getUrlGetCoches()
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    cochesList = parseJson(response)
                    // Llama a la devolución de llamada con la lista obtenida
                    callback(cochesList)
                },
                { error ->
                    Toast.makeText(context, "Error del servidor: ${error.message} " , Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }

        private fun parseJson(json: String): List<CocheModel> {
            val coches = mutableListOf<CocheModel>()
            try {
                val jsonArray = JSONArray(json)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val idCoche = jsonObject.getInt("id_coche")
                    val nombCoche = jsonObject.getString("nomb_coche")
                    val colorCoche = jsonObject.getString("color_coche")
                    val numCharges = jsonObject.getInt("num_charges")
                    val numChangeBattery = jsonObject.getInt("num_change_battery")
                    val totalVueltas = jsonObject.getInt("total_vueltas")
                    val condCoche = jsonObject.getString("condic_coche")

                    val imgCocheBase64 = jsonObject.getString("img_coche")
                    val imgBytes = Base64.decode(imgCocheBase64, Base64.DEFAULT)

                    val coche = CocheModel(idCoche, imgBytes, nombCoche, colorCoche, numCharges, numChangeBattery, totalVueltas, condCoche)
                    coches.add(coche)
                }
            } catch (e: JSONException) {
                Log.e("JSON parse error", e.toString())
            }
            return coches
        }
    }
}