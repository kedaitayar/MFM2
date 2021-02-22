package io.github.kedaitayar.mfm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TransactionType(
    @PrimaryKey(autoGenerate = true)
    var transactionTypeId: Int? = null,
    var transactionTypeName: String = ""
)
