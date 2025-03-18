package com.mandarinkafe.mandarin.edit_meal.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.bundle.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.Cart
import com.mandarinkafe.mandarin.core.ui.MainActivity
import com.mandarinkafe.mandarin.databinding.FragmentEditMealBinding
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.mockPizzaAddsCheeseList
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin

class EditMealBSFragment : BottomSheetDialogFragment() {
    private val gson: Gson by lazy { Gson() }
    private var _binding: FragmentEditMealBinding? = null
    private val binding get() = requireNotNull(_binding) { "Binding wasn't initialized" }
    private val viewModel by viewModel<EditMealViewModel> { parametersOf(meal) }
    private var _meal: Meal? = null
    private val meal get() = requireNotNull(_meal!!) { "meal wasn't initialized" }
    private var mealPrice = 0

    private var addsCategoriesPizza = arrayListOf<String>(
        "МЯСО", "СЫР", "ОВОЩИ", "РЫБА И МОРЕПРОДУКТЫ"
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _meal = gson.fromJson(
            requireArguments().getString(MEAL),
            Meal::class.java
        )

        setupRecyclerView()
        setMealData()
        setTabs(addsCategoriesPizza)

        viewModel.checkIfFavorite()

        viewModel.getIsFavoriteLiveData().observe(viewLifecycleOwner) { isFavorite ->
            toggleFavorite(isFavorite)
        }

    }

    override fun onResume() {
        super.onResume()
        _meal = gson.fromJson(
            requireArguments().getString(MEAL),
            Meal::class.java
        )
    }

    private fun setMealData() {
        mealPrice = meal.price

        binding.apply {
            tvMealTitleTop.text = meal.name
            tvMealIngredients.text = meal.description
            tvMealWeight.apply {
                if (meal.weight == null || meal.weight == 0) isVisible = false
                text = getString(R.string.meal_weight_template, meal.weight)
            }

            tvMealPriceOriginal.text = getString(R.string.meal_price_template, meal.price)

            ibBack.setOnClickListener {
                dismiss()
            }

            fabAddToCartPrice.apply {
                text = getString(
                    R.string.meal_price_template,
                    mealPrice
                )
                setOnClickListener {
                    onCartButtonClick()
                }
            }
            ivAddToFavorite.setOnClickListener {
                viewModel.toggleFavorite()
            }
        }
    }

    private fun onCartButtonClick() {
        Toast.makeText(
            requireContext(),
            "Добавляю в корзину ${meal.name}, $mealPrice ₽",
            Toast.LENGTH_SHORT
        ).show()
        Cart.addItem(meal)
        (requireActivity() as MainActivity).updateCartAdapter()

        findNavController().popBackStack()
    }

    private fun toggleFavorite(isFavorite: Boolean) {
        binding.ivAddToFavorite.apply {
            animate()
                .alpha(0f) // Прозрачность 0
                .setDuration(150)
                .withEndAction { // Меняем изображение, когда оно исчезнет
                    setImageResource(
                        if (isFavorite) R.drawable.ic_favorite_active
                        else R.drawable.ic_favorite_inactive
                    )
                    // Плавно показываем новое изображение
                    animate()
                        .alpha(1f) // Прозрачность 1
                        .setDuration(150)
                        .start()
                }
                .start()
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.rvAdds
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = MealAdditionalsAdapter(
            mockPizzaAddsCheeseList,
            object : MealAdditionalsAdapter.AddsClickListener {

                override fun plusToCartClick(additional: Meal) {
                    //TODO сделать логику корзины для добавок к блюду
                    mealPrice += additional.price
                    binding.fabAddToCartPrice.text =
                        getString(R.string.meal_price_template, mealPrice)

                }

                override fun minusToCartClick(additional: Meal) {
                    //TODO сделать логику корзины для добавок к блюду
                    mealPrice -= additional.price
                    binding.fabAddToCartPrice.text =
                        getString(R.string.meal_price_template, mealPrice)
                }
            })
    }

    private fun setTabs(addsCategories: ArrayList<String>) {
        val tabLayout = binding.tabLayoutAddsCategories

        if (addsCategories.isEmpty()) {
            Log.e("DEBUG", "Список подкатегорий пуст!")
            return
        }
        addsCategories.forEach { addsCategory ->
            tabLayout.addTab(tabLayout.newTab().setText(addsCategory))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val MEAL = "meal"
        private val gson: Gson = getKoin().get()
        fun createArgs(meal: Meal): Bundle =
            bundleOf(MEAL to gson.toJson(meal))
    }
}