package com.example.dragoncentury.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.dragoncentury.databinding.ActivityLoginBinding
import com.example.dragoncentury.services.ApiUrlManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val apiServices = ApiUrlManager
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogIn.setOnClickListener{
            val nickUser = binding.editTxtUserName.text.toString()
            val passUser = binding.editTxtPassword.text.toString()
            if (TextUtils.isEmpty(nickUser)) {
                binding.editTxtUserName.error = "Ingrese su Nombre de Usuario"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(passUser)) {
                binding.editTxtPassword.error = "Ingrese su Contraseña"
                return@setOnClickListener
            }
            logear(nickUser, passUser)
        }
    }


    private fun logear(nickUser: String, passwUser: String) {

        val url = apiServices.getUrlLogin()

        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.length() > 0) {
                        val idUser = jsonObject.getInt("id_user")

                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        // Procesa la respuesta del servidor
                        val sharedPref = getSharedPreferences("login_data", MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putInt("id_user", idUser)
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
                Toast.makeText(this, "Error en la petición: ${error.message}", Toast.LENGTH_SHORT)
                    .show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["nick_user"] = nickUser
                params["passw_user"] = passwUser
                return params
            }
        }
        queue.add(stringRequest)
    }
}