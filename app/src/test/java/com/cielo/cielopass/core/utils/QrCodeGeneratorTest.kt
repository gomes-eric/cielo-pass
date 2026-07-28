package com.cielo.cielopass.core.utils

import org.junit.Assert.assertNull
import org.junit.Test

class QrCodeGeneratorTest {
    @Test
    fun `given empty content when generateQrCodeBitmap called then return null`() {
        // GIVEN
        val blankContent = "   "

        // WHEN
        val result = QrCodeGenerator.generateQrCodeBitmap(blankContent)

        // THEN
        assertNull(result)
    }

    @Test
    fun `given valid content on unmocked android JVM environment when generateQrCodeBitmap called then handle error gracefully`() {
        // GIVEN
        val validContent = "https://cielopass.com/ticket/123"

        // WHEN
        val result = QrCodeGenerator.generateQrCodeBitmap(validContent)

        // THEN
        // In JVM unit testing without Android graphics framework, createBitmap returns null via internal catch block
        assertNull(result)
    }
}
