package com.example.dragoncentury.services

class ApiUrlManager {
    companion object {

        val instance: ApiUrlManager by lazy {
            ApiUrlManager()
        }

        private const val URL_LOGIN = "http://172.16.2.145/sv_dragoncentury/login.php"

        public fun getUrlLogin(): String {
            return URL_LOGIN
        }
    }
}