package com.mandarinkafe.mandarin.cart.domain.impl

import com.mandarinkafe.mandarin.cart.domain.api.CartRepository
import com.mandarinkafe.mandarin.cart.domain.usecase.CartInteractor

class CartInteractorImpl(repository: CartRepository) : CartInteractor