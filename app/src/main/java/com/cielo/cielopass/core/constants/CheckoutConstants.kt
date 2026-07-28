package com.cielo.cielopass.core.constants

object CheckoutConstants {
    // Checkout & Validation Messages
    const val MSG_EVENT_NOT_FOUND = "Evento não encontrado"
    const val MSG_INVALID_NAME_ERROR = "Informe o nome completo (nome e sobrenome)"
    const val MSG_INVALID_EMAIL_ERROR = "Informe um e-mail válido"
    const val MSG_INVALID_DOCUMENT_ERROR = "CPF (11 dígitos) ou CNPJ (14 dígitos) inválido"
    const val MSG_INVALID_QUANTITY_PREFIX = "Quantidade inválida (máximo disponível: "
    const val MSG_STARTING_CIELO_PAYMENT = "Iniciando pagamento Cielo..."
    const val MSG_ACTIVE_TRANSACTION_EXISTS = "Já existe uma transação pendente em andamento."
    const val MSG_ACTIVE_TRANSACTION_TOAST = "Já existe uma transação pendente."

    fun formatQuantityError(available: Int): String = "$MSG_INVALID_QUANTITY_PREFIX$available)"
}
