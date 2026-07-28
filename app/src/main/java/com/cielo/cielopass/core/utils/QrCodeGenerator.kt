package com.cielo.cielopass.core.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

object QrCodeGenerator {
    fun generateQrCodeBitmap(
        content: String,
        width: Int = 512,
        height: Int = 512,
    ): Bitmap? {
        if (content.isBlank()) return null

        return try {
            val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height)
            val matrixWidth = bitMatrix.width
            val matrixHeight = bitMatrix.height
            val pixels = IntArray(matrixWidth * matrixHeight)

            for (y in 0 until matrixHeight) {
                val offset = y * matrixWidth
                for (x in 0 until matrixWidth) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                }
            }

            createBitmap(matrixWidth, matrixHeight).apply {
                setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight)
            }
        } catch (_: Exception) {
            null
        }
    }
}
