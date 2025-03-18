package com.mandarinkafe.mandarin.menu.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.Cart
import com.mandarinkafe.mandarin.core.ui.MainActivity
import com.mandarinkafe.mandarin.core.ui.RVItem
import com.mandarinkafe.mandarin.databinding.FragmentMenuBinding
import com.mandarinkafe.mandarin.edit_meal.ui.EditMealBSFragment
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.domain.models.mockBannersList
import com.mandarinkafe.mandarin.menu.presentation.BannerAdapter
import com.mandarinkafe.mandarin.menu.presentation.MenuAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class MockMenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = requireNotNull(_binding) { "Binding wasn't initialized" }

    private var _bannersAdapter: BannerAdapter? = null
    private val bannersAdapter get() = requireNotNull(_bannersAdapter!!) { "bannersAdapter wasn't initialized" }

    private var _menuAdapter: MenuAdapter? = null
    private val menuAdapter get() = requireNotNull(_menuAdapter!!) { "menuAdapter wasn't initialized" }

    private var menuItems: List<RVItem>? = listOf()


    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val viewModel: MockMenuViewModel by activityViewModel()

    private var isTabSyncing = false
    private var autoScrollJob: Job? = null
    private var userInteractingWithViewPager = false

    private val banners = mockBannersList


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPagerBanners.registerOnPageChangeCallback(pageChangeCallback)
        setupBannersViewPager()
        setPlaceholderCLickListeners()
        setRvAdapter()
        viewModel.getMenu()

        viewModel.getScreenState().observe(viewLifecycleOwner)
        { state -> renderMenuScreen(state) }
    }

    private fun renderMenuScreen(state: MockMenuViewModel.ScreenState) {
        when (state) {
            is MockMenuViewModel.ScreenState.Loading -> {
                setProgressBarVisibility(VisibilityStatus.VISIBLE)
            }

            is MockMenuViewModel.ScreenState.Error -> {
                setPlaceholderVisibility(VisibilityStatus.VISIBLE)
            }

            is MockMenuViewModel.ScreenState.Content -> {
                setMenuVisibility(VisibilityStatus.VISIBLE)

                menuItems = state.menuItems
                if (!menuItems.isNullOrEmpty()) {
                    setTabs(menuItems!!)
                    menuAdapter.setMenuList(menuItems!!)
                    setScrollSync(menuItems!!)
                    addOnTabSelectedListener(menuItems!!)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.tabLayoutCategories.removeAllTabs()
        stopAutoScrollBanners()
        _binding = null
        _bannersAdapter = null
        _menuAdapter = null
    }


    private fun setPlaceholderCLickListeners() {
        binding.placeholder.apply {
            bvPlaceholderRetry.setOnClickListener { viewModel.getMenu() }
            bvPlaceholderCall.setOnClickListener {
            }
        }
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            // Если пользователь начал взаимодействовать с ViewPager2
            when (state) {
                ViewPager2.SCROLL_STATE_DRAGGING -> {
                    userInteractingWithViewPager = true
                    Log.d("DEBUG ViewPager", "SCROLL_STATE_DRAGGING")
                    //TODO добавить сюда дебаунс автопрокрутки (запуск через 5 сек после прекращения взаимодействия)
                }
            }
        }
    }

    private fun setupBannersViewPager() {
        _bannersAdapter = BannerAdapter(banners) { -> onBannerCLick() }
        binding.viewPagerBanners.adapter = bannersAdapter
        val screenWidth = resources.displayMetrics.widthPixels
        val aspectRatio = 2.91f
        val height = (screenWidth / aspectRatio).toInt()
        binding.viewPagerBanners.layoutParams.height = height
        startAutoScrollBanners()
    }

    private fun onBannerCLick() { //тут может быть обработка кликов по баннерам
    }


    private fun startAutoScrollBanners() {
        autoScrollJob = lifecycleScope.launch {
            while (isActive) {
                delay(BANNER_AUTO_SCROLL_DELAY)
                if (!userInteractingWithViewPager) {
                    binding.viewPagerBanners.apply {
                        currentItem = (currentItem + 1)
                    }
                }
            }
        }
    }

    private fun stopAutoScrollBanners() {
        autoScrollJob?.cancel()
    }


    private fun setTabs(menuItems: List<RVItem>) {
        menuItems.forEach { item ->
            if (item is MenuRVItem.HeaderItem) {
                val tab = binding.tabLayoutCategories.newTab().setText(item.categoryName)
                if (!item.tabIcon.isNullOrEmpty()) {
                    lifecycleScope.launch {
                        val drawable = loadIconAsync(item.tabIcon)
                        if (drawable != null) {
                            tab.icon = drawable
                        }
                    }
                }
                binding.tabLayoutCategories.addTab(tab)
                Log.d("DEBUG TABS", "Метод setTabs, добавляю вкладку ${tab.text}")
            }
        }
    }

    private suspend fun loadIconAsync(url: String): Drawable? {
        return withContext(Dispatchers.IO) {
            try {
                Glide.with(binding.tabLayoutCategories.context)
                    .asDrawable()
                    .load(url)
                    .submit()
                    .get()
            } catch (e: Exception) {
                Log.e("DEBUG TABS ERROR", "Ошибка загрузки иконки: $e")
                null
            }
        }
    }

    private fun addOnTabSelectedListener(
        menuItems: List<RVItem>
    ) {
        Log.d(
            "DEBUG",
            "Вызов метода addOnTabSelectedListener для установки OnTabSelectedListener"
        )
        binding.tabLayoutCategories.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    Log.d(
                        "DEBUG",
                        "Вызов onTabSelected для вкладки ${tab.text.toString()}, значение isTabSyncing = $isTabSyncing"
                    )
                    val currentHeaderIndex = menuItems.indexOfFirst {
                        it is MenuRVItem.HeaderItem && it.categoryName == tab.text.toString()
                    }
                    val currentHeader = menuItems[currentHeaderIndex]
                    setSubTabs(currentHeader as MenuRVItem.HeaderItem)

                    if (isTabSyncing) return

                    val headerPosition = menuItems.indexOfFirst {
                        it is MenuRVItem.HeaderItem && it.categoryName == tab.text.toString()

                    }
                    if (headerPosition != -1) {
                        scrollToCategory(headerPosition)
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.d("DEBUG", "Очищаю дочерние вкладки в методе onTabUnselected")
                binding.tabLayoutSubCategories.removeAllTabs()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setScrollSync(menuItems: List<RVItem>) {
        Log.d("DEBUG", "Вызвала метод setScrollSync для установки addOnScrollListener")
        binding.rvMenu.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (isTabSyncing) return // Игнорируем вызовы, если синхронизация уже идёт
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                val firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()

                if (firstVisiblePosition != RecyclerView.NO_POSITION) {
                    for (position in firstVisiblePosition..lastVisiblePosition) {
                        val currentItem = menuItems[position]
                        val categoryName = when (currentItem) {
                            is MenuRVItem.HeaderItem -> currentItem.categoryName
                            is MenuRVItem.MealItem, is MenuRVItem.SubHeaderItem -> {
                                // Найти ближайший HeaderItem сверху
                                findNearestHeader(menuItems, position)
                            }
                            else -> null
                        }

                        if (categoryName != null) {
                            val headerIndex = menuItems.indexOfFirst {
                                it is MenuRVItem.HeaderItem && it.categoryName == categoryName
                            }
                            if (headerIndex != -1 && headerIndex != binding.tabLayoutCategories.selectedTabPosition) {
                                isTabSyncing = true

                                val tab = binding.tabLayoutCategories.getTabAt(headerIndex)
                                tab?.select()
                                isTabSyncing = false
                            }
                            break
                        }
                    }
                }
            }
        })
    }


    private fun findNearestHeader(menuItems: List<RVItem>, position: Int): String? {
        for (i in position downTo 0) {
            val item = menuItems[i]
            if (item is MenuRVItem.HeaderItem) {
                return item.categoryName
            }
        }
        return null
    }


    private fun scrollToCategory(
        position: Int
    ) {
        val layoutManager = binding.rvMenu.layoutManager as LinearLayoutManager
        val offset = resources.getDimensionPixelSize(R.dimen.recycler_view_offset_1)
        layoutManager.scrollToPositionWithOffset(position, offset)
    }


    private fun setSubTabs(category: MenuRVItem.HeaderItem) {
        val tabLayoutSub = binding.tabLayoutSubCategories
        tabLayoutSub.removeAllTabs()
        val subCategories = category.subCategoriesNames

        if (subCategories.isNullOrEmpty()) {
            tabLayoutSub.isVisible = false
            Log.e("DEBUG SUB TABS", "Список подкатегорий пуст или null")
            return
        } else {
            subCategories.forEach { it ->
                tabLayoutSub.addTab(tabLayoutSub.newTab().setText(it))
            }
            tabLayoutSub.isVisible = true

            // Проверяем слушатели, чтобы они не добавлялись повторно
            if (tabLayoutSub.tabCount > 0 && tabLayoutSub.getTabAt(0)?.tag == null) {
                tabLayoutSub.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {}

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                })
            }
        }
    }


    private fun showMealDetails(meal: Meal) {
        findNavController().navigate(
            R.id.action_mockMenuFragment_to_mealDetails,
            EditMealBSFragment.createArgs(meal)
        )
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setRvAdapter(): MenuAdapter {
        _menuAdapter = MenuAdapter(object : MenuAdapter.MealClickListener {
            override fun onMealClick(meal: Meal) {
                if (clickDebounce()) {
                    if (meal.isEditable) showMealDetails(meal)
                }
            }

            override fun onEditClick(meal: Meal) {
                showMealDetails(meal)
            }

            override fun onFavoriteToggleClick(meal: Meal, position: Int) {
                viewModel.toggleFavorite(meal)
            }

            override fun onAddToCartClick(meal: Meal) {
                Cart.addItem(meal)
                (requireActivity() as MainActivity).updateCartAdapter()
            }

            override fun plusToCartClick(meal: Meal) {
                Cart.addItem(meal)
                (requireActivity() as MainActivity).updateCartAdapter()
            }

            override fun minusToCartClick(meal: Meal) {
                //TODO тут нужен метод корзины на минус
            }
        })
        binding.rvMenu.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = menuAdapter
        }
        return menuAdapter
    }


    private fun setProgressBarVisibility(status: VisibilityStatus) {
        binding.apply {
            when (status) {
                VisibilityStatus.VISIBLE -> {
                    progressbar.isVisible = true
                    setPlaceholderVisibility(VisibilityStatus.HIDDEN)
                    setMenuVisibility(VisibilityStatus.HIDDEN)
                }

                VisibilityStatus.HIDDEN -> {
                    progressbar.isVisible = false
                }
            }
        }
    }

    private fun setMenuVisibility(status: VisibilityStatus) {
        when (status) {
            VisibilityStatus.VISIBLE -> {
                setProgressBarVisibility(VisibilityStatus.HIDDEN)
                setPlaceholderVisibility(VisibilityStatus.HIDDEN)
                binding.appbarlayoutContent.isVisible = true
                binding.rvMenu.isVisible = true
            }

            VisibilityStatus.HIDDEN -> {
                binding.appbarlayoutContent.isVisible = false
                binding.rvMenu.isVisible = false
            }
        }

    }

    private fun setPlaceholderVisibility(status: VisibilityStatus) {
        binding.placeholder.apply {
            when (status) {
                VisibilityStatus.VISIBLE -> {
                    ivPlaceholderImage.isVisible = true
                    tvPlaceholderMessage.apply {
                        isVisible = true
                        text = getString(
                            R.string.loading_error_template,
                            getString(R.string.cafe_phone_number)
                        )
                    }

                    bvPlaceholderCall.isVisible = true
                    bvPlaceholderRetry.isVisible = true
                    setProgressBarVisibility(VisibilityStatus.HIDDEN)
                    setMenuVisibility(VisibilityStatus.HIDDEN)
                    bvPlaceholderMockMenu.isVisible = true
                }

                VisibilityStatus.HIDDEN -> {
                    ivPlaceholderImage.isVisible = false
                    tvPlaceholderMessage.isVisible = false
                    bvPlaceholderCall.isVisible = false
                    bvPlaceholderRetry.isVisible = false
                    bvPlaceholderMockMenu.isVisible = false
                }
            }
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    private companion object {
        enum class VisibilityStatus {
            VISIBLE,
            HIDDEN
        }

        private const val TAB_SYNC_DEBOUNCE = 200L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val BANNER_AUTO_SCROLL_DELAY = 4000L
    }
}