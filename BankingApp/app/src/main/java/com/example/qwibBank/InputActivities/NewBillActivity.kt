package com.example.qwibBank.InputActivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.qwibBank.Entities.Bill
import java.util.*
import android.view.View
import android.widget.Toast
import com.example.qwibBank.R
import com.example.qwibBank.TrueLayer.toSimpleString
import com.google.android.material.floatingactionbutton.FloatingActionButton


class NewBillActivity : AppCompatActivity() {


    private lateinit var editRecipientView: EditText
    private lateinit var editAmountView: EditText
    private lateinit var dateView: EditText
    var selectedDates: ArrayList<Date>? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.getSupportActionBar()?.hide()

        var bill: Bill? = intent.getParcelableExtra("bill")

        if (bill != null) {
            setContentView(R.layout.activity_edit_bill)
            editRecipientView = findViewById(R.id.edit_recipient)
            editRecipientView.setText(bill.recipient)
            editAmountView = findViewById(R.id.edit_amount)
            editAmountView.setText(bill.amount.toString())
            dateView = findViewById(R.id.date_view)
            selectedDates = bill.repeat
            dateView.setText(
                toSimpleString(
                    bill.repeat
                )
            )


            val deleteButton = findViewById<FloatingActionButton>(R.id.button_delete)
            deleteButton.setOnClickListener {
                val replyIntent = Intent()
                replyIntent.putExtra("bill", bill)
                replyIntent.putExtra("delete", true)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }

        } else {
            setContentView(R.layout.activity_new_bill)
            dateView = findViewById(R.id.date_view)
            editRecipientView = findViewById(R.id.edit_recipient)
            editAmountView = findViewById(R.id.edit_amount)
        }

        val intent = Intent(this, PickDate::class.java)
        dateView.setOnClickListener(object : View.OnClickListener {
            override  fun onClick(v: View) {
                bill?.let {
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
                    bill?.let {
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
            val  recipient = editRecipientView.text.toString()
            val repeat = selectedDates

            var numeric = amountS.matches("-?\\d+(\\.\\d+)?".toRegex())

            val replyIntent = Intent()
            if (editRecipientView.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "Recipient name cannot be empty",
                    Toast.LENGTH_LONG
                ).show()
            }else if (!numeric) {
                Toast.makeText(
                    this,
                    "Please insert amount correctly",
                    Toast.LENGTH_LONG
                ).show()
            }else if (repeat == null) {
                Toast.makeText(
                    this,
                    "Select a date!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val newBill: Bill

                val amount = amountS.toInt()

                if (bill != null) {
                    newBill =
                        Bill(bill.id, recipient, amount, repeat)
                    replyIntent.putExtra("bill", newBill)
                    replyIntent.putExtra("edit", true)
                } else {
                    newBill = Bill(null, recipient, amount, repeat)
                    replyIntent.putExtra("bill", newBill)
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


