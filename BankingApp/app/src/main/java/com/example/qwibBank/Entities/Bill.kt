package com.example.qwibBank.Entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.qwibBank.TrueLayer.toSimpleString
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "bill_table")
class Bill(@PrimaryKey(autoGenerate = true) val id: Int?,
           val recipient: String,
           val amount: Int,
           val repeat: ArrayList<Date>
) : Parcelable {
    override fun toString(): String {
        return(recipient+"\n£"+amount+"\n"+ toSimpleString(
            repeat
        ))
    }
}