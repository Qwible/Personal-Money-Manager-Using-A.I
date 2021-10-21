package com.example.qwibBank.InputActivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qwibBank.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.timessquare.CalendarPickerView
import java.util.*
import kotlin.collections.ArrayList


class PickDate: AppCompatActivity() {


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.getSupportActionBar()?.hide()

        setContentView(R.layout.select_dates)

        val fabBack = findViewById<FloatingActionButton>(R.id.back)
        fabBack.setOnClickListener {
            finish()
        }


        val calendarView = findViewById<View>(R.id.calendar_view) as CalendarPickerView

        val past = intent.getBooleanExtra("past", false)
        val dateString = intent.getStringExtra("dates")
        val multi = intent.getBooleanExtra("multi", false)
        Log.d("Multi", multi.toString())
        val mode : CalendarPickerView.SelectionMode

        mode = if (multi) {
            CalendarPickerView.SelectionMode.MULTIPLE
        } else {
            CalendarPickerView.SelectionMode.SINGLE
        }

        if (past) {
            //getting past year
            val prevYear = Calendar.getInstance()
            prevYear.add(Calendar.YEAR, -5)

            val today = Calendar.getInstance()

            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DATE, 1)

            calendarView.init(prevYear.time, tomorrow.time)
                .inMode(mode)


            if (dateString != null) {
                val dates = ArrayList(dateString?.split(";")?.mapNotNull { Date(it.toLong()) } ?: emptyList())
                dates?.forEach {
                    calendarView.selectDate(it)
                }
            } else {
                calendarView.selectDate(today.time)
            }

        } else {
            val nextYear = Calendar.getInstance()
            nextYear.add(Calendar.YEAR, 5)

            val today = Calendar.getInstance()

            calendarView.init(today.time, nextYear.time)
                .inMode(mode)

            if (dateString != null) {
                val dates = ArrayList(dateString?.split(";")?.mapNotNull { Date(it.toLong()) } ?: emptyList())
                dates?.forEach {
                    calendarView.selectDate(it)
                }
            }
        }


        //action while clicking on a date
        calendarView.setOnDateSelectedListener(object :
            CalendarPickerView.OnDateSelectedListener {
            override fun onDateSelected(date: Date) {

                Toast.makeText(
                    applicationContext,
                    "Selected Date is : $date",
                    Toast.LENGTH_SHORT
                ).show()

            }

            override fun onDateUnselected(date: Date) {

                Toast.makeText(
                    applicationContext,
                    "UnSelected Date is : $date",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        val save = findViewById<FloatingActionButton>(R.id.button_save)
        save.setOnClickListener {

            val newDates = calendarView.selectedDates
            if (newDates != null) {
                val newDateString: String =
                    newDates?.joinToString(separator = ";") { it.time.toString() } ?: ""
                val replyIntent = Intent()
                replyIntent.putExtra("dates", newDateString)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "No date selected!",
                    Toast.LENGTH_LONG).show()
            }
        }

    }
}