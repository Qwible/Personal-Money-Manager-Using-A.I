package com.example.qwibBank.Entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.qwibBank.TrueLayer.toSimpleString
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "saving_table")
class SavingGoal(@PrimaryKey(autoGenerate = true) val id: Int?,
                 val name: String,
                 val amount: Int,
                 val date: Date
) : Parcelable {
    override fun toString(): String {
        if (this.name == "Saved") {
            return(""+name+"\n£"+amount+"\n"+ "Constant")
        } else {
            return(""+name+"\n£"+amount+"\n"+ toSimpleString(date))
        }
    }
}