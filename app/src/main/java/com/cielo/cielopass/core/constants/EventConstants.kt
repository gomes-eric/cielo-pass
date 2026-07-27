package com.cielo.cielopass.core.constants

object EventConstants {
    // Default fallback values
    const val DEFAULT_EVENT_TITLE = "Evento Sem Título"
    const val DEFAULT_EVENT_DESCRIPTION = "Sem descrição."
    const val DEFAULT_EVENT_DATE = "Data a definir"
    const val DEFAULT_EVENT_VENUE = "Local a definir"

    // Toast & Status Messages
    const val MSG_EVENT_CREATED_SUCCESS = "Evento criado com sucesso!"
    const val MSG_ERROR_CREATE_EVENT = "Erro ao criar evento."
    const val MSG_ERROR_OBSERVE_EVENTS = "Erro ao carregar eventos."
    const val MSG_MOCK_EVENTS_SEEDED = "Eventos demonstrativos injetados!"
    const val MSG_ERROR_SEED_MOCK_EVENTS = "Erro ao gerar eventos demonstrativos."
    const val MSG_EVENTS_CLEARED = "Todos os eventos foram removidos."
    const val MSG_ERROR_CLEAR_EVENTS = "Erro ao limpar eventos."

    const val MSG_ERROR_LOAD_EVENT_DETAILS = "Erro ao carregar detalhes do evento."
    const val MSG_EVENT_NOT_FOUND = "Evento não encontrado."
    const val MSG_TICKETS_SOLD_OUT = "Ingressos esgotados para este evento."
    const val MSG_STARTING_CIELO_PAYMENT = "Iniciando pagamento via Cielo..."
    const val MSG_ACTIVE_TRANSACTION_EXISTS = "Existe uma transação pendente em andamento."
    const val MSG_EVENT_DELETED_SUCCESS = "Evento excluído com sucesso."
    const val MSG_ERROR_DELETE_EVENT = "Erro ao excluir evento."
    const val MSG_EVENT_UPDATED_SUCCESS = "Evento atualizado com sucesso!"
    const val MSG_ERROR_UPDATE_EVENT = "Erro ao atualizar evento."

    // Mock Event 1
    const val MOCK_EVENT_1_TITLE = "Festival Cielo Pass 2026"
    const val MOCK_EVENT_1_DESCRIPTION =
        "O maior festival de música e tecnologia do ano com mais de 20 atrações ao vivo e experiências imersivas."
    const val MOCK_EVENT_1_DATE = "15 Ago 2026 • 18:00"
    const val MOCK_EVENT_1_VENUE = "Allianz Parque - São Paulo, SP"

    // Mock Event 2
    const val MOCK_EVENT_2_TITLE = "Noite de Jazz & Blues"
    const val MOCK_EVENT_2_DESCRIPTION =
        "Sessão exclusiva de jazz contemporâneo e blues com artistas renomados e alta gastronomia."
    const val MOCK_EVENT_2_DATE = "20 Ago 2026 • 20:00"
    const val MOCK_EVENT_2_VENUE = "Teatro Bradesco - São Paulo, SP"

    // Mock Event 3
    const val MOCK_EVENT_3_TITLE = "Tech Conference Cielo 2026"
    const val MOCK_EVENT_3_DESCRIPTION =
        "Conferência para desenvolvedores Android, Kotlin e especialistas em soluções de meios de pagamento."
    const val MOCK_EVENT_3_DATE = "05 Set 2026 • 09:00"
    const val MOCK_EVENT_3_VENUE = "Centro de Convenções Rebouças - SP"

    // Mock Event 4
    const val MOCK_EVENT_4_TITLE = "Stand-up Comedy All-Stars"
    const val MOCK_EVENT_4_DESCRIPTION =
        "Uma noite inteira de risadas com os melhores comediantes do Brasil em um show inesquecível."
    const val MOCK_EVENT_4_DATE = "12 Set 2026 • 21:00"
    const val MOCK_EVENT_4_VENUE = "Espaço Unimed - São Paulo, SP"
}
