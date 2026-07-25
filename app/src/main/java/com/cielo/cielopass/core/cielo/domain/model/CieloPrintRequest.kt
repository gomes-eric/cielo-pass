package com.cielo.cielopass.core.cielo.domain.model

sealed interface CieloPrintRequest {
    data class Text(
        val text: String,
        val align: Int = 1,
        val textSize: Int = 20,
        val typeface: Int = 0,
    ) : CieloPrintRequest

    data class Image(
        val imagePath: String,
    ) : CieloPrintRequest

    data class MultiColumn(
        val columns: List<String>,
        val alignments: List<Int> = listOf(1, 0, 2),
        val textSizes: List<Int> = listOf(20, 20, 20),
    ) : CieloPrintRequest
}
