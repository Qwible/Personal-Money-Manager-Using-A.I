package com.example.qwibBank.Screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qwibBank.*
import com.example.qwibBank.RecyclerAdaptors.TransactionListAdapter
import com.example.qwibBank.InputActivities.NewTransactionActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.fragment.app.activityViewModels
import com.example.qwibBank.Entities.*
import com.example.qwibBank.Entities.Account
import com.example.qwibBank.Misc.ListItem
import com.example.qwibBank.Misc.TransactionItem


class TransactionFragment : Fragment() {

    private val bankingViewModel: BankingViewModel by activityViewModels()
    private val newTransactionActivityRequestCode = 1
    private lateinit var recyclerView: RecyclerView
    private  lateinit var  emptyView: TextView
    private lateinit var adapter:TransactionListAdapter
    private var set = false

    fun setCategory(category:Category) {
        set = true
        category.let {
            bankingViewModel.fetchCategoryTransactions(it.name).observe(requireActivity(), Observer { transactions ->
                // Update the cached copy of the transactions in the adapter.
                transactions?.let {
                    adapter.setTransactions(it.sorted())
                }
                if (transactions.isNullOrEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                    emptyView.text = "No transactions recorded"
                } else {
                    recyclerView.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                }
            })
        }
    }

    fun setAccount(account: Account) {
        set = true
        account.let {
            bankingViewModel.fetchAccountTransactions(it.name).observe(requireActivity(), Observer { transactions ->
                // Update the cached copy of the transactions in the adapter.
                transactions?.let {
                    adapter.setTransactions(it.sorted())
                }
                if (transactions.isNullOrEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                    emptyView.text = "No transactions recorded"
                } else {
                    recyclerView.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                }
            })
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        emptyView = view.findViewById(R.id.empty_view)

        adapter = TransactionListAdapter(requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())


        val name = view.findViewById<TextView>(R.id.name)
        name.text = "Transactions"

        bankingViewModel.allCategories.observe(requireActivity(), Observer { categories ->
            // Update the cached copy of the transactions in the adapter.
            categories?.let {
                adapter.setCategories(it)
            }
        })
        bankingViewModel.allAccounts.observe(requireActivity(), Observer { accounts ->
            // Update the cached copy of the transactions in the adapter.
            accounts?.let {
                adapter.setAccounts(it)
            }
        })

        bankingViewModel.allTransactions.observe(requireActivity(), Observer { transactions ->
            // Update the cached copy of the transactions in the adapter.
            transactions?.let {
                if (!set) {
                    adapter.setTransactions(it.sorted())
                }
            }
            if (transactions.isNullOrEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                emptyView.text = "No transactions recorded"
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        })




        adapter.setOnItemClickListener {
            val intent = Intent(requireActivity(), NewTransactionActivity::class.java)
            if (it.type == ListItem.TYPE_TRANSACTION) {
                val item = it as TransactionItem
                intent.putExtra("transaction", item.transaction)
                startActivityForResult(intent, newTransactionActivityRequestCode)
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(requireActivity(), NewTransactionActivity::class.java)
            startActivityForResult(intent, newTransactionActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newTransactionActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val transaction = data?.getParcelableExtra<Transaction>("transaction")
            val edit = data?.getBooleanExtra("edit", false)
            val delete = data?.getBooleanExtra("delete", false)
            if (transaction != null) {
                if (delete == true) {
                    bankingViewModel.deleteTransaction(transaction)
                    Toast.makeText(
                        activity,
                        "Deleted!",
                        Toast.LENGTH_LONG
                    ).show()

                } else if (edit == true) {
                    val oldAmount = data.getDoubleExtra("old_amount", 0.0)
                    bankingViewModel.updateTransaction(transaction, oldAmount)
                    Toast.makeText(
                        activity,
                        "Updated!",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    bankingViewModel.insertTransaction(transaction)
                    Toast.makeText(
                        activity,
                        "Inserted!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else if(requestCode == newTransactionActivityRequestCode && resultCode == Activity.RESULT_CANCELED) {
            val error = data?.getStringExtra("error")
            error?.let {
                Toast.makeText(
                    activity,
                    it,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}





