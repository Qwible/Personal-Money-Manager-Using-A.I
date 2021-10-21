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
import com.example.qwibBank.Entities.Bill
import com.example.qwibBank.Misc.DividerItemDecorator
import com.example.qwibBank.InputActivities.NewBillActivity
import com.example.qwibBank.RecyclerAdaptors.BillListAdapter


import com.google.android.material.floatingactionbutton.FloatingActionButton

class BillsFragment : Fragment() {

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
        //bill list
        val billRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        val emptyView: TextView= view.findViewById(R.id.empty_view)

        val billAdapter = BillListAdapter(requireActivity())
        billRecyclerView.adapter = billAdapter
        billRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        val dividerItemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.divider
                )!!
            )
        billRecyclerView.addItemDecoration(dividerItemDecoration)

        val name = view.findViewById<TextView>(R.id.name)
        name.text = "Bills"

        bankingViewModel.allBills.observe(requireActivity(), Observer { bills ->
            // Update the cached copy of the bills in the adapter.
            bills?.let { billAdapter.setBills(it) }
            if (bills.isNullOrEmpty()) {
                billRecyclerView.visibility = View.INVISIBLE
                emptyView.visibility = View.VISIBLE
                emptyView.text = "Please enter your Bills"
            } else {
                billRecyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        })

        billAdapter.setOnItemClickListener {
            val intent = Intent(requireActivity(), NewBillActivity::class.java)
            intent.putExtra("bill", it)
            startActivityForResult(intent, 1)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fabBill = view.findViewById<FloatingActionButton>(R.id.fab)
        fabBill.setOnClickListener {
            val intent = Intent(requireActivity(), NewBillActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val bill = data?.getParcelableExtra<Bill>("bill")
            val edit = data?.getBooleanExtra("edit", false)
            val delete = data?.getBooleanExtra("delete", false)

            if (bill != null) {
                if (delete == true) {
                    bankingViewModel.deleteBill(bill)
                    Toast.makeText(
                        activity,
                        "Deleted!",
                        Toast.LENGTH_LONG
                    ).show()

                } else if (edit == true) {
                    bankingViewModel.updateBill(bill)
                    Toast.makeText(
                        activity,
                        "Updated!",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    bankingViewModel.insertBill(bill)
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








