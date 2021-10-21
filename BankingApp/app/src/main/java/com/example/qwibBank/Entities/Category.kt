package com.example.qwibBank.Entities

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "category_table", indices = arrayOf(Index(value = ["name"], unique = true)))
class Category(@PrimaryKey(autoGenerate = true)val id: Int?,
               @ColumnInfo(name = "name")val name: String,
               val allocated: Int,
               @ColumnInfo(name = "logo") val logo: String
) : Parcelable, Comparable<Category> {
    override fun toString(): String {
        return(name)
    }
    override fun compareTo(other: Category) : Int{
        if (other.name == "Uncategorised") {
            return -1
        }
        if (other.name == "Other" && this.name != "Uncategorised") {
            return -1
        } else {
            return 0
        }
    }
    @Ignore var amount: Double = 0.0
}