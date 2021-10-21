package com.example.qwibBank.RecyclerAdaptors


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qwibBank.Entities.Category
import com.example.qwibBank.Misc.SVGUtils
import com.example.qwibBank.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class CategoryAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val context = context
    private var categories = emptyList<Category>() // Cached copy of categories
    private var editListener: ((item: Category) -> Unit)? = null
    private var addListener: ((item: Category) -> Unit)? = null
    private var minusListener: ((item: Category) -> Unit)? = null

    fun setOnItemClickListener(listener: (item: Category) -> Unit) {
        this.editListener = listener
    }

    fun setAddClickListener(listener: (item: Category) -> Unit) {
        this.addListener = listener
    }
    fun setMinusClickListener(listener: (item: Category) -> Unit) {
        this.minusListener = listener
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val current = categories[position]
        holder.bind(current)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item_allocate, parent, false)
        return CategoryViewHolder(itemView)
    }


    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var categoryName: TextView = itemView.findViewById(R.id.category_name)
        private var categoryAmount: TextView = itemView.findViewById(R.id.category_amount)
        private var addButton: FloatingActionButton = itemView.findViewById(R.id.add)
        private var minusButton: FloatingActionButton = itemView.findViewById(R.id.minus)
        private var imageView: ImageView = itemView.findViewById(R.id.logo)

        init {
            categoryName.setOnClickListener { editListener?.invoke(categories[adapterPosition]) }
            addButton.setOnClickListener { addListener?.invoke(categories[adapterPosition]) }
            minusButton.setOnClickListener { minusListener?.invoke(categories[adapterPosition]) }
        }

        fun bind(current: Category) {
            val amount = Math.round(current.amount * 100.0) / 100.0
            SVGUtils()
                .fetchImage(context, current.logo, imageView)

            if (current.name == "Uncategorised") {
                categoryName.text =  current.toString()
                categoryAmount.text =  "£"+amount.toString()
                minusButton.visibility = View.GONE
                addButton.visibility = View.GONE
            } else {
                categoryName.text =  current.toString()

                categoryAmount.text =  "£"+amount.toString()+"/"+current.allocated.toString()

                SVGUtils()
                    .fetchImage(context, current.logo, imageView)

                //Change the colour of the text and disable the minus button if the allocated amount is exceeded by the spend.
                if (current.amount > current.allocated){
                    minusButton.alpha = 0.25f
                    minusButton.setEnabled(false)
                    minusButton.setClickable(false)
                    categoryAmount.setTextColor(Color.RED)
                    categoryName.setTextColor(Color.RED)
                } else {
                    minusButton.alpha = 1f
                    minusButton.setEnabled(true)
                    minusButton.setClickable(true)
                    categoryAmount.setTextColor(Color.BLACK)
                    categoryName.setTextColor(Color.BLACK)
                }
            }
            }




    }

    internal fun setCategories(categories: List<Category>) {
        this.categories = categories
        notifyDataSetChanged()
    }


    override fun getItemCount() = categories.size
}