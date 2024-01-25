package com.example.dragoncentury.services

class ApiUrlManager {
    companion object {

        val instance: ApiUrlManager by lazy {
            ApiUrlManager()
        }

        private const val SERVER_NAME = "192.168.1.26"

        private const val URL_LOGIN = "http://$SERVER_NAME/sv_dragoncentury/login.php"
        private const val URL_GET_COCHES = "http://$SERVER_NAME/sv_dragoncentury/getCoches.php"
        fun getUrlLogin(): String {
            return URL_LOGIN
        }

        fun getUrlGetCoches(): String {
            return URL_GET_COCHES
        }
    }
}