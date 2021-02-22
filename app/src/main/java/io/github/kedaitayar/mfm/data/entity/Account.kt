package io.github.kedaitayar.mfm2.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["accountName"], unique = true)])
data class Account(
    @PrimaryKey(autoGenerate = true)
    var accountId:Long? = null,
    var accountName: String = ""
)
