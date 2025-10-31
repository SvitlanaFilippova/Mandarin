    # Mandarin

# Мультиплатформенное приложение для заказа еды из локальной службы доставки Mandarin 🔎

Kotlin Multiplatform проект, объединяющий Android и iOS клиенты с общим интерфейсом, бизнес-логикой,
сетевым слоем, репозиториями и базой данных.
Приложение предназначено для удобного заказа блюд, кастомизации ингредиентов и отслеживания статуса
доставки в реальном времени.

Изначально Mandarin был полностью нативным Android-приложением, созданным на Kotlin + Jetpack
Compose, с архитектурой MVI + Hilt + Retrofit + Coil + SharedPreferences.
После завершения основной Android-версии проект был полностью мигрирован на Kotlin + Compose Multiplatform

## Над приложением работают 👨🏻‍💻

<a href="https://github.com/mandarin-kafe/Mandarin/graphs/contributors">  
  <img src="https://contrib.rocks/image?repo=mandarin-kafe/Mandarin&max=6" />
</a>

[**Светлана Филиппова**](https://github.com/SvitlanaFilippova)

[**Сергей Шахов**](https://github.com/SergeySh97)

[**Евгений Артеменко**](https://github.com/Ar-Eugene)  

[**Александр Родионов**](https://github.com/AlexDeyl) — [серверная часть проекта](https://github.com/SvitlanaFilippova/Mandarin-server)

## Возможности приложения ✨

| Функциональность                              | Статус           |
|-----------------------------------------------|------------------|
| Получение актуального меню                    | ✔️ Реализовано   |
| Поиск блюда по названию с поддержкой опечаток | ✔️ Реализовано   |
| Фильтры по тегам                              | ✔️ Реализовано   |
| Кастомизация блюда                            | ✔️ Реализовано   |
| Корзина                                       | ✔️ Реализовано   |
| Интерактивная карта с зонами доставки         | ✔️ Реализовано   |
| Избранные с поддержкой кастомизированных блюд | ✔️ Реализовано   |
| Оформление заказа                             | ✔️ Реализовано   |
| Отслеживание статуса заказа                   | ✔️ Реализовано   |
| История заказов                               | ✔️ Реализовано   |
| Сохранение адресов доставки                   | ✔️ Реализовано   |
| Форма обратной связи                          | ✔️ Реализовано   |
| Авторизация через sms                         | 🛠️ В разработке  |
| Онлайн-оплата заказа                          | ⏳ Планируется   |

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
| Интеграции       | iikoCloud API, Telegram Bot API                                      |
| Качество кода    | Detekt, Lint                                                         |
| Логирование      | Napier                                                               |
| Pull-to-refresh  | Materii PullRefresh                                                  |

## Скриншоты приложения 📱

<img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/1_Menu.jpg?raw=true" width="30%" height="30%"> <img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/2_Search.jpg?raw=true" width="30%" height="30%"> <img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/3_Cart.jpg?raw=true" width="30%" height="30%">

<img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/4_Checkout.jpg?raw=true" width="30%" height="30%"> <img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/5_favorites.jpg?raw=true" width="30%" height="30%"> <img src="https://github.com/mandarin-kafe/Mandarin/blob/dev/screenshots/6_map.jpg?raw=true" width="30%" height="30%">

## Общие требования 🗒️

Приложение поддерживает устройства, начиная с Android 8.0 (minSdkVersion = 26) или iOS 16.0 и выше .

## Начало работы 🛠️

Клонируйте этот репозиторий на свой компьютер: [git clone](https://github.com/mandarin-kafe/Mandarin)

### Android
Откройте проект в Android Studio.

Подключите свой телефон/эмулятор.

Соберите и запустите проект.


### iOS
Откройте /iosApp/ в Xcode

Установите зависимости (pod install)

Запустите сборку и установите приложение на симулятор или реальное устройство

⚠️ Из соображений безопасности API-ключи внешних сервисов, таких как карты и iiko, не включены в
исходный код. Поэтому при сборке проекта через Android Studio часть функциональности будет
недоступна.

📦 Полноценная Android-версия приложения (APK) доступна для скачивания в [RuStore](https://www.rustore.ru/catalog/app/com.mandarinkafe.mandarin) и в разделе [Releases](https://github.com/mandarin-kafe/Mandarin/releases)
