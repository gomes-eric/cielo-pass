package com.cielo.cielopass.core.constants

object NotificationConstants {
    const val NOTIFICATION_ID = 1001
    const val CHANNEL_ID = "cielo_payment_channel"
    const val CHANNEL_NAME = "Processando Pagamento Cielo"
    const val CHANNEL_DESCRIPTION = "Mantém o app ativo durante o pagamento na LIO"
    const val NOTIFICATION_TITLE = "Processando Pagamento"
    const val NOTIFICATION_TEXT = "Aguardando confirmação do terminal Cielo..."
    const val ACTION_START_PAYMENT_SERVICE = "com.cielo.cielopass.START_PAYMENT_SERVICE"
    const val ACTION_STOP_PAYMENT_SERVICE = "com.cielo.cielopass.STOP_PAYMENT_SERVICE"
}
