package com.cielo.cielopass.core.cielo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CieloPaymentResponseDTO(
    @SerialName("id") val id: String? = null,
    @SerialName("reference") val reference: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("price") val price: Long? = null,
    @SerialName("paidAmount") val paidAmount: Long? = null,
    @SerialName("pendingAmount") val pendingAmount: Long? = null,
    @SerialName("reason") val reason: String? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("items") val items: List<CieloResponseItemDTO>? = null,
    @SerialName("payments") val payments: List<CieloPaymentItemDTO>? = null,
    @SerialName("code") val code: Int? = null,
)

@Serializable
data class CieloResponseItemDTO(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("quantity") val quantity: Int? = null,
    @SerialName("sku") val sku: String? = null,
    @SerialName("unitOfMeasure") val unitOfMeasure: String? = null,
    @SerialName("unitPrice") val unitPrice: Long? = null,
)

@Serializable
data class CieloPaymentItemDTO(
    @SerialName("id") val id: String? = null,
    @SerialName("amount") val amount: Long? = null,
    @SerialName("authCode") val authCode: String? = null,
    @SerialName("brand") val brand: String? = null,
    @SerialName("cieloCode") val cieloCode: String? = null,
    @SerialName("nsu") val nsu: String? = null,
    @SerialName("mask") val mask: String? = null,
    @SerialName("installments") val installments: Int? = null,
    @SerialName("paymentFields") val paymentFields: CieloPaymentFieldsDTO? = null,
)

@Serializable
data class CieloPaymentFieldsDTO(
    @SerialName("statusCode") val statusCode: String? = null,
    @SerialName("productName") val productName: String? = null,
    @SerialName("paymentTransactionId") val paymentTransactionId: String? = null,
)
