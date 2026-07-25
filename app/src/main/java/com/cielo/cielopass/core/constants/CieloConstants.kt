package com.cielo.cielopass.core.constants

object CieloConstants {
    // DeepLink Schemes & Hosts
    const val SCHEME_LIO = "lio"
    const val SCHEME_ORDER = "order"
    const val SCHEME_ORDERS = "orders"
    const val SCHEME_URI_APP_SAMPLE = "uriappsample"

    const val HOST_PAYMENT = "payment"
    const val HOST_ORDERS = "orders"
    const val HOST_ENABLED_PRODUCTS = "enabledproducts"
    const val HOST_ORDER = "order"
    const val HOST_TERMINAL_INFO = "terminalinfo"
    const val HOST_ESTABLISHMENTS = "establishments"
    const val HOST_PAYMENT_REVERSAL = "payment-reversal"
    const val HOST_PRINT = "print"
    const val HOST_RESPONSE = "response"

    // Query & Request Parameter Keys
    const val PARAM_REQUEST = "request"
    const val PARAM_URL_CALLBACK = "urlCallback"
    const val PARAM_RESPONSE = "response"
    const val PARAM_RESPONSE_CODE = "responsecode"
    const val PARAM_ORDER_ID = "order_id"
    const val PARAM_ID = "id"
    const val PARAM_REASON = "reason"
    const val PARAM_MESSAGE = "message"

    // Callback URIs
    const val CALLBACK_ORDER_RESPONSE = "$SCHEME_ORDER://$HOST_RESPONSE"

    // Base URIs
    const val BASE_URI_PAYMENT = "$SCHEME_LIO://$HOST_PAYMENT?$PARAM_REQUEST="
    const val BASE_URI_ORDERS = "$SCHEME_LIO://$HOST_ORDERS?$PARAM_REQUEST="
    const val URI_ENABLED_PRODUCTS = "$SCHEME_LIO://$HOST_ENABLED_PRODUCTS?$PARAM_URL_CALLBACK=$CALLBACK_ORDER_RESPONSE"
    const val BASE_URI_ORDER = "$SCHEME_LIO://$HOST_ORDER?$PARAM_REQUEST="
    const val URI_TERMINAL_INFO = "$SCHEME_LIO://$HOST_TERMINAL_INFO?$PARAM_URL_CALLBACK=$CALLBACK_ORDER_RESPONSE"
    const val URI_ESTABLISHMENTS = "$SCHEME_LIO://$HOST_ESTABLISHMENTS?$PARAM_URL_CALLBACK=$CALLBACK_ORDER_RESPONSE"
    const val BASE_URI_REVERSAL = "$SCHEME_LIO://$HOST_PAYMENT_REVERSAL?$PARAM_REQUEST="
    const val BASE_URI_PRINT = "$SCHEME_LIO://$HOST_PRINT?$PARAM_REQUEST="

    const val URL_CALLBACK_SUFFIX = "&$PARAM_URL_CALLBACK=$CALLBACK_ORDER_RESPONSE"

    // JSON Parser Keys
    const val KEY_BATTERY_LEVEL = "batteryLevel"
    const val KEY_LOGIC_NUMBER = "logicNumber"
    const val KEY_DEVICE_MODEL = "deviceModel"
    const val KEY_CODE = "code"
    const val KEY_NAME = "name"
    const val KEY_OPERATION = "operation"
    const val KEY_STYLES = "styles"
    const val KEY_PRINTER = "printer"
    const val KEY_PAGE_SIZE = "pageSize"
    const val KEY_ORDERS = "orders"
    const val KEY_PAYMENTS = "payments"
    const val KEY_PAYMENT_ID = "paymentId"
    const val KEY_PAID_AMOUNT = "paidAmount"
    const val KEY_RESULTS = "results"
    const val KEY_TOTAL_PAGES = "totalPages"
    const val KEY_TOTAL_ELEMENTS = "totalElements"
    const val KEY_ORDER_QUERY = "orderQuery"
    const val KEY_QUERY_RESULT = "queryResult"

    // Keywords
    const val KEYWORD_CREDITO = "CREDITO"
    const val KEYWORD_DEBITO = "DEBITO"
    const val KEYWORD_PIX = "PIX"
    const val KEYWORD_VOUCHER = "VOUCHER"
    const val KEYWORD_REVERSAL = "REVERSAL"
    const val KEYWORD_REVERSAL_LOWER = "reversal"

    // Payment Codes & Aliases
    const val CODE_DEBIT_IMMEDIATE = "DEBITO_AVISTA"
    const val CODE_CREDIT_IMMEDIATE = "CREDITO_AVISTA"
    const val CODE_CREDIT_INSTALLMENT_MERCHANT = "CREDITO_PARCELADO_LOJA"
    const val CODE_CREDIT_INSTALLMENT_ADMIN = "CREDITO_PARCELADO_ADM"
    const val CODE_PIX = "PIX"
    const val CODE_VOUCHER_FOOD = "VOUCHER_ALIMENTACAO"
    const val CODE_VOUCHER_MEAL = "VOUCHER_REFEICAO"
    const val RAW_ALL = "ALL"

    const val ALIAS_PARCELADO_LOJA = "PARCELADO_LOJA"
    const val ALIAS_PARCELADO_ADM = "PARCELADO_ADM"
    const val CODE_NUM_1 = "1"
    const val CODE_NUM_2 = "2"
    const val CODE_NUM_3 = "3"
    const val CODE_NUM_4 = "4"
    const val CODE_NUM_5 = "5"

    // Print Operations & Attributes
    const val OPERATION_PRINT_TEXT = "PRINT_TEXT"
    const val OPERATION_PRINT_IMAGE = "PRINT_IMAGE"
    const val OPERATION_PRINT_MULTI_COLUMN_TEXT = "PRINT_MULTI_COLUMN_TEXT"

    const val ATTR_ALIGN = "key_attributes_align"
    const val ATTR_TEXT_SIZE = "key_attributes_textsize"
    const val ATTR_TYPEFACE = "key_attributes_typeface"

    // SubAcquirer Defaults
    const val DEFAULT_COUNTRY_CODE = "0076"
    const val DEFAULT_INFORMATION_TYPE = "J"
    const val DEFAULT_IBGE_CODE = "0000000"

    // Transaction Statuses
    const val STATUS_PENDING = "PENDING"
    const val STATUS_APPROVED = "APPROVED"
    const val STATUS_CANCELLED = "CANCELLED"
    const val STATUS_FAILED = "FAILED"
    const val STATUS_UNKNOWN = "UNKNOWN"

    // Messages & Errors
    const val MSG_USER_CANCELLED_OPERATION = "Operação cancelada pelo usuário"
    const val MSG_ERROR_PARSING_RESPONSE = "Error parsing response"
    const val MSG_ERROR_LAUNCHING_PAYMENT = "Erro ao iniciar pagamento"
    const val MSG_PREFIX_TERMINAL_ERROR = "Erro terminal"
    const val MSG_PREFIX_TRANSACTION_ERROR = "Erro na transação"

    // Parsing DelimitERS & Encodings
    const val DELIMITER_AMP = "&"
    const val DELIMITER_EQUALS = "="
    const val ENCODING_UTF_8 = "UTF-8"
}
