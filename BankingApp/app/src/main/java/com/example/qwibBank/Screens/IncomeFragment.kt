package com.example.qwibBank.Screens

import android.app.Activity
import android.content.Intent
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
import androidx.viewpager2.widget.ViewPager2
import com.example.qwibBank.*
import com.example.qwibBank.Entities.Income
import com.example.qwibBank.Misc.DividerItemDecorator
import com.example.qwibBank.InputActivities.NewIncomeActivity
import com.example.qwibBank.RecyclerAdaptors.IncomeListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class IncomeFragment : Fragment() {

    private val bankingViewModel: BankingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_constant, container, false)
        parentFragment?.view?.findViewById<ViewPager2>(R.id.pager)?.apply {
            requestLayout()
        }

        //income list
        val incomeRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        val emptyView: TextView= view.findViewById(R.id.empty_view)

        val incomeAdapter = IncomeListAdapter(requireActivity())
        incomeRecyclerView.adapter = incomeAdapter
        incomeRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        val dividerItemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.divider
                )!!
            )
        incomeRecyclerView.addItemDecoration(dividerItemDecoration)
        val name = view.findViewById<TextView>(R.id.name)
        name.text = "Income"

        bankingViewModel.allIncome.observe(requireActivity(), Observer { income ->
            // Update the cached copy of the savings in the adapter.
            income?.let { incomeAdapter.setIncome(it) }
            if (income.isNullOrEmpty()) {
                incomeRecyclerView.visibility = View.INVISIBLE
                emptyView.visibility = View.VISIBLE
                emptyView.text = "Please enter your Income"
            } else {
                incomeRecyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        })

        incomeAdapter.setOnItemClickListener {
            val intent = Intent(requireActivity(), NewIncomeActivity::class.java)
            intent.putExtra("income", it)
            startActivityForResult(intent, 1)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fabIncome = view.findViewById<FloatingActionButton>(R.id.fab)
        fabIncome.setOnClickListener {
            val intent = Intent(requireActivity(), NewIncomeActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val income = data?.getParcelableExtra<Income>("income")
            val edit = data?.getBooleanExtra("edit", false)
            val delete = data?.getBooleanExtra("delete", false)

            if (income != null) {
                if (delete == true) {
                    bankingViewModel.deleteIncome(income)
                    Toast.makeText(
                        activity,
                        "Deleted!",
                        Toast.LENGTH_LONG
                    ).show()

                } else if (edit == true) {
                    bankingViewModel.updateIncome(income)
                    Toast.makeText(
                        activity,
                        "Updated!",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    bankingViewModel.insertIncome(income)
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








