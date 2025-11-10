# План миграции адресов и истории заказов на серверное хранилище

## Структура файлов для реализации

### 1. Адреса (Addresses)

#### Создать файлы:
1. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/savedadresses/data/network/dto/AddressDto.kt`
   - DTO для адреса (сериализуемый)

2. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/savedadresses/data/network/dto/GeoPointDto.kt`
   - DTO для координат (сериализуемый)

3. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/savedadresses/data/network/AddressServerApi.kt`
   - API класс для HTTP запросов к серверу

4. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/savedadresses/data/network/AddressResponse.kt`
   - Response класс для GET запроса

5. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/savedadresses/data/network/AddressUpdateRequest.kt`
   - Request класс для POST запроса

6. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/savedadresses/data/remote/AddressRemoteDataSource.kt`
   - Интерфейс для удалённого хранилища адресов

7. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/savedadresses/data/remote/AddressRemoteDataSourceImpl.kt`
   - Реализация удалённого хранилища адресов

8. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/savedadresses/data/mapper/AddressMapper.kt`
   - Маппер для преобразования между Address и AddressDto

#### Изменить файлы:
1. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/savedadresses/data/impl/SavedAddressRepositoryImpl.kt`
   - Заменить `AddressStorage` на `AddressRemoteDataSource`
   - Убрать зависимость от локального хранилища

2. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/address/di/AddressModule.kt`
   - Убрать регистрацию `AddressStorageImpl`
   - Добавить регистрацию `AddressServerApi` и `AddressRemoteDataSourceImpl`

---

### 2. История заказов (Orders History)

#### Создать файлы:
1. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/ordershistory/data/network/dto/SavedOrderDto.kt`
   - DTO для сохранённого заказа (сериализуемый)

2. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/ordershistory/data/network/OrdersHistoryServerApi.kt`
   - API класс для HTTP запросов к серверу

3. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/ordershistory/data/network/OrdersHistoryResponse.kt`
   - Response класс для GET запроса

4. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/ordershistory/data/network/OrdersHistoryUpdateRequest.kt`
   - Request класс для POST запроса

5. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/ordershistory/data/remote/OrdersHistoryRemoteDataSource.kt`
   - Интерфейс для удалённого хранилища истории заказов

6. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/ordershistory/data/remote/OrdersHistoryRemoteDataSourceImpl.kt`
   - Реализация удалённого хранилища истории заказов

7. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/ordershistory/data/mapper/OrdersHistoryMapper.kt`
   - Маппер для преобразования между SavedOrder и SavedOrderDto

#### Изменить файлы:
1. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/ordershistory/data/impl/OrdersHistoryRepositoryImpl.kt`
   - Заменить `OrdersHistoryStorage` на `OrdersHistoryRemoteDataSource`
   - Убрать зависимость от локального хранилища (SQLDelight)

2. `composeApp/src/commonMain/kotlin/com/mandarinkafe/mandarin/features/ordershistory/di/OrdersHistoryModule.kt`
   - Убрать регистрацию `SQLDelightOrdersHistoryStorage` и `AppDatabase.savedOrderQueries`
   - Добавить регистрацию `OrdersHistoryServerApi` и `OrdersHistoryRemoteDataSourceImpl`

---

## Шаги реализации

1. **Создать DTO классы** - структуры данных для сериализации/десериализации
2. **Создать ServerApi классы** - HTTP клиенты для запросов к серверу
3. **Создать RemoteDataSource интерфейсы и реализации** - абстракция для работы с удалённым хранилищем
4. **Создать Mapper классы** - преобразование между доменными моделями и DTO
5. **Обновить Repository реализации** - заменить локальное хранилище на удалённое
6. **Обновить DI модули** - зарегистрировать новые зависимости
7. **Удалить старые файлы** - удалить локальные хранилища (AddressStorageImpl, SQLDelightOrdersHistoryStorage)

---

## Паттерн реализации (по аналогии с Favorites)

### Структура:
```
data/network/
  - dto/          # DTO классы для сериализации
  - *ServerApi.kt # HTTP клиент
  - *Response.kt  # Response классы
  - *Request.kt   # Request классы

data/remote/
  - *RemoteDataSource.kt      # Интерфейс
  - *RemoteDataSourceImpl.kt  # Реализация

data/mapper/
  - *Mapper.kt    # Мапперы между доменными моделями и DTO
```

### Зависимости:
- `ServerApi` использует `HttpClient` с `SERVER_AUTH_CLIENT_QUALIFIER`
- `RemoteDataSourceImpl` использует `ServerApi` и `AuthRepository`
- `RepositoryImpl` использует `RemoteDataSource` вместо локального хранилища

