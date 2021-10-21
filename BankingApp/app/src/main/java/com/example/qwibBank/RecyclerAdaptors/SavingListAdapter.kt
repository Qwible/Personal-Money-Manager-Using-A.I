package com.example.qwibBank.RecyclerAdaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qwibBank.Entities.SavingGoal
import com.example.qwibBank.R


class SavingListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<SavingListAdapter.SavingViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var savings = emptyList<SavingGoal>() // Cached copy of savings
    private var listener: ((item: SavingGoal) -> Unit)? = null

    inner class SavingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val savingItemView: TextView = itemView.findViewById(R.id.textView)
        init {
            savingItemView.setOnClickListener { listener?.invoke(savings[adapterPosition]) }
        }

        fun bind(current: SavingGoal) {
            savingItemView.text =  current.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavingViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return SavingViewHolder(itemView)
    }

    fun setOnItemClickListener(listener: (item: SavingGoal) -> Unit) {
        this.listener = listener
    }


    override fun onBindViewHolder(holder: SavingViewHolder, position: Int) {
        val current = savings[position]
        holder.bind(current)
    }

    internal fun setSavings(savings: List<SavingGoal>) {
        this.savings = savings
        notifyDataSetChanged()
    }

    override fun getItemCount() = savings.size
}