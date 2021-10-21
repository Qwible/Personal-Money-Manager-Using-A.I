package com.example.qwibBank.Screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.qwibBank.BankingViewModel
import com.example.qwibBank.InputActivities.CategoriseActivity
import com.example.qwibBank.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private val bankingViewModel: BankingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //notification views
        val predictionNotification = view.findViewById<TextView>(R.id.predictionNotification)
        val overBudgetNotification = view.findViewById<TextView>(R.id.overBudgetNotification)
        val createAccountNotification = view.findViewById<TextView>(R.id.createAccountNotification)
        val createBudgetNotification = view.findViewById<TextView>(R.id.createBudgetNotification)
        val overspendNotification = view.findViewById<TextView>(R.id.overspendNotification)
        val catergoriseNotification = view.findViewById<TextView>(R.id.categoriseNotification)

        var noAccounts = false

        //get the total amount of transactions and make a prediction with it.
        bankingViewModel.prediction.observe(requireActivity(), Observer { bool ->
            bool?.let {
                if (it && !noAccounts) {
                    predictionNotification.visibility = View.VISIBLE
                } else {
                    predictionNotification.visibility = View.GONE
                }
            }
        })


        bankingViewModel.uncategorisedTransactions.observe(
            requireActivity(),
            Observer { transactions ->
                transactions.let {
                    if (it.isNullOrEmpty()) {
                        catergoriseNotification.visibility = View.GONE
                    } else {
                        catergoriseNotification.visibility = View.VISIBLE
                        catergoriseNotification.text =
                            "You have " + it.size + " uncategorised transactions, click here to start sorting them"
                        catergoriseNotification.setOnClickListener() {
                            val intent =
                                Intent(requireActivity(), CategoriseActivity::class.java)
                            startActivityForResult(intent, 1)
                        }
                    }
                }
            })

        bankingViewModel.overBudget.observe(requireActivity(), Observer { overspend ->
            overspend.let {
                if (it == "nobudget") {
                    if (!noAccounts) {
                        createBudgetNotification.visibility = View.VISIBLE
                        createBudgetNotification.setOnClickListener() {
                            val navController =
                                requireActivity().findNavController(R.id.nav_host_fragment)
                            val navView: BottomNavigationView =
                                requireActivity().findViewById(R.id.nav_view)
                            navView.getMenu().getItem(2).setChecked(true);
                            navController.navigate(R.id.EditBudget)
                        }
                    }

                    predictionNotification.visibility = View.GONE
                    overBudgetNotification.visibility = View.GONE
                    overspendNotification.visibility = View.GONE

                } else if (it == "true") {
                    createBudgetNotification.visibility = View.GONE
                    predictionNotification.visibility = View.GONE
                    overBudgetNotification.visibility = View.VISIBLE
                } else {
                    createBudgetNotification.visibility = View.GONE
                    overBudgetNotification.visibility = View.GONE
                }
            }
        })

        bankingViewModel.allAccounts.observe(requireActivity(), Observer { accounts ->
            if (accounts.isNullOrEmpty()) {
                noAccounts = true
                createAccountNotification.visibility = View.VISIBLE
                predictionNotification.visibility = View.GONE
                overBudgetNotification.visibility = View.GONE
                createBudgetNotification.visibility = View.GONE
                createAccountNotification.setOnClickListener() {
                    val navController =
                        requireActivity().findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.Accounts)
                }
            } else {
                createAccountNotification.visibility = View.GONE
            }
        })

        bankingViewModel.categoriesWithSpend.observe(requireActivity(), Observer { categories ->
            categories.let {
                val overSpent = ArrayList<String>()
                it.forEach {
                    if (it.amount > it.allocated) {
                        overSpent.add(it.name)
                    }
                }
                if (overSpent.isNullOrEmpty()) {
                    overspendNotification.visibility = View.GONE
                } else if (overSpent.size == 1) {
                    overspendNotification.visibility = View.VISIBLE
                    overspendNotification.text =
                        "Oops, looks like you've overspent on " + overSpent[0] + ", click here and lets sort it out..."
                    overspendNotification.setOnClickListener() {
                        val navController =
                            requireActivity().findNavController(R.id.nav_host_fragment)
                        navController.navigate(R.id.Budget)
                    }
                } else {
                    overspendNotification.visibility = View.VISIBLE
                    overspendNotification.text =
                        "Oops, looks like you've overspent in " + overSpent.size + " categories, click here and lets sort it out..."
                    overspendNotification.setOnClickListener() {
                        val navController =
                            requireActivity().findNavController(R.id.nav_host_fragment)
                        navController.navigate(R.id.Budget)
                    }
                }
            }
        })
    }
}






