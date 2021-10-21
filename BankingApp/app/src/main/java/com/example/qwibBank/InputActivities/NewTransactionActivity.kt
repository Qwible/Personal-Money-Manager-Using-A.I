package com.example.qwibBank.InputActivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.example.qwibBank.*
import com.example.qwibBank.TrueLayer.toSimpleString
import com.example.qwibBank.Entities.Account
import com.example.qwibBank.Entities.Category
import com.example.qwibBank.Entities.Transaction
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class NewTransactionActivity : AppCompatActivity() {


    private lateinit var editAmountView: EditText
    private lateinit var editEntityView: EditText
    private lateinit var editCategoryView: Spinner
    private lateinit var editAccountView: Spinner
    private lateinit var dateView:EditText
    var selectedDate:Date? = null
    private lateinit var bankingViewModel: BankingViewModel
    private lateinit var incomeView: TextView
    var income: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.getSupportActionBar()?.hide()

        bankingViewModel = ViewModelProvider(this).get(BankingViewModel::class.java)

        var transaction: Transaction? = intent.getParcelableExtra("transaction")
        if (transaction != null) {
            setContentView(R.layout.activity_edit_transaction)
            editAmountView = findViewById(R.id.edit_amount)
            editAmountView.setText(transaction.amount.toString())
            editEntityView = findViewById(R.id.edit_merchant)
            editEntityView.setText(transaction.merchant)
            editCategoryView = findViewById(R.id.edit_category)
            val accountView: TextView = findViewById(R.id.account)
            accountView.text = transaction.accountName
            dateView = findViewById(R.id.date_view)
            selectedDate = transaction.timestamp
            dateView.setText(
                toSimpleString(
                    transaction.timestamp
                )
            )
            incomeView = findViewById(R.id.income_category)

            if(!editAmountView.text.startsWith("-")){
                editCategoryView.isEnabled = false
                editCategoryView.isClickable = false
                editCategoryView.visibility = View.INVISIBLE
                incomeView.visibility = View.VISIBLE
                income = true
            }
            val deleteButton = findViewById<FloatingActionButton>(R.id.button_delete)
            deleteButton.setOnClickListener {
                val replyIntent = Intent()
                replyIntent.putExtra("transaction", transaction)
                replyIntent.putExtra("delete", true)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }

        } else {
            setContentView(R.layout.activity_new_transaction)
            editAmountView = findViewById(R.id.edit_amount)
            editEntityView = findViewById(R.id.edit_merchant)
            editCategoryView = findViewById(R.id.edit_category)
            editAccountView = findViewById(R.id.spinner_account)
            incomeView = findViewById(R.id.income_category)
            dateView = findViewById(R.id.date_view)

            //populate accounts dropdown
            bankingViewModel.allAccounts.observe(this, Observer { a ->
                // Update the cached copy of the transactions in the adapter.
                a?.let {
                    if (a.isEmpty()) {
                        val replyIntent = Intent()
                        replyIntent.putExtra("error", "You need to make an account first!")
                        setResult(Activity.RESULT_CANCELED, replyIntent)
                        finish()
                    }
                    // Create an ArrayAdapter
                    val adapter = ArrayAdapter<Account>(this, android.R.layout.simple_spinner_item, a)
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // Apply the adapter to the spinner
                    editAccountView.adapter = adapter

                }
            })
        }


        //populate category dropdown
        var categories: List<Category>
        bankingViewModel.allCategories.observe(this, Observer { c ->
            // Update the cached copy of the transactions in the adapter.
            c?.let {
                categories = c
                if (c.isEmpty()) {
                    val replyIntent = Intent()
                    replyIntent.putExtra("error", "You need to make some categories first")
                    setResult(Activity.RESULT_CANCELED, replyIntent)
                    finish()
                }
                // Create an ArrayAdapter
                val adapter = ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, c)
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                editCategoryView.adapter = adapter

                if (transaction != null) {
                    for (i in 0 until categories.size) {
                        if (transaction.category == categories[i].name) {
                            editCategoryView.setSelection(i)
                        }
                     }
                }
            }
        })

        editAmountView.addTextChangedListener {
            if (!editAmountView.text.startsWith("-")) {
                editCategoryView.isEnabled = false
                editCategoryView.isClickable = false
                editCategoryView.visibility = View.INVISIBLE
                incomeView.visibility = View.VISIBLE
                income = true
            } else {
                editCategoryView.isEnabled = true
                editCategoryView.isClickable = true
                editCategoryView.visibility = View.VISIBLE
                incomeView.visibility = View.INVISIBLE
                income = false
            }
        }


        val intent = Intent(this, PickDate::class.java)
        dateView.setOnClickListener(object : View.OnClickListener {
            override  fun onClick(v: View) {
                transaction?.let {
                    val dateList = arrayListOf(selectedDate)
                    val newDateString: String =
                        dateList.joinToString(separator = ";") { it?.time.toString() }
                    intent.putExtra("dates", newDateString)
                }
                intent.putExtra("past", true)
                startActivityForResult(intent, 1)
            }
        })
        dateView.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                if (hasFocus) {
                    transaction?.let {
                        val dateList = arrayListOf(selectedDate)
                        val newDateString: String =
                            dateList.joinToString(separator = ";") { it?.time.toString() }
                        intent.putExtra("dates", newDateString)
                    }
                    intent.putExtra("past", true)
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
            val merchant = editEntityView.text.toString()
            val timestamp = selectedDate
            val numeric = amountS.matches("-?\\d+(\\.\\d+)?".toRegex())
            var category = ""
            category = if (income) {
                "Income"
            } else {
                val categoryObj: Category =
                    editCategoryView.selectedItem as Category
                categoryObj.name
            }

            val replyIntent = Intent()
            if (!numeric) {
                Toast.makeText(
                    this,
                    "Amount should be a number",
                    Toast.LENGTH_LONG
                ).show()
            }else if (editEntityView.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "No merchant selected",
                    Toast.LENGTH_LONG
                ).show()
            }else if (timestamp == null){
                Toast.makeText(
                    this,
                    "No date selected!",
                    Toast.LENGTH_LONG
                ).show()
            } else {

                val amount = amountS.toDouble()

                val newTransaction: Transaction

                if (transaction != null) {
                    newTransaction = Transaction(
                        transaction.id,
                        merchant,
                        amount,
                        transaction.accountName,
                        timestamp,
                        category
                    )
                    replyIntent.putExtra("old_amount", transaction.amount)
                    replyIntent.putExtra("transaction", newTransaction)
                    replyIntent.putExtra("edit", true)
                } else {
                    val account: Account =
                        editAccountView.selectedItem as Account

                    newTransaction = Transaction(
                        null,
                        merchant,
                        amount,
                        account.name,
                        timestamp,
                        category
                    )
                    replyIntent.putExtra("transaction", newTransaction)
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


