package com.mandarinkafe.mandarin.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.databinding.ItemCartBinding
import com.mandarinkafe.mandarin.menu.domain.models.Meal

class CartAdapter(private val meals: List<Meal>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = ItemCartBinding.bind(item)

        fun bind(meal: Meal) = with(binding) {
            tvMealTitle.text = meal.name
            tvMealIngredients.text = meal.description
            tvMealWeight.text = meal.weight.toString() + " г"
            cartPrice.text = meal.price.toString() + " ₽"
            Glide.with(itemView.context)
                .load(meal.imageUrl)
                .into(ivMealPicture)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(meals[position])
    }

    override fun getItemCount(): Int {
        return meals.size
    }
}