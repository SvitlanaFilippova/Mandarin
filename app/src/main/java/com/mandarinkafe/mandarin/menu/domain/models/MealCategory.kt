package com.mandarinkafe.mandarin.menu.domain.models

import com.mandarinkafe.mandarin.menu.data.DtoToDomainConverter.Companion.PIZZA_ICON
import com.mandarinkafe.mandarin.menu.data.DtoToDomainConverter.Companion.SUSHI_ICON


data class MealCategory(
    val id: String,
    val name: String,
    val meals: List<Meal>?, //перечень всех блюд, входящих в категорию. Для родительской категории - null
    val subCategories: List<MealCategory>?,
    val tabIcon: String?
)


val mockMenuData = arrayListOf<MealCategory>(
    MealCategory(
        id = "1",
        name = "ПИЦЦА", meals = null,
        subCategories = listOf(
            MealCategory(
                id = "d3872541-5a16-4c21-b9e7-c8ab8912fd36",
                name = "Классическая",
                meals = mockPizza35List,
                subCategories = null,
                tabIcon = null
            ),
            MealCategory(
                id = "d3872541-5a16-4c21-b9e7-c8ab8912fd36",
                name = "Римская",
                meals = mockPizzaRimList,
                subCategories = null,
                tabIcon = null
            )
        ),
        tabIcon = PIZZA_ICON
    ),
    MealCategory(
        id = "2",
        name = "СУШИ И РОЛЛЫ", meals = null,
        subCategories = listOf(
            MealCategory(
                id = "903872541-5a16-3533-b9e7-c8ab8912fd36",
                name = "Роллы",
                meals = mockRollsList,
                subCategories = null,
                tabIcon = null
            ),
            MealCategory(
                id = "903872541-5a16-3533-b9e7-c8ab8912fd32",
                name = "Маки-суши",
                meals = mockMakiList,
                subCategories = null,
                tabIcon = null
            ),
        ),
        tabIcon = SUSHI_ICON
    ),
    MealCategory(
        id = "c21-b9e7-c8ab8912fd44444909090544436",
        name = "БУРГЕРЫ",
        meals = mockBurgerList,
        subCategories = null,
        tabIcon = "https://static.tildacdn.com/tild3330-6165-4238-b132-303663663062/noroot.png"
    ),
    MealCategory(
        id = "d3222222222fd891246465554fd36",
        name = "ХОТ-ДОГИ И ДОНЕР",
        meals = mockHotDogList,
        subCategories = null,
        tabIcon = "https://static.tildacdn.com/tild6430-3834-4539-b837-616230626534/noroot.png"
    ),
    MealCategory(
        id = "j3872541-5a16-444448912555f9090d36",
        name = "WOK",
        meals = mockWokList,
        subCategories = null,
        tabIcon = "https://static.tildacdn.com/tild3233-3165-4430-b231-313839323231/_.png"
    )
)

val mockAddsForPizza = arrayListOf<MealCategory>(
    MealCategory(
        id = "parent_pizza_adds_id",
        name = "Добавки к пицце",
        meals = null,
        subCategories = listOf(
            MealCategory(
                id = "d3872-5ab9e7-c8ab812250d36",
                name = "Сыр",
                meals = mockPizzaAddsCheeseList,
                subCategories = null,
                tabIcon = null
            ),
            MealCategory(
                id = "adds_for_pizza_zs78g3zsg535",
                name = "Мясо",
                meals = mockPizzaAddsMeatList,
                subCategories = null,
                tabIcon = null
            )
        ),
        tabIcon = null
    )
)
