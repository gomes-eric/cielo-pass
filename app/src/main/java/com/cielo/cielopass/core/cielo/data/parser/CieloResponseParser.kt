package com.cielo.cielopass.core.cielo.data.parser

import com.cielo.cielopass.core.cielo.data.dto.CieloEstablishmentDTO
import com.cielo.cielopass.core.cielo.data.dto.CieloPaymentResponseDTO
import com.cielo.cielopass.core.cielo.data.dto.CieloTerminalInfoDTO
import com.cielo.cielopass.core.cielo.data.parser.CieloResponseParser.ResponseType.ENABLED_PRODUCTS
import com.cielo.cielopass.core.cielo.data.parser.CieloResponseParser.ResponseType.ESTABLISHMENTS
import com.cielo.cielopass.core.cielo.data.parser.CieloResponseParser.ResponseType.ORDERS_LIST
import com.cielo.cielopass.core.cielo.data.parser.CieloResponseParser.ResponseType.ORDER_QUERY
import com.cielo.cielopass.core.cielo.data.parser.CieloResponseParser.ResponseType.PAYMENT
import com.cielo.cielopass.core.cielo.data.parser.CieloResponseParser.ResponseType.PRINT
import com.cielo.cielopass.core.cielo.data.parser.CieloResponseParser.ResponseType.REVERSAL
import com.cielo.cielopass.core.cielo.data.parser.CieloResponseParser.ResponseType.TERMINAL_INFO
import com.cielo.cielopass.core.cielo.data.parser.CieloResponseParser.ResponseType.UNKNOWN
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.EnabledProducts
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.Establishments
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.OrderQuery
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.OrdersList
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.Payment
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.Print
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.Reversal
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.TerminalError
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.TerminalInfo
import com.cielo.cielopass.core.cielo.domain.model.CieloEstablishment
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Approved
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Cancelled
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Failed
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResultItem
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResultPayment
import com.cielo.cielopass.core.cielo.domain.model.CieloTerminalInfoResult
import com.cielo.cielopass.core.constants.CieloConstants.DELIMITER_AMP
import com.cielo.cielopass.core.constants.CieloConstants.DELIMITER_EQUALS
import com.cielo.cielopass.core.constants.CieloConstants.ENCODING_UTF_8
import com.cielo.cielopass.core.constants.CieloConstants.KEYWORD_CREDITO
import com.cielo.cielopass.core.constants.CieloConstants.KEYWORD_DEBITO
import com.cielo.cielopass.core.constants.CieloConstants.KEYWORD_PIX
import com.cielo.cielopass.core.constants.CieloConstants.KEYWORD_REVERSAL
import com.cielo.cielopass.core.constants.CieloConstants.KEYWORD_REVERSAL_LOWER
import com.cielo.cielopass.core.constants.CieloConstants.KEYWORD_VOUCHER
import com.cielo.cielopass.core.constants.CieloConstants.KEY_BATTERY_LEVEL
import com.cielo.cielopass.core.constants.CieloConstants.KEY_CODE
import com.cielo.cielopass.core.constants.CieloConstants.KEY_DEVICE_MODEL
import com.cielo.cielopass.core.constants.CieloConstants.KEY_LOGIC_NUMBER
import com.cielo.cielopass.core.constants.CieloConstants.KEY_NAME
import com.cielo.cielopass.core.constants.CieloConstants.KEY_OPERATION
import com.cielo.cielopass.core.constants.CieloConstants.KEY_ORDERS
import com.cielo.cielopass.core.constants.CieloConstants.KEY_ORDER_QUERY
import com.cielo.cielopass.core.constants.CieloConstants.KEY_PAGE_SIZE
import com.cielo.cielopass.core.constants.CieloConstants.KEY_PAID_AMOUNT
import com.cielo.cielopass.core.constants.CieloConstants.KEY_PAYMENTS
import com.cielo.cielopass.core.constants.CieloConstants.KEY_PAYMENT_ID
import com.cielo.cielopass.core.constants.CieloConstants.KEY_PRINTER
import com.cielo.cielopass.core.constants.CieloConstants.KEY_QUERY_RESULT
import com.cielo.cielopass.core.constants.CieloConstants.KEY_RESULTS
import com.cielo.cielopass.core.constants.CieloConstants.KEY_STYLES
import com.cielo.cielopass.core.constants.CieloConstants.KEY_TOTAL_ELEMENTS
import com.cielo.cielopass.core.constants.CieloConstants.KEY_TOTAL_PAGES
import com.cielo.cielopass.core.constants.CieloConstants.MSG_ERROR_PARSING_RESPONSE
import com.cielo.cielopass.core.constants.CieloConstants.MSG_PREFIX_TERMINAL_ERROR
import com.cielo.cielopass.core.constants.CieloConstants.MSG_PREFIX_TRANSACTION_ERROR
import com.cielo.cielopass.core.constants.CieloConstants.MSG_USER_CANCELLED_OPERATION
import com.cielo.cielopass.core.constants.CieloConstants.PARAM_ID
import com.cielo.cielopass.core.constants.CieloConstants.PARAM_MESSAGE
import com.cielo.cielopass.core.constants.CieloConstants.PARAM_ORDER_ID
import com.cielo.cielopass.core.constants.CieloConstants.PARAM_REASON
import com.cielo.cielopass.core.constants.CieloConstants.PARAM_REFERENCE
import com.cielo.cielopass.core.constants.CieloConstants.PARAM_RESPONSE
import com.cielo.cielopass.core.constants.CieloConstants.PARAM_RESPONSE_CODE
import com.cielo.cielopass.core.constants.CieloConstants.SCHEME_ORDER
import com.cielo.cielopass.core.constants.CieloConstants.SCHEME_ORDERS
import com.cielo.cielopass.core.constants.CieloConstants.SCHEME_URI_APP_SAMPLE
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.net.URI
import java.net.URLDecoder
import java.util.Base64
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.Unknown as UnknownResponse
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Unknown as UnknownResult

