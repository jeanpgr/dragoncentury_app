package com.example.dragoncentury.models

data class CocheModel(
    val idCoche: Int,
    val imgCoche: ByteArray,
    var nameCoche: String,
    var colorCoche: String,
    var numCargasCoche: Int,
    var numCambBat: Int,
    var numVueltas: Int,
    var condicionCoche: String
)