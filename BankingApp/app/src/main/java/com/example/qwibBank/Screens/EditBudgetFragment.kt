package com.example.qwibBank.Screens

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.qwibBank.BankingViewModel
import com.example.qwibBank.Entities.*
import com.example.qwibBank.Misc.mEvent
import com.example.qwibBank.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.sign


class GenBudgetFragment : Fragment() {
    private lateinit var collectionAdapter: collectionAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var savingsList: List<SavingGoal>
    private lateinit var billsList: List<Bill>
    private lateinit var incomeList: List<Income>
    private lateinit var accountsList: List<Account>
    private lateinit var eventList: ArrayList<mEvent>
    private lateinit var currentUser: User

    private val bankingViewModel: BankingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //Initialise the tablayout and viewpager
        tabLayout = view.findViewById(R.id.tab_layout)
        collectionAdapter =
            collectionAdapter(this)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = collectionAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Savings"
                1 -> tab.text = "Bills"
                2 -> tab.text = "Income"
                else -> tab.text = "Undefined"
            }
        }.attach()



        val editPeriodView = view.findViewById<Spinner>(R.id.spinner_period)
        //populate category spinner
        val periodList: ArrayList<String> = ArrayList()
        periodList.add("per Week")
        periodList.add("per Month")
        // Create an ArrayAdapter
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, periodList)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        editPeriodView.adapter = adapter


        editPeriodView.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                val selected = editPeriodView.selectedItem as String
                val selectedString = if (selected == "per Month") {
                    "Monthly"
                } else {
                    "Weekly"
                }
                bankingViewModel.updateUser(User(1, currentUser.allowance, selectedString))
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })
        class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }


        bankingViewModel.allSavings.observe(requireActivity(), Observer { savings ->
            // Update the cached copy of the savings in the adapter.
            savings?.let { savingsList = it }
        })
        bankingViewModel.allIncome.observe(requireActivity(), Observer { income ->
            // Update the cached copy of the savings in the adapter.
            income?.let { incomeList = it }
        })
        bankingViewModel.allBills.observe(requireActivity(), Observer { bills ->
            // Update the cached copy of the savings in the adapter.
            bills?.let { billsList = it }
        })
        bankingViewModel.allAccounts.observe(requireActivity(), Observer { accounts ->
            // Update the cached copy of the savings in the adapter.
            accounts?.let { accountsList = it }
        })

        val editAllowance: EditText = requireActivity().findViewById(R.id.budget)
        bankingViewModel.user.observe(requireActivity(), Observer { user ->
            // Update the cached copy of the transactions in the adapter.
            user?.let {
                currentUser = it
                Log.d("CURRENT PERIOD", user.period)
                editAllowance.setText(user.allowance.toString())
                if (it.period == "Weekly") {
                    editPeriodView.setSelection(0)
                } else {
                    editPeriodView.setSelection(1)
                }

            }
        })


        editAllowance.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && editAllowance.text.isNotEmpty()) {
                bankingViewModel.updateUser(
                    User(1, editAllowance.text.toString().toInt(), currentUser.period)
                )
            }
        }

        val fabIncome = view.findViewById<Button>(R.id.budget_button)
        fabIncome.setOnClickListener {

            //budget calculation:

            eventList = arrayListOf()
            var balance = 0.0

            // Combine income, bills + goals into one list
            savingsList.forEach {
                if (it.id == 1) {
                    balance -= it.amount
                } else {
                    var event = mEvent(
                        -it.amount,
                        it.date
                    )
                    eventList.add(event)
                }
            }
            billsList.forEach {
                var amount = it.amount
                it.repeat.forEach {
                    var event =
                        mEvent(-amount, it)
                    eventList.add(event)
                }
            }
            incomeList.forEach {
                var amount = it.amount
                it.repeat.forEach {
                    var event = mEvent(amount, it)
                    eventList.add(event)
                }
            }

            accountsList.forEach {
                balance += it.balance
            }

            //period = n. of weeks/months till the furthest income/bill/goal
            var period = 0
            val current = Date()
            var max = eventList.maxBy { it.date }
            max?.let {
                var timeDiff = max.date.time - current.time
                Log.d("Time diff", timeDiff.toString())

                period = if (currentUser.period == "Weekly") {
                    (timeDiff / TimeUnit.DAYS.toMillis(7)).toInt()
                } else {
                    (timeDiff / TimeUnit.DAYS.toMillis(30)).toInt()
                }
            }
            if (period == 0) {
                period = 1
            }

            // Calulate allowance = (balance+income)-(bill+goals)/period
            var allowance = balance
            eventList.forEach {
                allowance += it.amount
            }

            allowance /= period

            if (eventList.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    "Please insert your income bills and goals to before generating a budget",
                    Toast.LENGTH_LONG
                ).show()
            } else if (allowance.sign == -1.0) {
                Toast.makeText(
                    requireActivity(),
                    "Your income is less than your outgoing! Adjust your goals to generate a budget",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                bankingViewModel.updateUser(User(1, allowance.toInt(), currentUser.period))
            }

        }

        val fabBack = view.findViewById<FloatingActionButton>(R.id.back)
        fabBack.setOnClickListener {
            view.findNavController().navigate(R.id.Budget)
        }
    }
}

class collectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        return when(position) {
            0 -> SavingsFragment()
            1 -> BillsFragment()
            2 -> IncomeFragment()
            else -> SavingsFragment()
        }
    }
}

