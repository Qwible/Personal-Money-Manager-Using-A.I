package com.example.qwibBank.Entities

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import java.util.*


@Entity(tableName = "transaction_table",  foreignKeys = arrayOf(
    ForeignKey(entity = Account::class,
        parentColumns = arrayOf("name"),
        childColumns = arrayOf("accountName"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
        ) ,
    ForeignKey(entity = Category::class,
        parentColumns = arrayOf("name"),
        childColumns = arrayOf("category"),
        onDelete = ForeignKey.SET_DEFAULT,
        onUpdate = ForeignKey.CASCADE

    )),
    indices = arrayOf(Index(value = ["obid"], unique = true))
)

@Parcelize
class Transaction(@PrimaryKey(autoGenerate = true)val id: Int?,
                  val merchant: String,
                  val amount: Double,
                  val accountName: String,
                  val timestamp: Date,
                  @ColumnInfo(name = "category", defaultValue = "Other") var category: String,
                  @ColumnInfo(name = "obid") val obid: String? = null) : Parcelable, Comparable<Transaction> {

    override fun toString(): String {
        return(merchant+"\n"+"£"+amount)
    }
    override fun compareTo(other:Transaction): Int{
        return (-1)*this.timestamp.compareTo(other.timestamp)
    }
}





