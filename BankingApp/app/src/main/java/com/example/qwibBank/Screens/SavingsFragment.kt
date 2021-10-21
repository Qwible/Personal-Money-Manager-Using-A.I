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
import com.example.qwibBank.Entities.SavingGoal
import com.example.qwibBank.Misc.DividerItemDecorator
import com.example.qwibBank.InputActivities.NewSavingActivity
import com.example.qwibBank.RecyclerAdaptors.SavingListAdapter


import com.google.android.material.floatingactionbutton.FloatingActionButton

class SavingsFragment : Fragment() {

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
        //saving list
        val savingRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        val emptyView: TextView= view.findViewById(R.id.empty_view)

        val savingAdapter = SavingListAdapter(requireActivity())
        savingRecyclerView.adapter = savingAdapter
        savingRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        val dividerItemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.divider
                )!!
            )
        savingRecyclerView.addItemDecoration(dividerItemDecoration)


        val name = view.findViewById<TextView>(R.id.name)
        name.text = "Saving Goals"

        bankingViewModel.allSavings.observe(requireActivity(), Observer { savings ->
            // Update the cached copy of the savings in the adapter.
            savings?.let { savingAdapter.setSavings(it) }
            if (savings.isNullOrEmpty()) {
                savingRecyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                emptyView.text = "Please enter your saving goals"
            } else {
                savingRecyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        })

        savingAdapter.setOnItemClickListener {
            val intent = Intent(requireActivity(), NewSavingActivity::class.java)
            intent.putExtra("saving", it)
            startActivityForResult(intent, 1)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fabSaving = view.findViewById<FloatingActionButton>(R.id.fab)
        fabSaving.setOnClickListener {
            val intent = Intent(requireActivity(), NewSavingActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val saving = data?.getParcelableExtra<SavingGoal>("saving")
            val edit = data?.getBooleanExtra("edit", false)
            val delete = data?.getBooleanExtra("delete", false)

            if (saving != null) {
                if (delete == true) {
                    bankingViewModel.deleteSaving(saving)
                    Toast.makeText(
                        activity,
                        "Deleted!",
                        Toast.LENGTH_LONG
                    ).show()

                } else if (edit == true) {
                    bankingViewModel.updateSaving(saving)
                    Toast.makeText(
                        activity,
                        "Updated!",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    bankingViewModel.insertSaving(saving)
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








