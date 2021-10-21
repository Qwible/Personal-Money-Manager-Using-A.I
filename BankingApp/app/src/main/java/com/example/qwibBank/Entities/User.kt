package com.example.qwibBank.Entities

import android.gesture.Prediction
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "user_table")
class User(@PrimaryKey(autoGenerate = true) val id: Int?,
           val allowance: Int,
           val period: String
) : Parcelable