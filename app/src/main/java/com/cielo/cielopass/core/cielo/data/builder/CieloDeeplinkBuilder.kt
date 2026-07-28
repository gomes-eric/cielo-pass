package com.cielo.cielopass.core.cielo.data.builder

import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import com.cielo.cielopass.core.cielo.data.dto.CieloItemDTO
import com.cielo.cielopass.core.cielo.data.dto.CieloOrderQueryRequestDTO
import com.cielo.cielopass.core.cielo.data.dto.CieloOrdersListRequestDTO
import com.cielo.cielopass.core.cielo.data.dto.CieloPaymentRequestDTO
import com.cielo.cielopass.core.cielo.data.dto.CieloPrintRequestDTO
import com.cielo.cielopass.core.cielo.data.dto.CieloReversalRequestDTO
import com.cielo.cielopass.core.cielo.data.dto.CieloSubAcquirerDTO
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.EnabledProducts
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.Establishments
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.OrderQuery
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.OrdersList
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.Payment
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.Print
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.Reversal
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.TerminalInfo
import com.cielo.cielopass.core.cielo.domain.model.CieloOrderQueryRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloOrdersListRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloPrintRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloPrintRequest.Image
import com.cielo.cielopass.core.cielo.domain.model.CieloPrintRequest.MultiColumn
import com.cielo.cielopass.core.cielo.domain.model.CieloPrintRequest.Text
import com.cielo.cielopass.core.cielo.domain.model.CieloReversalRequest
import com.cielo.cielopass.core.constants.CieloConstants.ATTR_ALIGN
import com.cielo.cielopass.core.constants.CieloConstants.ATTR_TEXT_SIZE
import com.cielo.cielopass.core.constants.CieloConstants.ATTR_TYPEFACE
import com.cielo.cielopass.core.constants.CieloConstants.BASE_URI_ORDER
import com.cielo.cielopass.core.constants.CieloConstants.BASE_URI_ORDERS
import com.cielo.cielopass.core.constants.CieloConstants.BASE_URI_PAYMENT
import com.cielo.cielopass.core.constants.CieloConstants.BASE_URI_PRINT
import com.cielo.cielopass.core.constants.CieloConstants.BASE_URI_REVERSAL
import com.cielo.cielopass.core.constants.CieloConstants.OPERATION_PRINT_IMAGE
import com.cielo.cielopass.core.constants.CieloConstants.OPERATION_PRINT_MULTI_COLUMN_TEXT
import com.cielo.cielopass.core.constants.CieloConstants.OPERATION_PRINT_TEXT
import com.cielo.cielopass.core.constants.CieloConstants.URI_ENABLED_PRODUCTS
import com.cielo.cielopass.core.constants.CieloConstants.URI_ESTABLISHMENTS
import com.cielo.cielopass.core.constants.CieloConstants.URI_TERMINAL_INFO
import com.cielo.cielopass.core.constants.CieloConstants.URL_CALLBACK_SUFFIX
import kotlinx.serialization.json.Json

