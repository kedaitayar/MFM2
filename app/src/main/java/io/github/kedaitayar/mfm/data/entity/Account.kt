package io.github.kedaitayar.mfm.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(indices = [Index(value = ["accountName"], unique = true)])
data class Account(
    @PrimaryKey(autoGenerate = true)
    var accountId:Long? = null,
    var accountName: String = ""
): Parcelable
