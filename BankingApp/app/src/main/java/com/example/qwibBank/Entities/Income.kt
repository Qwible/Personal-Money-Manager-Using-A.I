package com.example.qwibBank.Entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.qwibBank.TrueLayer.toSimpleString
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
@Entity(tableName = "income_table")
class Income(@PrimaryKey(autoGenerate = true) val id: Int?,
             val source: String,
             val amount: Int,
             val repeat: ArrayList<Date>
) : Parcelable {
    override fun toString(): String {
        return(source+"\n£"+amount+"\n"+ toSimpleString(
            repeat
        ))
    }
}