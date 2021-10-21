package com.example.qwibBank

import android.net.Uri
import androidx.room.TypeConverter
import java.util.*
import kotlin.collections.ArrayList

class Converters {
    @TypeConverter
    fun toArrayListOfDates(string: String?): ArrayList<Date> {
        return ArrayList(string?.split(";")?.mapNotNull { Date(it.toLong()) } ?: emptyList())
    }

    @TypeConverter
    fun fromArrayListOfDates(list: ArrayList<Date>?): String {
        return list?.joinToString(separator = ";") { it.time.toString() } ?: ""
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
    @TypeConverter
    fun fromString(value: String?): Uri? {
        return if (value == null) null else Uri.parse(value)
    }

    @TypeConverter
    fun toString(uri: Uri?): String? {
        return uri.toString()
    }

}