package com.example.qwibBank

import android.content.Context
import android.util.Log
import com.example.qwibBank.Entities.Transaction
import java.io.File
import java.io.PrintWriter
import kotlin.math.sign

fun exportData(transactions : List<Transaction>, context: Context) {
    var week: ArrayList<dayData> = arrayListOf()
    var data: dayData? = null
    var currentDay = -1
    var finalData: ArrayList<dayData> = arrayListOf()


    transactions.forEach {
        val transaction = it
        if (data == null) {
            data = dayData(
                6 - transaction.timestamp.day,
                -transaction.amount,
                0.0
            )
        }
        if (it.amount.sign == -1.0) {
            if (currentDay == it.timestamp.day) {
                data?.let {
                    it.total += -transaction.amount
                }
            } else {
                data?.let {
                    week.add(it)
                    if (currentDay == 0) {
                        val final_data = it
                        week.forEach {
                            it.final_spend = final_data.total
                            finalData.add(it)
                        }
                        week = arrayListOf()
                        data =
                            dayData(
                                6 - transaction.timestamp.day,
                                -transaction.amount,
                                0.0
                            )
                    } else {
                        data =
                            dayData(
                                6 - transaction.timestamp.day,
                                it.total - transaction.amount,
                                0.0
                            )
                    }

                }
                currentDay = it.timestamp.day
            }
        }
    }
    val path = context.getExternalFilesDir(null)
    val exportDirectory = File(path, "Exports")
    exportDirectory.mkdirs()

    val file = File(exportDirectory, "export.csv")
    file.delete()
    file.createNewFile()
    Log.d("Day", "file1")
    try {
        // response is the data written to file
        Log.d("Day", "file2")

        PrintWriter(file).use { out ->
            Log.d("Day", "file3")
            out.println("it.remaining,it.total,it.final_spend")
            finalData.forEach {
                Log.d("Day", "file4 " + it)
                out.println("${it.remaining},${it.total},${it.final_spend}")
            }
        }

    } catch (e: Exception) {
        Log.d("Day" , "error: "+e.toString())
    }

}




data class dayData(
    val remaining : Int,
    var total : Double,
    var final_spend: Double
)