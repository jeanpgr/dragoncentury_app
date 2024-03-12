package com.example.dragoncentury.models

import java.math.BigDecimal

data class SalesSummaryModel  (
    val detailSalesList: List<DetailSales>,
    val sumTotSales: BigDecimal,
    val sumTotGast: BigDecimal,
    val sumTotCort: Int
)

data class DetailSales(
    val idReport: Int,
    val nombUser: String,
    val apellUser: String,
    val fecha: String,
    val totalVueltas: Int,
    val totalCortesias: Int,
    val totalGasto: BigDecimal,
    val totalVenta: BigDecimal
)