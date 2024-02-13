package com.example.dragoncentury.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.dragoncentury.databinding.ActivityLoginBinding
import com.example.dragoncentury.models.UserModel
import com.example.dragoncentury.viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()
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


    private fun getUserLogin(nickUser: String, passwUser: String): UserModel? {
        var userModel : UserModel? = null
        userViewModel.getUserLogin(this, nickUser, passwUser)

        userViewModel.getLiveDataUserLogin().observe(this, Observer { user ->
            userModel = user
        })
        return userModel
    }

    private fun logear(nickUser: String, passwUser: String) {

        val user = getUserLogin(nickUser, passwUser)
        if (user != null) {
            val sharedPref = getSharedPreferences("login_data", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putInt("id_user", user.idUser)
            editor.putString("nomb_user", user.nombUser)
            editor.putString("apell_user", user.apellUser)
            editor.putString("rol_user", user.rolUser)

            editor.apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Bienvenido ${user.nombUser} ${user.apellUser}", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Credenciales Incorrectas", Toast.LENGTH_SHORT).show()
        }
    }
}