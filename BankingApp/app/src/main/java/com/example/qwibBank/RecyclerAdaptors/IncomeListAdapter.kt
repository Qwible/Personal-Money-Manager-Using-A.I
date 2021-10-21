package com.example.qwibBank.RecyclerAdaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qwibBank.Entities.Income
import com.example.qwibBank.R


class IncomeListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<IncomeListAdapter.IncomeViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var income = emptyList<Income>() // Cached copy of income
    private var listener: ((item: Income) -> Unit)? = null

    inner class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val incomeItemView: TextView = itemView.findViewById(R.id.textView)
        init {
            incomeItemView.setOnClickListener { listener?.invoke(income[adapterPosition]) }
        }

        fun bind(current: Income) {
            incomeItemView.text =  current.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return IncomeViewHolder(itemView)
    }

    fun setOnItemClickListener(listener: (item: Income) -> Unit) {
        this.listener = listener
    }


    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val current = income[position]
        holder.bind(current)
    }

    internal fun setIncome(income: List<Income>) {
        this.income = income
        notifyDataSetChanged()
    }

    override fun getItemCount() = income.size
}