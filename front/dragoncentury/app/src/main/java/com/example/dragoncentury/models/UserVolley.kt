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

class UserVolley {

    companion object {

        private val apiServices = ApiUrlManager

        fun getUser(context: Context, idUser: Int, callback: (UserModel?) -> Unit) {
            val queue = Volley.newRequestQueue(context)
            val url = apiServices.getUrlGetUser()

            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    val user = parseJson(response)
                    callback(user)
                }, Response.ErrorListener { error ->
                    Toast.makeText(context, "Error del servidor: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getParams(): Map<String, String>? {
                    val params = HashMap<String, String>()
                    params["id_user"] = idUser.toString()

                    return params
                }
            }
            queue.add(stringRequest)
        }

        private fun parseJson(json: String): UserModel? {
            var userModel: UserModel? = null
            try {
                val jsonObject = JSONObject(json)
                val idUser = jsonObject.getInt("id_user")
                val nombUser = jsonObject.getString("nomb_user")
                val apellUser = jsonObject.getString("apell_user")
                val rolUser = jsonObject.getString("rol_user")

                // Inicializa un nuevo objeto UserModel con los datos obtenidos del JSON
                userModel = UserModel(idUser, nombUser, apellUser, rolUser)
            } catch (e: JSONException) {
                println("Error al analizar el JSON: ${e.message}")
            }
            return userModel
        }




    }
}