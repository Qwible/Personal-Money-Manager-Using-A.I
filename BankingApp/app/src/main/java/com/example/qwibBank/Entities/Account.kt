package com.example.qwibBank.Entities

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize


@Entity(
    tableName = "account_table",
    indices = arrayOf(Index(value = ["name"], unique = true), Index(value = ["obid"], unique = true))
)
@Parcelize
class Account(@PrimaryKey(autoGenerate = true) @ColumnInfo val id: Int?,
              @ColumnInfo(name = "name") val name: String,
              @ColumnInfo(name = "balance") val balance: Double,
              @ColumnInfo(name = "logo") val logo: String,
              @ColumnInfo(name = "obid") val obid: String? = null

) : Parcelable {
    override fun toString(): String {
        return(name)
    }
    fun toString2(): String {
        return(name+"\n£"+balance)
    }

}