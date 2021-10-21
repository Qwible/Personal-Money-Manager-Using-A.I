package com.example.qwibBank.Screens


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qwibBank.BankingViewModel
import com.example.qwibBank.Entities.Category
import com.example.qwibBank.InputActivities.CategoriseActivity
import com.example.qwibBank.InputActivities.NewCategoryActivity
import com.example.qwibBank.Misc.DividerItemDecorator
import com.example.qwibBank.R
import com.example.qwibBank.RecyclerAdaptors.CategoryAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class BudgetFragment : Fragment() {
    lateinit var context: AppCompatActivity
    private val bankingViewModel: BankingViewModel by activityViewModels()
    private lateinit var adapter: CategoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private var categoriesList: List<Category>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_allocate, container, false)
        val totalSpent = view.findViewById<TextView>(R.id.spent)
        val toAllocate = view.findViewById<TextView>(R.id.budget_to_allocate)
        val spentText = view.findViewById<TextView>(R.id.spent_text)
        val budgetText = view.findViewById<TextView>(R.id.allowance)
        val remainText = view.findViewById<TextView>(R.id.remaining)

        //get user data
        bankingViewModel.user.observe(requireActivity(), Observer { user ->
            // Update the cached copy of the categorys in the adapter.
            user?.let {
                val day: Calendar = Calendar.getInstance()

                if (it.period == "Weekly") {
                    remainText.text  = (7 - day.get(Calendar.DAY_OF_WEEK)).toString() + " days left"
                    spentText.text = "spent this week"
                } else {
                    val res: Int = day.getActualMaximum(Calendar.DATE)
                    remainText.text  = (res - day.get(Calendar.DAY_OF_MONTH)).toString() + " days left"
                    spentText.text = "spent this month"
                }
                budgetText.text = it.allowance.toString()
            }
        })

        //get to allocate amount
        bankingViewModel.toAllocate.observe(requireActivity(), Observer { allocate ->
            // Update the cached copy of the categorys in the adapter.
            allocate?.let {
                toAllocate.text = "£"+it.toString()
            }
        })


        //get all categories and populate adaptor
        recyclerView = view.findViewById(R.id.recyclerview)
        emptyView = view.findViewById(R.id.empty_view)

        adapter = CategoryAdapter(requireActivity())
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

        bankingViewModel.categoriesWithSpend.observe(requireActivity(), Observer { categories ->
            // Update the cached copy of the categorys in the adapter.
            categories?.let {
                adapter.setCategories(it.sorted())
                categoriesList = categories
            }
            if (categoriesList.isNullOrEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        })


        //get all outgoings for this period
        bankingViewModel.periodTransactions.observe(requireActivity(), Observer { transactions ->
            transactions?.let {
                val total = it.first
                totalSpent.text = "£"+total.toString()+"/"
            }
        })


        //edit listener
        adapter.setOnItemClickListener {
            if (it.name == "Uncategorised") {
                val intent = Intent(requireActivity(), CategoriseActivity::class.java)
                intent.putExtra("category", it)
                startActivityForResult(intent, 1)
            } else {
                val intent = Intent(requireActivity(), NewCategoryActivity::class.java)
                intent.putExtra("category", it)
                startActivityForResult(intent, 1)
            }

        }

        //add listener
        adapter.setAddClickListener {
            val left = toAllocate.text.toString().removePrefix("£").toInt()
            if (left-5 <= 0) {
                Toast.makeText(
                    activity,
                    "Nothing left to Allocate",
                    Toast.LENGTH_LONG
                ).show()
                var category = Category(it.id, it.name,it.allocated + left, it.logo)
                bankingViewModel.updateCategory(category)
            } else {
                var category = Category(it.id, it.name,it.allocated + 5, it.logo)
                bankingViewModel.updateCategory(category)
            }
        }

        //minus listener
        adapter.setMinusClickListener {
            if (it.allocated - 5 <= 0) {
                var category = Category(it.id, it.name,0, it.logo)
                bankingViewModel.updateCategory(category)
            } else {
                var category = Category(it.id, it.name,it.allocated - 5, it.logo)
                bankingViewModel.updateCategory(category)
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var noAccounts = false
        bankingViewModel.allAccounts.observe(requireActivity(), Observer { accounts ->
            if (accounts.isNullOrEmpty()) {
                noAccounts = true
            }
        })

        val fabBudget = view.findViewById<FloatingActionButton>(R.id.edit_budget)
        fabBudget.setOnClickListener {
            if (noAccounts) {
                Toast.makeText(
                    activity,
                    "You need to make some accounts before generating a budget!",
                    Toast.LENGTH_LONG).show()
            } else {
                view.findNavController().navigate(R.id.EditBudget)
            }
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(requireActivity(), NewCategoryActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val category = data?.getParcelableExtra<Category>("category")
            val edit = data?.getBooleanExtra("edit", false)
            val delete = data?.getBooleanExtra("delete", false)
            if (category != null) {
                if (delete == true) {
                    bankingViewModel.deleteCategory(category)
                    Toast.makeText(
                        activity,
                        "Deleted!",
                        Toast.LENGTH_LONG
                    ).show()

                } else if (edit == true) {
                    bankingViewModel.updateCategory(category)
                    Toast.makeText(
                        activity,
                        "Updated!",
                        Toast.LENGTH_LONG).show()

                }else {
                    bankingViewModel.insertCategory(category)
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










