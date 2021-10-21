package com.example.qwibBank.Misc

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class mEvent(val amount: Int, val date: Date) : Parcelable
