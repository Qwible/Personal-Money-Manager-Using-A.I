package com.example.qwibBank.RecyclerAdaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import com.example.qwibBank.R
import java.io.FileNotFoundException


class ImageAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val context = context
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var listener: ((item: String) -> Unit)? = null
    private var images = emptyList<String>()

    private val accountImages = listOf(
        R.drawable.ic_credit_card_black_24dp.toString(),
        R.drawable.ic_account.toString(),
        R.drawable.ic_home.toString(),
        R.drawable.ic_lamp.toString(),
        R.drawable.ic_attach_money_black_24dp.toString(),
        R.drawable.ic_bank.toString(),
        R.drawable.ic_home.toString(),
        R.drawable.ic_law.toString(),
        R.drawable.ic_money.toString(),
        R.drawable.ic_real_estate.toString(),
        R.drawable.ic_money_bag.toString(),
        R.drawable.ic_vault.toString(),
        R.drawable.ic_wife.toString(),
        R.drawable.ic_wallet.toString(),
        R.drawable.ic_payment.toString(),
        R.drawable.ic_museum.toString()
        )

    private val categoryImages = listOf(
        R.drawable.ic_atm.toString(),
        R.drawable.ic_attach_money_black_24dp.toString(),
        R.drawable.ic_bank.toString(),
        R.drawable.ic_home.toString(),
        R.drawable.ic_law.toString(),
        R.drawable.ic_money.toString(),
        R.drawable.ic_real_estate.toString(),
        R.drawable.ic_util.toString(),
        R.drawable.ic_bill.toString(),
        R.drawable.ic_pizza.toString(),
        R.drawable.ic_food.toString(),
        R.drawable.ic_basket.toString(),
        R.drawable.ic_coffee.toString(),
        R.drawable.ic_gym.toString(),
        R.drawable.ic_climber.toString(),
        R.drawable.ic_football.toString(),
        R.drawable.ic_sport.toString(),
        R.drawable.ic_music.toString(),
        R.drawable.ic_rave.toString(),
        R.drawable.ic_beer.toString(),
        R.drawable.ic_cocktail.toString(),
        R.drawable.ic_casino_roulette.toString(),
        R.drawable.ic_dress.toString(),
        R.drawable.ic_fashion.toString(),
        R.drawable.ic_fire.toString(),
        R.drawable.ic_joystick.toString(),
        R.drawable.ic_entertainment.toString(),
        R.drawable.ic_laptop.toString(),
        R.drawable.ic_mobile_phone.toString(),
        R.drawable.ic_hammock.toString(),
        R.drawable.ic_ticket.toString(),
        R.drawable.ic_car.toString(),
        R.drawable.ic_train.toString(),
        R.drawable.ic_book.toString(),
        R.drawable.ic_calculator.toString(),
        R.drawable.ic_stationary.toString(),
        R.drawable.ic_cosmetics.toString(),
        R.drawable.ic_heart.toString(),
        R.drawable.ic_drug.toString(),
        R.drawable.ic_tooth.toString(),
        R.drawable.ic_charity.toString(),
        R.drawable.ic_gift.toString(),
        R.drawable.ic_paw.toString(),
        R.drawable.ic_cat.toString(),
        R.drawable.ic_lamp.toString()
    )



    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image)
        init {
            imageView.setOnClickListener { listener?.invoke(images[adapterPosition]) }
        }

        fun bind(current: String) {
            try {
                val defaultIcon = context.resources.getDrawable(current.toInt())
                imageView.setImageDrawable(defaultIcon)

            } catch (e: FileNotFoundException) {
                Log.d("FileError", e.toString())
                val drawable = context.resources.getDrawable(R.drawable.ic_credit_card_black_24dp)
                imageView.setImageDrawable(drawable)

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_image, parent, false)
        return ImageViewHolder(itemView)
    }

    fun setOnItemClickListener(listener: (item: String) -> Unit) {
        this.listener = listener
    }


    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val current = images[position]
        holder.bind(current)
    }

    internal fun setImages(type: String) {
        if (type == "account") {
            images = accountImages
        } else {
            images = categoryImages
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = images.size
}