class CieloResponseParser(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    },
) {
    private enum class ResponseType {
        PAYMENT,
        ORDERS_LIST,
        ENABLED_PRODUCTS,
        ORDER_QUERY,
        TERMINAL_INFO,
        ESTABLISHMENTS,
        REVERSAL,
        PRINT,
        UNKNOWN,
    }

    fun parse(uriString: String): CieloDeeplinkResponse {
        return try {
            val cleanUriString = uriString.trim().replace("\r", "").replace("\n", "")
            val uri = URI(cleanUriString)
            val scheme = uri.scheme

            if (scheme != SCHEME_ORDER && scheme != SCHEME_ORDERS && scheme != SCHEME_URI_APP_SAMPLE) {
                return UnknownResponse(uriString)
            }

            val queryParams = parseQueryParams(uri.rawQuery)
            val response = queryParams[PARAM_RESPONSE]
            val responseCode = queryParams[PARAM_RESPONSE_CODE]?.toIntOrNull()

            if (response.isNullOrBlank()) {
                return parseCodeOnlyResponse(queryParams, responseCode, cleanUriString)
            }

            val decodedJson = decodeBase64(response)

            when (determineResponseType(decodedJson)) {
                PAYMENT -> parsePaymentResponse(decodedJson, responseCode)
                ORDERS_LIST -> parseOrdersListResponse(decodedJson)
                ENABLED_PRODUCTS -> parseEnabledProductsResponse(decodedJson)
                ORDER_QUERY -> parseOrderQueryResponse(decodedJson)
                TERMINAL_INFO -> parseTerminalInfoResponse(decodedJson)
                ESTABLISHMENTS -> parseEstablishmentsResponse(decodedJson)
                REVERSAL -> parseReversalResponse(decodedJson, responseCode)
                PRINT -> parsePrintResponse(decodedJson, responseCode)
                UNKNOWN -> UnknownResponse(uriString)
            }
        } catch (e: Exception) {
            Payment(UnknownResult(uriString, e.message ?: MSG_ERROR_PARSING_RESPONSE))
        }
    }

    private fun determineResponseType(decodedJson: String): ResponseType =
        try {
            val element = json.parseToJsonElement(decodedJson)
            val objectKeys = (element as? JsonObject)?.keys ?: emptySet()

            when {
                isPrintResponse(objectKeys) -> PRINT
                isOrdersListResponse(objectKeys) -> ORDERS_LIST
                isEnabledProductsResponse(element) -> ENABLED_PRODUCTS
                isTerminalInfoResponse(objectKeys) -> TERMINAL_INFO
                isEstablishmentsResponse(element) -> ESTABLISHMENTS
                isReversalResponse(element, objectKeys) -> REVERSAL
                isOrderQueryResponse(objectKeys) -> ORDER_QUERY
                isPaymentResponse(objectKeys) -> PAYMENT
                else -> UNKNOWN
            }
        } catch (_: Exception) {
            UNKNOWN
        }

    private fun isPaymentResponse(keys: Set<String>): Boolean =
        keys.contains(PARAM_ORDER_ID) ||
            keys.contains(PARAM_ID) ||
            keys.contains(KEY_CODE) ||
            keys.contains(KEY_PAYMENTS) ||
            keys.contains(KEY_PAYMENT_ID) ||
            keys.contains(KEY_PAID_AMOUNT) ||
            keys.contains(PARAM_REASON) ||
            keys.contains(PARAM_MESSAGE)

    private fun isOrdersListResponse(keys: Set<String>): Boolean =
        keys.contains(KEY_PAGE_SIZE) ||
            keys.contains(KEY_TOTAL_PAGES) ||
            keys.contains(KEY_TOTAL_ELEMENTS) ||
            keys.contains(KEY_ORDERS) ||
            keys.contains(KEY_RESULTS)

    private fun isEnabledProductsResponse(element: JsonElement): Boolean {
        if (element !is JsonArray) return false
        val first = element.firstOrNull() ?: return false
        if (first !is JsonPrimitive) return false
        val content = first.content.uppercase()

        return content.contains(KEYWORD_CREDITO) ||
            content.contains(KEYWORD_DEBITO) ||
            content.contains(KEYWORD_PIX) ||
            content.contains(KEYWORD_VOUCHER)
    }

    private fun isOrderQueryResponse(keys: Set<String>): Boolean =
        keys.contains(KEY_ORDER_QUERY) ||
            keys.contains(KEY_QUERY_RESULT)

    private fun isTerminalInfoResponse(keys: Set<String>): Boolean =
        keys.contains(KEY_BATTERY_LEVEL) ||
            keys.contains(KEY_LOGIC_NUMBER) ||
            keys.contains(KEY_DEVICE_MODEL)

    private fun isEstablishmentsResponse(element: JsonElement): Boolean {
        if (element !is JsonArray) return false
        val first = element.firstOrNull() as? JsonObject ?: return false
        val keys = first.keys

        return keys.contains(KEY_CODE) && keys.contains(KEY_NAME)
    }

    private fun isReversalResponse(
        element: JsonElement,
        keys: Set<String>,
    ): Boolean {
        if (keys.contains(KEYWORD_REVERSAL_LOWER)) return true
        val reason = ((element as? JsonObject)?.get(PARAM_REASON) as? JsonPrimitive)?.content.orEmpty()

        return reason.contains(KEYWORD_REVERSAL, ignoreCase = true)
    }

    private fun isPrintResponse(keys: Set<String>): Boolean =
        keys.contains(KEY_OPERATION) ||
            keys.contains(KEY_STYLES) ||
            keys.contains(KEY_PRINTER)

    private fun parsePaymentResponse(
        decodedJson: String,
        responseCodeParam: Int?,
    ): Payment {
        val dto = json.decodeFromString<CieloPaymentResponseDTO>(decodedJson)
        val finalOrderId = dto.id.orEmpty()
        val finalCode = dto.code ?: responseCodeParam ?: 0
        val result = buildPaymentResult(finalOrderId, finalCode, dto, decodedJson)

        return Payment(result)
    }

    private fun parseOrdersListResponse(decodedJson: String): OrdersList = OrdersList(rawJson = decodedJson, error = null)

    private fun parseEnabledProductsResponse(decodedJson: String): EnabledProducts {
        val products = json.decodeFromString<List<String>>(decodedJson)

        return EnabledProducts(products = products, error = null)
    }

    private fun parseOrderQueryResponse(decodedJson: String): OrderQuery = OrderQuery(rawJson = decodedJson, error = null)

    private fun parseTerminalInfoResponse(decodedJson: String): TerminalInfo {
        val dto = json.decodeFromString<CieloTerminalInfoDTO>(decodedJson)

        if (dto.code != null && dto.code != 0) {
            return TerminalInfo(
                info = null,
                error = dto.reason ?: "$MSG_PREFIX_TERMINAL_ERROR (${dto.code})",
            )
        }

        val result = CieloTerminalInfoResult(
            batteryLevel = dto.batteryLevel ?: 0.0,
            deviceModel = dto.deviceModel,
            imeiNumber = dto.imeiNumber,
            logicNumber = dto.logicNumber,
            merchantCode = dto.merchantCode,
            serialNumber = dto.serialNumber,
        )

        return TerminalInfo(info = result, error = null)
    }

    private fun parseEstablishmentsResponse(decodedJson: String): Establishments {
        val dtos = json.decodeFromString<List<CieloEstablishmentDTO>>(decodedJson)
        val list = dtos.map { CieloEstablishment(code = it.code.orEmpty(), name = it.name.orEmpty()) }

        return Establishments(establishments = list, error = null)
    }

    private fun parseReversalResponse(
        decodedJson: String,
        responseCodeParam: Int?,
    ): Reversal {
        val dto = json.decodeFromString<CieloPaymentResponseDTO>(decodedJson)
        val finalOrderId = dto.id.orEmpty()
        val finalCode = responseCodeParam ?: 0
        val result = buildPaymentResult(finalOrderId, finalCode, dto, decodedJson)

        return Reversal(result)
    }

    private fun parsePrintResponse(
        decodedJson: String,
        responseCodeParam: Int?,
    ): CieloDeeplinkResponse {
        val dto = try {
            json.decodeFromString<CieloPaymentResponseDTO>(decodedJson)
        } catch (_: Exception) {
            null
        }
        val code = responseCodeParam ?: 0
        val isSuccess = (code == 0)
        val message = dto?.reason ?: dto?.message ?: if (isSuccess) null else "$MSG_PREFIX_TERMINAL_ERROR ($code)"

        return if (isSuccess) {
            Print(isSuccess = true, message = message)
        } else {
            TerminalError(code = code, message = message)
        }
    }

    private fun parseCodeOnlyResponse(
        queryParams: Map<String, String>,
        responseCodeParam: Int?,
        rawUri: String,
    ): CieloDeeplinkResponse {
        val orderId = queryParams[PARAM_ORDER_ID] ?: queryParams[PARAM_ID]
        val reference = queryParams[PARAM_REFERENCE]
        val code = responseCodeParam ?: queryParams[KEY_CODE]?.toIntOrNull() ?: 0
        val reason = queryParams[PARAM_REASON] ?: queryParams[PARAM_MESSAGE]

        return if (orderId.isNullOrBlank()) {
            if (code != 0 && code != 1) {
                TerminalError(
                    code = code,
                    message = reason ?: "$MSG_PREFIX_TERMINAL_ERROR ($code)",
                )
            } else {
                buildResultFromCode("", reference, code, reason, rawUri)
            }
        } else {
            buildResultFromCode(orderId, reference, code, reason, rawUri)
        }
    }

    private fun parseQueryParams(rawQuery: String?): Map<String, String> {
        if (rawQuery.isNullOrBlank()) return emptyMap()

        val map = mutableMapOf<String, String>()
        val pairs = rawQuery.split(DELIMITER_AMP)

        for (pair in pairs) {
            val idx = pair.indexOf(DELIMITER_EQUALS)
            if (idx > 0) {
                val key = URLDecoder.decode(pair.substring(0, idx), ENCODING_UTF_8)
                val value = URLDecoder.decode(pair.substring(idx + 1), ENCODING_UTF_8)
                map[key] = value
            }
        }

        return map
    }

    private fun decodeBase64(base64Str: String): String {
        val cleanStr = base64Str
            .trim()
            .replace("\r", "")
            .replace("\n", "")
            .replace(' ', '+')

        return try {
            val decodedBytes = Base64.getUrlDecoder().decode(cleanStr)
            String(decodedBytes, Charsets.UTF_8)
        } catch (_: Exception) {
            try {
                val decodedBytes = Base64.getDecoder().decode(cleanStr)
                String(decodedBytes, Charsets.UTF_8)
            } catch (_: Exception) {
                cleanStr
            }
        }
    }

    private fun buildPaymentResult(
        orderId: String,
        code: Int,
        dto: CieloPaymentResponseDTO,
        rawJson: String,
    ): CieloPaymentResult {
        val items = dto.items
            ?.mapNotNull { item ->
                val id = item.id ?: return@mapNotNull null
                val sku = item.sku.orEmpty()
                CieloPaymentResultItem(id = id, sku = sku)
            }.orEmpty()

        val payments = dto.payments
            ?.mapNotNull { payment ->
                val id = payment.id ?: return@mapNotNull null
                val authCode = payment.authCode.orEmpty()
                val nsu = (payment.cieloCode ?: payment.nsu).orEmpty()

                CieloPaymentResultPayment(id = id, authCode = authCode, nsu = nsu)
            }.orEmpty()

        val paidAmount = dto.paidAmount.takeIf { it != null && it > 0L } ?: dto.payments?.sumOf { it.amount ?: 0L } ?: 0L

        return when (code) {
            0 -> Approved(
                orderId = orderId,
                reference = dto.reference,
                amount = paidAmount,
                items = items,
                payments = payments,
                rawResponse = rawJson,
            )

            1 -> Cancelled(
                code = code,
                reason = dto.reason ?: dto.message ?: MSG_USER_CANCELLED_OPERATION,
            )

            else -> Failed(
                code = code,
                reason = dto.reason ?: dto.message ?: "$MSG_PREFIX_TRANSACTION_ERROR: ($code)",
            )
        }
    }

    private fun buildResultFromCode(
        orderId: String,
        reference: String?,
        code: Int,
        reason: String?,
        rawUri: String,
    ): Payment {
        val result = when (code) {
            0 -> Approved(
                orderId = orderId,
                reference = reference,
                amount = 0L,
                items = emptyList(),
                payments = emptyList(),
                rawResponse = rawUri,
            )

            1 -> Cancelled(
                code = code,
                reason = reason ?: MSG_USER_CANCELLED_OPERATION,
            )

            else -> Failed(
                code = code,
                reason = reason ?: "$MSG_PREFIX_TRANSACTION_ERROR ($code)",
            )
        }

        return Payment(result)
    }
}
