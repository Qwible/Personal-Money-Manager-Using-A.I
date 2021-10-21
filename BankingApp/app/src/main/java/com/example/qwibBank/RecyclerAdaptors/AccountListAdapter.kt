package com.example.qwibBank.RecyclerAdaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qwibBank.Entities.Account
import com.example.qwibBank.Misc.SVGUtils
import com.example.qwibBank.R


class AccountListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<AccountListAdapter.AccountViewHolder>() {

    private val context = context
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var accounts = emptyList<Account>() // Cached copy of accounts
    private var listener: ((item: Account) -> Unit)? = null

    fun setOnItemClickListener(listener: (item: Account) -> Unit) {
        this.listener = listener
    }
    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val current = accounts[position]
        holder.bind(current)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item_account, parent, false)
        return AccountViewHolder(itemView)
    }
    inner class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var accountItemView: TextView = itemView.findViewById(R.id.textView)
        private var imageView: ImageView = itemView.findViewById(R.id.logo)

        init {
            accountItemView.setOnClickListener { listener?.invoke(accounts[adapterPosition]) }
        }

        fun bind(current: Account) {
            SVGUtils()
                .fetchImage(context, current.logo, imageView)
            accountItemView.text =  current.toString2()
        }

    }

    internal fun setAccounts(accounts: List<Account>) {
        this.accounts = accounts
        notifyDataSetChanged()
    }

    override fun getItemCount() = accounts.size
}