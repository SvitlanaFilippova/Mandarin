# Требуемые эндпоинты для миграции на серверное хранилище

## 1. Адреса (Addresses)

### GET /addresses
Получить все сохранённые адреса пользователя.

**Заголовки:**
- `Authorization: Bearer {token}`
- `x-api-key: {api_key}`

**Ответ:**
```json
{
  "data": [
    {
      "id": "string",
      "point": {
        "latitude": 0.0,
        "longitude": 0.0
      },
      "streetAndBuilding": "string",
      "addressType": "APARTMENT" | "PRIVATE_HOUSE" | "OTHER",
      "apartmentNumber": "string",
      "entrance": "string",
      "floor": "string",
      "intercom": "string",
      "comment": "string"
    }
  ]
}
```

**Структура AddressDto:**
- `id: String` - уникальный идентификатор адреса
- `point: GeoPointDto?` - координаты (может быть null)
  - `latitude: Double`
  - `longitude: Double`
- `streetAndBuilding: String?` - улица и дом (может быть null)
- `addressType: String?` - тип адреса: "APARTMENT", "PRIVATE_HOUSE", "OTHER" (может быть null)
- `apartmentNumber: String?` - номер квартиры (может быть null)
- `entrance: String?` - подъезд (может быть null)
- `floor: String?` - этаж (может быть null)
- `intercom: String?` - домофон (может быть null)
- `comment: String?` - комментарий (может быть null)

---

### POST /addresses
Создать или обновить адрес.

**Заголовки:**
- `Authorization: Bearer {token}`
- `x-api-key: {api_key}`

**Тело запроса:**
```json
{
  "data": {
    "id": "string",
    "point": {
      "latitude": 0.0,
      "longitude": 0.0
    },
    "streetAndBuilding": "string",
    "addressType": "APARTMENT" | "PRIVATE_HOUSE" | "OTHER",
    "apartmentNumber": "string",
    "entrance": "string",
    "floor": "string",
    "intercom": "string",
    "comment": "string"
  }
}
```

**Примечание:** Все поля кроме `id` могут быть `null`. Поле `point` также может быть `null`.

**Ответ:**
```json
{
  "resultCode": 200
}
```

**Примечание:** Если адрес с таким `id` уже существует, он должен быть обновлён. Если `id` не указан или адреса с таким `id` нет, создаётся новый адрес.

---

### DELETE /addresses/{id}
Удалить адрес по идентификатору.

**Заголовки:**
- `Authorization: Bearer {token}`
- `x-api-key: {api_key}`

**Параметры пути:**
- `id: String` - идентификатор адреса

**Ответ:**
```json
{
  "resultCode": 200
}
```

---

## 2. История заказов (Orders History)

### GET /orders/history
Получить историю заказов пользователя.

**Заголовки:**
- `Authorization: Bearer {token}`
- `x-api-key: {api_key}`

**Ответ:**
```json
{
  "data": [
    {
      "id": "string",
      "number": "string",
      "timestamp": 0,
      "whenCreated": "string",
      "orderType": "DELIVERY" | "SELF_PICKUP",
      "addressLine1": "string",
      "addressDetails": "string",
      "mealNames": "string"
    }
  ]
}
```

**Структура SavedOrderDto:**
- `id: String` - внутренний ID заказа
- `number: String` - номер заказа, который видят операторы в терминале
- `timestamp: Long` - временная метка создания заказа (Unix timestamp в миллисекундах)
- `whenCreated: String` - дата и время создания заказа (строковое представление)
- `orderType: String?` - тип заказа: "DELIVERY" или "SELF_PICKUP" (может быть null)
- `addressLine1: String` - первая строка адреса
- `addressDetails: String` - детали адреса
- `mealNames: String` - названия блюд (через запятую или другой разделитель)

**Примечание:** Поле `status` не хранится в истории заказов, оно проверяется и задаётся отдельно через другие эндпоинты.


### POST /orders/history
Сохранить заказ в историю.

**Заголовки:**
- `Authorization: Bearer {token}`
- `x-api-key: {api_key}`

**Тело запроса:**
```json
{
  "data": {
    "id": "string",
    "number": "string",
    "timestamp": 0,
    "whenCreated": "string",
    "orderType": "DELIVERY" | "SELF_PICKUP",
    "addressLine1": "string",
    "addressDetails": "string",
    "mealNames": "string"
  }
}
```

**Ответ:**
```json
{
  "resultCode": 200
}
```

**Примечание:** Если заказ с таким `id` уже существует, он должен быть обновлён.

---

### DELETE /orders/history/{id}
Удалить заказ из истории по идентификатору.

**Заголовки:**
- `Authorization: Bearer {token}`
- `x-api-key: {api_key}`

**Параметры пути:**
- `id: String` - идентификатор заказа

**Ответ:**
```json
{
  "resultCode": 200
}
```

---

## Общие требования

1. **Авторизация:** Все эндпоинты требуют авторизации через Bearer token в заголовке `Authorization`.

2. **API Key:** Все запросы должны содержать заголовок `x-api-key` с API ключом.

3. **Формат ответа:** Все успешные ответы должны возвращать HTTP статус 200 и объект с полем `resultCode: 200`.

4. **Ошибки:**
   - 401 Unauthorized - если токен невалиден или отсутствует
   - 400 Bad Request - если данные запроса некорректны
   - 500 Internal Server Error - при внутренних ошибках сервера

5. **Пустые коллекции:** Если данных нет, возвращать пустой массив `[]` в поле `data`.

6. **Обновление данных:** При POST запросах, если запись с указанным `id` уже существует, она должна быть обновлена (upsert операция).

