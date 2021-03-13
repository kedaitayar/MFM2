package io.github.kedaitayar.mfm.ui.transaction

import io.github.kedaitayar.mfm.data.entity.Transaction

interface EditTransactionChild {
    fun onButtonSaveClick(transaction: Transaction)
}