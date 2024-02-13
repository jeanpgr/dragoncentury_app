package com.example.dragoncentury.models

import java.math.BigDecimal

data class ReportModel(
    val idReporte: Int,
    val fecha: String,
    val totalVueltas: Int,
    val totalVenta: BigDecimal,
    val descripNov: String,
    val gastoTotal: BigDecimal,
    val idUserPer: Int,
    val nombsUser: String,
    val detalleCoches: List<CocheReportModel>,
    val withGasto: Boolean
)