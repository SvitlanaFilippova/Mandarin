package com.mandarinkafe.mandarin.features.cart.data.local

//
//class SharedPrefsCartStorageImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
//    CartStorage {
//
//    override suspend fun clearCart() {
//        sharedPreferences.edit { remove(CART_KEY) }
//    }
//
//
//    override suspend fun saveCart(items: List<StoredCartItem>) {
//        sharedPreferences.edit {
//            putString(CART_KEY, Gson().toJson(items))
//        }
//    }
//
//    override suspend fun getCart(): List<StoredCartItem> {
//        return try {
//            val json = sharedPreferences.getString(CART_KEY, null)
//            val listType = object : TypeToken<List<StoredCartItem>>() {}.type
//            if (json.isNullOrEmpty()) {
//                mutableListOf()
//            } else {
//                Gson().fromJson(json, listType) ?: mutableListOf()
//            }
//        } catch (e: ClassCastException) {
//            Log.d("getCart error", "ClassCastException: ${e.message}. Очищаю сохранённую корзину")
//            clearCart()
//            mutableListOf()
//
//        } catch (e: NullPointerException) {
//            Log.d("getCart error", "NullPointerException: ${e.message}. Очищаю сохранённую корзину")
//            clearCart()
//            mutableListOf()
//        }
//    }
//
//    private companion object {
//        const val CART_KEY = "CART_KEY"
//    }
//}