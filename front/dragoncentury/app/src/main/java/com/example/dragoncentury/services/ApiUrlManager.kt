package com.example.dragoncentury.services

class ApiUrlManager {
    companion object {

        val instance: ApiUrlManager by lazy {
            ApiUrlManager()
        }

        private const val SERVER_NAME = "192.168.1.26"

        private const val URL_LOGIN = "http://$SERVER_NAME/api/login.php"
        private const val URL_GET_COCHES = "http://$SERVER_NAME/api/getCoches.php"
        private const val URL_UPDATE_COCHE = "http://$SERVER_NAME/api/updateCoche.php"
        private const val URL_FILT_REPORT = "http://$SERVER_NAME/api/filtrarReportes.php"
        private const val URL_GENER_REPORT = "http://$SERVER_NAME/api/generarReporte.php"
        private const val URL_GET_ULT_REP = "http://$SERVER_NAME/api/getUltimateReports.php"

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

        fun getUrlGetUltRep(): String {
            return URL_GET_ULT_REP
        }
    }
}