package com.mandarinkafe.mandarin.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.databinding.RecommendItemBinding

import com.mandarinkafe.mandarin.menu.domain.models.Meal

class RecommendedAdapter(private val recommendedItems: List<Meal>, private val onItemClicked: (Meal) -> Unit) : RecyclerView.Adapter<RecommendedAdapter.RecommendedViewHolder>() {

    class RecommendedViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = RecommendItemBinding.bind(item)

        fun bind(meal: Meal) = with(binding) {
            recommendNameMealInCard.text = meal.name
            weightRecommendMealInCard.text = meal.weight.toString() + " г"
            priceRecommendMealInCard.text = meal.price.toString() + " ₽"
            Glide.with(itemView.context)
                .load(meal.imageUrl)
                .transform(RoundedCorners(10))
                .into(recommendPictureInCard)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recommend_item, parent, false)
        return RecommendedViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendedViewHolder, position: Int) {
        val meal = recommendedItems[position]
        holder.bind(meal)
        holder.itemView.setOnClickListener { onItemClicked(meal) } // вызываем коллбэк при нажатии
    }

    override fun getItemCount(): Int {
        return recommendedItems.size
    }
}