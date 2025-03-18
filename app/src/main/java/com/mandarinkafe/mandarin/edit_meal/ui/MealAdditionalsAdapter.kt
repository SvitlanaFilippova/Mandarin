package com.mandarinkafe.mandarin.edit_meal.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.databinding.ListAdditionalsItemBinding
import com.mandarinkafe.mandarin.menu.domain.models.Meal

class MealAdditionalsAdapter(
    private val additionals: ArrayList<Meal>,
    private val clickListener: AddsClickListener
) :
    RecyclerView.Adapter<MealAdditionalsAdapter.AdditionalsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdditionalsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_additionals_item, parent, false)
        return AdditionalsViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: AdditionalsViewHolder, position: Int) {
        holder.bind(additionals[position])
    }


    override fun getItemCount(): Int = additionals.size


    class AdditionalsViewHolder(itemView: View, private val clickListener: AddsClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private val binding = ListAdditionalsItemBinding.bind(itemView)
        private var numberInCart =
            0 // TODO временная переменная! брать цифру из логики корзины, а то холдер одну и ту же переменную привязывает к разным товарам


        fun bind(meal: Meal) {
            setOnClickListeners(meal)
            binding.apply {
                tvMealTitle.text =
                    itemView.context.getString(
                        R.string.meal_title_with_weight_template,
                        meal.name,
                        meal.weight
                    )

                tvMealPrice.text =
                    itemView.context.getString(R.string.meal_price_template, meal.price)
            }
        }

        private fun setOnClickListeners(meal: Meal) = with(binding) {

            btCartMinus.setOnClickListener {
                clickListener.minusToCartClick(meal)

                tvNumberInCart.text =
                    itemView.context.getString(
                        R.string.meal_in_cart_count_template,
                        --numberInCart
                    )
                tvTotalPriceInCart.text =
                    itemView.context.getString(
                        R.string.meal_price_template,
                        meal.price * numberInCart
                    )

                if (numberInCart <= 0) {
                    extraCartButtonsManager(inCart = false)
                }
            }
            btCartPlus.setOnClickListener {
                clickListener.plusToCartClick(meal)
                extraCartButtonsManager(inCart = true)
                tvNumberInCart.text =
                    itemView.context.getString(
                        R.string.meal_in_cart_count_template,
                        ++numberInCart
                    )

                tvTotalPriceInCart.text =
                    itemView.context.getString(
                        R.string.meal_price_template,
                        meal.price * numberInCart
                    )
            }
        }

        private fun extraCartButtonsManager(inCart: Boolean) {
            when (inCart) {
                true -> binding.apply {
                    tvMealPrice.visibility = View.GONE
                    groupExtraCartButtons.visibility= View.VISIBLE

                }

                false -> binding.apply {
                    tvMealPrice.visibility = View.VISIBLE
                    groupExtraCartButtons.visibility= View.GONE
                }
            }
        }
    }


    interface AddsClickListener {
        fun plusToCartClick(additional: Meal)
        fun minusToCartClick(additional: Meal)

    }
}
