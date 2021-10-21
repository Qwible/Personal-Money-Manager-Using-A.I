package com.example.qwibBank.InputActivities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qwibBank.*
import com.example.qwibBank.RecyclerAdaptors.TransactionListAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.qwibBank.Entities.*
import com.example.qwibBank.Misc.TransactionItem
import com.google.android.material.floatingactionbutton.FloatingActionButton


class CategoriseActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bankingViewModel: BankingViewModel
    private  lateinit var  emptyView: TextView
    private lateinit var adapter:TransactionListAdapter



    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.getSupportActionBar()?.hide()
        setContentView(R.layout.activity_categorise
        )
        bankingViewModel = ViewModelProvider(this).get(BankingViewModel::class.java)


        recyclerView = findViewById(R.id.recyclerview)
        emptyView = findViewById(R.id.empty_view)

        adapter = TransactionListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        val name = findViewById<TextView>(R.id.name)
        name.text = "Uncategorised Transactions"

        bankingViewModel.allCategories.observe(this, Observer { categories ->
            // Update the cached copy of the transactions in the adapter.
            categories?.let {
                val spendCategory = arrayListOf<Category>()
                it.forEach {
                    if (it.name != "Income") {
                        spendCategory.add(it)
                    }
                }
                adapter.setCategories(spendCategory)
            }
        })
        bankingViewModel.allAccounts.observe(this, Observer { accounts ->
            // Update the cached copy of the transactions in the adapter.
            accounts?.let {
                adapter.setAccounts(it)
            }
        })


        val unCatTransactions = ArrayList<Transaction>()

        bankingViewModel.fetchCategoryTransactions("Uncategorised").observe(this, Observer { transactions ->
            // Update the cached copy of the transactions in the adapter.
            transactions?.let {
                adapter.setTransactions(it)
            }
            if (transactions.isNullOrEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                emptyView.text = "No uncategorised transactions remaining"
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        })

        adapter.setSpinnerListener {
            val transactionItem = it.first as TransactionItem
            val transaction = transactionItem.transaction
            transaction?.category = it.second.name
            if (transaction != null) {
                Log.d("CATEGORISE", it.second.name)
                Toast.makeText(
                    this,
                    "Updated!",
                    Toast.LENGTH_LONG
                ).show()
                bankingViewModel.updateTransaction2(transaction)
            }
        }

        val fabBack = findViewById<FloatingActionButton>(R.id.back)
        fabBack.setOnClickListener {
            finish()
        }

    }
}





