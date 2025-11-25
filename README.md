# Mandarin 🔎

Мультиплатформенное приложение для заказа еды из локальной службы доставки Mandarin

Kotlin Multiplatform проект, объединяющий Android и iOS клиенты с общим интерфейсом, бизнес-логикой,
сетевым слоем, репозиториями и базой данных.
Приложение предназначено для удобного заказа блюд, кастомизации ингредиентов и отслеживания статуса
доставки в реальном времени.

Изначально Mandarin был полностью нативным Android-приложением, созданным на Kotlin + Jetpack
Compose, с архитектурой MVI + Hilt + Retrofit + Coil + SharedPreferences.
После завершения основной Android-версии проект был полностью мигрирован на Kotlin + Compose
Multiplatform.

## Команда проекта 👨🏻‍💻

<a href="https://github.com/mandarin-kafe/Mandarin/graphs/contributors">  
  <img src="https://contrib.rocks/image?repo=mandarin-kafe/Mandarin&max=6" />
</a>

[**Светлана Филиппова**](https://github.com/SvitlanaFilippova) — **архитектор и ведущий разработчик**  
Архитектура проекта, дизайн UI/UX, разработка клиентской части и серверной логики

[**Сергей Шахов**](https://github.com/SergeySh97) — **разработчик**  
BottomNavigation, интеграция с Yandex MapKit

[**Александр Родионов**](https://github.com/AlexDeyl) — **backend разработчик**  
Админ-панель, настройка зон доставки через интерактивную карту, управление баннерами и
сопутствующими товарами, первичная настройка БД [(репозиторий сервера)](https://github.com/SvitlanaFilippova/Mandarin-server)


## Возможности приложения ✨

| Функциональность                                     | Статус         |
|------------------------------------------------------|----------------|
| Получение и обработка актуального меню               | ✔️ Реализовано |
| Синхронизация прокрутки меню и табов категорий       | ✔️ Реализовано |
| Поиск блюда по названию с поддержкой опечаток        | ✔️ Реализовано |
| Фильтры по тегам                                     | ✔️ Реализовано |
| Кастомизация блюда                                   | ✔️ Реализовано |
| Избранные с поддержкой кастомизированных блюд        | ✔️ Реализовано |
| Корзина с умными рекомендациями товаров              | ✔️ Реализовано |
| Интерактивная карта с зонами доставки                | ✔️ Реализовано |
| Сохранение адресов доставки                          | ✔️ Реализовано |
| Оформление заказа                                    | ✔️ Реализовано |
| Онлайн-оплата заказа                                 | ✔️ Реализовано |
| Автоматическое применение скидок по карте лояльности | ✔️ Реализовано |
| Отслеживание статуса заказа                          | ✔️ Реализовано |
| История заказов                                      | ✔️ Реализовано |
| Быстрый повтор заказа                                | ✔️ Реализовано |
| Отмена заказа с автоматическим возвратом оплаты      | ✔️ Реализовано |
| Авторизация через звонок или sms                     | ✔️ Реализовано |
| Личный кабинет и гостевой режим                      | ✔️ Реализовано |
| Форма обратной связи                                 | ✔️ Реализовано |

## Используемые инструменты и технологии 📚

| Категория        | Технологии                                                           |
|------------------|----------------------------------------------------------------------|
| UI               | Compose Multiplatform, Material3, Moko Resources                     |
| Архитектура      | MVI, StateFlow, Coroutines, <del>Hilt</del> ->  Koin                 |
| Навигация        | Navigation Compose (KMP)                                             |
| Работа с данными | SQLDelight, <del>SharedPreferences</del> -> DataStore                |
| Работа с данными | <del>Retrofit</del> -> Ktor (OkHttp / Darwin), kotlinx.serialization |
| Изображения      | <del>Coil</del> -> Kamel                                             |
| Карты            | Yandex MapKit                                                        |
| Интеграции       | iikoCloud API, Telegram Bot API, YooKassa SDK                        |
| Качество кода    | Detekt, Lint                                                         |
| Логирование      | Napier                                                               |
| Pull-to-refresh  | Materii PullRefresh                                                  |

## Архитектура 🏗️

Проект использует архитектуру **MVI (Model-View-Intent)** с четким разделением на слои:

- **Presentation Layer**: UI компоненты на Compose Multiplatform, ViewModels с использованием
  StateFlow для управления состоянием
- **Domain Layer**: Бизнес-логика, use cases и domain-модели, независимые от платформы
- **Data Layer**: Репозитории, сетевой слой (Ktor), локальная база данных (SQLDelight), DataStore
  для хранения настроек

Проект организован по модульному принципу: каждая функциональность (feature) имеет собственную
структуру с разделением на `data/`, `domain/` и `presentation/` слои. Dependency Injection
реализован через Koin.

## Интерфейс приложения 📱

<img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/1_Menu.gif?raw=true" width="30%" height="30%"> <img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/2_Search.jpg?raw=true" width="30%" height="30%"> <img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/3_Favorites.jpg?raw=true" width="30%" height="30%">

<img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/4_Map.gif?raw=true" width="30%" height="30%"> <img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/5_Cart.jpg?raw=true" width="30%" height="30%"> <img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/6_Checkout.jpg?raw=true" width="30%" height="30%">

<img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/7_Contacts.jpg?raw=true" width="30%" height="30%"> <img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/8_Account.jpg?raw=true" width="30%" height="30%"> <img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/9_MealDetails.gif?raw=true" width="30%" height="30%">

## Общие требования 🗒️

Приложение поддерживает устройства, начиная с Android 8.0 (minSdkVersion = 26) или iOS 16.0 и выше .

## Начало работы 🛠️

Клонируйте этот репозиторий на свой
компьютер: [git clone](https://github.com/mandarin-kafe/Mandarin)

### Android

Откройте проект в Android Studio.

Подключите свой телефон/эмулятор.

Соберите и запустите проект.

### iOS

Откройте /iosApp/ в Xcode

Установите зависимости (pod install)

Запустите сборку и установите приложение на симулятор или реальное устройство

⚠️ Из соображений безопасности API-ключи внешних сервисов, таких как карты и iiko, не включены в
исходный код. Поэтому при самостоятельной сборке проекта часть функциональности будет
недоступна.

## Скачать приложение 📦

- **Android
  **: [RuStore](https://www.rustore.ru/catalog/app/com.mandarinkafe.mandarin) | [Releases](https://github.com/mandarin-kafe/Mandarin/releases)
- **iOS**: *Скоро в App Store*
