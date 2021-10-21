package com.example.qwibBank.InputActivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.qwibBank.Entities.Income
import java.util.*
import android.widget.Toast
import com.example.qwibBank.R
import com.example.qwibBank.TrueLayer.toSimpleString
import com.google.android.material.floatingactionbutton.FloatingActionButton


class NewIncomeActivity : AppCompatActivity() {


    private lateinit var editSourceView: EditText
    private lateinit var editAmountView: EditText
    private lateinit var dateView: EditText
    var selectedDates:ArrayList<Date>? = null
    
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.getSupportActionBar()?.hide()

        var income: Income? = intent.getParcelableExtra("income")

        if (income != null) {
            setContentView(R.layout.activity_edit_income)
            editSourceView = findViewById(R.id.edit_name)
            editSourceView.setText(income.source)
            editAmountView = findViewById(R.id.edit_amount)
            editAmountView.setText(income.amount.toString())
            dateView = findViewById(R.id.date_view)
            selectedDates = income.repeat
            dateView.setText(
                toSimpleString(
                    income.repeat
                )
            )


            val deleteButton = findViewById<FloatingActionButton>(R.id.button_delete)
            deleteButton.setOnClickListener {
                val replyIntent = Intent()
                replyIntent.putExtra("income", income)
                replyIntent.putExtra("delete", true)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }

        } else {
            setContentView(R.layout.activity_new_income)
            editSourceView = findViewById(R.id.edit_name)
            editAmountView = findViewById(R.id.edit_amount)
            dateView = findViewById(R.id.date_view)
        }

        val intent = Intent(this, PickDate::class.java)
        dateView.setOnClickListener(object : View.OnClickListener {
            override  fun onClick(v: View) {
                income?.let {
                    val dateList = selectedDates
                    val newDateString: String =
                        dateList!!.joinToString(separator = ";") { it.time.toString() }
                    intent.putExtra("dates", newDateString)
                }
                intent.putExtra("multi", true)
                startActivityForResult(intent, 1)
            }
        })
        dateView.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                if (hasFocus) {
                    income?.let {
                        val dateList = selectedDates
                        val newDateString: String =
                            dateList!!.joinToString(separator = ";") { it.time.toString() }
                        intent.putExtra("dates", newDateString)
                    }
                    intent.putExtra("multi", true)
                    startActivityForResult(intent, 1)
                }
            }
        })

        val fabBack = findViewById<FloatingActionButton>(R.id.back)
        fabBack.setOnClickListener {
            finish()
        }

        val button = findViewById<FloatingActionButton>(R.id.button_save)
        button.setOnClickListener {


            val amountS = editAmountView.text.toString()
            val  source = editSourceView.text.toString()
            val repeat = selectedDates
            val numeric = amountS.matches("-?\\d+(\\.\\d+)?".toRegex())

            val replyIntent = Intent()
            if (!numeric) {
                Toast.makeText(
                    this,
                    "Please enter amount correctly",
                    Toast.LENGTH_LONG
                ).show()
            }else if (editSourceView.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "No source selected",
                    Toast.LENGTH_LONG
                ).show()
            }else if (repeat == null){
                Toast.makeText(
                    this,
                    "No date selected!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val newIncome: Income
                val amount = amountS.toInt()

                if (income != null) {
                    newIncome =
                        Income(income.id, source, amount, repeat)
                    replyIntent.putExtra("income", newIncome)
                    replyIntent.putExtra("edit", true)
                } else {
                    newIncome = Income(null, source, amount, repeat)
                    replyIntent.putExtra("income", newIncome)
                }

                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val dateString = data?.getStringExtra("dates")
        if (dateString!= null) {
            val dates = ArrayList(dateString?.split(";")?.mapNotNull { Date(it.toLong()) } ?: emptyList())
            selectedDates = dates
            dateView.setText(
                toSimpleString(
                    dates
                )
            )
        }
    }
}


