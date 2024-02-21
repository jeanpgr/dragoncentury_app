package com.example.dragoncentury.services

class ApiUrlManager {
    companion object {

        val instance: ApiUrlManager by lazy {
            ApiUrlManager()
        }

        private const val SERVER_NAME = "ws-dragoncentury.info"

        private const val URL_LOGIN = "https://$SERVER_NAME/api/login.php"
        private const val URL_GET_USER = "https://$SERVER_NAME/api/getUser.php"
        private const val URL_GET_COCHES = "https://$SERVER_NAME/api/getCoches.php"
        private const val URL_UPDATE_COCHE = "https://$SERVER_NAME/api/updateCoche.php"
        private const val URL_FILT_REPORT = "https://$SERVER_NAME/api/filtrarReportes.php"
        private const val URL_GENER_REPORT = "https://$SERVER_NAME/api/generarReporte.php"

        fun getUrlLogin(): String {
            return URL_LOGIN
        }

        fun getUrlGetCoches(): String {
            return URL_GET_COCHES
        }

        fun getUrlUpdateCoche(): String {
            return URL_UPDATE_COCHE
        }

        fun getUrlFiltReport(): String {
            return URL_FILT_REPORT
        }

        fun getUrlGenerateReport(): String {
            return URL_GENER_REPORT
        }

        fun getUrlGetUser(): String {
            return URL_GET_USER
        }
    }
}