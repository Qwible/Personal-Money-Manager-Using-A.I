package com.example.qwibBank.InputActivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.qwibBank.BankingViewModel
import com.example.qwibBank.Entities.Account
import com.example.qwibBank.Misc.SVGUtils
import com.example.qwibBank.R
import com.example.qwibBank.Screens.TransactionFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


class NewAccountActivity : AppCompatActivity() {

    private lateinit var bankingViewModel: BankingViewModel
    private lateinit var editNameView: EditText
    private lateinit var editBalanceView: EditText
    private lateinit var editLogo: ImageView
    private var logo:String = R.drawable.ic_create_black_24dp.toString()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.getSupportActionBar()?.hide()

        val accountId = intent.getIntExtra("accountId", -1)


        if (accountId != -1) {
            setContentView(R.layout.activity_edit_account)
            editNameView = findViewById(R.id.edit_name)
            editBalanceView = findViewById(R.id.edit_balance)
            editLogo = findViewById(R.id.logo)

            bankingViewModel = ViewModelProvider(this).get(BankingViewModel::class.java)
            bankingViewModel.fetchAccount(accountId).observe(this, Observer { account ->
                // Update the cached copy of the accounts in the adapter.
                account?.let {
                    logo = it.logo
                    SVGUtils()
                        .fetchImage(this, it.logo, editLogo)

                    editNameView.setText(account.name)
                    editBalanceView.setText(account.balance.toString())

                    val transactions =  supportFragmentManager.findFragmentById(R.id.transactions_container) as TransactionFragment
                    transactions.setAccount(account)

                    val deleteButton = findViewById<FloatingActionButton>(R.id.button_delete)
                    deleteButton.setOnClickListener {
                        val replyIntent = Intent()
                        replyIntent.putExtra("account", account)
                        replyIntent.putExtra("delete", true)
                        setResult(Activity.RESULT_OK, replyIntent)
                        finish()
                    }
                }
            })
        } else {
            setContentView(R.layout.activity_new_account)
            editNameView = findViewById(R.id.edit_name)
            editBalanceView = findViewById(R.id.edit_balance)
            editLogo = findViewById(R.id.logo)
            SVGUtils()
                .fetchImage(this, logo, editLogo)
        }

        editLogo.setOnClickListener {
            val intent = Intent(this, PickIcon::class.java)
            intent.putExtra("type", "account")
            startActivityForResult(intent, 1)
        }

        val fabBack = findViewById<FloatingActionButton>(R.id.back)
        fabBack.setOnClickListener {
            finish()
        }

        val button = findViewById<FloatingActionButton>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            val numeric = editBalanceView.text.matches("-?\\d+(\\.\\d+)?".toRegex())
            if (editNameView.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "Name cannot be empty",
                    Toast.LENGTH_LONG
                ).show()
            }else if (!numeric) {
                Toast.makeText(
                    this,
                    "Please insert balance correctly",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                var newAccount: Account

                if (accountId != -1) {
                    newAccount = Account(accountId, editNameView.text.toString(), editBalanceView.text.toString().toDouble(), logo)
                    replyIntent.putExtra("edit", true)
                } else {
                    newAccount = Account(null, editNameView.text.toString(), editBalanceView.text.toString().toDouble(), logo)
                    replyIntent.putExtra("edit", false)

                }
                replyIntent.putExtra("account", newAccount)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val logoUrl = data?.getStringExtra("icon")
        if (logoUrl != null) {
            logo = logoUrl
            val drawable = this.resources.getDrawable(logo.toInt())
            editLogo.setImageDrawable(drawable)
        }
    }
}

