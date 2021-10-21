package com.example.qwibBank.RecyclerAdaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qwibBank.Entities.Bill
import com.example.qwibBank.R


class BillListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<BillListAdapter.BillViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var bills = emptyList<Bill>() // Cached copy of bills
    private var listener: ((item: Bill) -> Unit)? = null

    inner class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val billItemView: TextView = itemView.findViewById(R.id.textView)
        init {
            billItemView.setOnClickListener { listener?.invoke(bills[adapterPosition]) }
        }

        fun bind(current: Bill) {
            billItemView.text =  current.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return BillViewHolder(itemView)
    }

    fun setOnItemClickListener(listener: (item: Bill) -> Unit) {
        this.listener = listener
    }


    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val current = bills[position]
        holder.bind(current)
    }

    internal fun setBills(bills: List<Bill>) {
        this.bills = bills
        notifyDataSetChanged()
    }

    override fun getItemCount() = bills.size
}