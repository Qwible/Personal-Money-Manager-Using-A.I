package com.example.qwibBank.Entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "token_table")
class AccessToken(@PrimaryKey var refresh_token: String,
                  var access_token: String,
                  var token_type: String,
                  var expires_in: Int
) : Parcelable {
    override fun toString(): String {
        return (access_token)
    }
}