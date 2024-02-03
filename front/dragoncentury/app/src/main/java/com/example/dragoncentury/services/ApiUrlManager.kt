package com.example.dragoncentury.services

class ApiUrlManager {
    companion object {

        val instance: ApiUrlManager by lazy {
            ApiUrlManager()
        }

        private const val SERVER_NAME = "192.168.100.76"

        private const val URL_LOGIN = "http://$SERVER_NAME/sv_dragoncentury/login.php"
        private const val URL_GET_COCHES = "http://$SERVER_NAME/sv_dragoncentury/getCoches.php"
        private const val URL_UPDATE_COCHE = "http://$SERVER_NAME/sv_dragoncentury/updateCoche.php"
        private const val URL_FILT_REPORT = "http://$SERVER_NAME/sv_dragoncentury/filtrarReportes.php"
        private const val URL_GENER_REPORT = "http://$SERVER_NAME/sv_dragoncentury/generarReporte.php"
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

        fun getGeneurReport(): String {
            return URL_GENER_REPORT
        }
    }
}