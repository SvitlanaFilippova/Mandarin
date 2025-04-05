package com.mandarinkafe.mandarin.meal_customization.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.Cart
import com.mandarinkafe.mandarin.core.ui.MainActivity
import com.mandarinkafe.mandarin.databinding.FragmentEditMealBinding
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMealBSFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentEditMealBinding? = null
    private val binding get() = requireNotNull(_binding) { "Binding wasn't initialized" }
    private val viewModel: MealCustomizationViewModel by viewModels()
    private val args by navArgs<EditMealBSFragmentArgs>()
    private var meal: Meal? = null
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
        meal = Gson().fromJson(args.meal, Meal::class.java)
        setupRecyclerView()
        setMealData()
        setTabs(addsCategoriesPizza)

        viewModel.checkIfFavorite()

        viewModel.getIsFavoriteLiveData().observe(viewLifecycleOwner) { isFavorite ->
            toggleFavorite(isFavorite)
        }

    }

    private fun setMealData() {
        meal?.let {
            mealPrice = it.price
            binding.apply {
                tvMealTitleTop.text = it.name
                tvMealIngredients.text = it.description
                tvMealWeight.apply {
                    if (it.weight == null || it.weight == 0) isVisible = false
                    text = getString(R.string.meal_weight_template, it.weight)
                }

                tvMealPriceOriginal.text = getString(R.string.meal_price_template, it.price)

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
    }

    private fun onCartButtonClick() {
        Toast.makeText(
            requireContext(),
            "Добавляю в корзину ${meal?.name}, $mealPrice ₽",
            Toast.LENGTH_SHORT
        ).show()
        Cart.addItem(meal!!)
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
//        val recyclerView = binding.rvAdds
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = MealAdditionalsAdapter(
//            mockPizzaAddsCheeseList,
//            object : MealAdditionalsAdapter.AddsClickListener {
//
//                override fun plusToCartClick(additional: Meal) {
//                    //TODO сделать логику корзины для добавок к блюду
//                    mealPrice += additional.price
//                    binding.fabAddToCartPrice.text =
//                        getString(R.string.meal_price_template, mealPrice)
//
//                }
//
//                override fun minusToCartClick(additional: Meal) {
//                    //TODO сделать логику корзины для добавок к блюду
//                    mealPrice -= additional.price
//                    binding.fabAddToCartPrice.text =
//                        getString(R.string.meal_price_template, mealPrice)
//                }
//            })
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
}