package com.example.dragoncentury

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.dragoncentury.databinding.ActivityLoginBinding
import com.example.dragoncentury.services.ApiUrlManager

class LoginActivity : AppCompatActivity() {

    private val apiServices = ApiUrlManager
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogIn.setOnClickListener{
            val nick_user = binding.editTxtUserName.text.toString()
            val passw_user = binding.editTxtPassword.text.toString()
            if (TextUtils.isEmpty(nick_user)) {
                binding.editTxtUserName.error = "Ingrese su Nombre de Usuario"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(passw_user)) {
                binding.editTxtPassword.error = "Ingrese su Contraseña"
                return@setOnClickListener
            }
            logear(nick_user, passw_user)
        }
    }


    private fun logear(nick_user: String, passw_user: String) {

        val url = apiServices.getUrlLogin()

        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.length() > 0) {
                        val id_user = jsonObject.getString("id_user")

                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        // Procesa la respuesta del servidor
                        val sharedPref = getSharedPreferences("login_data", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("id_user", id_user)
                        editor.apply()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Credenciales Incorrectas", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error en la petición: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["nick_user"] = nick_user
                params["passw_user"] = passw_user
                return params
            }
        }
        queue.add(stringRequest)
    }

}


/*
#ffd02c
#f18413
#e5261d
#ad191c
#eb5c73
#1e1d38
#c8d2d3
#fffbdf
 */