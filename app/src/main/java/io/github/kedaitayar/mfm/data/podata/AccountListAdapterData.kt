package io.github.kedaitayar.mfm.data.podata


data class AccountListAdapterData(
    var accountId: Long = 0,
    var accountName: String = "",
    var accountIncome: Double,
    var accountExpense: Double,
    var accountTransferIn: Double,
    var accountTransferOut: Double
)
