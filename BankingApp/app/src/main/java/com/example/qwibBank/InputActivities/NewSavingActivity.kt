package com.example.qwibBank.InputActivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qwibBank.Entities.SavingGoal
import com.example.qwibBank.TrueLayer.toSimpleString
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import android.view.View


class NewSavingActivity : AppCompatActivity() {


    private lateinit var editNameView: EditText
    private lateinit var editAmountView: EditText
    private lateinit var dateView: EditText
    var selectedDate:Date? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.getSupportActionBar()?.hide()

        var saving: SavingGoal? = intent.getParcelableExtra("saving")
        if (saving != null) {
            setContentView(com.example.qwibBank.R.layout.activity_edit_saving)

            editNameView = findViewById(com.example.qwibBank.R.id.edit_name)
            editNameView.setText(saving.name)
            editAmountView = findViewById(com.example.qwibBank.R.id.edit_amount)
            editAmountView.setText(saving.amount.toString())
            dateView = findViewById(com.example.qwibBank.R.id.date_view)
            selectedDate = saving.date
            val deleteButton = findViewById<FloatingActionButton>(com.example.qwibBank.R.id.button_delete)

            if (saving.id != 1) {
                dateView.setText(
                    toSimpleString(
                        saving.date
                    )
                )

                deleteButton.setOnClickListener {
                    val replyIntent = Intent()
                    replyIntent.putExtra("saving", saving)
                    replyIntent.putExtra("delete", true)
                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                }
            } else {
                dateView.setText("Constant")

                deleteButton.alpha = 0.25f
                deleteButton.setEnabled(false)
                deleteButton.setClickable(false)
                editNameView.setEnabled(false)
                editNameView.setClickable(false)
                dateView.setEnabled(false)
                dateView.setClickable(false)
            }
        } else {
            setContentView(com.example.qwibBank.R.layout.activity_new_saving)
            editNameView = findViewById(com.example.qwibBank.R.id.edit_name)
            editAmountView = findViewById(com.example.qwibBank.R.id.edit_amount)
            dateView = findViewById(com.example.qwibBank.R.id.date_view)
        }




        val intent = Intent(this, PickDate::class.java)
        dateView.setOnClickListener(object : View.OnClickListener {
            override  fun onClick(v: View) {
                saving?.let {
                    val dateList = arrayListOf(selectedDate)
                    val newDateString: String =
                        dateList.joinToString(separator = ";") { it?.time.toString() }
                    intent.putExtra("dates", newDateString)
                }
                startActivityForResult(intent, 1)
            }
        })
        dateView.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                if (hasFocus) {
                    saving?.let {
                        val dateList = arrayListOf(selectedDate)
                        val newDateString: String =
                            dateList.joinToString(separator = ";") { it?.time.toString() }
                        intent.putExtra("dates", newDateString)
                    }
                    startActivityForResult(intent, 1)
                }
            }
        })

        val fabBack = findViewById<FloatingActionButton>(com.example.qwibBank.R.id.back)
        fabBack.setOnClickListener {
            finish()
        }

        val button = findViewById<FloatingActionButton>(com.example.qwibBank.R.id.button_save)
        button.setOnClickListener {

            val amountS = editAmountView.text.toString()
            val name = editNameView.text.toString()
            val date = selectedDate

            val numeric = amountS.matches("-?\\d+(\\.\\d+)?".toRegex())

            val replyIntent = Intent()
            if (!numeric) {
                Toast.makeText(
                    this,
                    "Please enter amount correctly",
                    Toast.LENGTH_LONG
                ).show()
            }else if (editNameView.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "No source selected",
                    Toast.LENGTH_LONG
                ).show()
            }else if (date == null){
                Toast.makeText(
                    this,
                    "No date selected!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val newSavingGoal: SavingGoal

                val amount = amountS.toInt()
                if (saving != null) {
                    newSavingGoal =
                        SavingGoal(saving.id, name, amount, date)
                    replyIntent.putExtra("saving", newSavingGoal)
                    replyIntent.putExtra("edit", true)
                } else {
                    newSavingGoal =
                        SavingGoal(null, name, amount, date)
                    replyIntent.putExtra("saving", newSavingGoal)
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
            val date = dates[0]
            selectedDate = date
            dateView.setText(
                toSimpleString(
                    date
                )
            )
        }
    }
}



