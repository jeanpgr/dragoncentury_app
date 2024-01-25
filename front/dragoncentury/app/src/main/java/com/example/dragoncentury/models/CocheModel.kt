package com.example.dragoncentury.models

data class CocheModel(
    val idCoche: Int,
    val imgCoche: ByteArray,
    val nameCoche: String,
    val colorCoche: String,
    val numCargasCoche: Int,
    val numCambBat: Int,
    val numVueltas: Int,
    val condicionCoche: String
)