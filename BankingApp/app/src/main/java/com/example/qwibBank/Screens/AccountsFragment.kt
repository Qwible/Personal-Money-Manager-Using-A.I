package com.example.qwibBank.Screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qwibBank.*
import com.example.qwibBank.Entities.AccessToken
import com.example.qwibBank.TrueLayer.truelayerLink
import com.example.qwibBank.Entities.Account
import com.example.qwibBank.Misc.DividerItemDecorator
import com.example.qwibBank.RecyclerAdaptors.AccountListAdapter
import com.example.qwibBank.InputActivities.NewAccountActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AccountsFragment : Fragment() {

    private val bankingViewModel: BankingViewModel by activityViewModels()
    private lateinit var accessTokens: List<AccessToken>
    private val newAccountActivityRequestCode = 1



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_accounts, container, false)
        val adapter = AccountListAdapter(requireActivity())

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        val emptyView: TextView= view.findViewById(R.id.empty_view)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        val dividerItemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.divider
                )!!
            )
        recyclerView.addItemDecoration(dividerItemDecoration)

        val name = view.findViewById<TextView>(R.id.name)
        name.text = "Accounts"

        bankingViewModel.allAccounts.observe(requireActivity(), Observer { accounts ->
            // Update the cached copy of the accounts in the adapter.
            accounts?.let {
                adapter.setAccounts(it)
                var balance = 0.0
                it.forEach{
                    balance += it.balance
                }
                var totalBalance = view.findViewById<TextView>(R.id.total_balance)

                balance = Math.round(balance * 100.0) / 100.0
                totalBalance.text = "£"+balance.toString()
            }
            if (accounts.isNullOrEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                emptyView.text = "No accounts recorded\nPlease create or link an account"
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        })

        bankingViewModel.allTokens.observe(requireActivity(), Observer { tokens ->
            // Update the cached copy of the accounts in the adapter.
            tokens?.let { accessTokens = it }
        })


        adapter.setOnItemClickListener {
            val intent = Intent(requireActivity(), NewAccountActivity::class.java)
            intent.putExtra("accountId", it.id)
            startActivityForResult(intent, newAccountActivityRequestCode)
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(requireActivity(), NewAccountActivity::class.java)
            startActivityForResult(intent, newAccountActivityRequestCode)
        }

        val fabLink = view.findViewById<FloatingActionButton>(R.id.fab_linked)
        fabLink.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://auth.truelayer-sandbox.com/?response_type=code&client_id=sandbox-qwible-feab6b&scope=info%20accounts%20balance%20cards%20transactions%20direct_debits%20standing_orders%20offline_access&redirect_uri=http://qwibbank.example.com/auth&providers=uk-ob-all%20uk-oauth-all%20uk-cs-mock")
            )
            startActivity(browserIntent)
        }

        val fabSync = view.findViewById<FloatingActionButton>(R.id.fab_sync)
        fabSync.setOnClickListener {
            accessTokens.forEach {
                truelayerLink(
                    "",
                    bankingViewModel,
                    it,
                    requireActivity()
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newAccountActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val account: Account? = data?.getParcelableExtra("account")
            val edit = data?.getBooleanExtra("edit", false)
            val delete = data?.getBooleanExtra("delete", false)
            if (account != null) {
                if (delete == true) {
                    bankingViewModel.deleteAccount(account)
                    Toast.makeText(
                        activity,
                        "Deleted!",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (edit == true) {
                    bankingViewModel.updateAccount(account)
                    Toast.makeText(
                        activity,
                        "Updated!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    bankingViewModel.insertAccount(account)
                    Toast.makeText(
                        activity,
                        "Inserted!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}



