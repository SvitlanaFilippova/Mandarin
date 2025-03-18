package com.mandarinkafe.mandarin.cart

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mandarinkafe.mandarin.databinding.FragmentCartBinding
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.mockPizza35List


class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var cartAdapter: CartAdapter
    private lateinit var recommendedAdapter: RecommendedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupRecommendedRecyclerView()
        displayCartItems()
        binding.buttonOrder.setOnClickListener {
            sendEmail()
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(Cart.meals)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = cartAdapter
    }

    private fun setupRecommendedRecyclerView() {

        recommendedAdapter = RecommendedAdapter(mockPizza35List){ meal ->
            addToCart(meal) // добавляем обработчик нажатия
        }

        binding.recommendedRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recommendedRecyclerView.adapter = recommendedAdapter
    }

    fun displayCartItems() {
        cartAdapter.notifyDataSetChanged()
    }
    private fun addToCart(item: Meal) {
        Cart.addItem(item) // используем метод из Cart для добавления элемента
        cartAdapter.notifyItemInserted(Cart.meals.size - 1) // уведомляем адаптер о добавлении
    }
    private fun sendEmail() {
        // Собираем информацию о товарах
        val cartItems = Cart.meals
        val emailBody = StringBuilder("Уважаемый продавец, \n\nЯ хочу заказать следующие товары:\n\n")

        for (item in cartItems) {
            emailBody.append("${item.name} - ${item.weight} г, Цена: ${item.price} ₽\n")
        }

        emailBody.append("\nС уважением,\nТвой ждун")

        // Создаем Intent для отправки электронной почты
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // только email приложения
            putExtra(Intent.EXTRA_EMAIL, arrayOf("artemenko2014@mail.ru"))
            putExtra(Intent.EXTRA_SUBJECT, "Заказ товаров")
            putExtra(Intent.EXTRA_TEXT, emailBody.toString())
        }

        try {
            startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Нет приложения для отправки почты", Toast.LENGTH_SHORT).show()
        }
    }

}