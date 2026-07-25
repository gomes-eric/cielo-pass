package com.cielo.cielopass.core.cielo.domain.model

import com.cielo.cielopass.core.constants.CieloConstants.ALIAS_PARCELADO_ADM
import com.cielo.cielopass.core.constants.CieloConstants.ALIAS_PARCELADO_LOJA
import com.cielo.cielopass.core.constants.CieloConstants.CODE_CREDIT_IMMEDIATE
import com.cielo.cielopass.core.constants.CieloConstants.CODE_CREDIT_INSTALLMENT_ADMIN
import com.cielo.cielopass.core.constants.CieloConstants.CODE_CREDIT_INSTALLMENT_MERCHANT
import com.cielo.cielopass.core.constants.CieloConstants.CODE_DEBIT_IMMEDIATE
import com.cielo.cielopass.core.constants.CieloConstants.CODE_NUM_1
import com.cielo.cielopass.core.constants.CieloConstants.CODE_NUM_2
import com.cielo.cielopass.core.constants.CieloConstants.CODE_NUM_3
import com.cielo.cielopass.core.constants.CieloConstants.CODE_NUM_4
import com.cielo.cielopass.core.constants.CieloConstants.CODE_NUM_5
import com.cielo.cielopass.core.constants.CieloConstants.CODE_PIX
import com.cielo.cielopass.core.constants.CieloConstants.CODE_VOUCHER_FOOD
import com.cielo.cielopass.core.constants.CieloConstants.CODE_VOUCHER_MEAL
import com.cielo.cielopass.core.constants.CieloConstants.RAW_ALL

sealed interface CieloPaymentCode {
    val code: String?

    data object DebitImmediate : CieloPaymentCode {
        override val code = CODE_DEBIT_IMMEDIATE
    }

    data object CreditImmediate : CieloPaymentCode {
        override val code = CODE_CREDIT_IMMEDIATE
    }

    data object InstallmentMerchant : CieloPaymentCode {
        override val code = CODE_CREDIT_INSTALLMENT_MERCHANT
    }

    data object InstallmentAdmin : CieloPaymentCode {
        override val code = CODE_CREDIT_INSTALLMENT_ADMIN
    }

    data object Pix : CieloPaymentCode {
        override val code = CODE_PIX
    }

    data object VoucherFood : CieloPaymentCode {
        override val code = CODE_VOUCHER_FOOD
    }

    data object VoucherMeal : CieloPaymentCode {
        override val code = CODE_VOUCHER_MEAL
    }

    data object All : CieloPaymentCode {
        override val code: String? = null
    }

    data class Custom(
        override val code: String,
    ) : CieloPaymentCode

    companion object {
        fun fromString(raw: String?): CieloPaymentCode =
            when (raw?.uppercase()) {
                CODE_DEBIT_IMMEDIATE, CODE_NUM_1 -> DebitImmediate
                CODE_CREDIT_IMMEDIATE, CODE_NUM_2 -> CreditImmediate
                CODE_CREDIT_INSTALLMENT_MERCHANT, ALIAS_PARCELADO_LOJA, CODE_NUM_3 -> InstallmentMerchant
                CODE_CREDIT_INSTALLMENT_ADMIN, ALIAS_PARCELADO_ADM, CODE_NUM_4 -> InstallmentAdmin
                CODE_PIX, CODE_NUM_5 -> Pix
                CODE_VOUCHER_FOOD -> VoucherFood
                CODE_VOUCHER_MEAL -> VoucherMeal
                null, "", RAW_ALL -> All
                else -> Custom(raw)
            }
    }
}
