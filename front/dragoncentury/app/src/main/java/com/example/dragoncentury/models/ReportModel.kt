package com.example.dragoncentury.models

import java.math.BigDecimal

data class ReportModel(
    val idReporte: Int,
    val idNovPago: Int,
    val idUser: Int,
    val date: String,
    val totalVueltas: Int,
    val totalGastos: BigDecimal,
    val totalVenta: BigDecimal
)