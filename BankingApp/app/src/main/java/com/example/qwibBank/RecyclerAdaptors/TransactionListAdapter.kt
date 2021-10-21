package com.example.qwibBank.RecyclerAdaptors


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.qwibBank.Entities.*
import com.example.qwibBank.Misc.HeaderItem
import com.example.qwibBank.Misc.SVGUtils
import com.example.qwibBank.Misc.ListItem
import com.example.qwibBank.Misc.TransactionItem
import com.example.qwibBank.R
import com.example.qwibBank.TrueLayer.toSimpleString
import com.google.android.material.floatingactionbutton.FloatingActionButton


class TransactionListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<TransactionListAdapter.TransactionViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var mItems: MutableList<ListItem> = ArrayList()// Cached copy of mItems
    private var spinnerlistener: ((item: Pair<ListItem, Category>) -> Unit)? = null
    private var listener: ((item: ListItem) -> Unit)? = null
    private var categories = emptyList<Category>() // Cached copy of categories
    private var accounts = emptyList<Account>() // Cached copy of categories
    private val context = context
    private var uncategorised = false

    override fun getItemViewType(position: Int): Int {
        return mItems[position].type
    }

    fun setOnItemClickListener(listener: (item: ListItem) -> Unit) {
        this.uncategorised = false
        this.listener = listener
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val type = getItemViewType(position)
        if (type == ListItem.TYPE_HEADER) {
            val header: HeaderItem = mItems[position] as HeaderItem
            holder.bind(header)
        } else {
            val transaction: TransactionItem = mItems[position] as TransactionItem
            holder.bind(transaction)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : TransactionViewHolder {
        if (viewType == ListItem.TYPE_HEADER) {
            val itemView = inflater.inflate(R.layout.recyclerview_item_date, parent, false)
            return TransactionViewHolder(itemView)
        } else if (uncategorised) {
            val itemView = inflater.inflate(R.layout.recyclerview_item_uncategorised, parent, false)
            return TransactionViewHolder(itemView)
        } else {
            val itemView = inflater.inflate(R.layout.recyclerview_item_transaction, parent, false)
            return TransactionViewHolder(itemView)
        }
    }



    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var transactionItemView: TextView = itemView.findViewById(R.id.textView)

        fun bind(current: HeaderItem) {
            transactionItemView.text =  current.date
        }

        fun bind(current: TransactionItem) {
            if (uncategorised) {
                transactionItemView.text =  current.transaction.toString()
                val saveButton = itemView.findViewById<FloatingActionButton>(R.id.save)
                val editCategoryView = itemView.findViewById<Spinner>(R.id.spinner_category)
                //populate category spinner
                // Create an ArrayAdapter
                val adapter = ArrayAdapter<Category>(context, android.R.layout.simple_spinner_item, categories)
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                editCategoryView.adapter = adapter
                for (i in 0 until categories.size) {
                    if (current.transaction?.category == categories[i].name) {
                        editCategoryView.setSelection(i)
                    }
                }
                saveButton.setOnClickListener {
                    val selected = editCategoryView.selectedItem as Category
                    spinnerlistener?.invoke(Pair(mItems[adapterPosition], selected))
                }

            } else {
                val accountLogo :ImageView = itemView.findViewById(R.id.account)
                val categoryLogo :ImageView = itemView.findViewById(R.id.category)

                val account: Account? = accounts.find { it.name == current.transaction?.accountName }
                val category: Category? = categories.find { it.name == current.transaction?.category }

                account?.let {
                    SVGUtils()
                        .fetchImage(context, account.logo, accountLogo)}
                category?.let {
                    SVGUtils()
                        .fetchImage(context, category.logo, categoryLogo)}
                transactionItemView.text =  current.transaction.toString()
                transactionItemView.setOnClickListener { listener?.invoke(mItems[adapterPosition])}
            }

        }
    }


    internal fun setTransactions(transactions : List<Transaction>) {
        mItems = ArrayList()
        val mTransactionMap: MutableMap<String, MutableList<Transaction>> = mutableMapOf()
        transactions.forEach {
            val transaction = it
            val date =
                toSimpleString(it.timestamp)
            if (mTransactionMap.containsKey(date)) {
                val list = mTransactionMap.get(date)
                list?.let{
                    list.add(transaction)
                    mTransactionMap.put(date, list)
                }
            } else {
                mTransactionMap.put(date, mutableListOf(it))
            }
        }
        for (date in mTransactionMap.keys) {
            val header = HeaderItem()
            header.date = date
            mItems.add(header)
            for(t in mTransactionMap[date]!!) {
                val item = TransactionItem()
                item.transaction = t
                mItems.add(item)
            }
        }
        notifyDataSetChanged()
    }

    internal fun setAccounts(accounts: List<Account>) {
        this.accounts = accounts
        notifyDataSetChanged()
    }
    internal fun setCategories(categories: List<Category>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    internal fun setSpinnerListener(listener: (item: Pair<ListItem, Category>) -> Unit) {
        this.uncategorised = true
        this.spinnerlistener = listener
        notifyDataSetChanged()
    }

    override fun getItemCount() = mItems.size
}