package com.mandarinkafe.mandarin.menu.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.RVItem
import com.mandarinkafe.mandarin.databinding.ListMenuHeaderBinding
import com.mandarinkafe.mandarin.databinding.ListMenuItemBinding
import com.mandarinkafe.mandarin.databinding.ListMenuSubHeaderBinding
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.presentation.MenuAdapter.MealClickListener


class MenuAdapter(
    private val clickListener: MealClickListener
) : ListDelegationAdapter<List<RVItem>>() {
    init {
        delegatesManager.apply {
            addDelegate(MenuHeaderDelegate())
            addDelegate(MenuSubHeaderDelegate())
            addDelegate(MealItemDelegate(clickListener))
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setMenuList(menuData: List<RVItem>) {
        items = menuData
        notifyDataSetChanged()
    }

    interface MealClickListener {
        fun onMealClick(meal: Meal)
        fun onFavoriteToggleClick(meal: Meal, position: Int)
        fun onAddToCartClick(meal: Meal)
        fun onEditClick(meal: Meal)
        fun plusToCartClick(meal: Meal)
        fun minusToCartClick(meal: Meal)

    }
}


class MenuHeaderDelegate : AdapterDelegate<List<RVItem>>() {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ListMenuHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuHeaderHolder(binding)
    }

    override fun isForViewType(items: List<RVItem>, position: Int): Boolean {
        return items[position] is MenuRVItem.HeaderItem
    }

    override fun onBindViewHolder(
        items: List<RVItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ) {
        (holder as MenuHeaderHolder).bind(items[position] as MenuRVItem.HeaderItem)
    }

    class MenuHeaderHolder(private val binding: ListMenuHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuRVItem.HeaderItem) {
            binding.tvCategoryTitle.text = item.categoryName
        }
    }
}

class MenuSubHeaderDelegate : AdapterDelegate<List<RVItem>>() {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ListMenuSubHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuSubHeaderHolder(binding)
    }

    override fun isForViewType(items: List<RVItem>, position: Int): Boolean {
        return items[position] is MenuRVItem.SubHeaderItem
    }

    override fun onBindViewHolder(
        items: List<RVItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ) {
        (holder as MenuSubHeaderHolder).bind(items[position] as MenuRVItem.SubHeaderItem)
    }

    class MenuSubHeaderHolder(private val binding: ListMenuSubHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuRVItem.SubHeaderItem) {
            binding.tvCategoryTitle.text = item.categoryName
        }
    }
}

class MealItemDelegate(private val clickListener: MealClickListener) :
    AdapterDelegate<List<RVItem>>() {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding =
            ListMenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealItemHolder(clickListener, binding)
    }

    override fun isForViewType(items: List<RVItem>, position: Int): Boolean {
        return items[position] is MenuRVItem.MealItem
    }

    override fun onBindViewHolder(
        items: List<RVItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ) {
        (holder as MealItemHolder).bind(items[position] as MenuRVItem.MealItem)
    }


    class MealItemHolder(
        private val clickListener: MealClickListener,
        private val binding: ListMenuItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private val cornerRadius =
            itemView.context.resources.getDimensionPixelSize(R.dimen.image_corner_radius_2)

        private var numberInCart = 0
        // TODO временная переменная! брать цифру из логики корзины, а то холдер одну и ту же переменную привязывает к разным товарам


        fun bind(itemRV: MenuRVItem.MealItem) {
            val meal = itemRV.meal
            setMealData(meal)
            setOnClickListeners(meal)
            binding.ivEditMeal.isVisible = meal.isEditable
        }

        private fun setMealData(meal: Meal) = with(binding) {
            tvMealTitle.text = meal.name

            if (meal.description.isNullOrEmpty()) {
                tvMealIngredients.isVisible = false
            } else {
                tvMealIngredients.isVisible = true
                tvMealIngredients.text = meal.description
            }
            if (meal.weight == null || meal.weight == 0) {
                tvMealWeight.isVisible = false
            } else {
                tvMealWeight.isVisible = true
                tvMealWeight.text =
                    itemView.context.getString(R.string.meal_weight_template, meal.weight)
            }
            btAddToCartPrice.text =
                itemView.context.getString(R.string.meal_price_template, meal.price)

            Glide.with(itemView)
                .load(meal.imageUrl)
                .centerCrop()
                .transform(RoundedCorners(cornerRadius))
                .placeholder(R.drawable.logo_orange)
                .into(ivMealPicture)
            tvMealIngredients.requestLayout()

            ivAddToFavorite.setImageResource(getFavoriteDrawable(meal.isFavorite))
        }


        private fun setOnClickListeners(meal: Meal) = with(binding) {
            itemView.setOnClickListener { clickListener.onMealClick(meal) }
            ivAddToFavorite.setOnClickListener {
                clickListener.onFavoriteToggleClick(meal, getBindingAdapterPosition())
                toggleFavorite(meal)
            }
            ivEditMeal.setOnClickListener { clickListener.onEditClick(meal) }
            btAddToCartPrice.setOnClickListener {
                clickListener.onAddToCartClick(meal)
                onPlusClick(meal)
                extraCartButtonsManager(inCart = true)
            }

            btCartMinus.setOnClickListener {
                clickListener.minusToCartClick(meal)
                onMinusClick(meal)
            }
            btCartPlus.setOnClickListener {
                clickListener.plusToCartClick(meal)
                onPlusClick(meal)
            }
        }

        private fun onPlusClick(item: Meal) = with(binding) {
            tvNumberInCart.text =
                itemView.context.getString(
                    R.string.meal_in_cart_count_template,
                    ++numberInCart
                )

            tvTotalPriceInCart.text =
                itemView.context.getString(
                    R.string.meal_price_template,
                    item.price * numberInCart
                )
        }

        private fun onMinusClick(meal: Meal) = with(binding) {

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
            if (numberInCart == 0) {
                extraCartButtonsManager(inCart = false)
            }

        }


        private fun getFavoriteDrawable(inFavorite: Boolean): Int {
            val drawableId =
                if (inFavorite) R.drawable.ic_favorite_active else R.drawable.ic_favorite_inactive
            return drawableId
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private fun toggleFavorite(meal: Meal) {
            val newDrawableId = getFavoriteDrawable(!meal.isFavorite)

            binding.ivAddToFavorite.apply {
                animate()
                    .alpha(0f) // Прозрачность 0
                    .setDuration(150)
                    .withEndAction { // Меняем изображение, когда оно исчезнет
                        setImageResource(newDrawableId)

                        animate()
                            .alpha(1f) // Прозрачность 1
                            .setDuration(150)
                            .start()
                    }
                    .start()
            }
            meal.isFavorite = !meal.isFavorite
        }

        private fun extraCartButtonsManager(inCart: Boolean) {
            when (inCart) {
                true -> binding.apply {
                    btAddToCartPrice.visibility = View.GONE
                    groupExtraCartButtons.visibility = View.VISIBLE
                }

                false -> binding.apply {
                    btAddToCartPrice.visibility = View.VISIBLE
                    groupExtraCartButtons.visibility = View.GONE
                }
            }
        }

    }
}