class CieloDeeplinkBuilder(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    },
) {
    fun buildUri(deeplink: CieloDeeplink): Uri = buildUriString(deeplink).toUri()

    fun buildUriString(deeplink: CieloDeeplink): String =
        when (deeplink) {
            is Payment -> buildPaymentUriString(deeplink.request)
            is OrdersList -> buildOrdersListUriString(deeplink.request)
            is EnabledProducts -> URI_ENABLED_PRODUCTS
            is OrderQuery -> buildOrderQueryUriString(deeplink.request)
            is TerminalInfo -> URI_TERMINAL_INFO
            is Establishments -> URI_ESTABLISHMENTS
            is Reversal -> buildReversalUriString(deeplink.request)
            is Print -> buildPrintUriString(deeplink.request)
        }

    fun buildPaymentUri(request: CieloPaymentRequest): Uri = buildPaymentUriString(request).toUri()

    fun buildOrdersListUri(request: CieloOrdersListRequest): Uri = buildOrdersListUriString(request).toUri()

    fun buildEnabledProductsUri(): Uri = URI_ENABLED_PRODUCTS.toUri()

    fun buildOrderQueryUri(request: CieloOrderQueryRequest): Uri = buildOrderQueryUriString(request).toUri()

    fun buildTerminalInfoUri(): Uri = URI_TERMINAL_INFO.toUri()

    fun buildEstablishmentsUri(): Uri = URI_ESTABLISHMENTS.toUri()

    fun buildReversalUri(request: CieloReversalRequest): Uri = buildReversalUriString(request).toUri()

    fun buildPrintUri(request: CieloPrintRequest): Uri = buildPrintUriString(request).toUri()

    fun buildPaymentUriString(request: CieloPaymentRequest): String {
        val dto = CieloPaymentRequestDTO(
            clientId = request.clientId.takeUnless { it.isNullOrBlank() }.orEmpty(),
            accessToken = request.accessToken.takeUnless { it.isNullOrBlank() }.orEmpty(),
            merchantCode = request.merchantCode,
            email = request.email,
            installments = request.installments,
            paymentCode = request.paymentCode?.code,
            value = request.amount.toString(),
            reference = request.reference,
            items = request.items.map {
                CieloItemDTO(
                    name = it.name,
                    quantity = it.quantity,
                    sku = it.sku,
                    unitOfMeasure = it.unitOfMeasure,
                    unitPrice = it.unitPrice,
                )
            },
            subAcquirer = request.subAcquirer?.let { sub ->
                CieloSubAcquirerDTO(
                    softDescriptor = sub.softDescriptor,
                    terminalId = sub.terminalId,
                    merchantCode = sub.merchantCode.orEmpty(),
                    city = sub.city,
                    telephone = sub.telephone,
                    state = sub.state,
                    postalCode = sub.postalCode,
                    address = sub.address,
                    identifier = sub.identifier,
                    merchantCategoryCode = sub.merchantCategoryCode,
                    countryCode = sub.countryCode,
                    informationType = sub.informationType,
                    document = sub.document,
                    businessName = sub.businessName,
                    ibgeCode = sub.ibgeCode,
                )
            },
        )
        val jsonStr = json.encodeToString(dto)
        val base64 = encodeBase64(jsonStr)

        return "${BASE_URI_PAYMENT}$base64${URL_CALLBACK_SUFFIX}"
    }

    fun buildOrdersListUriString(request: CieloOrdersListRequest): String {
        val dto = CieloOrdersListRequestDTO(
            pageSize = request.pageSize.coerceAtMost(5),
            page = request.page,
            clientId = request.clientId,
            accessToken = request.accessToken,
        )
        val jsonStr = json.encodeToString(dto)
        val base64 = encodeBase64(jsonStr)

        return "${BASE_URI_ORDERS}$base64${URL_CALLBACK_SUFFIX}"
    }

    fun buildOrderQueryUriString(request: CieloOrderQueryRequest): String {
        val dto = CieloOrderQueryRequestDTO(
            orderId = request.orderId,
            amount = request.amount,
            authCode = request.authCode,
            cieloCode = request.cieloCode,
            clientId = request.clientId,
            accessToken = request.accessToken,
        )
        val jsonStr = json.encodeToString(dto)
        val base64 = encodeBase64(jsonStr)

        return "${BASE_URI_ORDER}$base64${URL_CALLBACK_SUFFIX}"
    }

    fun buildReversalUriString(request: CieloReversalRequest): String {
        val dto = CieloReversalRequestDTO(
            id = request.orderId,
            clientId = request.clientId,
            accessToken = request.accessToken,
            cieloCode = request.cieloCode,
            authCode = request.authCode,
            value = request.value,
        )
        val jsonStr = json.encodeToString(dto)
        val base64 = encodeBase64(jsonStr)

        return "${BASE_URI_REVERSAL}$base64${URL_CALLBACK_SUFFIX}"
    }

    fun buildPrintUriString(request: CieloPrintRequest): String {
        val dto = when (request) {
            is Text -> CieloPrintRequestDTO(
                operation = OPERATION_PRINT_TEXT,
                styles = listOf(
                    mapOf(
                        ATTR_ALIGN to request.align,
                        ATTR_TEXT_SIZE to request.textSize,
                        ATTR_TYPEFACE to request.typeface,
                    ),
                ),
                value = listOf(request.text),
            )

            is Image -> CieloPrintRequestDTO(
                operation = OPERATION_PRINT_IMAGE,
                styles = listOf(emptyMap()),
                value = listOf(request.imagePath),
            )

            is MultiColumn -> CieloPrintRequestDTO(
                operation = OPERATION_PRINT_MULTI_COLUMN_TEXT,
                styles = request.alignments.mapIndexed { idx, align ->
                    mapOf(
                        ATTR_ALIGN to align,
                        ATTR_TEXT_SIZE to (request.textSizes.getOrNull(idx) ?: 20),
                    )
                },
                value = request.columns,
            )
        }
        val jsonStr = json.encodeToString(dto)
        val base64 = encodeBase64(jsonStr)

        return "${BASE_URI_PRINT}$base64${URL_CALLBACK_SUFFIX}"
    }

    private fun encodeBase64(str: String): String =
        try {
            Base64.encodeToString(str.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        } catch (_: Exception) {
            java.util.Base64
                .getEncoder()
                .encodeToString(str.toByteArray(Charsets.UTF_8))
        }
}
